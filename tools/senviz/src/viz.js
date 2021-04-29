/* viz.js : 
code set = [viz.html viz.js utils4.js ]

2021-02-08 Mon
data sourced from web_serial

derived from rcsupport.js
2020-11-15 Sun  + added CONNECT_HUB = false SHOW_DATA = true
    + added '?r' intercept for requesting accesscode and returning accesscode
    + added '?E' intercept for setting msgFromServer
    + using p5.serialport must first run p5.serialcontrol app
*/
/* sensact.js : from touchpad.js <- vkb.js <- words

code set = [sensact.html sensact.js utils.js ]

note [2020-05-30 Sat] **go_mode implemented but not used (exmpale below)
** erase_fn hidden
** w_value hidden

2020-05-20 Wed
event handling using p5.clickable.js

2020-05-20 Wed
gendisplay.html touchpad.js utils.js triggers.js (incomplete)

2020-05-18 Mon
changing serial io to p5.serialport.js interface rather than websocket() - cf README.md
but still working with websocket code - must first run ../serial-port-json-server

2016-05-18
    - based originally on 2015/words 2015/kb.js written in processing.js
    - this version works with gyroscope (using "YPR" data) and touchpad (using "+")
    - gyroscopes move cross-hair to target keys, dwell click
    - same action can be achieved by moving the mouse
    
    - can also detect touchpad keys and show them on the key, click action is immediate
	
    gendisplay.html + touchpad.js + utils.js + triggers.js (incomplete)	
*/

"use strict"

var menu = [
    /*
        {
            id: "calibrate",
            display: "CALIBRATE",
            fn: calibrate_fn
        },
        {
            id: "connect_mode",
            display: "CONNECT",
            fn: connect_fn
        },
        */
    {
        id: "set_run_mode",
        display: "RUN SENSACT",
        fn: run_fn
    },
    {
        id: "set_report_mode",
        display: "REPORT",
        fn: query_fn
    },
    {
        id: "set_idle_mode",
        display: "IDLE",
        fn: idle_fn
    },
    /*
        {
            id: "w_values",
            display: "W VALUES",
            fn: w_values_fn
        }, 
        */
    {
        id: "design",
        display: "DESIGN",
        fn: design_fn
    },
    {
        id: "settings",
        display: "SETTINGS",
        fn: settings_fn
    },
    {
        id: "move",
        display: "MOVE",
        fn: move_fn
    },
    /*
        {
            id: "go",
            display: "GO",
            fn: go_fn
        },
        {
            id: "erase",
            display: "ERASE",
            fn: erase_fn
        },
        */

    {
        id: "save",
        display: "STORE ARRANGEMENT",
        fn: save_fn
    },
    {
        id: "retrieve",
        display: "RESTORE",
        fn: get_fn
    },
    {
        id: "setfrom",
        display: "SET FROM INPUT BOX",
        fn: setfrom_fn
    },
    //* support portal 
    {
        id: "gettriggers",
        display: "GET TRIGS",
        fn: get_triggers_fn
    },
    {
        id: "sendtriggers",
        display: "SEND TRIGS",
        fn: send_triggers_fn
    },
    /*
    {
        id: "loadnewtriggers",
        display: "LOAD NEW TRIGS",
        fn: load_new_triggers_fn
    },
    {
        id: "shownewtriggers",
        display: "SHOW NEW TRIGS",
        fn: show_new_triggers_fn
    },
    //*/
    /*
    {
        id: "remote", // remote server
        display: "Reset", // changed from "Remote",
        fn: remote_fn
    },
    */
    /* support only
    {
        id: "entercode", // enter code
        display: "enter",
        fn: entercode_fn
    },
    */
    {
        id: "clearoutput",
        display: "clear",
        fn: clearoutput_fn
    },
    {
        id: "clearprofile",
        display: "clear",
        fn: clearprofile_fn
    }

],
    myText = "",
    C_WIDTH = 1200, // interface canvace w, h
    C_HEIGHT = 660,
    C_RIGHT = 740,
    C_SHIFT = 380, // display area below
    keySize = 20,
    gridSize = 20,
    DWELL_TIME = 1000,
    REFRACTORY = 1200,
    dataLastUpdated = 0,
    profileArea = null,
    portArea = null,
    ipArea = null,
    codeArea = null,
    startTime = null,
    outputArea = null,
    dropzone = null,
    core_mode = "Idle",
    versionText = "---",
    BASETEXTCOLOUR = "#6363fc",
    BACKGROUNDCOLOUR = 'rgb(255,255,255)',
    FOREGROUNDCOLOUR = 'rgb(220,220,200)';

var IDLE_MODE = 'V';

// from Andrew's triggers.js
// -- Command and Block Headers -- //
var REPORT_MODE = 'Q';
var RUN_MODE = 'R';
var START_OF_SENSOR_DATA = 'S';
var START_OF_TRIGGER_BLOCK = 'T';
var REQUEST_TRIGGERS = 'U';
var GET_VERSION = 'V';
var KEYBOARD_CMD = 'W';
var MOUSE_SPEED = 'Y';
var MIN_COMMAND = 'Q';
var MAX_COMMAND = 'V';
// -- Data block separators -- //
var TRIGGER_START = 't';
var TRIGGER_END = 'z';
var END_OF_BLOCK = 'Z';

