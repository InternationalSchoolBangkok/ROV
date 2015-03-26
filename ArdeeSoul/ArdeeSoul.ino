#include <Servo.h>

#define dataWidth 32
#define motorNumber 8
int motorMap[8] = {8, 4, 9, 6, 5, 7, 2, 3}; //map to figure out which pin goes to which motor
bool reverseMap[8] = {true, false, true, false, false, false, true, true}; //map to find which motors to revers
Servo mots[motorNumber];

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial1.begin(9600);
  for (int i = 0; i < motorNumber; i++) {
    mots[i].attach(motorMap[i]); //attach motor i in array to to pin i
    mots[i].write(90);
  }
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available() >= dataWidth + 4) {
    byte in[dataWidth + 4];
    for (int i = 0; i < dataWidth + 4; i++) {
      in[i] = Serial.read();
    }
    delay(5);//wait for any accidental extra data leaving ~15ms to next BC
    while (Serial.available() > 0) {
      Serial.read();//read away remaining data
    }

    //respond to raspi with 32 data and a \n
    for (int i = 0; i < dataWidth; i++) {
      Serial.write(random(65, 90));
    }
    Serial.write('8');
    Serial.write('=');
    Serial.write('D');
    Serial.write('\n');
    //end of og response
    parse((char*)in);//possible weird behavior due to cast
    //debug using Serial1
    /*for(int i=0;i<dataWidth+4;i++){
      Serial1.write(in[i]); //debug
    }*/
    //end of debug
  }
}

void parse(char bytes[]) {
  //using the channel incrementor write to the rest of the channels here
  //i.e. if(bytes[8]==127) then set lights to high
  int i = 0; //i is the channel incrementor and is not to be fucked with
  for (i = 0; i < motorNumber; i++) {
    setMotor(i, bytes[i]);
  }
  //setMotor(7,bytes[1]);//to test if they pull too much I alone 
}
void setMotor(int motorIndex, int value) {
  value = (reverseMap[motorIndex] == true) ? -value : value;
  int motAsServo = map(value, -381, 381, 0, 175);//map receiver bytes should by -127 to 127 but this too fast
  mots[motorIndex].write(motAsServo); //set servos on pins 2-9 to correct vals.
  //Serial.print("MOT: ");
  //Serial.println(motAsServo, DEC);
}
//0bcdefghijklmnopqrstuvwxyzabcdef8=D









