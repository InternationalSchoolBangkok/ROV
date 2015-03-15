#include <Servo.h>

#define dataWidth 32
#define motorNumber 8

Servo mots[motorNumber];

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial1.begin(9600);
  for (int i = 2; i < motorNumber; i++) {
    mots[i].attach(i); //attach motor i in array to to pin i
  }
  pinMode(13,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available()>=dataWidth+4){
    byte in[dataWidth+4];
    for(int i=0;i<dataWidth+4;i++){
      in[i] = Serial.read();
    }
    delay(5);//wait for any accidental extra data leaving ~15ms to next BC
    while(Serial.available()>0){
      Serial.read();//read away remaining data
    }

    //respond to raspi with 32 data and a \n
    for(int i=0;i<dataWidth;i++){
      Serial.write(random(65,90));
    }
    Serial.write('8');
    Serial.write('=');
    Serial.write('D');
    Serial.write('\n');
    //end of og response

      parse((char*)in);//possible weird behavior due to cast
    //debug using Serial1
    //Serial1.println("DS");
    for(int i=0;i<dataWidth+4;i++){
      //Serial1.print(in[i],DEC); //debug
      //Serial1.print(" "); //debug
    }
    //Serial1.println();
    //end of debug
  }
}

void parse(char bytes[]){
  //using the channel incrementor write to the rest of the channels here 
  //i.e. if(bytes[8]==127) then set lights to high
  int i = 0; //i is the channel incrementor and is not to be fucked with
  for(i=0;i<motorNumber;i++){
    int motAsServo = map(bytes[i], -127, 127, 0, 180);//map receiver bytes
    mots[i+2].write(motAsServo); //set servos on pins 2-9 to correct vals.
  }
  for(int i=0;i<6;i++){
    //Serial1.print(bytes[i],DEC);
  }
  //Serial1.println(bytes[0],DEC);
  if(bytes[0]>50){
    digitalWrite(13,HIGH);
  }
  else{
    digitalWrite(13,LOW);
  }
}
//0bcdefghijklmnopqrstuvwxyzabcdef8=D 







