//ylh

let serial;
let latestData = "waiting for data";

function setup() {
	createCanvas(windowWidth, windowHeight);

	serial = new p5.SerialPort();

	serial.list();

	//serial.open('/dev/tty.usbmodemHIDFG1'); 

	//serial.open('/dev/tty.wchusbserial14340');

	serial.open('/dev/tty.usbmodem141401');

	serial.on('connected', serverConnected);

	serial.on('list', gotList);

	serial.on('data', gotData);

	serial.on('error', gotError);

	serial.on('open', gotOpen);

	serial.on('close', gotClose);
}

function serverConnected() {
	print("Connected to Server");
	latestData += "Connected to server";
}

function gotList(thelist) {
	print("List of Serial Ports:");
	latestData += "\nList of serial ports";

	for (let i = 0; i < thelist.length; i++) {
		print(i + " " + thelist[i]);
		latestData += "" + i + " " + thelist[i];
	}
}

function gotOpen() {
	print("Serial Port is Open");
	latestData +="serial port is open";
}

function gotClose() {
	print("Serial Port is Closed");
	latestData += "Serial Port is Closed";
}

function gotError(theerror) {
	print(theerror);
	console.log(theerror);
	latestData += "=" + theerror;
}

function gotData() {
	let currentString = serial.readLine();
	trim(currentString);
	if (!currentString) return;
	console.log(currentString);
	latestData += "|" + currentString;
}

function draw() {
	background(255, 255, 255);
	fill(0, 0, 0);
	text(latestData, 10, 10);
	// Polling method
	/*
	if (serial.available() > 0) {
	 let data = serial.read();
	 ellipse(50,50,data,data);
	}
	*/
}