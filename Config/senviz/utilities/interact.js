let arguments = process.argv.slice(2);
let dev = "dev"
let mqtt_server = 'mqtt://127.0.0.1';

//*
if (arguments.length == 0) {
	console.log("Usage: node interact.js [ topic 'mqtt://127.0.0.1']");
	return;
}

if (arguments.length > 0 ) {
	dev = arguments[0];
} 

if (arguments.length > 1) {
	mqtt_server = arguments[1]
}
//*/


console.log(dev)

let mqtt = require('mqtt');
let client = mqtt.connect(mqtt_server);

client.on('connect', () => {
	console.log("Connected to topic [" + dev + "] on " + mqtt_server);
	client.subscribe(dev);
	client.subscribe(dev + "-data");
	client.subscribe(dev + "-DEBUG");
	console.log("----------");
	query();
});

client.on("disconnected", function () {
	mqtt.connect();
});

var sig = "+x";
var bit = 0;

client.on("message", function (topic, message) {
	bit = 1-bit
	console.log(sig[bit]+topic + ' [' + getHourMin() + "] " + message);
	query();
})

const readline = require('readline').createInterface({
	input: process.stdin,
	output: process.stdout
})

function getHourMin() {
	let current_datetime = new Date();
	let h = current_datetime.getHours();
	let m = current_datetime.getMinutes();
	if (m < 10) m = "0" + m;
	let s = current_datetime.getSeconds();
	if (s < 10) s = "0" + s;
	return ("" + h + ":" + m + ":" + s);
}



function query() {
	bit = 1 - bit;
	readline.question('' + dev + ' ', (cmd) => {
		//	console.log(cmd);
		client.publish(dev + '-R', cmd);
		//		readline.close();
		query()
	});
	/*
	setTimeout( ()=>{
		query();
	}, 4000);
	//*/
}