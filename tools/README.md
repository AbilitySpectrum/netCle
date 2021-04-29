# senviz

This contains some of my experimental code for working with Sensact/netClé.

Basic features:-

- set hub to Run, Idle, Report 
- in Report, all the sensors data are displayed in meter style
- in Settings, 
	- Move - re-arrange the meters on the canvas
	- Store: save the arrangement to local storage
	- Restore: restore the arrangement
	- Get triggers: Get the config from the physical hub. The configuration is pasted in the `scratch pad` and also decoded in the right hand panel
	- Send triggers: sends the config (in the `scratch pad`) to the hub
	- Choose a config file on the computer and send it to the hub


### caveat emptor - do not look under the hood!
Under the hood, I have buried a number of disparate beta applications, including `client` code which talks to a server, so that the hub could be remotely configured by someone running the `technician` code.

I have also buried `runcore()` etc which is an early attempt to execute Sensact function in javascript, using the hub's sensor data. In future, it may be that the Sensact core could run in javascript, using higher level descriptors for the input conditions and output actions.

These are all here in a jumble because I have come to rely on having the data on display -- the basic function of senviz.

# mpu9250

Code for Arduino to get yaw/pitch/roll data out of MPU9250.

One version can be used in connection with `senviz` 
The data are visualize in the meters for gyro x, y and z.

A second version requires P5.serialcontrol to be running.
The data are visualized as a tilting cube.