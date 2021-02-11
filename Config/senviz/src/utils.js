/* 
utils4.js : 2021-01-31 Sun : for use by viz.js (under development)

using web_serial, per Don
? using mqtt as data source

utils3 : 
2020-12-09 Wed moved from serialio to p5.serialport
2020-11-15 Sun  + added CONNECT_HUB (PASS_THROUGH) where data is not interpreted, if set to TRUE
    + added '?r' intercept for requesting accesscode and returning accesscode
    + added '?E' intercept for setting msgFromServer

(a) serialio to handle serial port + (b) input stream + (c) output stream

(a prime) p5.serialport to handle serial
*/

"use strict";

// global values - accessed/changed in viz.js and rcsupport.js

var DEV = true  // if true, then allow changes to SERVER_PORT and SERVER_ADDR; false= using hardcoded values below

var CONNECT_HUB = true   // connected to hub i.e. Client (or in future, a Developer who interacts locally with hub)
//var PASS_THROUGH = true  // legacy for utils2.js compatibility

const STAND_ALONE = 0
const AS_CLIENT = 1
const AS_SUPPORT = 2

var APP_MODE = STAND_ALONE

var SHOW_DATA = true  // display dials

var accessCode = 0
var msgFromServer = ""

var SERVER_PORT = '31007';
var SERVER_ADDR = '127.0.0.1';

if (DEV) {
    SERVER_ADDR = '127.0.0.1';
}

function set_app_mode(m) {
    APP_MODE = m
    SHOW_DATA = true
    CONNECT_HUB = true // set to true if we want to connect to hub; requires web_serial or p5.serialcontrol
    // var serialio = p5SerialFn()

    if (m === AS_CLIENT) {
        SERVER_PORT = 31007  // **** client
        SHOW_DATA = true
        CONNECT_HUB = true
        remoteio.web_socket();

    } else if (m === AS_SUPPORT) {
        SERVER_PORT = 31006  // **** support
        SHOW_DATA = true
        CONNECT_HUB = false
        remoteio.web_socket();
    } else {
        // set to stand_alone if unrecognizable
        // no remoteio
        APP_MODE = STAND_ALONE
    }
}

// updater as CLIENT
var serverSocket = null;
var myInt = 0;

var remoteio = remoteioFn()

function debug(v) {
    console.log(v);
}

var d0 = function (v) {
    console.log(v);
}

function setdebug(level) {
    debug = function () { }

    if (level === 1) {
        debug = function (v) {
            updateOutput(v)
        }
    }
}

/*
// const client = new Paho.MQTT.Client("ws://" + SERVER_ADDR + "/ws", "myClientId" + new Date().getTime());
// const client = new Paho.MQTT.Client("mqtt://" + SERVER_ADDR + ":1883", "myClientId" + new Date().getTime());
const connectUrl = 'mqtt://'+ SERVER_ADDR +':1883'
const client = mqtt.connect(connectUrl, options)
const myTopic = "0005"

client.connect({ onSuccess: onConnect })
let counter = 0
function onConnect() {
  console.log("connection successful")
  client.subscribe(myTopic)   //subscribe to our topic
  setInterval(()=>{
   publish(myTopic,                                
  `The count is now ${count++}`)
},5000)}                      //publish count every 5s

client.onMessageArrived = onMessageArrived;
function onMessageArrived(message) {
    updateOutput(message)
//   let el= document.createElement('div')
//   el.innerHTML = message.payloadString
//   document.body.appendChild(el)
}

// const mqtt = require('mqtt')
// import mqtt from 'mqtt'

// connection option
const options = {
            clean: true, // retain session
      connectTimeout: 4000, // Timeout period
      // Authentication information
      //clientId: 'emqx_test',
      //username: 'emqx_test',
      //password: 'emqx_test',
}

// Connect string, and specify the connection method by the protocol
// ws Unencrypted WebSocket connection
// wss Encrypted WebSocket connection
// mqtt Unencrypted TCP connection
// mqtts Encrypted TCP connection
// wxs WeChat applet connection
// alis Alipay applet connection
// const connectUrl = 'ws://'+ SERVER_ADDR +':8084/mqtt'
const connectUrl = 'mqtt://'+ SERVER_ADDR +':1883'
const client = mqtt.connect(connectUrl, options)

client.on('reconnect', (error) => {
    console.log('reconnecting:', error)
})

client.on('error', (error) => {
    console.log('Connection failed:', error)
})

client.on('message', (topic, message) => {
  console.log('receive message：', topic, message.toString())
})

/*
var mqttio = (function mqttio() {
    let mqtt = null;

    return {
        web_socket: web_socket,
        changeport: changeport,
        send: send,
        connected: function () {
            return connected;
        },
        thisport: function () {
            return currentport;
        }
    };

}());
*/

// talking to a remote (or local) server which 
// talks to a wifi peripheral connected hub

