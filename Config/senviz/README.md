2020-11-13 Fri: 2020-05-30 Sat: 2017-12-31 Sun: 2016-06-11 Sat

viz.html - generic visualizer; data from web serial (later, mqtt)

remote config - sensact remote config

# viz.html - local configuration

# remoteconfig - from senviz
## vizclient.html -- ./src/viz.js ./src/as_client.js ./src/utils.js
## vizsupport.html -- ./src/viz.js ./src/as_support.js ./src/utils.js
## node rcexchange.js 127.0.0.1  -- test with local server
## run "java -jar netCleCondig.jar 127:0:0:1"

[[obsolete client runs ./serial-port-json-server (changed to p5.serialcontrol.app in utils3.js; web_serial in utils4.js)]]
tech also runs java Config Tool as above
client runs vizclient.html
server provides 'ID' which client gives to tech
tech runs vizsupport.html, enters ID
tech is connected to sensact via client's web app

## 2021-02-10 Wed using Web_Serial
## 2021-01-31 note on serial port server
moved to using p5.serialcontrol app, changed to utils3.js

~/0/senviz

# visualizer - originally from ~/0/vkb
## senviz.html ./src/senviz.js ./src/utils.js

Can simultaneously communicate with hub via
(a) local serial port (./serial-port-json-server)
(b) remote server socket_exchange.js

# interchange
### rcexchange.js

derived from nodemcu2019/socket_exchange.js

node rcexchange.js

issues:-
- when hub drops, does not remove its obj properly

design:-

- senviz makes connection to exchange first i.e. "monitor"
- updater (peripheral) next i.e. "hub"
- (optioally) Config connects to exchange i.e. "config"

mqtt channel = 'exch'

#### use systemctl services to keep exchange.js running

https://linoxide.com/linux-command/linux-systemd-commands/
1) config file in: /etc/systemd/system/ylh-exch.service
2) run once: sudo systemctl start ylh-exch.service
3) check: systemctl status ylh-exch
4) program change, relaunch: sudo systemctl restart ylh-exch
5) enable service at boot: sudo systemctl enable ylh-exch

# utilities/

### mqtt_listener.js

listens to a channel

### interact.js 

via mqtt, interacts with wifi-peripheral (n-updater.js) 

# Transfer Protocol 
Numbers
Numbers are transferred in MSB order with 4 bits of data transferred per byte.  The high-order bits of each byte are set to place the whole byte in a printable part of the ASCII range.

Numbers are divided into two types: actual numeric values (trigger values and time values) and numeric identifiers (sensor and action ID numbers and state values).

