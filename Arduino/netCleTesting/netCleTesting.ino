/*
 * Test Code for Sensact board V3
 * 
 * Update: For V4.1 Hardware.
 * 
 * Commands can be sent via the serial link to drive Sensact functions.
 * 
 * 'i' + 0 to 15.  Sets power to the input jacks according to bit values.
 * 'o' + 0 to 3.   Turns on the output ports according to bit values.
 * 'l' + 0 to 7.   Sets the value of LED outputs.
 * 'b'             Sounds the buzzer.
 * 'r'             Reads the value of all input pins..
 * 'r' 1 to 6      Reads the value of a particular input pin.
 */

#include "Gyro.h"

#define INPUT_1   A0 
#define INPUT_2   A1 
#define INPUT_3   A2 
#define INPUT_4   A3 
#define INPUT_5   A4 
#define INPUT_6   A5 

#define OUTPUT_A 11  
#define OUTPUT_B 12  
#define IR_PIN 9

#define LED_RED   5
#define LED_GREEN 6
#define LED_BLUE  7

#define BUZZER  10

GyroSensor gyro;

void setup() {
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_BLUE, OUTPUT);
  pinMode(LED_GREEN, OUTPUT);

  pinMode(INPUT_1, INPUT);
  pinMode(INPUT_2, INPUT);
  pinMode(INPUT_3, INPUT);
  pinMode(INPUT_4, INPUT);
  pinMode(INPUT_5, INPUT);
  pinMode(INPUT_6, INPUT);

  pinMode(IR_PIN, OUTPUT);
  pinMode(OUTPUT_A, OUTPUT);
  pinMode(OUTPUT_B, OUTPUT);  

  Serial.begin(9600);
  while(!Serial);
}

int reading = 0;
long lastReadTime;
#define READ_INTERVAL_MS 1000

void loop() {
  int cmd;
  int val;

  if (reading && ((millis() - lastReadTime) > READ_INTERVAL_MS) ) {
    doRead();
    lastReadTime = millis();
  }
  if (Serial.available()) {
    cmd =  getNextToken();
    if (cmd == -1) { // Nothing but white space.
      return;
    } 
    reading = 0;
    switch(cmd) {                
      case 'o':
        val = getNextToken();
        if (val == 'a') {
          doOutput(OUTPUT_A);
          Serial.println("Output A on" ); 
        } else if (val == 'b') {
          doOutput(OUTPUT_B);
          Serial.println("Output B on" ); 
        } else if (val == '0') {
          doOutput(0);
          Serial.println("Outputs off" ); 
        } else {
          Serial.println("Bad output option.");
        }
        break;

      case 'l':
        val = getNextToken();
        if (val == 'r') {
          setLED(LED_RED);
          Serial.println("LED Red" ); 
        } else if (val == 'g') {
          setLED(LED_GREEN);
          Serial.println("LED Green" ); 
        } else if (val == 'b') {
          setLED(LED_BLUE);
          Serial.println("LED Blue" ); 
        } else if (val == '0') {
          setLED(0);
          Serial.println("LED off");
        } else {
          Serial.println("Bad LED value.");
        }
        break;
        
      case 'b':
        Serial.println("BEEP!");
        doBeep();
        break;
        
      case 'r':
        Serial.println("Read All");
        doRead();
        reading = 1;
        lastReadTime = millis();
        break;

      case 'g':
        gyro.init();
        delay(10);     
        gyro.readValues();
        break;

      case 't':
      Serial.println("TV IR");
        for(int i=0; i<8; i++) {
          analogWrite(IR_PIN, 80);  // ~30% duty cycle
          delay(250);
          digitalWrite(IR_PIN, 0);
          delay(250);
        }
        break;

      case 'L':
        Serial.println("Light Box");
        doLightBox();
        break;
        
      case 'h':
        Serial.println("Help");
        doHelp();
        break;

      default:
        Serial.println("Huh?");
        break;
    }
    
  }

}

int getNextToken() {
  int val;
  val = Serial.read();
  // Ignore white space
  while(val == ' ' || val == '\r' || val == '\n' || val == '\t') {
    val = Serial.read();
  }
  return val;
}

void doBeep() {
  tone( BUZZER, 400, 250);
  delay(250);
  tone( BUZZER, 500, 250);
  delay(250);
  tone( BUZZER, 600, 250);
  delay(250);
  tone( BUZZER, 800, 500);
}

void setLED(int color) {
  digitalWrite(LED_RED, LOW);
  digitalWrite(LED_GREEN, LOW);
  digitalWrite(LED_BLUE, LOW);
  if (color != 0) {
    digitalWrite(color, HIGH);
  }
}

void doOutput(int val) {
  digitalWrite(OUTPUT_A, LOW);
  digitalWrite(OUTPUT_B, LOW);
  digitalWrite(val, HIGH);
}

void print5(int val) {
  Serial.print(' ');
  int v = val / 1000;
  Serial.print(v);
  val = val - 1000 * v;
  v = val / 100;
  Serial.print(v);
  val = val - 100 * v;
  v = val / 10;  
  Serial.print(v);
  val = val - 10 * v;
  Serial.print(val);
  Serial.print(' ');
}

void doRead() {
    int val = analogRead(INPUT_5);
    print5(val);
    val = analogRead(INPUT_6);
    print5(val);
    val = analogRead(INPUT_3);
    print5(val);
    val = analogRead(INPUT_4);
    print5(val);
    val = analogRead(INPUT_1);
    print5(val);
    val = analogRead(INPUT_2);
    print5(val);
    Serial.println();
}

#define MCP23008_ADDRESS 0x20

void doLightBox() {
  Wire.begin();
  Wire.beginTransmission(MCP23008_ADDRESS);
  Wire.write((byte)0);
  Wire.write((byte)0x00);  // Set all pins to output mode
  Wire.endTransmission(); 

  int i;
  for(i=0x80; ; i >>= 1) {
    Wire.beginTransmission(MCP23008_ADDRESS);
    Wire.write((byte)9);  // GPIO Address
    Wire.write(i);
    Wire.endTransmission();  
    delay(200);
    if (i == 0) break;
  }  
  Serial.println("Light box done.");
}

void doHelp() {
  Serial.println(" 'o' + 'a' or 'b. Turns on an output port.");
  Serial.println(" 'o0' (o + zero) Turns all outputs off.");
  Serial.println(" 'l' + 'r', 'g' or 'b'.   Sets the color of the LED.");
  Serial.println(" 'l0'            Turns the LED off.");
  Serial.println(" 'b'             Sounds the buzzer.");
  Serial.println(" 'r'             Reads the value of all input pins.");
  Serial.println("                 repeating until another command is entered.");
  Serial.println("                 Output is lines of 6 digits, giving the values for the 6 inputs.");
  Serial.println(" 'g'             Reads I2C Gyroscope.");
  Serial.println(" 'L'             Blinks lights on the light box.");
  Serial.println("                 This is an alternate way to test the I2C connection");
  Serial.println(" 't'             Runs the TV IR.  On/Off cycling every 1/4 second for two seconds.");
  Serial.println("                 Watch with a cell phone camera or with a multi-tester.");
}