// -- Value encoding -- //
var NUMBER_MASK = 0x60;
var ID_MASK = 0x40;
var CONDITION_MASK = '0';

// var BOOL_TRUE = 'p';
// var BOOL_FALSE = 'q';

var sensors = []
var actions = []

var Triggers = null
var Mouse_Parameters = null

var CoreStates = {}

let t_current_yaw = 0, t_current_pitch = 0

// this is run every draw() cycle so no need to check time elapsed
function runCore() {

    // let mmove = new Uint8Array(4)
    const threshold = 1
    let dx = sensor_value[8] - t_current_yaw, dy = sensor_value[9] - t_current_pitch
    let mmove = ""
    if (Math.abs(dx) > threshold || Math.abs(dy) > threshold) {
        if (dx < 0) {
            while (dx < 0) {
                mmove += "L"
                dx += 1
            }
        }
        if (dx > 0) {
            while (dx > 0) {
                mmove += "R"
                dx -= 1
            }
        }
        if (dy < 0) {
            while (dy < 0) {
                mmove += "D"
                dy += 1
            }
        }
        if (dy > 0) {
            while (dy > 0) {
                mmove += "u"
                dy -= 1
            }
        }
        /*
        let b2 = num2bytes(sensor_value[8] - t_current_yaw)
        mmove[0] = b2[0]
        mmove[1] = b2[1]
        b2 = num2bytes(sensor_value[9] - t_current_pitch)
        mmove[2] = b2[0]
        mmove[3] = b2[1]
        */

        t_current_yaw = sensor_value[8]
        t_current_pitch = sensor_value[9]

        //writer.write(mmove)
        console.log(mmove)
        writer.write(encoder.encode(mmove))

    }

    return

    if (Triggers === null) return

    let t_now = millis()
    Object.keys(Triggers).forEach(function (sensor) { // step through all sensors with trigger defines
        let triggerset = Triggers[sensor].triggers
        let statecontrol = Triggers[sensor].statecontrol
        let thisstateset = triggerset[statecontrol.current_state]

        Object.values(thisstateset).forEach(thisstate => { // evaluate the triggers (for current state)
            let test_condition = sensor_value[sensor] + thisstate.condition + thisstate.threshold
            if (eval(test_condition)) {
                if (statecontrol.start_time === 0) { // first time condition satisfied
                    statecontrol.start_time = t_now
                }
                if ((t_now - statecontrol.start_time) >= thisstate.duration) {
                    // condition satisfied, execute all actions for this trigger
                    Object.values(thisstate.execute).forEach(thisaction => { // evaluate the triggers (for current state)
                        // thisaction
                        console.log("triggered: " + thisstate._info)
                        // we can only do W commands
                        if (thisaction.action === "Nothing") {
                        }
                        else if (thisaction.action === "Serial Send") {
                            //writer.write(encoder.encode("W" + String.fromCharCode(thisaction.state)))
                            writer.write(encoder.encode("W" + thisaction.state))
                            writer.write(encoder.encode(33 + thisaction.value))
                        } else {
                            console.log(" |->  unimplemented")
                        }
                    });

                    Triggers[sensor].statecontrol = JSON.parse(JSON.stringify(blankstate))
                    Triggers[sensor].statecontrol.current_state = thisstate.next_state
                }
            }

        });
    });
}

var blankstate = {
    current_state: 1,
    start_time: 0
}

function resetCore() {
    if (Triggers === null) return

    Object.values(Triggers).forEach(function (trigger) {
        trigger.statecontrol = JSON.parse(JSON.stringify(blankstate))
    });
}

//const MAX_SENSORS = 20; // total number of sensors, base=1
const HIST_SIZE = 1024;

/*
var serialio,
    latestData = "waiting for data",
    currentport = 'none',
    receivedData = '',
    portsequence = 0,
    portsList = [];
*/

var sensorCount = 0;
var sensor_value = [],
    ypr = [],
    res = [];

var plot1, plot2, plot3, plot4, i, plot_index;
var polygonPoints, mug, star;
var gaussianStack, gaussianCounter, timeSeriesList, timeSeriesCounter, histogramList, histogramCounter;
var points3 = [],
    points4 = [];
var current_sensed = 14,
    sense_reset = false;
var freq = 20;

var nmeters = 16; // 16 if including Light
var meters = [];
let inMotion = false,
    inGo = false;