function remoteioFn() {
    let ws = null;
    /*,
        currentport = 'none',
        receivedData = '',
        portsequence = 0,
        portsList = []; */
    let connected = false;


    if ("WebSocket" in window) {
        debug("WebSocket is supported by your Browser!");
    } else {
        // The browser doesn't support WebSocket
        debug("WebSocket NOT supported by your Browser!");
    }

    function web_socket() {
        if (ws && ws.readyState == 1) // open
            return;

        if (ws && ws.readyState != 3) { //not closed
            connected = false;
            setTimeout(function () {
                debug('remoteio in web_socket() - retrying');
                web_socket();
            }, 2000);
            return;
        }

        debug('opening new socket');
        ws = new WebSocket("ws://" + SERVER_ADDR + ":" + SERVER_PORT + "/ws");

        ws.onopen = function () {
            debug("remoteio connected")
            connected = true;
            //			refreshport();
        };

        ws.onmessage = function (evt) {
            //			debug(evt.data);

            // handling out of band/ control messages between support and client
            if (evt.data.substring(0, 1) === '?') { // 2020-12-09 Wed
                if (evt.data.substring(0, 2) === '?r') { // intercepting access code message
                    accessCode = evt.data.substring(2) // , 6) + '-' + evt.data.substring(6, 10)
                    return
                }
                if (evt.data.substring(0, 2) === '?E') { // intercepting error message 
                    debug(evt.data)
                    msgFromServer = evt.data.substring(2)
                    updateOutput(evt.data.substring(2)) // throw into outputArea 
                    return
                }
                debug("ws:msg not displayed: " + evt.data)
                // msgFromServer = evt.data.substring(2)
                // updateOutput(evt.data.substring(2)) // throw into outputArea 
                return
            }
            let localmsg = evt.data
            if (!SHOW_DATA && CONNECT_HUB) {
                /* we are Client 2020-12-10 Thu
                if (evt.data.substring(0, 1) === 'T') {
                    serialio.send('V')
                    debug('TO HUB: extra V')
                }*/
                // old:  serialio.send(evt.data)
            }
            if (SHOW_DATA) {
                // we are Support, we interpret the data -> in future, we may have a Developer mode which 
                // both talks to the hub as well as interprets the data
                onDataReceived(localmsg);
            }
            if(APP_MODE===AS_CLIENT){ // received from remote, re-directed to hub on serial
                writer.write(encoder.encode((localmsg)))
            }
        };

        ws.onclose = function () {
            // websocket is closed.
            debug("remoteio: Connection is closed. Retry");
            connected = false;
            web_socket();
        };

        ws.onerror = function () {
            debug("remoteio: Connection error... retry");
            connected = false;
            web_socket();
        }
    }

    function changeport() {
        ws = null; // new WebSocket("ws://" + ip + ":" + pt + "/ws");
        web_socket();
    }

    function send(data) {
        if (connected) ws.send(data);
    }


    return {
        web_socket: web_socket,
        changeport: changeport,
        send: send,
        connected: function () {
            return connected;
        },
        thisport: function () {
            return currentport;
        }
    };

}


function control_functions(str) {
    if (str.length == 1) {
        report_self();
    } else {
        if (str[1] == 'D') {
            D_mode = !D_mode;
            d0("got ?D, turning debug to " + D_mode);
            return;
        }
        if (str[1] == 'L') {
            d0("got ?L, loading code ...");
            load();
            return;
        }
        if (str[1] == 'T') {
            d0("got ?T, turning telnet on");
            telnet_module.setOptions({
                "mode": "on"
            });
            return;
        }
        if (str[1] == 'R') {
            d0("got ?R, resetting ...");
            reset();
            return;
        }
    }
}


//* (a') using p5.serialport - must first run p5.serialcontrol 
// https://github.com/p5-serial/p5.serialport


function p5serialFn() {
    let ws = null,
        currentport = '(none)',
        portsList = [],
        portsequence = 0
    let connected = false;
    let lastsent = new Date(),
        nMsg = 1;


    // Instantiate our SerialPort object
    let serial = new p5.SerialPort();

    // Let's list the ports available
    portsList = serial.list();

    // serial.open("/dev/cu.usbmodem1421");

    // Register some callbacks

    // When we connect to the underlying server
    serial.on('connected', serverConnected);

    // When we get a list of serial ports that are available
    serial.on('list', gotList);

    // When we some data from the serial port
    serial.on('data', gotData);

    // When or if we get an error
    serial.on('error', gotError);

    // When our serial port is opened and ready for read/write
    serial.on('open', gotOpen);


    // We are connected and ready to go
    function serverConnected() {
        // conneted = true
        debug("Connected to serial server");
    }

    // Got the list of ports
    function gotList(thelist) {
        // theList is an array of their names
        let j = 0
        for (let i = 0; i < thelist.length; i++) {
            // Display in the console
            debug(i + " " + thelist[i]);
            //portsList[j++] = thelist[i]
            //*
            if (thelist[i].includes("usb") || thelist[i].includes("USB") ||
                thelist[i].includes("rduino") || thelist[i].includes("eonardo")) {
                portsList[j++] = thelist[i]
            } //*/
        }
        portsequence = 0
        currentport = portsList[portsequence]
        if (CONNECT_HUB) {
            selectport() // try to open the first port -- wise? 
        }
    }

    // Connected to our serial device
    function gotOpen() {
        connected = true
        debug("Serial Port is open!")
    }

    // Ut oh, here is an error, let's log it
    function gotError(theerror) {
        debug(theerror)
        connected = false
        serial = new p5.SerialPort()
    }

    // There is data available to work with from the serial port
    function gotData() {
        let currentString = serial.readString() // serial.readStringUntil("\r\n");   //
        if (CONNECT_HUB) { // 2020-11-15 Sun
            remoteio.send(currentString)
        }
        if (SHOW_DATA) {
            onDataReceived(currentString)
            debug(currentString)
        }
    }

    // Methods available
    // serial.read() returns a single byte of data (first in the buffer)
    // serial.readChar() returns a single char 'A', 'a'
    // serial.readBytes() returns all of the data available as an array of bytes
    // serial.readBytesUntil('\n') returns all of the data available until a '\n' (line break) is encountered
    // serial.readString() retunrs all of the data available as a string
    // serial.readStringUntil('\n') returns all of the data available as a tring until a (line break) is encountered
    // serial.last() returns the last byte of data from the buffer
    // serial.lastChar() returns the last byte of data from the buffer as a char
    // serial.clear() clears the underlying serial buffer
    // serial.available() returns the number of bytes available in the buffer

    function refreshport() {
        serial.list()
        return
    }

    function nextport() {
        connected = false
        portsequence++;
        if (portsequence == portsList.length) portsequence = 0;
        currentport = portsList[portsequence];
        debug(currentport)
        return
    }

    function selectport() {
        connected = false
        serial.open(currentport)
        debug("serial:attempt to open " + currentport)
        return
    }

    // new protocol (from serial.js)
    function send(data) {
        if (!connected || !CONNECT_HUB) {
            debug('serial: NOT connected')
            return
        }

        //* mod to send < max bytes at a time
        // with delay 
        let i = 0;
        let j = data.length;
        let max = 60;
        const epoch = 80; //msec per message
        while (i < j) {
            let k = (j - i) < max ? (j - i) : max;
            let ss = data.substr(i, k);

            let now = new Date();
            let defer = 0;
            if ((now - lastsent) < (epoch * nMsg)) {
                defer = epoch * (nMsg + 1) - (now - lastsent);
            } else {
                nMsg = 1;
                lastsent = new Date();
            }
            nMsg += 1;


            setTimeout(() => {
                debug("==== " + defer + " |" + ss + "|====");
                serial.write(ss)
            }, defer);

            i = i + k;
        }
        //* /


        //serial.write(data);

        // debug("send serial >" + data + "<");
        return
    }

    return {
        refreshport: refreshport,
        nextport: nextport,
        selectport: selectport,
        send: send,
        connected: function () {
            return connected;
        },
        thisport: function () {
            return currentport;
        }
    };

}


