/* rcexchange.js : 2020-11-13 Fri : from exchange.js : 2020-05-31 Sun

nodemon rcexchange.js [127.0.0.1]

+ mqtt channel "rcexch" for debugging output (mqtt server is hardcoded)
   ws - (1) from rcsupport (31006) (2) from rcclient (31007)
   tcp - from Config (java) or wifi-peripheral (n-updater.js or m-updater) (31005)
+ sends status/debug msg on mqtt channel "rcexch"
+ receives interactive command from mqtt channel "rcexch-R" (input from "node interact.js channel")

 *=== from socket_central.js : 2020-05-01 Fri
 socket to socket exchange
*/
"use strict"

let VERSION = "0.2 2020-12-11 Fri"
let debug = 0

let process = require("process")

const TCP_PORT = 31005
const SUPPORT_WS_PORT = 31006
const CLIENT_WS_PORT = 31007

let HOST = '127.0.0.1'
let mqtt_server = 'mqtt://127.0.0.1'

const mqtt_channel = 'rcexch'

var args = process.argv.slice(2)
if (args.length > 0) {
   HOST = args[0]
}
console.log('rcexchange running on ' + HOST)

// accumulated messages, to dumped out on demand
let clientmsg = '', supportmsg = '', configmsg = ''

// paced sending
let lastsent = new Date(), nMsg = 1

//====== begin MQTT code
let pub = console.log;

let channel = mqtt_channel

let mqtt = require('mqtt')

let mqtt_c = mqtt.connect(mqtt_server);

mqtt_c.on('connect', () => {
    pub = function (s) {
        mqtt_c.publish(channel, "Host " + HOST + ":" + s);
    };
    pub("\n\n**" + VERSION + "** Publishing on '" + channel + "' on " + mqtt_server +
        '\n===>' + process.argv[1] + ' started ' + getDateTime() +
        ' listening on ' + HOST + ': tcp=' + TCP_PORT + " ws support=" + SUPPORT_WS_PORT + " client=" + CLIENT_WS_PORT);

    mqtt_c.subscribe(channel + "-R");
    mqtt_c.subscribe(channel + "-data");
    mqtt_c.subscribe(channel + "-DEBUG");
    console.log("----------");
});

mqtt_c.on("disconnected", () => {
    console.log('mqtt disconnected');
    mqtt.connect();
});

// interactive control comes through mqtt channel 'rcexch-R'
mqtt_c.on("message", function (topic, message) {
    if (debug >= 2) { pub("mqtt recd on " + topic + " >" + message + '<') }

    if (topic == (channel + '-R')) {
        if (message == 'L') { // "List" the sessions
            let msg = ""
            pub('\n\n' + Object.keys(sessionList).length + ' sessions/ ' + Object.keys(clientList).length + ' clients')
            for (const s in sessionList) {
                msg += "\nSession:" + s
                msg += dispCred(sessionList[s].config) + dispCred(sessionList[s].support) + dispCred(sessionList[s].client)
            }
            pub(msg)

        }
        if (message == 'l') { // "list" the credentials
            let msg = ""
            pub('\n\n' + Object.keys(clientList).length + ' clients')
            for (const s in clientList) {
                msg += "\n\tclient:" + s + ' ' + dispCred(s.cred)
                //msg += dispCred(sessionList[s].config) + dispCred(sessionList[s].support) + dispCred(sessionList[s].client)
            }
            pub(msg)

        }
        if (message == 'R') { // "RESET"
            resetAll()
        }
        if (message == 'd') { // dump all the data in the pipeline
            pub('\nclientmsg: ' + clientmsg)
            pub('\nsupportmsg: ' + supportmsg)
            pub('\nconfigmsg: ' + configmsg)
            clientmsg = supportmsg = configmsg = ''
        }
        if (message == '0') { // debug level - least amount of msgs
            debug = 0
        }
        if (message == '1') {
            debug = 1
        }
        if (message == '2') {
            debug = 2
        }
    }
})

function getDateTime() {
    let current_datetime = new Date()
    return ("" + current_datetime.getFullYear() + "-" + (current_datetime.getMonth() + 1) + "-" + current_datetime.getDate() + " " + current_datetime.getHours() + ":" + current_datetime.getMinutes());
}