function mk_meter(bX, bY, id) {

    var b = new Clickable(bX, bY);
    b.id = id || null;
    b.prop = {};
    b.resize(100, 100);

    b.color = _color.white; //Background color of the clickable
    b.cornerRadius = 10; //Corner radius of the clickable
    b.text = "+"; //Text of the clickable
    b.textSize = 12;
    //minusButton.strokeWeight = 2; 
    //Stroke width of the clickable
    // minusButton.stroke = "#000000"; 
    //Border color of the clickable
    //b.textColor = blue; //Color of the text
    //minusButton.textFont = "sans-serif";
    b.onPress = function () {
        b.pressed = true;
        b.textColor = color(250, 120, 120);
        b.diffx = mouseX - b.x;
        b.diffy = mouseY - b.y;
        if (!inMotion && b.id) {
            current_sensed = b.id;
            sense_reset = true;
        }
    }
    b.onRelease = function () {
        b.textColor = color(120, 120, 250);
        b.pressed = false;
    }
    b.onHover = function () {
        b.color = _color.lightgrey;

        if (!inGo) return;
        console.log("hover+go");

        if (b.go_mode && b.prop.dwell) {
            let col;
            strokeWeight(0);

            if (millis() < b.prop.dwellTime) return;

            console.log("dwell");
            if ((millis() - b.prop.dwellTime) <= DWELL_TIME) {
                let x = b.x + b.width / 2;
                let y = b.y + b.height / 2;
                let fractionFull = 1 - ((DWELL_TIME - (millis() - b.prop.dwellTime)) / DWELL_TIME);

                let col = (fractionFull / 2 + 0.5) * 255;

                col = (fractionFull / 2 + 0.5) * 255;
                fill(color(100, Math.floor(col), 100));
                arc(x, y, 60, 60, -0.5 * Math.PI, fractionFull * 2 * Math.PI - 0.5 * Math.PI);
            }
        } else {
            b.prop.dwell = true;
            b.prop.dwellTime = millis();
        }
    }

    b.onOutside = function () {
        //b.color = 'rgb(250,250,250)';
        b.color = _color.aliceblue;
        b.prop.dwell = false;
    }
    return b;
}


