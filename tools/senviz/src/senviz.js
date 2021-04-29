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
        }, {
			id: "gettriggers",
			display: "GET TRIGS",
			fn: get_triggers_fn
        },
		{
			id: "sendtriggers",
			display: "SEND TRIGS",
			fn: send_triggers_fn
        },
		{
			id: "remote", // remote server
			display: "Remote",
			fn: remote_fn
        },

		{
			id: "refresh",
			display: "Local",
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
        },
		{
			id: "clear",
			display: "clear",
			fn: clear_fn
        }

    ],
	myText = "",
	C_WIDTH = 1200, // interface canvace w, h
	C_HEIGHT = 660,
	C_RIGHT = 700,
	C_SHIFT = 340, // display area below
	keySize = 20,
	gridSize = 20,
	DWELL_TIME = 1000,
	REFRACTORY = 1200,
	dataLastUpdated = 0,
	profileArea = null,
	portArea = null,
	ipArea = null,
	startTime = null,
	outputArea = null,
	versionText = "---",
	BASETEXTCOLOUR = "#6363fc",
	BACKGROUNDCOLOUR = 'rgb(255,255,255)',
	FOREGROUNDCOLOUR = 'rgb(220,220,200)';

// from Andrew's triggers.js
// -- Command and Block Headers -- //
var REPORT_MODE = 'Q';
var RUN_SENSACT = 'R';
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
var BOOL_TRUE = 'p';
var BOOL_FALSE = 'q';

var sensors = [];
var actions = [];

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
var freq = 15;

var grey = "#F0F0F0",
	darkgrey = "#d0d0d0",
	white = "#FFFFFF";

var nmeters = 16; // 16 if including Light
var meters = [];
let inMotion = false,
	inGo = false;

function mk_meter(bX, bY, id) {

	var b = new Clickable(bX, bY);
	b.id = id || null;
	b.prop = {};
	b.resize(100, 100);

	b.color = white; //Background color of the clickable
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
		b.color = darkgrey;

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
		b.color = grey;
		b.prop.dwell = false;
	}
	return b;
}

let pg; // ylh 