/* (a) websocket code - must first run ../serial-port-json-server
var serialio = (function localio() {
    let ws = null,
        currentport = '(none)',
        receivedData = '',
        portsequence = 0,
        portsList = [];
    let connected = false;
    let lastsent = new Date(),
        nMsg = 1;

    if ("WebSocket" in window) {
        debug("WebSocket is supported by your Browser!");
    } else {
        // The browser doesn't support WebSocket
        debug("WebSocket NOT supported by your Browser!");
    }

    let PROTOCOL = "new";

    function web_socket() {
        // Let us open a web socket

        if (ws && ws.readyState == 1) // open
            return;

        if (ws && ws.readyState != 3) { //not closed
            setTimeout(function () {
                debug('serialio in web_socket() - retrying');
                web_socket();
            }, 2000);
            return;
        }

        debug('opening new socket');
        ws = new WebSocket("ws://localhost:8989/ws");

        ws.onopen = function () {
            // Web Socket is connected, send data using send()
            //    ws.send("list");
            //    debug("sending list");

            refreshport();
        };

        ws.onmessage = function (evt) {
            // var received_msg = evt.data;
            //    debug("Message is received...");
            parseJSPS(evt.data);
        };

        ws.onclose = function () {
            // websocket is closed.
            debug("serialio: Connection is closed. Retry");
            connected = false;
            web_socket();
        };

        ws.onerror = function () {
            debug("serialio: Connection error... retry");
            connected = false;
            web_socket();
        }

    }

    function refreshport() {
        if (ws && ws.readyState == 1)
            ws.send("list");
        else web_socket();
        return;
    }

    function nextport() {
        portsequence++;
        if (portsequence == portsList.length) portsequence = 0;
        currentport = portsList[portsequence];
        debug(currentport);
    }

    function selectport() {
        if (ws && ws.readyState == 1) {
            connected = true;
            ws.send('open ' + currentport + ' 9600');
        } else web_socket();
    }

    // new protocol (from serial.js)
    function send(data) {
        if (!connected) return;
        if (ws && ws.readyState == 1) {

            //* mod to send < max bytes at a time
            // with delay 
            let i = 0;
            let j = data.length;
            let max = 60;
            const epoch = 100; //msec per message
            while (i < j) {
                let k = (j - i) < max ? (j - i) : max;
                let ss = data.substr(i, k);

                let now = new Date();
                let defer = 0;
                if ((now - lastsent) < (epoch * nMsg)) {
                    defer = epoch * nMsg - (now - lastsent);
                } else {
                    nMsg = 1;
                    lastsent = new Date();
                }
                nMsg += 1;


                setTimeout(() =>
                    ws.send('send ' + currentport + ' ' + ss), defer);
                // debug("====|" + ss + "|====");
                i = i + k;
            }
            //* /
            ws.send('send ' + currentport + ' ' + data);
            debug("send " + data);

        } else {
            debug("serialio: In send with an unopened socket!");
            ws = null;
            web_socket();
            return;
        }
    }

    function parseJSPS(data) {
        // debug(">" + data + "<");
        var jObj = tryParse(data);
        if (jObj.valid === false) {
            debug("NOT json>" + data + "<");
            return;
        }
        jObj = jObj.value;
        if (jObj.SerialPorts) { // port list received
            portsList = [];
            portsequence = 0;
            let j = 0;
            var jsize = jObj.SerialPorts.length;
            debug("# ports:"+jsize)
            for (var i = 0; i < jsize; i++) {
                if (jObj.SerialPorts[i].Name.includes("usb") || jObj.SerialPorts[i].Name.includes("USB") ||
                jObj.SerialPorts[i].Name.includes("eonardo") || jObj.SerialPorts[i].Name.includes("duino") ) {
                    portsList[j++] = jObj.SerialPorts[i].Name;
                }
            }
            debug(portsList);
            if (portsList.length > 0) {
                portsequence = 0;
                currentport = portsList[0];
                selectport();
            }
        } else {

            //if (jObj.P && jObj.D) { // data received
            if (jObj.D) { // data received 2020-12-08 Tue
                var data = jObj.D;
                // debug('send to remote -->' + data + '<---');
                if (CONNECT_HUB) { // 2020-11-15 Sun
                    remoteio.send(data)
                    return
                }

                if (PROTOCOL == "new") {
                    onDataReceived(jObj.D)
                } else { // old
                    receivedData += data;
                    if (data[data.length - 1] == '\n') {
                        readData(receivedData);
                        receivedData = '';
                    }
                }
            } else { // error
                debug("serialio: info >>>" + data + "<<<");
            }

        }
    }

    return {
        web_socket: web_socket,
        refreshport: refreshport,
        nextport: nextport,
        selectport: selectport,
        send: send,
        connected: function () {
            return connected;
        },
        thisport: function () {
            return currentport;
        }
    };

}());
//*/