function setup() {
    frameRate(freq);

    //serialio.web_socket();  // 2020-11-15 Sun for rcsupport; we do not need local serialio for the USB port

    // default is STAND_ALONE -- set in utils4.js

    // legacy serialio (p5.serialcontrol or serial-json-server)
    var serialio = (function () { return { 'refreshport': null, 'nextport': null, 'selectport': null, 'send': null } })()

    startTime = millis();

    createCanvas(C_WIDTH, C_HEIGHT + C_SHIFT);
    //	pg = createGraphics(500, 500, WEBGL);

    let x = 200;
    for (let i = 0; i < nmeters; i++) {
        meters[i] = mk_meter(x, 300, i);
        meters[i].resize(100, 100);
        meters[i].text = i;
        if (i >= 1 && i <= 6 || i == 14) meters[i].meter_mode = true;
        if (i >= 8 && i <= 13) meters[i].swivel_mode = true;
        if (i == 15) meters[i].meter_mode = true;

        if (i == 0 || i == 7) {
            meters[i].hidden = true;
            continue;
        }
        x += 55;
    }
    meters[1].text = "3A";
    meters[2].text = "3B";
    meters[3].text = "2A";
    meters[4].text = "2B";
    meters[5].text = "1A";
    meters[6].text = "1B";

    /*========= 2020-05-30 Sat : example of how to use go_mode
    meters[6].text = "Acc_X";
    meters[6].base_mode = false;
    meters[6].meter_mode = false;
    meters[6].swivel_mode = false;
    meters[6].go_mode = true;
    */

    meters[8].text = "Acc_X"
    meters[9].text = "Acc_Y"
    meters[10].text = "Acc_Z"
    meters[11].text = "Gyro_X"
    meters[12].text = "Gyro_Y"
    meters[13].text = "Gyro_Z"

    for (let i = 8; i <= 13; i++) {
        meters[i].base_mode = false;
        if (M9250) {
            meters[8].text = "YAW"
            meters[9].text = "PITCH"
            meters[10].text = "ROLL"

            meters[i].lower_value = -180
            meters[i].upper_value = 180
        }
        else {
            meters[i].lower_value = -32000
            meters[i].upper_value = 32000
        }
    }

    meters[14].text = "Movement"
    meters[14].upper_value = 25000

    if (nmeters > 15) meters[15].text = "Light"


    if (CONNECT_HUB) {
        menu = ([{

            id: "connect_mode",
            display: "CONNECT",
            fn: connect_fn

        }]).concat(menu)
        /*
        menu = menu.concat({
            id: "refresh",
            display: "Scan",
            fn: serialio.refreshport
        },
            {
                id: "nextport",
                display: "Next",
                //			fn: nextport
                fn: serialio.nextport
            },
            {
                id: "selectport",
                display: "Select",
                fn: serialio.selectport
            });
            */
    }
    if (APP_MODE === AS_CLIENT) {
        menu = menu.concat(
            {
                id: "remote", // remote server
                display: "Reset", // changed from "Remote",
                fn: remote_fn
            });
    }
    if (APP_MODE === AS_SUPPORT) {
        menu = menu.concat(
            {
                id: "remote", // remote server
                display: "Reset", // changed from "Remote",
                fn: remote_fn
            },
            {
                id: "entercode", // enter code
                display: "enter",
                fn: entercode_fn
            });
    }

    let i = 0;
    for (let m of menu) { // new menu
        m.b = mk_meter(20, 20 + i * 37, null);
        m.b.text = m.display;
        m.b.button_mode = true;
        m.b.resize(80, 33);
        m.b.onPress = m.fn;

        if (m.id === "remote") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.y = C_HEIGHT - 85;
        }
        if (m.id === "entercode") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.x = 225
            m.b.y = C_HEIGHT - 118;
        }
        if (m.id === "refresh") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.y = C_HEIGHT - 55;
        }
        if (m.id === "nextport") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.x = m.b.x + 60;
            m.b.y = C_HEIGHT - 55;
        }
        if (m.id === "selectport") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.x = m.b.x + 60 * 2;
            m.b.y = C_HEIGHT - 55;
        }
        if (m.id === "clearoutput") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.x = C_RIGHT;
            m.b.y = C_HEIGHT - 20;
        }
        if (m.id === "clearprofile") {
            m.b.textSize = 12;
            m.b.resize(50, 25);
            m.b.x = 10;
            m.b.y = C_HEIGHT + 10;
        }

        i = i + 1;
    }

    //atGrid = matrix(C_WIDTH / gridSize + 1, (C_HEIGHT + C_SHIFT) / gridSize + 1, null);

    // Obtain the points for the third plot
    timeSeriesList = [];
    timeSeriesCounter = 0;

    for (i = 0; i < 150; i++) {
        timeSeriesList[i] = 0;
    }

    plot_index = 0;

    for (i = 0; i < timeSeriesList.length; i++) {
        points3[i] = new GPoint(i + 0.5 - timeSeriesList.length / 2, timeSeriesList[i] / timeSeriesCounter, "point " + i);
    }

    // key sensor's real-time display
    plot3 = new GPlot(this);
    plot3.setPos(C_RIGHT, 10);
    plot3.setYLim(-0.005, 1024);
    plot3.getTitle().setText("Time Series - Sensor " + meters[current_sensed].text + " (" + timeSeriesCounter + " points)");
    plot3.getTitle().setTextAlignment(this.LEFT);
    plot3.getTitle().setRelativePos(0.1);
    plot3.setPoints(points3);
    plot3.startHistograms(GPlot.VERTICAL);


    histogramList = [];
    histogramCounter = 0;

    for (i = 0; i < HIST_SIZE; i++) {
        histogramList[i] = 0;
    }

    plot_index = 0;

    for (i = 0; i < HIST_SIZE; i++) {
        points4[i] = new GPoint(histogramList[i], i + 0.5 - histogramList.length / 2, "point " + i);
    }

    plot4 = new GPlot(this);
    plot4.setPos(C_RIGHT, 320);
    plot4.setYLim(-0.005, 1024);
    plot4.getXAxis().getAxisLabel().setText("Range = " + meters[current_sensed].lower_value + " to " + meters[current_sensed].upper_value);
    plot4.getTitle().setText("Histogram = Sensor " + meters[current_sensed].text + " (" + histogramCounter + " points)");
    plot4.getTitle().setTextAlignment(this.LEFT);
    plot4.getTitle().setRelativePos(0.1);
    plot4.setPoints(points4);
    plot4.startHistograms(GPlot.VERTICAL);

    profileArea = createInput("(profile here)");
    profileArea.position(10, C_HEIGHT + 40);
    profileArea.size(680, 20);

    if (APP_MODE === AS_SUPPORT) {
        codeArea = createInput(0);
        codeArea.position(120, C_HEIGHT - 115)
        codeArea.size(95, 20)
    }

    if (DEV && APP_MODE !== STAND_ALONE) { // if not DEV, then using hardcoded value from utils?.js

        ipArea = createInput(SERVER_ADDR);
        ipArea.position(75, C_HEIGHT - 80);
        ipArea.size(95, 20);

        portArea = createInput(SERVER_PORT); // ylh
        // portArea = createInput(0);

        portArea.position(175, C_HEIGHT - 80);
        portArea.size(45, 20);
    }

    if (APP_MODE === AS_SUPPORT || APP_MODE === AS_CLIENT)
        remote_fn();


    outputArea = createDiv("---");
    outputArea.position(C_RIGHT, C_HEIGHT);
    outputArea.size(450, 380)
    outputArea.style('overflow:scroll')
    outputArea.id("outbox");

    dropzone = createFileInput(handleFile);
    dropzone.position(270, C_HEIGHT - 10);
    //dropzone.size(100, 100)
    dropzone.id("dropzone");

    function handleFile(file) {
        let new_config = file.data
        new_config = new_config.replace(/\s+/g, '')       // Remove whitespaces and newlines
        if (validate(new_config) === true) {
            profileArea.value(new_config)
            updateOutput(JSON.stringify(parse_configuration(new_config.slice(1, -1))))
            resetCore()
            if (writer !== undefined) {
                sendHubCommand('Get hub version')
                writer.write(encoder.encode(new_config));
                sendHubCommand('Get configuration')
            }

        } else {
            alert("'" + file.name + "' is an INVALID configuration file!")
        }

    }


    get_fn()
    //console.log("\n\n\n\n\n*** local storage disabled at startup***\n\n");
    settings_fn()
    design_fn()

    if (APP_MODE === AS_CLIENT)
        remoteio.send("?r")

}

