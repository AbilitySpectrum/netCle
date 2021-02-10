/* * * * * * * * * * * * * * * * * * * * * * * * * * * *
    Copyright (C) 2019 Andrew Hodgson

    This file is part of the Sensact Arduino software.

    Sensact Arduino software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sensact Arduino software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this Sensact Arduino software.
    If not, see <https://www.gnu.org/licenses/>.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */
int debug_hist[103];
bool debug_out = false;

#include "netCle.h"
#include "Sensors.h"
#include "Triggers.h"
#include "Actions.h"
#include "IO.h"
#include <EEPROM.h>
#ifdef SOFT_SERIAL
#include <SoftwareSerial.h>
#endif
//#include <IRLib2.h>
#include <Wire.h>

#ifdef MEMCHECK
#define MEMCHECK_SIZE 652
brkPoints BreakPoints;
char memcheck_done = 0;
char memcheck_started = 0;
#endif

enum rMode{RUN, REPORT, IDLEX}; // IDLE seems to be a keyword - thus IDLEX.
rMode runMode;

extern Sensors sensors;
extern PCInputSensor *pcInput;  // Needed here so we can push commands to it.
Triggers triggers;
extern Actors actors;

SerialInputStream serialInput;
SerialOutputStream serialOutput;

#ifdef SOFT_SERIAL
SoftwareSerial softSerial(11,7);  // Rx, Tx
SoftSerialInputStream softInput(&softSerial);
SoftSerialOutputStream softOutput(&softSerial);

InputStream *currentInput;  // Set to either serialInput or softInput.
OutputStream *currentOutput;
#endif

long lastActionTime = 0;

void setup() {
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_BLUE, OUTPUT);


  Serial.begin(9600);
#ifdef SOFT_SERIAL
  softSerial.begin(9600);
  currentInput = &serialInput;
  currentOutput = &serialOutput;
#endif

#ifdef MEMCHECK
  BreakPoints.atStart = (int) __brkval;
#endif
  sensors.init();
  actors.init();
  triggers.init(sensors.getHighestID());
#ifdef MEMCHECK
  BreakPoints.triggersInit = (int) __brkval;
#endif

  runMode = RUN;
  setLED();

}

void loop() {
  int cmd = checkForCommand();
  int val;

//  Serial.print(F("ram: ")); Serial.println(freeRam());
//  delay(1000);

#ifdef MEMCHECK
  if (runMode == IDLEX) {
    if (!memcheck_started) {
      startMemCheck();
      memcheck_started = 1;
    }
    if (!memcheck_done) {
      doMemCheck();
      memcheck_done = 1;
    }
//    Serial.print(F("ram: ")); Serial.println(freeRam());
  } else {
    memcheck_done = 0;
  }
#endif

  switch(cmd) {
    case START_OF_TRIGGER_BLOCK:
#ifdef SOFT_SERIAL
      currentInput->init();
      val = triggers.readTriggers(currentInput);
#else
      serialInput.init();
      val = triggers.readTriggers(&serialInput);
#endif
      if (val == IO_ERROR) {
        flashLED(LED_RED);
        tone(SENSACT_BUZZER, 190, 500);
      } else {
        flashLED(LED_GREEN);
        tone(SENSACT_BUZZER, 800, 20);
        delay(500);
        tone(SENSACT_BUZZER, 800, 20);
        triggers.reset();
        actors.reset();
        sensors.reset();
      }
      break;

    case REQUEST_TRIGGERS:
#ifdef SOFT_SERIAL
      currentOutput->init();
      triggers.sendTriggers(currentOutput);
#else
      serialOutput.init();
      triggers.sendTriggers(&serialOutput);
#endif
      break;

    case GET_VERSION: // Get Version also sets IDLEX mode.
      sendVersionInfo();
      runMode = IDLEX;
      setLED();
      break;

    case RUN_SENSACT:
      runMode = RUN;
      triggers.reset();
      actors.reset();
      sensors.reset();
      setLED();
      break;

    case REPORT_MODE:
      runMode = REPORT;
      setLED();
      break;

    case KEYBOARD_CMD:
#ifdef SOFT_SERIAL
      int cmd = currentInput->_getChar();
#else
      int cmd = Serial.read();
#endif
      //      pcInput->setNextCmd(cmd);
      if (cmd == '?') { // tmp debug messgage
        Serial.print("W");
        for (int i = 0; i < 103; i++) {

          if ( debug_hist[i] != 0 ) {
            Serial.print(i); Serial.print(","); Serial.print(debug_hist[i]); Serial.println();
          }
        }
        Serial.println("Z");
      } else if (cmd == '+') { // tmp debug messgage
        debug_out = !debug_out;
      } else {
        pcInput->setNextCmd(cmd);
      }
      break;
  }

  const SensorData *pSensorData;

  if (runMode == REPORT) {
    if ((lastActionTime + REPORTING_INTERVAL) < millis()) {
      pSensorData = sensors.getData();
      report(pSensorData);
      lastActionTime = millis();
    }

  } else if (runMode == RUN) {
    if ((lastActionTime + READING_INTERVAL) < millis()) {
      pSensorData = sensors.getData();
      const ActionData *pActionData = triggers.getActions(pSensorData);
      actors.doActions(pActionData);
      lastActionTime = millis();
    }
  } // ELSE IDLEX mode - do nothing.
}