function parseWebSerial(d) {

    if (d[0] == 'T') {
        //debug("RECEIVED triggers: " + d)
        updateOutput(JSON.stringify(parse_configuration(d.slice(1, -1))))
        updateTriggers(d); // put in profileArea
        // updateOutput(d) // also put in outputArea?
    }
    if (d[0] == 'S') {
        inputStream.init(d);
        updateSensors(inputStream);
    }
    if (d[0] == 'V') {
        debug(d);
        setVersion(d);
    }
    if (d[0] == 'W') { // 2020-05-18 Mon : debug info from Sensact
        updateWData(d);
    }
}


var receivedData = '';

function onDataReceived(data) {
    //d0("GOT DATA: >" + data + "<");

    // debug("to serial:" + data)

    receivedData += data;
    while (
        receivedData[receivedData.length - 1] == 'Z') {
        //	d0("|" + receivedData + "|");

        let e = receivedData.search('Z') + 1;
        let d = receivedData.substring(0, e);
        if (e < receivedData.length) {
            receivedData = receivedData.substring(e);
        } else receivedData = '';

        if (d[0] == 'T') {
            //debug("RECEIVED triggers: " + d)
            updateTriggers(d); // put in profileArea
            // updateOutput(d) // also put in outputArea?
        }
        if (d[0] == 'S') {
            inputStream.init(d);
            updateSensors(inputStream);
        }
        if (d[0] == 'V') {
            debug(d);
            setVersion(d);
        }
        if (d[0] == 'W') { // 2020-05-18 Mon : debug info from Sensact
            updateWData(d);
        }
        // processData(receivedData);
        //			receivedData = '';
    }
}

function tryParse(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return {
            value: str,
            valid: false
        };
    }
    return {
        value: JSON.parse(str),
        valid: true
    };
}

function mk100(s) {
    if (s < 10) {
        return "0" + s
    }
    else { return "" + s }
}

function getDateTime() {
    let current_datetime = new Date()
    return ("" + current_datetime.getFullYear() + "-" + (current_datetime.getMonth() + 1) + "-" + current_datetime.getDate() + " " + current_datetime.getHours() + ":" + current_datetime.getMinutes());
}

function getHourMinuteSecond() {
    let current_datetime = new Date()
    return ("" + current_datetime.getHours() + ":" + mk100(current_datetime.getMinutes()) + ":" + mk100(current_datetime.getSeconds()));
}


/*
function matrix(rows, cols, defaultValue) {
    var arr = [];

    // Creates all lines:
    for (var i = 0; i < rows; i++) {

        // Creates an empty line
        arr.push([]);

        // Adds cols to the empty line:
        arr[i].push(new Array(cols));

        for (var j = 0; j < cols; j++) {
            // Initializes:
            arr[i][j] = defaultValue;
        }
    }

    return arr;
}
*/

// === General Purpose Input Stream === //
var inputStream = {
    data: null,

    init: function (newData) {
        this.data = newData.split('');
    },

    getChar: function () {
        var tmp = this.data.shift();
        // Filter out white space that may have been added for readability.
        while (tmp == '\n' || tmp == '\r' || tmp == ' ') {
            tmp = this.data.shift();
        }
        return tmp;
    },

    // Note: 2-byte values may be negative.
    getNum: function (count) {
        var negative = false;
        var value = 0;
        for (var i = 0; i < count; i++) {
            var tmp = this.getChar().charCodeAt(0) - NUMBER_MASK;
            // console.log("  n "+ tmp)

            if (tmp < 0 || tmp > 15) {
                debug("- invalid sensor# " + tmp);
                throw "Invalid Number " + tmp;
            }
            value = (value << 4) + tmp;
            if ((i == 0) && (tmp & 0x8)) {
                // High order bit is set.  This is a negative number.
                negative = true;
            }
        }

        if (negative) {
            if (count == 4) { // 4 nibbles - two bytes
                // We will have a fairly large positive number at this point.
                // Turn it into the correct small number.
                value = value - 0x10000;
            }
        }
        return value;
    },

    getID: function (count) {
        var value = 0;
        for (var i = 0; i < count; i++) {
            var tmp = this.getChar().charCodeAt(0) - ID_MASK;
            // console.log("ID " + tmp);
            if (tmp < 0 || tmp > 15) {
                debug("- invalid");
                throw "Invalid ID " + tmp;
            }
            value = (value << 4) + tmp;
        }
        return value;
    },

    getCondition: function () {
        var tmp = this.getChar();
        switch (tmp) {
            case '1':
                return TRIGGER_ON_LOW;

            case '2':
                return TRIGGER_ON_HIGH;

            case '3':
                return TRIGGER_ON_EQUAL;

            default:
                throw "Invalid condition";
        }
    },

    getBoolean: function () {
        var tmp = this.getChar();
        switch (tmp) {
            case BOOL_TRUE:
                return true;
                break;
            case BOOL_FALSE:
                return false;
                break;
            default:
                throw "Invalid boolean";
                break;
        }
    }
};