//======= end MQTT code

var net = require("net");

let session = {}; // currently 'indexed' by (config's) ip -> arbitrarily generated

let blankSession = {
    config: null,
    support: null,
    client: null,
    accessCode: null,
    authorized: 0  // not authorized: 0=initial 1=config added 2=support authorized 3=client authorized (exchange allowed)
};
let blankCredential = { // go into each of "support", "hub", "config"
    sessionID: null,
    ip: null,
    addr: null,
    sock: null,
    role: null
};

function dispCred(c) {
    if (c) {
        return "\nID:" + c.sessionID + " role:" + c.role + " ip:" + c.ip + " add:" + c.addr
    } else {
        return "\n(null)"
    }
}
/* handshaking between Config and Support on the one hand, and Client on the other
sessionList[] is based on tech's IP, each tech can only run one session of Java Config and one session of Support
they are linked by client's random sessionID 


1. tech runs Java Config - on a unique IP - this creates a session in sessionList["IP]. 
    Prior session's support and client, if exist, are terminated
2. tech runs rcsupport.html on the same IP, enters Client's number, client's cred added to sessionList["IP"] as is support's cred
3. when a Client signs on, they are given a uniq access code -- unique to each IP -- code is displayed and read to tech
    if there is a prior sessionID for this IP, it is terminated first. list of Clients maintained on clientList["IP"] which contains 
    the credential 'cred'

Strings beginning with '?' are intercepted by client & support and displayed in the browser
'?E' = error string to be displayed

Support sends access code for verification with '?r####' 

*/

var clientList = {} // based on client's IP, contains random uniq access ID
var sessionList = {}  // based on tech's IP, accessed by "IP"+ip
var tstsessionList = {}  // for tst only; based on tech's IP, accessed by "IP"+ip

function addCred(cred) {
    let msg = "addCred: " + cred.role
    let ipstr = cred.ip

    if (cred.role === 'config') {
        // when config comes in, always start over
        sessionList[ipstr] = blankSession
        sessionList[ipstr].config = cred

        msg += ' new config session on ' + ipstr + '\nsessionList= ' //+ JSON.stringify(sessionList)
        //cred.sock.write('?Config connected')
    }

    if (cred.role === 'support') {
        if (sessionList[ipstr]) {
            // session from the same ip must already exist, i.e. set up first by Config
            sessionList[ipstr].support = cred
            cred.sock.send('?ESupport session started. Request new access code from client.')

            msg += ' added support to session on ' + ipstr + '\nsessionList= ' // + JSON.stringify(sessionList)
        } else {
            msg += ' unable to add support because config is not running'
            cred.sock.send('?EPlease start Config program')
        }
    }
    if (cred.role === 'client') {
        let deleteList = []
        let key

        //* clean up old entries if any
        for (key in clientList) {  // if it already has an entry
            msg = 'examining key ' + key
            if (clientList[key].cred && clientList[key].cred.ip === cred.ip) { // client from the same ip
                msg += ' ' + cred.ip + ' to delete'
                deleteList.push(key)
            }
        }
        while (deleteList.length > 0) {
            let k = deleteList.pop()
            msg += '  delete ' + k
            for (let skey in sessionList) {
                if (clientList[k].cred.sessionID && clientList[k].cred.sessionID === skey) {
                    sessionList[skey].client = null
                    msg += ' + removed session ' + skey
                }
            }
            if (clientList[k]) {
                msg += " *" + k + " deleted"
                delete clientList[k]
            }
        }
        //*/ 

        let rnd8d = Math.floor(Math.random() * 10000)

        clientList[rnd8d] = {}
        clientList[rnd8d].cred = cred

        cred.sock.send('?r' + rnd8d)
        if (debug > 1)
            msg += ' new session id=' + rnd8d + " on " + ipstr + '\nclientList= ' //+ JSON.stringify(clientList)
        cred.sock.send('?Client session added')
    }
    pub(msg)
}