int checkForCommand() {
  // Only one of Serial and SoftSerial should be available,
  // so we should enter only one while loop.
  while (Serial.available()) {
    int val = Serial.read();
    // Is it one of the unique command characters (Q,R,S,T,U or V) ?
    if (val >= MIN_COMMAND && val <= MAX_COMMAND) {
#ifdef SOFT_SERIAL
      currentInput = &serialInput;
      currentOutput = &serialOutput;
#endif
      return val;
    }
  }
#ifdef SOFT_SERIAL
  while (softSerial.available()) {
    int val = softSerial.read();
    // Is it one of the unique command characters (Q,R,S,T,U or V) ?
    if (val >= MIN_COMMAND && val <= MAX_COMMAND) {
      currentInput = &softInput;
      currentOutput = &softOutput;
      return val;
    }
  }
#endif
  return 0;
}

void flashLED(int led) {
  ledsOff();
  delay(250);
  digitalWrite(led, HIGH);
  delay(250);
  digitalWrite(led, LOW);
  delay(250);
  setLED();
}

void setLED() {
  ledsOff();
  if (runMode == RUN) {
     digitalWrite(LED_GREEN, HIGH);
  } else if (runMode == REPORT) {
     digitalWrite(LED_RED, HIGH);
  } else { // IDLEX mode
    digitalWrite(LED_BLUE, HIGH);
  }
}

void ledsOff() {
  digitalWrite(LED_RED, LOW);
  digitalWrite(LED_BLUE, LOW);
  digitalWrite(LED_GREEN, LOW);
}

void sendVersionInfo() {
#ifdef SOFT_SERIAL
  if (currentOutput == &softOutput) {
    softSerial.print(GET_VERSION);  // V
    softSerial.print(VERSION);  // version #
    softSerial.print(END_OF_BLOCK); // Z
  } else {
    Serial.print(GET_VERSION);  // V
    Serial.print(VERSION);  // version #
    Serial.print(END_OF_BLOCK); // Z
  }
#else
    Serial.print(GET_VERSION);  // V
    Serial.print(VERSION);  // version #
    Serial.print(END_OF_BLOCK); // Z
#endif
}

// Report sensor values
void report(const SensorData *sdata) {
#ifdef SOFT_SERIAL
  currentOutput->init();
  currentOutput->putChar(START_OF_SENSOR_DATA);
  int len = sdata->length();
  currentOutput->putNum(len);
  for(int i=0; i<len; i++) {
    const SensorDatum *d = sdata->getValue(i);
    currentOutput->putID(d->sensorID);
    currentOutput->putNum(d->sensorValue);
  }
  currentOutput->putChar('\n');  // For debug readability
  currentOutput->putChar(END_OF_BLOCK);
#else
  serialOutput.init();
  serialOutput.putChar(START_OF_SENSOR_DATA);
  int len = sdata->length();
  serialOutput.putNum(len);
  for (int i = 0; i < len; i++) {
    const SensorDatum *d = sdata->getValue(i);
    serialOutput.putID(d->sensorID);
    serialOutput.putNum(d->sensorValue);
  }
  serialOutput.putChar('\n');  // For debug readability
  serialOutput.putChar(END_OF_BLOCK);
#endif
}

int freeRam ()  {
  extern int __heap_start, *__brkval;
  int v;
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}

#ifdef MEMCHECK
char *memBuf;
void startMemCheck() {
  memBuf = new char[MEMCHECK_SIZE];
  for (int i = 0; i < MEMCHECK_SIZE; i++) {
    memBuf[i] = 1;
  }
}

void doMemCheck() {
  extern int __heap_start;
  int i;
  for (i = 0; i < MEMCHECK_SIZE; i++) {
    if (memBuf[i] != 1) {
      break;
    }
  }
  Serial.println();
  Serial.println((int) &__heap_start);
  Serial.println(BreakPoints.atStart);
  Serial.println(BreakPoints.sensorsAlloc);
  Serial.println(BreakPoints.sensorsInit);
  Serial.println(BreakPoints.actorsAlloc);
  Serial.println(BreakPoints.actorsInit);
  Serial.println(BreakPoints.triggersInit);
  Serial.println((int) __brkval);

  Serial.println((int) &i);

  Serial.println((int)memBuf);
  Serial.print("MemCheck: "); Serial.println(i);
}
#endif