// === General Purpose Output Stream === //
var outputStream = {
    data: null,
    outputFunction: null,

    init: function (outFunc) {
        this.data = [];
        this.outputFunction = outFunc;
    },

    putChar: function (ch) {
        this.data.push(ch);
    },

    putNum: function (n, length) {
        switch (length) { // Length is the number of bytes to send
            case 4: // All cases fall through
                this.putChar(String.fromCharCode(((n >> 28) & 0xf) | NUMBER_MASK));
                this.putChar(String.fromCharCode(((n >> 24) & 0xf) | NUMBER_MASK));
            case 3:
                this.putChar(String.fromCharCode(((n >> 20) & 0xf) | NUMBER_MASK));
                this.putChar(String.fromCharCode(((n >> 16) & 0xf) | NUMBER_MASK));
            case 2:
                this.putChar(String.fromCharCode(((n >> 12) & 0xf) | NUMBER_MASK));
                this.putChar(String.fromCharCode(((n >> 8) & 0xf) | NUMBER_MASK));
            case 1:
                this.putChar(String.fromCharCode(((n >> 4) & 0xf) | NUMBER_MASK));
                this.putChar(String.fromCharCode((n & 0xf) | NUMBER_MASK));
        }
    },

    putID: function (n, length) {
        switch (length) { // Length is the number of bytes to send
            case 2:
                this.putChar(String.fromCharCode(((n >> 4) & 0xf) | ID_MASK));
            case 1:
                this.putChar(String.fromCharCode((n & 0xf) | ID_MASK));
        }
    },

    putCondition: function (cond) {
        this.putChar(cond);
    },

    putBoolean: function (b) {
        if (b) {
            this.putChar('p');
        } else {
            this.putChar('q');
        }
    },

    flush: function () {
        var output = this.data.join('');
        this.outputFunction(output);
    }

};



// Check if Serial API is supported by the browser.
if ("serial" in navigator) {
    //console.log("Serial Web API is supported")
} else {
    //console.log("Serial Web API is NOT supported. Would you like to continue anyway?")
}

const encoder = new TextEncoder();
let writer
let buffer = ""
let version
let reader
let triggers
let port

let TRIGGERS

// Used by decode_condition and decode_repeat functions
const BOOL_TRUE = 'p'
const BOOL_FALSE = 'q'

const TRIGGER_ON_LOW = '1'
const TRIGGER_ON_HIGH = '2'
const TRIGGER_ON_EQUAL = '3'

const TRIGGER_ON_LOW_REPEAT = '1'
const TRIGGER_ON_HIGH_REPEAT = '2'
const TRIGGER_ON_EQUAL_REPEAT = '3'

const TRIGGER_ON_LOW_NO_REPEAT = '5'
const TRIGGER_ON_HIGH_NO_REPEAT = '6'
const TRIGGER_ON_EQUAL_NO_REPEAT = '7'

const INPUTS = {
    1: '3A', 2: '3B', 3: '2A', 4: '2B', 5: '1A',
    6: '1B', 7: 'USB', 8: 'ACCEL-X', 9: 'ACCEL-Y',
    10: 'ACCEL-Z', 11: 'GYRO-X', 12: 'GYRO-Y', 13: 'GYRO-Z',
    14: 'GYRO-ANY'
}

async function sendHubCommand(cmd) {
    const commands = {
        "Get hub version": 'V',
        "Get input levels": 'Q',
        "Get configuration": 'U',
        "Set to RUN mode": 'R',
    }

    if (cmd === 'Load configuration') {
        writer.write(encoder.encode(TRIGGERS));
        return
    }
    if ((writer != undefined) && (cmd in commands)) {
        //console.log(cmd)
        writer.write(encoder.encode(commands[cmd]));
    } else {
        //console.log("No connection to Hub.")
        alert("No connection to Hub! Is it plugged in?")
    }

}

function decode_byte(nibbles) {
    let upper_nibble = (nibbles.charCodeAt(0) & 0b00001111) << 4
    let lower_nibble = nibbles.charCodeAt(1) & 0b00001111
    return upper_nibble | lower_nibble
}

function decode_number(bytes) {
    let size = bytes.length
    let negative = false

    if ((bytes.charCodeAt(0) & 0b00001000) > 0) {
        negative = true
    }

    if (size % 2 != 0) {
        //console.log("Encoded numbers must be an even number of bytes: '" + bytes + "'")
        return -1
    }

    let value = 0
    while (size > 0) {
        value = value << 8 | decode_byte(bytes)
        bytes = bytes.substring(2)
        size -= 2
    }

    if (negative === true) {
        value = value - 0x10000
    }

    return value
}

function decode_id(bytes) {
    let size = bytes.length

    if (size === 1) {
        return bytes.charCodeAt(0) & 0b00001111
    }

    if (size === 2) {
        let result = decode_byte(bytes)
        if (result <= 127) {
            return result
        } else {
            //console.log("Invalid id value specified: '" + bytes + "'")
            return -1
        }
    }

    if (size > 2) {
        //console.log("Id value contains too many bytes: '" + bytes + "'")
        return -1
    }
}