function draw() {
    background('rgb(250,250,250)'); // make the screen _color.aliceblue
    fill(BASETEXTCOLOUR);
    strokeWeight(2);
    textAlign(LEFT);
    textSize(12);

    if (APP_MODE === AS_CLIENT) {
        text('ACCESS CODE ' + accessCode, 24, C_HEIGHT - 105)
    }

    if (APP_MODE === AS_SUPPORT) {
        text('ACCESS CODE', 24, C_HEIGHT - 105);
    }
    if (APP_MODE === AS_SUPPORT || APP_MODE === AS_CLIENT) {
        if (msgFromServer.length > 0) text(msgFromServer, 300, C_HEIGHT - 105)
        text("" + (remoteio.connected() ? "connected" : " NOT connected"), 225, C_HEIGHT - 70);
    }
    if (CONNECT_HUB) {
        //ylh text("USB serial: " + serialio.thisport() + (serialio.connected() ? " connected" : " NOT connected"), 28, C_HEIGHT - 20);
    }

    text("Hub firmware: " + versionText, 15, C_HEIGHT);
    text("Send Config ", 200, C_HEIGHT);
    text("SCRATCH PAD", 70, C_HEIGHT + 25);

    for (var m of menu) {
        m.b.textColor = _color.royalblue
        if ((inMotion && m.id === "move") ||
            (inGo && m.id === "go") ||
            (inSettings && m.id === "settings")
        ) m.b.textColor = _color.firebrick

        if (m.id === "design" && inDesign) m.b.textColor = _color.firebrick // _color.khaki

        m.b.color = _color.papayawhip
        if ((m.id === "set_run_mode" && core_mode === RUN_MODE) ||
            (m.id === "set_report_mode" && core_mode === REPORT_MODE) ||
            (m.id === "set_idle_mode" && core_mode === IDLE_MODE) ||
            (m.id === "design" && inDesign)
        ) {
            m.b.color = _color.lightsalmon
        }

        m.b.draw();
    }

    if (SHOW_DATA) {

        draw_plots();

        for (let i = 0; i < nmeters; i++) {
            if (i == current_sensed) meters[i].color = _color.powderblue
            else meters[i].color = _color.aliceblue
            meters[i].draw();
            if ((millis() - dataLastUpdated) > 1200) {
                dataLastUpdated = 0;
                meters[i].value = 0;
            }
        }

    }
    //scrollToTheBottom();
} //draww

function draw_plots() {
    // Add one more point to the uniform stack
    var mmin = 1000000000,
        mmax = -100000000;

    if (dataLastUpdated !== 0) {

        if (sense_reset) {
            sense_reset = false;
            timeSeriesCounter = histogramCounter = 0;
            for (i = 0; i < timeSeriesList.length; i++) timeSeriesList[i] = 0;
            for (i = 0; i < HIST_SIZE; i++) histogramList[i] = 0;
        }

        if (++plot_index >= timeSeriesList.length) {
            plot_index = plot_index - 1;
            for (let i = 0; i < plot_index; i++) {
                timeSeriesList[i] = timeSeriesList[i + 1];
            }
        }

        timeSeriesList[plot_index] = sensor_value[current_sensed];
    }

    for (i = 0; i < plot_index; i++) {
        if (timeSeriesList[i] < mmin) mmin = timeSeriesList[i];
        if (timeSeriesList[i] > mmax) mmax = timeSeriesList[i];
        points3[i] = new GPoint(i + 0.5, timeSeriesList[i], "point " + i);
    }

    plot3.setYLim(mmin - 100, mmax + 100);
    plot3.setPoints(points3);
    plot3.getTitle().setText("Time Series - Sensor " + meters[current_sensed].text + " = " + timeSeriesList[plot_index] + "");
    //plot4.setPos(C_RIGHT, sensor_value[15]);

    plot3.getXAxis().getAxisLabel().setText("" + Math.floor(1000 / freq) + "ms/reading");

    // Draw the forth plot
    plot3.beginDraw();
    plot3.drawBackground();
    plot3.drawBox();
    plot3.drawXAxis();
    plot3.drawYAxis();
    plot3.drawTitle();
    plot3.drawHistograms();
    plot3.endDraw();

    mmin = 10000000;
    mmax = -10000000000;


    if (dataLastUpdated !== 0) {

        let hist_value = Math.round((sensor_value[current_sensed] /
            (meters[current_sensed].upper_value -
                meters[current_sensed].lower_value)) * HIST_SIZE)
        histogramList[hist_value] = histogramList[hist_value] + 1;
        histogramCounter = histogramCounter + 1;
    }

    for (i = 0; i < HIST_SIZE; i++) {
        if (histogramList[i] < mmin) mmin = histogramList[i];
        if (histogramList[i] > mmax) mmax = histogramList[i];
        points4[i] = new GPoint(i + 0.5, histogramList[i], "point " + i);
    }


    plot4.setYLim(mmin - 5, mmax + 10);
    plot4.setPoints(points4);
    plot4.getTitle().setText("Histogram - Sensor " + meters[current_sensed].text + " (" + histogramCounter + " pts)");
    //plot4.setPos(C_RIGHT, sensor_value[15]);
    plot4.getXAxis().getAxisLabel().setText("X " + Math.floor((meters[current_sensed].upper_value - meters[current_sensed].lower_value) / 1024) +
        "\n Range = " + meters[current_sensed].lower_value + " to " + meters[current_sensed].upper_value);
    // Draw the forth plot
    plot4.beginDraw();
    plot4.drawBackground();
    plot4.drawBox();
    plot4.drawXAxis();
    plot4.drawYAxis();
    plot4.drawTitle();
    plot4.drawHistograms();
    plot4.endDraw();

    if (core_mode === REPORT_MODE) {
        //runCore()
    }
}