For actual numeric values the high-order bits are set to 6.  This means the number are encoded in the range 0x60 to 0x6F which is ‘\`’ (back-quote) to ‘o’ (lower-case O).

Numeric identifiers have 4 as the high-order bits putting them in the range ‘@’ to ‘O’.

Major Commands 
This leaves the letters p to z and P to Z available for other things.

The upper case characters P to Z are reserved for commands and the start and end of transmission blocks.

Command or Block Marker
Byte Value
Put Sensact into Report mode
Q
Start of trigger data block
T
Start of sensor data block
S
Put Sensact into Run mode
R
Request triggers from Sensact
U
Get version infomation
V
End of data block
Z


#### netcle.h
// === Protocol Values === //
// -- Commands and Block Headers -- //
#ifdef HUB20
#define VERSION         "2.00b"
#else
#define VERSION         "1.03b"
#endif
#define REPORT_MODE       'Q'
#define RUN_SENSACT       'R'
#define START_OF_SENSOR_DATA 'S'
#define START_OF_TRIGGER_BLOCK 'T'
#define REQUEST_TRIGGERS  'U'
#define GET_VERSION       'V'
#define KEYBOARD_CMD      'W'
#define MOUSE_SPEED       'Y'
#define MIN_COMMAND  'Q'
#define MAX_COMMAND  'W'

// -- Data block separators -- //
#define TRIGGER_START  't'
#define TRIGGER_END    'z'
#define END_OF_BLOCK   'Z'

// -- Value encoding -- //
#define NUMBER_MASK 0x60
#define ID_MASK     0x40
#define CONDITION_MASK '0'
#define BOOL_TRUE   'p'
#define BOOL_FALSE  'q'

// === Timing Constants - all in ms === //
#define REPORTING_INTERVAL  200  // Interval between reports of signal levels
#define READING_INTERVAL     10  // Interval between reading signals in run mode
#define REFRACTORY          800  // Interval within which the output will not re-trigger. (unused in this version)
#define PULSE_WIDTH         500  // Output pulse width - for relays
#define DEFAULT_REPEAT_INTERVAL 250 // Repeat interval for most things.
#define MOUSE_REPEAT_INTERVAL   100 // Time between repeats of mouse move actions.

// === Special Action ID === //
// This action is actually performed in the trigger code.
#define CHANGE_SENSOR_STATE 10  // Must not conflict with any action state defined in Actions.cpp

// === LED Values === //
#define LED_RED    5
#define LED_GREEN  6
#define LED_GREEN_ANALOG A7 // On Leonardo digital pin 6 == analog pin 7.  Why? Who knows!
#ifdef HUB20
#define LED_BLUE   4
#define LED_BLUE_ANALOG A6 // On Leonardo digital pin 4 == analog pin 6.  Why? Who knows!
#else
#define LED_BLUE   7
#endif

// === Sensor Pins === //
#define SENSACT_IN1A A0
#define SENSACT_IN1B A1
#define SENSACT_IN2A A2
#define SENSACT_IN2B A3
#define SENSACT_IN3A A4
#define SENSACT_IN3B A5

// === Action Pins === //
#define SENSACT_IR_OUT  9
#define SENSACT_BUZZER  10
#ifdef HUB20
#define SENSACT_RELAY_1 12
#define SENSACT_RELAY_2 8
#else
#define SENSACT_RELAY_1 11
#define SENSACT_RELAY_2 12
#endif

// ==== Some Limits === //
#ifdef SOFT_SERIAL
#define MAX_TRIGGERS 30         // Maximum number of triggers allowed - 15 bytes each.
#else
#define MAX_TRIGGERS 40         // Maximum number of triggers allowed - 15 bytes each.
#endif
#define MAX_ACTIONS 10            // Maximum number of actions allowed per trigger check - 5 bytes each.
#define MAX_SENSORS 9            // 2 bytes each
#define MAX_ACTORS  10           // 2 bytes each

#### Protocol (original)

tag: value1 [ : value2 [: value3] ]

E.g. "YPR: 123: 456: 789" would be yaw pitch row input. These are display on 3 dials.


"+:1" shows the '1' key as 'hit'.

#### Set-up


Run **serial port JSON server**. The binarny executable can be downloaded from <https://github.com/chilipeppr/serial-port-json-server>. Mac, Windows, Linux versions are available.

Then run **gendisplay.html** in a browser.

Press 'Refresh Port', then 'Next port' (press untl the right one shows up) then 'Select Port'.

#### release
2016-05-18 Wed

using p5.js

vkb.js is the main program
utils.js contains web socket handling routines
    watch for the readData() callback

consider using robot.js for mouse control (INCOMPLETE): node mouserobot.js


##### ./2015/words
using processing.js + browserify for kb.js

words
- dictionary and spellchecker analysis
- mouse text entry system

readwords.ls - able to read New General Service List as well as aspell
  lsc -cw readwords

kb.js - processing code for visual keyboard

use the following to form browser app
  watch "cat html-elem1 kb.js html-elem2 > kb.html"

browserify -d -r fs:browserify-fs -r lodash -r lowdb > lib/bundle.js
  issues with existsSync so cannot use lowdb
  using localStorage instead
