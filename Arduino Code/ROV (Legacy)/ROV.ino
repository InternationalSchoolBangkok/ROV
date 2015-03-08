/*
Written by Nico Ramirez
below is an example output string from the ardu imu
!!!VER:1.9,RLL:-15.81,PCH:-47.43,YAW:109.61,IMUH:253,TOW:0***
*/
#include <Servo.h>
#define imuChannelNumber 3  //how many channels to be parsed from the IMU
#define rpiChannelNumber 32  //how many channels incoming from the rpi
#define motorNumber 8
#define sendBackChannelNumber 3  //how many channels to send back to the rasputin
//anything > 0 = enable
#define DEBUG_IMU 0
#define DEBUG_RPI 0
#define DEBUG_TO_RPI 0
#define DISPLAY_MAIN_FREQ 1
//global variables
char motorPower[motorNumber];
Servo mots[motorNumber];
unsigned long loopCounter = 0;
unsigned long lastLoop = 0;
//globals end here
void setup() {
  //initialize all serial coms
  Serial.begin(38400);  //this line is strictly for debugging
  Serial1.begin(38400); //this line is for the arduimu
  //attach motors 1-8 to pins 2-9
  for (int i = 2; i < motorNumber; i++) {
    mots[i].attach(i);
  }
}
//main program loop - NO BLOCKING FUNCTIONS
/******************************************/
void loop() {
  unsigned long startTime = micros();
  //char imuChannels[imuChannelNumber];
  char rpiChannels[rpiChannelNumber];
  //readDataFromIMU(imuChannels);
  readDataFromRPI(rpiChannels);
  //calc motor values
  updateMotors();
  //sendDataToPi(imuChannels);
  //to ensure main loop is running at decent speed (>100hz)
#if DISPLAY_MAIN_FREQ
  if (loopCounter-lastLoop == 1000) {
    int freq = 1000000 / (micros() - startTime);
    Serial.print("Update Frequency: ");
    Serial.println(freq);
    lastLoop = loopCounter;
  }
#endif
loopCounter++;
}
//end of main loop
/******************************************/
void updateMotors() {
  for (int i = 0; i < motorNumber; i++) {
    int motAsServo = map(motorPower[i], -128, 127, 0, 180);
    mots[i].write(motAsServo);
  }
}
char* keyStrings[sendBackChannelNumber] = {"RLL:", "PCH:", "YAW:"};
void sendDataToPi(char imuChannels[]) {
  //!!RASP:,RLL:-15.81,PCH:-47.43,YAW:109.61,**
  String toRasputin = "!!RASP:,";
  for (int i = 0; i < sendBackChannelNumber; i++) {
    toRasputin = toRasputin + keyStrings[i] + ',' + (int)imuChannels[i] + ' ';
  }
  toRasputin = toRasputin + "**";
#if DEBUG_TO_RPI
  Serial.println(toRasputin);
#endif
}



