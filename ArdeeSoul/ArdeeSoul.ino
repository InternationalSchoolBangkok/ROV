#include <Servo.h>

#define dataWidth 32
#define motorNumber 8
int motorMap[8] = {6, 7, 9, 8, 3, 4, 2, 5}; //map to figure out which pin goes to which motor
bool reverseMap[8] = {true, false, false, false, false, false, true, false}; //map to find which motors to revers
Servo mots[motorNumber];
Servo arm;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial1.begin(9600);
  analogReference(INTERNAL1V1);
  for (int i = 0; i < motorNumber; i++) {
    mots[i].attach(motorMap[i]); //attach motor i in array to to pin i
    mots[i].write(90);
  }
  arm.attach(10);
}
int sensorCount = 0;
long sensorSum = 0;
float depth;
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
    byte bytes[3];
    memcpy(bytes, (byte *)&depth, 4);
    for (int i = 0; i < 4; i++) {
      Serial.write(bytes[i]);
    }
    for (int i = 0; i < dataWidth - 4; i++) {
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
  //pressure sensor stuff
  sensorSum += analogRead(A0);
  sensorCount++;
  if (sensorCount == 100) {
    float avSensor = (float)sensorSum / (float)sensorCount;
    float voltage = avSensor * 0.0010742188F;
    float pressure = voltage * 111.11F - 22.22f;
    depth = pressure / 9.741F;
    sensorSum = 0;
    sensorCount = 0;
  }
}

void parse(char bytes[]) {
  //using the channel incrementor write to the rest of the channels here
  //i.e. if(bytes[8]==127) then set lights to high
  int i = 0; //i is the channel incrementor and is not to be fucked with
  for (i = 0; i < motorNumber; i++) {
    setMotor(i, bytes[i]);
  }
  if(bytes[8]>0){
    arm.write(1000);//open
  }else if(bytes[8]<0){
    arm.write(2000);//close
  }else{
    arm.write(1500);//nuetral
  }
}
void setMotor(int motorIndex, int value) {
  value = (reverseMap[motorIndex] == true) ? -value : value;
  int motAsServo = map(value, -381, 381, 0, 175);//map receiver bytes should by -127 to 127 but this too fast
  mots[motorIndex].write(motAsServo); //set servos on pins 2-9 to correct vals.
  //Serial.print("MOT: ");
  //Serial.println(motAsServo, DEC);
}
//0bcdefghijklmnopqrstuvwxyzabcdef8=D