function setup() {
	frameRate(freq);

	serialio.web_socket();
	remoteio.web_socket();

	startTime = millis();

	createCanvas(C_WIDTH, C_HEIGHT + C_SHIFT);
	//	pg = createGraphics(500, 500, WEBGL);

	let x = 200;
	for (let i = 0; i < nmeters; i++) {
		meters[i] = mk_meter(x, 300, i);
		meters[i].resize(60, 60);
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
	meters[1].text = "1A";
	meters[2].text = "1B";
	meters[3].text = "2A";
	meters[4].text = "2B";
	meters[5].text = "3A";
	meters[6].text = "3B";

	/*========= 2020-05-30 Sat : example of how to use go_mode
	meters[6].text = "Acc_X";
	meters[6].base_mode = false;
	meters[6].meter_mode = false;
	meters[6].swivel_mode = false;
	meters[6].go_mode = true;
	*/

	meters[8].text = "Acc_X";
	meters[9].text = "Acc_Y";
	meters[10].text = "Acc_Z";
	meters[11].text = "Gyro_X";
	meters[12].text = "Gyro_Y";
	meters[13].text = "Gyro_Z";

	for (let i = 8; i <= 13; i++) {
		meters[i].base_mode = false;
		meters[i].upper_value = 32000;
	}

	meters[14].text = "Movement";
	meters[14].upper_value = 25000;

	if (nmeters > 15) meters[15].text = "Light";

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
		if (m.id === "clear") {
			m.b.textSize = 12;
			m.b.resize(50, 25);
			m.b.x = C_RIGHT;
			m.b.y = C_HEIGHT - 20;
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
	plot4.getXAxis().getAxisLabel().setText("Range = 0 to " + meters[current_sensed].upper_value);
	plot4.getTitle().setText("Histogram = Sensor " + meters[current_sensed].text + " (" + histogramCounter + " points)");
	plot4.getTitle().setTextAlignment(this.LEFT);
	plot4.getTitle().setRelativePos(0.1);
	plot4.setPoints(points4);
	plot4.startHistograms(GPlot.VERTICAL);

	profileArea = createInput("(profile here)");
	profileArea.position(10, C_HEIGHT + 25);
	profileArea.size(680, 20);
	ipArea = createInput(SERVER_ADDR);
	ipArea.position(75, C_HEIGHT - 80);
	ipArea.size(95, 20);

	// portArea = createInput(SERVER_PORT); // ylh
	portArea = createInput(0);

	portArea.position(175, C_HEIGHT - 80);
	portArea.size(45, 20);

	remote_fn();


	outputArea = createP("---");
	outputArea.position(C_RIGHT, C_HEIGHT);
	outputArea.id("outbox");


	get_fn();
	//console.log("\n\n\n\n\n*** local storage disabled at startup***\n\n");
	settings_fn();
}

function draw() {
	background('rgb(250,250,250)'); // make the screen grey
	fill(BASETEXTCOLOUR);
	strokeWeight(2);
	textAlign(LEFT);
	textSize(12);
	text("" + (remoteio.connected() ? "connected" : " NOT connected"), 225, C_HEIGHT - 70);

	text("USB serial: " + serialio.thisport() + (serialio.connected() ? " connected" : " NOT connected"), 28, C_HEIGHT - 20);

	text(versionText, 28, C_HEIGHT);

	//	textSize(24);
	//	text(myText, 300, C_HEIGHT + 20);

	draw_plots();

	//	visualize_ypr();

	for (let i = 0; i < nmeters; i++) {
		meters[i].draw();
		if ((millis() - dataLastUpdated) > 1200) {
			dataLastUpdated = 0;
			meters[i].value = 0;
		}
	}

	for (var m of menu) {
		m.b.textColor = "#7070F0";
		if (inMotion && m.id === "move") m.b.textColor = "#F01010";
		if (inGo && m.id === "go") m.b.textColor = "#F01010";
		if (inSettings && m.id === "settings") m.b.textColor = "#F01010";

		m.b.draw();
	}

	scrollToTheBottom();
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

		let hist_value = Math.round((sensor_value[current_sensed] / meters[current_sensed].upper_value) * HIST_SIZE);
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
	plot4.getXAxis().getAxisLabel().setText("X " + Math.floor(meters[current_sensed].upper_value / 1024) + "\n Range = 0 to " + meters[current_sensed].upper_value);
	// Draw the forth plot
	plot4.beginDraw();
	plot4.drawBackground();
	plot4.drawBox();
	plot4.drawXAxis();
	plot4.drawYAxis();
	plot4.drawTitle();
	plot4.drawHistograms();
	plot4.endDraw();
}

var yaw = 0,
	pitch = 0,
	roll = 0;

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

let inSettings = true;

function settings_fn() {
	for (let m of menu) {
		if (m["id"] === "move" || m["id"] === "go" || m["id"] === "erase" || m["id"] === "refresh" || m["id"] === "nextport" || m["id"] === "selectport" || m["id"] === "save" || m["id"] === "retrieve" || m["id"] === "gettriggers" || m["id"] === "sendtriggers" || m["id"] === "retrieve" || m["id"] === "setfrom") {
			m.b.hidden = inSettings;
		}
	}
	inSettings = !inSettings;
	if (!inSettings) inMotion = false;
}

function remote_fn() {
	SERVER_ADDR = ipArea.value();
	SERVER_PORT = portArea.value();
	remoteio.changeport();
	remoteio.web_socket();
}

function move_fn() {
	inMotion = !inMotion;
}

function go_fn() {
	inGo = !inGo;
}

function erase_fn() {
	//	myText = "";
	profileArea.value(""); // ?
}

function clear_fn() {
	//	myText = "";
	outputArea.html("");
}

let w_value_mode = false;

function w_values_fn() {
	// send "W+" to Arduino, receive "W123\nZ" in return
	// send "W?" to Arduino, receive "histogram" in return

	serialio.send('W?');
	if (!serialio.connected())
		remoteio.send('W?');
}

function run_fn() {

	serialio.send(RUN_SENSACT);
	remoteio.send(RUN_SENSACT);

}

function query_fn() { // send Q

	serialio.send(REPORT_MODE);
	remoteio.send(REPORT_MODE);
}

function idle_fn() {

	serialio.send(GET_VERSION);
	remoteio.send(GET_VERSION);

}

function get_triggers_fn() {
	// send only one get triggers
	serialio.send(GET_VERSION);
	remoteio.send(GET_VERSION);


	remoteio.send(REQUEST_TRIGGERS);
	if (!remoteio.connected())
		serialio.send(REQUEST_TRIGGERS);
}

function send_triggers_fn() {
	// send only one
	remoteio.send(GET_VERSION);
	if (!remoteio.connected())
		serialio.send(GET_VERSION);

	remoteio.send(profileArea.value());
	if (!remoteio.connected())
		serialio.send(profileArea.value());
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

function scrollToTheBottom() {
	let elem = document.getElementById(outputArea.id());
	elem.scrollTop = elem.scrollHeight;
}

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
		setit(localStorage.meters);
		profileArea.value(localStorage.meters);
		/*
		let tmp = JSON.parse(localStorage.meters);
		for (let i = 0; i < nmeters; i++) {
			meters[i].x = tmp[i].x;
			meters[i].y = tmp[i].y;
		}
		*/
	} else {
		localStorage.meters = JSON.stringify(default_settings);
		setit(localStorage.meters);
	}
}

// set profile usng what's in profileArea input area
function setfrom_fn() {
	setit(profileArea.value());
}

function setit(p) {

	let tmp = JSON.parse(p);
	for (let i = 0; i < nmeters; i++) {
		meters[i].x = tmp[i].x;
		meters[i].y = tmp[i].y;
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

		for (var i = 0; i < sensorCount; i++) {
			let id = stream.getID(2);
			let value = stream.getNum(4);
			sensor_value[id] = value;
			meters[id].value = parseInt(sensor_value[id]);

			//d0(""+id+" "+value);

			//			list += id + ": " + value + "\n";
			/*
			if (id < 8 || id > 13) {
				senseKeyOn(id, sensor_value[id]);

			} else {
				if (id == 8) ypr[0] = sensor_value[id];
				if (id == 9) ypr[1] = sensor_value[id];
				if (id == 10) ypr[2] = sensor_value[id];
				if (id == 11) gyro[0] = sensor_value[id];
				if (id == 12) gyro[1] = sensor_value[id];
				if (id == 13) gyro[2] = sensor_value[id];
			}
			*/
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

var default_settings = [{
	"x": 200,
	"y": 300,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": 0,
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": true,
	"diffx": 0,
	"diffy": 0,
	"id": null,
	"prop": {
		"dwell": false
	}
}, {
	"x": 147,
	"y": 19,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "1A",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 1,
	"prop": {
		"dwell": false
	}
}, {
	"x": 226,
	"y": 17,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "1B",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 2,
	"prop": {
		"dwell": false
	}
}, {
	"x": 318,
	"y": 16,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "2A",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 3,
	"prop": {
		"dwell": false
	}
}, {
	"x": 389,
	"y": 15,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "2B",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 4,
	"prop": {
		"dwell": false
	}
}, {
	"x": 479,
	"y": 15,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "3A",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 5,
	"prop": {
		"dwell": false
	}
}, {
	"x": 557,
	"y": 11,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "3B",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 6,
	"prop": {
		"dwell": false
	}
}, {
	"x": 187,
	"y": 377,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": 7,
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": true,
	"diffx": 0,
	"diffy": 0,
	"id": 7,
	"prop": {
		"dwell": false
	}
}, {
	"x": 168,
	"y": 209,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Acc_X",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": false,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": true,
	"go_mode": false,
	"value": 0,
	"upper_value": 32000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 8,
	"prop": {
		"dwell": false
	}
}, {
	"x": 243,
	"y": 208,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Acc_Y",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": false,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": true,
	"go_mode": false,
	"value": 0,
	"upper_value": 32000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 9,
	"prop": {
		"dwell": false
	}
}, {
	"x": 314,
	"y": 206,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Acc_Z",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": false,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": true,
	"go_mode": false,
	"value": 0,
	"upper_value": 32000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 10,
	"prop": {
		"dwell": false
	}
}, {
	"x": 391,
	"y": 206,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Gyro_X",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": false,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": true,
	"go_mode": false,
	"value": 0,
	"upper_value": 32000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 11,
	"prop": {
		"dwell": false
	}
}, {
	"x": 464,
	"y": 206,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Gyro_Y",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": false,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": true,
	"go_mode": false,
	"value": 0,
	"upper_value": 32000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 12,
	"prop": {
		"dwell": false
	}
}, {
	"x": 534,
	"y": 205,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Gyro_Z",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": false,
	"meter_mode": false,
	"button_mode": false,
	"swivel_mode": true,
	"go_mode": false,
	"value": 0,
	"upper_value": 32000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 13,
	"prop": {
		"dwell": false
	}
}, {
	"x": 391,
	"y": 105,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Movement",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 25000,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 14,
	"prop": {
		"dwell": false
	}
}, {
	"x": 320,
	"y": 107,
	"width": 60,
	"height": 60,
	"color": "#F0F0F0",
	"cornerRadius": 10,
	"strokeWeight": 1,
	"stroke": "#F0F0F0",
	"text": "Light",
	"textColor": {
		"mode": "rgb",
		"maxes": {
			"rgb": [255, 255, 255, 255],
			"hsb": [360, 100, 100, 1],
			"hsl": [360, 100, 100, 1]
		},
		"_array": [0.47058823529411764, 0.47058823529411764, 0.9803921568627451, 1],
		"levels": [120, 120, 250, 255],
		"name": "p5.Color"
	},
	"textSize": 12,
	"textFont": "sans-serif",
	"base_mode": true,
	"meter_mode": true,
	"button_mode": false,
	"swivel_mode": false,
	"go_mode": false,
	"value": 0,
	"upper_value": 1024,
	"hidden": false,
	"diffx": 0,
	"diffy": 0,
	"id": 15,
	"prop": {
		"dwell": false
	}
}];