async function parse_input_levels(bytes) {
    unipolar = {
        "1": port3a_meter, "2": port3b_meter, "3": port2a_meter, "4": port2b_meter,
        "5": port1a_meter, "6": port1b_meter, "14": gyro_any_meter,
    }
    bipolar = {
        "8": accel_x_meter, "9": accel_y_meter, "10": accel_z_meter, "11": gyro_x_meter,
        "12": gyro_y_meter, "13": gyro_z_meter,
    }
    const valid_bytes = '`abcdefghijklmno@ABCDEFGHIJKLMNO'

    for (let c of bytes) {
        if (bytes.indexOf(c) === -1) {
            //console.log("Invalid input level data\r\n")
            return false
        }
    }

    let count = decode_number(bytes.slice(0, 4))
    bytes = bytes.slice(4)
    while (count > 0) {
        let id = decode_id(bytes.slice(0, 2))
        let value = decode_number(bytes.slice(2, 6))
        if (id in unipolar) {
            unipolar[id.toString()].value = value
        }
        if (id in bipolar) {
            if (value != 0) {
                bipolar[id.toString()].value = value
            } else {
                // Clear display - peripheral probably not connected
                bipolar[id.toString()].value = -32768
            }
        }
        bytes = bytes.slice(6)
        count -= 1
    }
    return true
}


function decode_state(state_byte) {
    return state_byte.charCodeAt(0) & 0b00001111
}

function decode_input(bytes) {

    return INPUTS[decode_id(bytes)]
}

function decode_condition(c) {

    if (c === TRIGGER_ON_LOW || c === TRIGGER_ON_LOW_NO_REPEAT) { return '<' }
    if (c === TRIGGER_ON_HIGH || c === TRIGGER_ON_HIGH_NO_REPEAT) { return '>' }
    if (c === TRIGGER_ON_EQUAL || c === TRIGGER_ON_EQUAL_NO_REPEAT) { return '=' }

    //console.log("Invalid trigger condition: '" + c + "'" );
    process.exit(1);
}

function decode_output(output_bytes, parameter_bytes) {

    const MOUSE = {
        1: 'Mouse Up', 2: 'Mouse Down', 3: 'Mouse Left', 4: 'Mouse Right',
        5: 'Mouse Left Click', 6: 'Mouse Left Press', 7: 'Mouse Left Release',
        8: 'Mouse Right Click', 10: 'Nudge Up', 11: 'Nudge Down',
        12: 'Nudge Left', 13: 'Nudge Right', 14: 'Nudge Stop',
        20: 'Mouse Wheel Up', 21: 'Mouse Wheel Down',
        30: 'Mouse Right Press', 31: 'Mouse Right Release'
    }

    const RELAY = { 0: 'Pulse', 1: 'On', 2: 'Off' }

    let out = {}
    let output = decode_id(output_bytes)
    let parameters = decode_number(parameter_bytes)

    if (output === 0) { out.action = 'Nothing'; return out }
    if (output === 1) { out.action = 'Relay'; out.state = RELAY[parameters]; return out }
    if (output === 2) { out.action = 'Relay 2'; out.state = RELAY[parameters]; return out }
    if (output === 3) { out.action = 'BT Keyboard'; out.state = parameters; return out }
    if (output === 4) { out.action = 'USB Keyboard'; out.state = parameters; return out }
    if (output === 5) { out.action = 'USB'; out.state = MOUSE[parameters]; return out }
    if (output === 6) { out.action = 'Serial Send'; out.state = parameters; return out }
    if (output === 7) {
        out.action = 'Buzzer'; out.frequency = (parameters >> 16) & 0x0000ffff;
        out.duration = parameters & 0x0000ffff; return out
    }
    if (output === 8) { out.action = 'IR LED'; out.state = parameters; return out }
    if (output === 9) { out.action = 'BT'; out.state = MOUSE[parameters]; return out }
    if (output === 10) { out.action = 'Set State'; out.sensor = INPUTS[parameters >> 8]; out.sensor_state = parameters & 0xff; return out }
    if (output === 11) { out.action = 'Light Box'; out.state = parameters.toString(2); return out }
    if (output === 12) { out.action = 'LCD Display'; out.state = parameters.toString(2); return out }

    out.action = output
    out.state = parameters
    return out
}

function decode_repeat(r) {

    if (r === BOOL_FALSE || r === TRIGGER_ON_LOW_NO_REPEAT ||
        r === TRIGGER_ON_HIGH_NO_REPEAT || r === TRIGGER_ON_EQUAL_NO_REPEAT) {
        return false
    }

    if (r === BOOL_TRUE || r === TRIGGER_ON_LOW_REPEAT ||
        r === TRIGGER_ON_HIGH_REPEAT || r === TRIGGER_ON_EQUAL_REPEAT) {
        return true
    }

    //console.log("Invalid trigger repeat parameter: '" + enc_byte + "'" );
    process.exit(1);
}

function translate_trigger(bytes) {
    let trigger = {}

    trigger.state = decode_state(bytes.substring(2, 3));
    trigger.input = decode_input(bytes.substring(0, 2));
    trigger.condition = decode_condition(bytes.substring(7, 8));
    if (trigger.input !== 'USB') {
        trigger.threshold = decode_number(bytes.substring(3, 7));
    } else {
        trigger.character = String.fromCharCode(decode_number(bytes.substring(3, 7)));
    }
    trigger.duration = decode_number(bytes.substring(19, 23));

    // decode output and parameters
    trigger.output = decode_output(bytes.substring(8, 10), bytes.substring(11, 19));
    trigger.repeat = decode_repeat(bytes[7]);
    trigger.next_state = decode_state(bytes.substring(10, 12));
    return trigger
}

function decode_triggers(bytes, trigger_length) {
    let count = 0
    let triggers = {}
    while ((bytes.length > 0) && (bytes[0] !== 'Y')) {
        let trigger = bytes.substring(0, trigger_length)

        trigger = translate_trigger(trigger)
        triggers[count] = trigger
        bytes = bytes.substring(trigger_length)
        count += 1
    }

    return triggers
}