/*
var yaw = 0,
    pitch = 0,
    roll = 0, 
    pg;

function visualize_ypr() {

    pg.rotateX(PI / 2);

    pg.normalMaterial();

    // Start display
    push();

    yaw = random(-1, 1);
    pitch = random(-1, 1);
    roll = random(-1, 1);

    // Apply the rotation in Z-Y-X
    pg.rotateZ(radians(yaw));
    pg.rotateY(radians(roll));
    pg.rotateX(radians(pitch));

    // Make an object
    pg.box(200, 200, 200);

    // Finish display
    pop();
}
//*/


let inSettings = true
let inDesign = true

function design_fn() {
    inDesign = !inDesign;
    if (inDesign) {
        set_layout(JSON.stringify(small_dials))
    } else {
        set_layout(JSON.stringify(big_dials))
    }
}

function settings_fn() {
    for (let m of menu) {
        if (m["id"] === "move" || m["id"] === "go" || m["id"] === "erase" || 
        m["id"] === "refresh" || m["id"] === "nextport" || m["id"] === "selectport" || 
        m["id"] === "save" || m["id"] === "retrieve" || m["id"] === "gettriggers" || 
        m["id"] === "sendtriggers" || m["id"] === "retrieve" || m["id"] === "setfrom" ||
        m["id"] === "loadnewtriggers" || m["id"] === "shownewtriggers" 
        ) {
            m.b.hidden = inSettings;
        }
    }
    inSettings = !inSettings;
    if (!inSettings) inMotion = false;
}

function entercode_fn() {
    if (APP_MODE === AS_SUPPORT)
        remoteio.send("?r" + codeArea.value())
}

function remote_fn() {
    if (APP_MODE === AS_CLIENT || APP_MODE === AS_SUPPORT) {
        if (DEV) {
            SERVER_ADDR = ipArea.value();
            SERVER_PORT = portArea.value();
        }
        remoteio.changeport();
        remoteio.web_socket();
    }
}

function move_fn() {
    inMotion = !inMotion;
}

function go_fn() {
    inGo = !inGo;
}

function erase_fn() {
    //profileArea.value(""); // ?
}

function clearoutput_fn() {
    outputArea.html("");
}
function clearprofile_fn() {
    profileArea.value("");
}

let w_value_mode = false;

function w_values_fn() {
    // send "W+" to Arduino, receive "W123\nZ" in return
    // send "W?" to Arduino, receive "histogram" in return

    serialio.send('W?');
    if (!serialio.connected() && APP_MODE === AS_SUPPORT)
        remoteio.send('W?');
}

var webserialConnected = false

function connect_fn() {
    if (CONNECT_HUB)
        updateConnection()
}

function run_fn() {

    if (CONNECT_HUB) {
        // serialio.send(RUN_MODE);
        sendHubCommand("Set to RUN mode")  // web_serial
        core_mode = RUN_MODE
    }
    if (APP_MODE === AS_SUPPORT) {
        remoteio.send(RUN_MODE);
    }

}

function query_fn() { // send Q

    if (CONNECT_HUB) {
        // serialio.send(REPORT_MODE);
        sendHubCommand("Get input levels")  // web_serial
        core_mode = REPORT_MODE
    }
    if (APP_MODE === AS_SUPPORT) {
        remoteio.send(REPORT_MODE);
    }
}

function idle_fn() {

    if (CONNECT_HUB) {
        // serialio.send(GET_VERSION);
        sendHubCommand("Get hub version")  // web_serial
        core_mode = IDLE_MODE
    }
    if (APP_MODE === AS_SUPPORT) {
        remoteio.send(GET_VERSION);
    }
}

function get_triggers_fn() {

    if (CONNECT_HUB) {
        // serialio.send(GET_VERSION)
        // serialio.send(REQUEST_TRIGGERS);
        sendHubCommand("Get hub version")  // web_serial
        sendHubCommand("Get configuration")  // web_serial
    }
    if (APP_MODE === AS_SUPPORT) {
        remoteio.send(REQUEST_TRIGGERS);
    }
}