function addToSession(ip, addr, sock, role) {

    let cred = {}; // blankCredential;

    cred.sessionID = ip
    if (role === 'client') cred.sessionID = null // not yet assigned
    cred.ip = ip;
    cred.addr = addr;
    cred.sock = sock;
    cred.role = role;

    addCred(cred) // this supercedes the tst code below

    return cred

    let sessionID = ip
    let b = blankSession;
    tstsessionList[sessionID] = tstsessionList[sessionID] || b;

    let thissession = tstsessionList[sessionID]

    let msg = "Attempt to add " + role + "\n" +
        " ---> [" + ip + " | " + addr + " | " + sock + " | " + role + " ]"
    let ipstr = "IP" + ip


    //* this part is for testing, automaticallly connect config, then support, then client -- no authorization
    if (role === "config") {

        thissession.config = cred;
        thissession.authorized = 1

        if (thissession.support) thissession.support.sock.close();
        if (thissession.client) thissession.client.sock.close();

        thissession.client = thissession.support = null;
        msg += " ... ADDED"
    }
    if (role === "support") {
        if (thissession.config) {
            thissession.support = cred;
            thissession.authorized = 2

            if (thissession.client) thissession.client.sock.close();
            thissession.client = null;
            msg += "... ADDED"
        } else {
            msg += "  NOT added as Config (Java) is not running"
        }
    }
    if (role === "client") {
        // lastly add client
        if (thissession.config && thissession.support) {
            thissession.client = cred;
            thissession.authorized = 3
            msg += "... ADDED"
        }
        else {
            msg += "  NOT added as Config or Support are not running"
        }
    }
    //*/

    pub(msg)
    return cred;
}

function removeFromSession(cred) {

    return //******
}

function toExchange(me, data) {
    // using byIP, addIP
    // pub(JSON.stringify(me));
    let msg = "toEx from " + me.role + " to "

    // let thissession = tstsessionList[me.sessionID] // test version

    let thissession = sessionList[me.ip] // if role = config or support

    if (me.role === 'client') thissession = sessionList[me.sessionID]

    // use 'd' in mqtt to dump these messages
    if (me.role === 'client') clientmsg += data
    if (me.role === 'support') supportmsg += data
    if (me.role === 'config') configmsg += data // this is also used to collect data and send them off togeether

    try {

        if (me.role === "config") {
            msg += "client"

            if (thissession.client) thissession.client.sock.send("" + data);
        }
        if (me.role === "support") {

            if (data.substring(0, 2) === '?r') { // authorize connecting to this client
                let id = data.substring(2, 10)
                if (clientList[id]) {
                    pub('!!! supports entry matches clients >' + id + '<')
                    clientList[id].cred.sessionID = me.ip // now the client has the sessionID
                    sessionList[me.ip].client = clientList[id].cred // set the client's credentials
                    sessionList[me.ip].accessCode = id // set the client's credentials
                    me.sock.send('?ECode Accepted - Client on-line')
                } else {
                    pub('NOT MATCHED' + id)
                    sessionList[me.ip].client = null // set the client's credentials
                    me.sock.send('?EWrong Access Code')
                }
                return;
            }

            msg += "client"
            if (thissession.client) thissession.client.sock.send("" + data);
        }
        if (me.role === "client") {
            msg += "config+support"
            // below should work but 'T' data do not get processed properly by config for whatever reason

            if (thissession.config) thissession.config.sock.write(""+data)
            if (thissession.support) thissession.support.sock.send("" + data)

            /* collect all client data until 'Z' before sending on
            if (data.substring(0, 1) === 'Z') {

                // if (thissession.config) thissession.config.sock.write(""+clientmsg)
                if (thissession.config) {
                    // pacedSend(, ""+clientmsg)

                    //* mod to send < max bytes at a time
                    // with delay 
                    let i = 0;
                    let j = clientmsg.length;
                    let max = 20;
                    const epoch = 80; //msec per message
                    while (i < j) {
                        let k = (j - i) < max ? (j - i) : max;
                        let ss = clientmsg.substr(i, k);

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
                            if (debug > 1) pub("==== " + defer + " |" + ss + "|====");
                            thissession.config.sock.write(ss)
                        }, defer);

                        i = i + k;
                    }

                } else {
                    pub('***none sent to config')
                    return
                }
                

                if (debug > 1) pub('\n\nclient sends ' + clientmsg.length + ' |' + clientmsg + '|\n')
                clientmsg = ''
            }//*/

        }

        if (debug > 1) msg += "\n   >" + data + "<"

        if (debug > 1) pub(msg)



    } catch (e) {
        pub('error Sending ...\n' + msg + ' ' + e);
        pub('\nerror: ' + msg)
    }
}

