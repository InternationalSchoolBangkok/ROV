/*
Written by Nico Ramirez
This file reads from the ARDU IMU which tells us the ROVs attitude
*/
char* searchStrings[] = {"RLL:", "PCH:", "YAW:"};

unsigned long lastUpdate = 0;

void readDataFromIMU(char imuChannels[]) {
  if (Serial1.available() >= 63) {
    char data1[7], data2[7], data3[7];
    char* dataStrings[3] = {data1, data2, data3};
    for (int i = 0; i < imuChannelNumber; i++) {
      //for each search string
      if (Serial1.find(searchStrings[i])) {
        char chars[7] = "      ";//fill array with empty space chars so it parses better
        for (int i2 = 0; i2 < 7; i2++) {
          char temp = Serial1.read();
          if (temp == ',') {
            break;
          } else {
            chars[i2] = temp;
          }
        }
        strncpy(dataStrings[i], chars, 7);
      }
      float tempFloat = atof(dataStrings[i]);
      tempFloat = constrain(tempFloat, -180, 180);
      imuChannels[i] = (char)map(tempFloat, -180, 180, -128, 127);
    }
#if DEBUG_IMU
    Serial.print((String)searchStrings[0]);
    Serial.print((int)imuChannels[0]);
    for (int i = 1; i < sizeof(searchStrings); i++) {
      Serial.print(" ");
      Serial.print((String)searchStrings[i]);
      Serial.print((int)imuChannels[i]);
    }
    Serial.print(" Update Hz: ");
    Serial.print(1000000 / (micros() - lastUpdate));
    Serial.println();
    lastUpdate = micros();
#endif
  }
}