function highlight_active_inputs_and_output_summary(triggers) {
    inputs = {
        "3A": "port3a", "3B": "port3b", "2A": "port2a", "2B": "port2b",
        "1A": "port1a", "1B": "port1b", "ACCEL-X": "accel_x", "ACCEL-Y": "accel_y",
        "ACCEL-Z": "accel_z", "GYRO-X": "gyro_x", "GYRO-Y": "gyro_y", "GYRO-Z": "gyro_z",
        "GYRO-ANY": "gyro_any", "USB": "usb"
    }

    for (let key in inputs) document.getElementById(inputs[key]).classList.replace('assigned', 'not_assigned')
    const keys = Object.keys(triggers);
    console.log(JSON.stringify(triggers))
    //console.log(keys.length)
    document.getElementById("trigger_count").innerHTML = ' ( ' + keys.length + ' triggers )'
    for (let key in keys) {
        let trigger = triggers[key]
        let output = trigger['output']
        //console.log(Object.keys(trigger))
        document.getElementById(inputs[trigger['input']]).classList.replace('not_assigned', 'assigned')

        document.getElementById("config_summary").innerHTML += key + ':  If ' + trigger['input']
        document.getElementById("config_summary").innerHTML += ' in state ' + trigger['state'] + ' and ' + trigger['input'] + ' '

        if (trigger['condition'] === '<') document.getElementById("config_summary").innerHTML += '&lt; '
        if (trigger['condition'] === '>') document.getElementById("config_summary").innerHTML += '&gt; '
        if (trigger['condition'] === '=') document.getElementById("config_summary").innerHTML += '= '

        if (trigger['input'] === 'USB') {

            document.getElementById("config_summary").innerHTML += trigger['character'] + ' for ' + trigger['duration'] + ' msec. Do '

        } else {

            document.getElementById("config_summary").innerHTML += trigger['threshold'] + ' for ' + trigger['duration'] + ' msec. Do '

        }

        if (output['action'] === 'Buzzer') {
            document.getElementById("config_summary").innerHTML += output['action'] + ' at ' + output['frequency'] + ' Hz for '
            document.getElementById("config_summary").innerHTML += output['duration'] + ' msec. then set state of ' + trigger['input'] + ' to ' + trigger['next_state'] + '<br>'
            continue
        }

        if (output['action'] === 'Nothing') {
            document.getElementById("config_summary").innerHTML += output['action'] + ' then set state of ' + trigger['input'] + ' to ' + trigger['next_state'] + '<br>'
            continue
        }

        if (output['action'] === 'Set State') {
            document.getElementById("config_summary").innerHTML += output['action'] + ' of input ' + output['sensor'] + ' to ' + output['sensor_state']
            document.getElementById("config_summary").innerHTML += ' then set state of ' + trigger['input'] + ' to ' + trigger['next_state'] + '<br>'
            continue
        }

        document.getElementById("config_summary").innerHTML += output['action'] + ' ' + output['state']
        document.getElementById("config_summary").innerHTML += ' then set state of ' + trigger['input'] + ' to ' + trigger['next_state'] + '<br>'
    }
}

function decode_mouse_parameters(bytes) {

    let mouse = {}
    mouse['Block Size'] = decode_number(bytes.substring(1, 5))
    mouse['Delay 1'] = decode_number(bytes.substring(5, 7))
    mouse['Jump 1'] = decode_number(bytes.substring(7, 9))
    mouse['Delay 2'] = decode_number(bytes.substring(9, 11))
    mouse['Jump 2'] = decode_number(bytes.substring(11, 13))
    mouse['Delay 3'] = decode_number(bytes.substring(13, 15))
    mouse['Jump 3'] = decode_number(bytes.substring(15, 17))
    mouse['Timer 1'] = decode_number(bytes.substring(17, 21))
    mouse['Timer 2'] = decode_number(bytes.substring(21, 25))

    return mouse
}

function output_mouse_summary(m) {
    document.getElementById("mouse_summary").innerHTML = 'Delay 1 = ' + m['Delay 1'] + ' msec. Jump 1 = ' + m['Jump 1'] + ' pixels'
    document.getElementById("mouse_summary").innerHTML += '<br>' + 'Delay 2 = ' + m['Delay 2'] + ' msec. Jump 2 = ' + m['Jump 2'] + ' pixels'
    document.getElementById("mouse_summary").innerHTML += '<br>' + 'Delay 3 = ' + m['Delay 3'] + ' msec. Jump 3 = ' + m['Jump 3'] + ' pixels'
    document.getElementById("mouse_summary").innerHTML += '<br>' + 'Timer 1 = ' + m['Timer 1'] + ' Timer 2 = ' + m['Timer 2']
}

async function parse_configuration(bytes) {
    //console.log("Hub Configuration", bytes[0], "\n");
    if (bytes[0] != '1') {
        alert("Hub uses unsupported configuration format.")
        return
    }
    bytes = bytes.slice(1)

    let trigger_count = decode_number(bytes.substring(0, 2))

    bytes = bytes.slice(2)

    let triggers = decode_triggers(bytes, 23)
    updateOutput( JSON.stringify(triggers))

    // ylh highlight_active_inputs_and_output_summary(triggers)

    if (bytes.indexOf('Y') >= 0) {
        //ylh document.getElementById("enc_mouse").innerHTML =  bytes.slice(bytes.indexOf('Y')) 
        let mouse_parameters = decode_mouse_parameters(bytes.slice(bytes.indexOf('Y')))
        // ylh output_mouse_summary(mouse_parameters)
    }

    //ylhh output_trigger_summary(triggers)
}