function send_triggers_fn() {

    if (CONNECT_HUB) {
        // serialio.send(GET_VERSION);
        // serialio.send(profileArea.value());
        sendHubCommand("Get hub version")  // web_serial
        writer.write(encoder.encode(profileArea.value()))
    }
    if (APP_MODE === AS_SUPPORT) {
        remoteio.send(GET_VERSION);
        remoteio.send(profileArea.value());
    }
}

function load_new_triggers_fn() {
    if (CONNECT_HUB) {
        try {
            let t = JSON.parse(profileArea.value())
            Triggers = t
            updateOutput("Triggers loaded.")
            resetCore()
        } catch (error) {
            updateOutput("** Triggers NOT loaded **")
        }
    }
}

function show_new_triggers_fn() {
    //profileArea.value(JSON.stringify(Triggers))
    updateOutput(JSON.stringify(Triggers,null,"  "))
}

// key pad key touched; action immediately, then show for FLASH_TIME [2020-05-30 Sat]-- not used
function padKeyOn(j) {
    let PAD_REFRACTORY = 300; // this must be less than FLASH_TIME
    let list = "";
    for (var k of keys) {
        if (k.el.value() == j) {
            // rising edge, just been touched OR re-touched after REFRACTORY while flashing
            if (!k.touched || (k.touched && (millis() - k.touchedTime) > PAD_REFRACTORY)) {

                // ACTION on rising edge here
                //                myText += k.el.value() + ",";
                list += k.el.value() + ",";

                k.touched = true;
                k.touchedTime = millis();
            }
        }
    }

    if (list.length > 0) {
        outputArea.html(outputArea.html() + "<br>[" + ((millis() - startTime) / 1000).toFixed(2) + "] " + list);
    }
}

function padKeyOff(k) {
}

// analog key
function senseKeyOn(j, val) {
    let PAD_REFRACTORY = 300; // this must be less than FLASH_TIME
    let list = "";
    for (var k of keys) {
        if (k.el.value() == j) {
            // rising edge, just been touched OR re-touched after REFRACTORY while flashing
            if (k.sensed >= 0 && (millis() - k.sensedTime) > PAD_REFRACTORY) {

                // ACTION on rising edge here
                //                myText += k.el.value() + ",";
                // commented out below: dont display all analog values
                //				list += k.el.value() + " (" + k.sensed + "), ";

                k.sensed = val;
                k.sensedTime = millis();
            }
        }
    }

    if (list.length > 0) {
        outputArea.html(outputArea.html() + "<br>[" + ((millis() - startTime) / 1000).toFixed(2) + "] " + list);
    }
}

function updateOutput(s) {
    outputArea.html("<pre>" + outputArea.html() +
        "<br>[" + getHourMinuteSecond() + "] " + s + "</pre>" )
    scrollToTheBottom();
}

function scrollToTheBottom() {
    let elem = document.getElementById(outputArea.id());
    elem.scrollTop = elem.scrollHeight;
}



function save_fn() {
    localStorage.meters = JSON.stringify(meters);
    /*
    localStorage.touchpadkeys = JSON.stringify(keys);
    profileArea.value(localStorage.touchpadkeys);
    //    localStorage.menu = JSON.stringify(menu);
*/
}


// retrieve profile from local storage
function get_fn() {
    if (localStorage.touchpadkeys) {
        /*	profileArea.value(localStorage.touchpadkeys);
        setit(localStorage.touchpadkeys);
        */
    }

    if (localStorage.meters) {
        set_layout(localStorage.meters);
        profileArea.value(localStorage.meters);
        /*
        let tmp = JSON.parse(localStorage.meters);
        for (let i = 0; i < nmeters; i++) {
            meters[i].x = tmp[i].x;
            meters[i].y = tmp[i].y;
        }
        */
    } else {
        localStorage.meters = JSON.stringify(big_dials);
        set_layout(localStorage.meters);
    }
    if (M9250) {
        meters[8].text = "YAW";
        meters[9].text = "PITCH";
        meters[10].text = "ROLL";

        meters[9].lower_value = -180
        meters[10].lower_value = -180
        meters[8].upper_value = 180
        meters[9].upper_value = 180
        meters[10].upper_value = 180
    }
    else {
        meters[8].lower_value = -32000
        meters[9].lower_value = -32000
        meters[10].lower_value = 32000
        meters[8].upper_value = 32000
        meters[9].upper_value = 32000
        meters[10].upper_value = 32000
    }

}

// set profile usng what's in profileArea input area
function setfrom_fn() {
    set_layout(profileArea.value())
}

