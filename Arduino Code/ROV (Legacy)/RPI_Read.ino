import serial
ser = serial.Serial('/dev/ttyAMA0', 9600, timeout=1)
ser.open()

ser.write("testing")
try:
        while 1:
                response = ser.readline()
                print response
except KeyboardInterrupt:
        ser.close()
/*
Written by Nico Ramirez
This file reads from RPI
*/
#define myBufferLength 500
char* rpiSearchStrings[] = {"MO1:", "MO2:", "MO3:", "MO4:", "MO5:", "MO6:", "MO7:", "MO8:",
                            "ARM:", "LHT:", "LSR:", "EX1:", "EX1:", "EX1:", "EX1:"
                           };
unsigned long lastRPIUpdate = 0;
char myBuffer[myBufferLength];
int bufferCursor = 0;

void readDataFromRPI(char rpiChannels[]) {
  while (Serial.available() > 0) {
    if (bufferCursor == myBufferLength) {
      parseMyBuffer();
      for (int i = 0; i < myBufferLength; i++) {
        myBuffer[i] = -1;
      }
      bufferCursor = 0;
    } else {
      myBuffer[bufferCursor] = Serial.read();
      bufferCursor++;
    }
  }
}
void parseMyBuffer() {
  
}