async function parse(response) {

    //console.log("Response:", response, "\n");

    if (response.indexOf('V') >= 0) {
        document.getElementById("hub_version").innerHTML = response.slice(1, -1)
        //console.log("Hub Firmware Version:", response.slice(1,-1), "\n");
    }

    if (response.indexOf('S') >= 0) {
        parse_input_levels(response.slice(1, -1))
    }

    if (response.indexOf('T') >= 0) {
        TRIGGERS = response
        document.getElementById("enc_config").innerHTML = response
        document.getElementById("enc_config").classList.replace('not_assigned', 'assigned')
        parse_configuration(response.slice(1, -1))
    }
}

async function updateConnection() {

    navigator.serial.addEventListener("connect", (event) => {
        // TODO: Automatically open event.target or warn user a port is available.
        console.log("Connection Detected")
        webserialConnected = true
    });

    navigator.serial.addEventListener("disconnect", (event) => {
        // TODO: Remove |event.target| from the UI.
        // If the serial port was opened, a stream error would be observed as well.
        console.log("Disconnect")
        webserialConnected = false
    });

    //console.log("port", port)
    if (port) {
        console.log("Disconnect")
        reader.cancel()
        return
    } else {
        console.log("Connect")
        getReader()
        // ylh document.getElementById("dropzone").classList.replace('not_assigned','assigned')
    }

}

async function disconnect() {
    if (reader) {
        await reader.cancel();
        await inputDone.catch(() => { });
        reader = null;
        inputDone = null;
    }
}

async function getReader() {
    // Filter on devices with the Arduino Leonardo USB Vendor/Product IDs.
    const filters = [
        { usbVendorId: 0x2341, usbProductId: 0x8036 },
        { usbVendorId: 0x10c4, usbProductId: 0xea60 },
    ];

    try {
        port = await navigator.serial.requestPort({ filters });
        await port.open({ baudRate: 9600 });
        console.log(port.getInfo())


        /* ylh
        connectButton.innerText = '🔌 Disconnect';
        document.getElementById("getSensors").disabled = false;    
        document.getElementById("getTriggers").disabled = false;    
        //document.getElementById("loadConfig").disabled = false;    
        document.getElementById("runHub").disabled = false;    
        document.getElementById("getVersion").disabled = false;    
        */

        writer = port.writable.getWriter();
        sendHubCommand('Get hub version')
        //sendHubCommand('Get triggers')
        //sendHubCommand('Get input levels')

        const decoder = new TextDecoder();
        reader = port.readable.getReader();

        while (true) {
            const { value, done } = await reader.read();
            if (done) {
                // Allow the serial port to be closed later.
                reader.releaseLock();
                break;
            }

            buffer = buffer + decoder.decode(value).replace(/[^0-9\'-zA-Z]/g, '')

            // Look for end of response
            // console.log("Buffer:", buffer, "\n");
            if (buffer.indexOf('Z') > 0) {
                let index = buffer.indexOf('Z') + 1
                let response = buffer.slice(0, index)
                // ylh parse(response)
                parseWebSerial(response)
                if(APP_MODE===AS_CLIENT) {
                    remoteio.send(response)
                }
                buffer = buffer.slice(index)
                // console.log("New Buffer:", buffer, "\n");
            }

        }
        //console.log("Closing port")
        writer.releaseLock();
        reader.releaseLock();
        await port.close();
        //console.log("Port closed:", port)
        // ylh connectButton.innerText = '🔌 Connect';

    } catch (e) {
        /* ylh
        //console.log("No Hub Found.")
        connectButton.innerText = '🔌 Connect';
        document.getElementById("getSensors").disabled = true;    
        document.getElementById("getTriggers").disabled = true;    
        //document.getElementById("loadConfig").disabled = true;    
        document.getElementById("runHub").disabled = true;    
        document.getElementById("getVersion").disabled = true;    
        */
    }

}

// Check that all characters are valid
function validate(data) {
    const valid_chars = '`abcdefghijklmno@ABCDEFGHIJKLMNO' + 'Y' + 'tz123567pq'

    if (data[0] !== 'T' || data[data.length - 1] !== 'Z') {
        //console.log('Invalid Configuration Data: Trigger data prefix \'T\' and/or suffix \'Z\' not found.')
        return false
    }

    for (let i = 1; i < data.length - 1; i++) {
        if (valid_chars.includes(data[i]) === false) {
            //console.log("Invalid Configuration Data: '" + data[i] + "'" + ' is not a valid configuration character.')
            return false
        }
    }

    return true
}

/*
(function() {
    let dropzone = document.getElementById('dropzone')

    let upload = function (files) {
        const reader = new FileReader()
        reader.readAsText(files[0])

        reader.onload = function(e) {
            let new_config = reader.result
            new_config = new_config.replace(/\s+/g, '')       // Remove whitepaces and newlines
            if (validate(new_config) === true) {
                if (port === undefined) alert ("Hub is NOT connected!")
                else {
                    writer.write(encoder.encode(new_config));
                    sendHubCommand('Get configuration')
                }
            } else {
               alert("'" + files[0].name + "' is an INVALID configuration file!")
            }
        }
    }

    dropzone.ondrop = function(e) {
        e.preventDefault()
        this.className = "dropzone"
        upload(e.dataTransfer.files)
    }

    dropzone.ondragover = function() {
        this.className = "dropzone dragover"
        return false
    }

    dropzone.ondragleave = function() {
        this.className = "dropzone"
        return false
    }

}())
//*/