// Create a server instance, and chain the listen function to it
// The function passed to net.createServer() becomes the event handler for the 'connection' event
// The sock object the callback function receives UNIQUE for each connection
net.createServer(function (sock) {
    // We have a connection - a socket object is assigned to the connection automatically
    let addr = sock.remoteAddress + ":" + sock.remotePort;
    pub('TCP connected: ' + addr);

    let myCred = addToSession(sock.remoteAddress, addr, sock, 'config');

    // Add a 'data' event handler to this instance of socket
    sock.on('data', function (data) {
        // pub('[' + getDateTime() + '] tcp (' + addr + ') >' + data + '<');
        pub('recd from tcp (' + addr + ') >' + data + '<');

        //* if Config sends us a V, we responds w version number right away, since Client is likely not ready yet
        if (data.includes('V')) {
            sock.write('V1.04Z');
            pub("-- server replies V to Config");
        }
        //*/

        toExchange(myCred, data);

    });
    // Add a 'close' event handler to this instance of socket
    sock.on('close', function (data) {
        pub('CLOSED: ' + addr);
        removeFromSession(myCred)
    });

}).listen(TCP_PORT, HOST);


// web socket
// https://github.com/websockets/ws#sending-and-receiving-text-data

function noop() { }

function heartbeat() {
    this.isAlive = true;
}

const WebSocket = require('ws');

let clientws = null;
let supportws = null;

resetAll()

function resetAll() {
    pub("RESET ALL")
    if (clientws) clientws.close()
    if (supportws) supportws.close()

    clientws = new WebSocket.Server({
        port: CLIENT_WS_PORT
    });
    supportws = new WebSocket.Server({
        port: SUPPORT_WS_PORT
    });


    clientws.on('connection', function connection(ws, req) {
        const addr = req.socket.remoteAddress;
        pub("client ws connected " + addr);

        let ip = addr.substr(7);
        let myCred = addToSession(ip, addr, ws, 'client');

        ws.isAlive = true;
        ws.on('pong', heartbeat);
        ws.on('message', function incoming(message) {

            if (debug > 1)
                pub('client ws (' + addr + ') >' + message + '<');

            toExchange(myCred, message)

        });
        //ws.send('something');
    });

    var interval = setInterval(function ping() {
        clientws.clients.forEach(function each(ws) {
            if (ws.isAlive === false) {
                pub("client NOT alive -- terminating ...")
                return ws.terminate()
            }
            ws.isAlive = false;
            ws.ping(noop);
        });
    }, 30000);

    clientws.on('close', function close() {
        clearInterval(interval);
    });


    supportws.on('connection', function connection(ws, req) {
        const addr = req.socket.remoteAddress;
        pub("supportws connected " + addr);

        let ip = addr.substr(7);
        let myCred = addToSession(ip, addr, ws, 'support')

        ws.isAlive = true;
        ws.on('pong', heartbeat);
        ws.on('message', function incoming(message) {
            if (debug > 1) pub('support ws (' + addr + ') >' + message + '<');

            toExchange(myCred, message)

        });
    });

    var supportinterval = setInterval(function ping() {
        supportws.clients.forEach(function each(ws) {
            if (ws.isAlive === false) return ws.terminate();
            ws.isAlive = false;
            ws.ping(noop);
        });
    }, 3000);

    supportws.on('close', function close() {
        clearInterval(supportinterval);
    });
}

function pacedSend(dev, data) {
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
            if (debug > 1) pub("==== " + defer + " |" + ss + "|====");
            dev(ss)
        }, defer);

        i = i + k;
    }
}
