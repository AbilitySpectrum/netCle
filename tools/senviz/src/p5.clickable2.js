//Determines if the mouse was pressed on the previous frame
var cl_mouseWasPressed = false;
//Last hovered button
var cl_lastHovered = null;
//Last pressed button
var cl_lastClicked = null;
//All created buttons
var cl_clickables = [];

//This function is what makes the magic happen and should be ran after
//each draw cycle.
p5.prototype.runGUI = function () {
	for (i = 0; i < cl_clickables.length; ++i) {
		if (cl_lastHovered != cl_clickables[i])
			cl_clickables[i].onOutside();
	}
	if (cl_lastHovered != null) {
		if (cl_lastClicked != cl_lastHovered) {
			cl_lastHovered.onHover();
		}
	}
	if (!cl_mouseWasPressed && cl_lastClicked != null) {
		cl_lastClicked.onPress();
	}
	if (cl_mouseWasPressed && !mouseIsPressed && cl_lastClicked != null) {
		if (cl_lastClicked == cl_lastHovered) {
			cl_lastClicked.onRelease();
		}
		cl_lastClicked = null;
	}
	cl_lastHovered = null;
	cl_mouseWasPressed = mouseIsPressed;
}

p5.prototype.registerMethod('post', p5.prototype.runGUI);

//Button Class
function Clickable(x, y) {
	this.x = x || 0; //X position of the clickable
	this.y = y || 0; //Y position of the clickable
	this.width = 25; //Width of the clickable
	this.height = 25; //Height of the clickable
	this.color = "#FFFFFF"; //Background color of the clickable
	this.cornerRadius = 10; //Corner radius of the clickable
	this.strokeWeight = 1; //Stroke width of the clickable
	this.stroke = "#F0F0F0"; //Border color of the clickable
	this.text = "Press Me"; //Text of the clickable
	this.textColor = color(120, 120, 250); // "#000000"; //Color for the text shown
	this.textSize = 16; //Size for the text shown
	this.textFont = "sans-serif"; //Font for the text shown	

	this.base_mode = true;
	this.meter_mode = false;
	this.button_mode = false;
	this.swivel_mode = false;
	this.go_mode = false;
	this.value = 0; // draw2
	this.lower_value = 0;
	this.upper_value = 1024;
	this.hidden = false;
	this.diffx = 0;
	this.diffy = 0;

	this.onHover = function () {
		//This function is ran when the clickable is hovered but not
		//pressed.
	}

	this.onOutside = function () {
		//This function is ran when the clickable is NOT hovered.
	}

	this.onPress = function () {
		//This function is ran when the clickable is pressed.
	}

	this.onRelease = function () {
		//This funcion is ran when the cursor was pressed and then
		//released inside the clickable. If it was pressed inside and
		//then released outside this won't work.
	}

	this.locate = function (x, y) {
		this.x = x;
		this.y = y;
	}

	this.resize = function (w, h) {
		this.width = w;
		this.height = h;
	}

	// 2020-05-21 Thu : ylh
	this.bordercolor = function (v) {
		this.stroke = v;
	}
	this.reading = function (v) {
		this.value = v;
	}
	this.range = function (v) {
		this.upper_value = v;
	}


	this.draw2 = function () {
		if (this.pressed && inMotion) {
			this.x = mouseX - this.diffx;
			this.y = mouseY - this.diffy;
		}
		if (this.meter_mode) this.draw_meter();
		if (this.button_mode) this.draw_button();
		if (this.swivel_mode) this.draw_swivel();
	}

	this.draw_meter = function () {
		// show value and on dial
		let x = this.x - 2;
		let y = this.y + this.height + 2;

		textSize(this.height * 0.16);
		strokeWeight(1);
		fill(this.textColor);
		text(this.value, this.x + this.width * 0.45, this.y + this.height * 0.8);

		// dial
		x = this.x + this.height / 2;
		y = this.y + this.height / 2;
		noFill();
		stroke(color(250, 220, 220));
		strokeWeight(this.width * 0.14);
		arc(x, y, this.width * 0.7, this.height * 0.7, Math.PI * 0.8, Math.PI * 0.2);
		strokeWeight(this.width * .1);
		stroke(color(120, 120, 250));
		let d = (this.value / this.upper_value) * Math.PI * 1.18 + Math.PI * 0.81;
		d = d > Math.PI * 2 ? d - Math.PI * 2 : d;
		d = d > Math.PI * 2 ? d - Math.PI * 2 : d;
		arc(x, y, this.width * 0.7, this.height * 0.7, Math.PI * 0.8, d);
	}
	this.draw_button = function () {}
	this.draw_swivel = function () {
		// show value and on dial
		let x = this.x + this.width / 2;
		let y = this.y + this.height / 2;

		let len = this.width;
		let angle = radians(this.value / this.upper_value) * 180;
		push();
		strokeWeight(0);
		fill(this.color);
		ellipse(x, y, len, len);
		stroke(100, 100, 255);
		strokeWeight(2);
		translate(x, y);
		rotate(angle);
		line(-len / 2, 0, len / 2, 0);
		pop();
	}

	this.draw = function () {
		if (this.hidden) return;
		if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
			cl_lastHovered = this;
			if (mouseIsPressed && !cl_mouseWasPressed)
				cl_lastClicked = this;
		}
		
		if (this.base_mode) {
			fill(this.color);
			stroke(this.stroke);
			strokeWeight(this.strokeWeight);
			rect(this.x, this.y, this.width, this.height, this.cornerRadius);
		}
		
		this.draw2();

		fill(this.textColor);
		noStroke();
		textAlign(CENTER, CENTER);
		textSize(this.textSize);
		textFont(this.textFont);
		text(this.text, this.x + 1, this.y + 1, this.width, this.height);
	}

	cl_clickables.push(this);
}