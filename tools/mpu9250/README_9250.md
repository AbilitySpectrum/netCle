# Working the MPU9250

### The MPU9250 

the MPU9250 is connected to the SCL, SDA, VCC and GND pins of the Leonardo

### The Arduino code

#### There are  two options:
1. the firmware masquerades as a Sensact and outputs (its only function) the yaw, pitch, roll data in the fields of Gyro-X, Y and Z
   The data can thus be visualized using "senviz" -- though for display only.
   Because of space constraint, there are no other Sensact functions on board.
   
2. when OUTPUT_FOR_VISUALIZER is defined, then the device outputs a 14-byte data stream
	and can be visualized using `Visualizer/main/index.html`

	When used this way, however, the browser code requires the app `P5.serialcontrol` to be running in the background.

	`P5.serialcontrol` for different platforms can be downloaded from https://github.com/p5-serial/p5.serialcontrol/releases/tag/0.1.2