function set_layout(p) {

    try {
        let tmp = JSON.parse(p);
        for (let i = 0; i < nmeters; i++) {
            meters[i].x = tmp[i].x;
            meters[i].y = tmp[i].y;
            meters[i].width = tmp[i].width
            meters[i].height = tmp[i].height
            meters[i].resize(tmp[i].width, tmp[i].height)
            meters[i].text = tmp[i].text;
        }
    } catch (error) {
        updateOutput("Error laying out dials.")
    }
    return;

    let thiskeys = JSON.parse(p);
    let i = 0;
    for (let i = 0; i < 26; i++) {
        keys[i].el.position(toGrid(thiskeys[i].el.x) - gridSize / 2, toGrid(thiskeys[i].el.y) - gridSize / 2);
        keys[i].el.gridx = toGrid(thiskeys[i].el.x) / gridSize;
        keys[i].el.gridy = toGrid(thiskeys[i].el.y) / gridSize;
        atGrid[keys[i].el.gridx][keys[i].el.gridy] = keys[i];
    }
}

// new transfer protocol: taken from Andrew's interface.js: updateMeterValues

function updateSensors(stream) { // updateMeterValues(stream) {
    let list = "";
    try {
        if (stream.getChar() != START_OF_SENSOR_DATA) {
            throw ("Invalid start of sensor data");
        }

        dataLastUpdated = millis();

        sensorCount = stream.getNum(4);
        // console.log("#"+sensorCount)

        for (var i = 0; i < sensorCount; i++) {
            let id = stream.getID(2);
            let value = stream.getNum(4);

            sensor_value[id] = value;
            if (M9250 && id >= 8 && id <= 10) { //??
                // console.log("   " + id + ": " + value)
                sensor_value[id] /= 10.0 //?? change unit back now we can handle float
                if (id == 8 && sensor_value[id] > 180.0) {
                    let tmp = sensor_value[id]
                    sensor_value[id] = Math.floor(tmp * 10 - 3600) / 10.0
                }
            }
            meters[id].value = parseFloat(sensor_value[id]);
        }

        if (stream.getChar() != END_OF_BLOCK) {
            throw ("Invalid end of sensor data");
        }

        if (list.length > 0) {
            outputArea.html(outputArea.html() + "<br>[" + ((millis() - startTime) / 1000).toFixed(2) + "] " + list);
        }

    } catch (err) {
        //profileArea.value("Err updateSensors: " + err);
        outputArea.html(outputArea.html() + '<br>Err updateSensors: ' + err);
    }
}

function updateMouse(data) {
    profileArea.value(data);
    //outputArea.html(outputArea.html() + "<br>" + data);

    return;

    // 2020-07-16 Thu ylh unfinished Mouse
    let list = "";
    try {
        if (stream.getChar() != MOUSE_SPEED) {
            throw ("Invalid start of mouse speed");
        }

        //dataLastUpdated = millis();

        let mouseLength = stream.getNum(4);

        for (var i = 0; i < sensorCount; i++) {
            let id = stream.getID(2);
            let value = stream.getNum(4);
            sensor_value[id] = value;
            meters[id].value = parseInt(sensor_value[id]);
        }

        if (stream.getChar() != END_OF_BLOCK) {
            throw ("Invalid end of sensor data");
        }

        if (list.length > 0) {
            outputArea.html(outputArea.html() + "<br>[" + ((millis() - startTime) / 1000).toFixed(2) + "] " + list);
        }

    } catch (err) {
        //profileArea.value("Err updateSensors: " + err);
        outputArea.html(outputArea.html() + '<br>Err updateSensors: ' + err);
    }
}

function updateTriggers(data) {
    profileArea.value(data);
    //outputArea.html(outputArea.html() + "<br>" + data);
}

function setVersion(data) {
    versionText = data.substring(0, data.length - 1);;
}

function updateWData(data) { // supercedes readData() below
    console.log('data ->' + data);
    myText = data; // myText not displayed
    outputArea.html(outputArea.html() + '<br>' + data);

    return;

    var pos = 0;
    var res = data.split(/[\n\r,: Z]+/); // split the data on the colons and commas
    var len = res.length;
    //	console.log(res + "--" + len);
    while (pos < len) {
        if (res[pos] == 'W') {
            senseKeyOn(7 + pos, parseInt(res[++pos]));
        } else

            // MPR121 touchpad
            if (res[pos] == '+') {
                padKeyOn(res[++pos]);
            } else if (res[pos] == '-') { // 3 digital pins
                padKeyOn(res[++pos]);
            } else

                // mpu-6050-i2cdevlib
                if (res[pos] == 'YPR') {
                    for (var i = 0; i < 3; i++) {
                        ypr[i] = res[++pos];
                    }
                    // ylh ******* swap yaw and roll
                    let x = ypr[2];
                    ypr[2] = ypr[1];
                    ypr[1] = x;
                }

        pos++;
    }
    //	console.log("W< done!");
    return;
}

/*
function drawDial(x, y, len, angle) {

    push();
    strokeWeight(0);
    fill(220);
    ellipse(x, y, len, len);
    stroke(100, 100, 255);
    strokeWeight(2);
    translate(x, y);
    rotate(angle);
    line(-len / 2, 0, len / 2, 0);
    pop();
}
//*/