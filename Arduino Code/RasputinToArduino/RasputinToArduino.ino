//compile time settings
#include <Servo.h>
#define printUpdateFreq 0
#define debugInput 1
#define motorNumber 8
//KEEP IN MIND THAT IT TAKES LOTS OF TIME TO SERIAL.PRINT 

Servo mots[motorNumber];
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  for (int i = 2; i < motorNumber; i++) {
    mots[i].attach(i); //attach motor i in array to to pin i
  }
}
char inBytes[64];
char headerSeq[] = "8=D";
int i=0;
int header = 0;

unsigned long loopCounter = 0;
unsigned long lastLoop = 0;
unsigned long startTime = 0;
void loop() {
  // put your main code here, to run repeatedly: 
  while(Serial.available()>0){
    inBytes[i] = Serial.read();
    boolean sameSame = false;
    if(i>1){
      sameSame = true;
      for(int b=i-3;b<i;b++){
        /*Serial.print("IB: ");
         Serial.write(inBytes[b+1]);
         Serial.print("HS: ");
         Serial.write(headerSeq[b+3-i]);
         Serial.println();*/
        if(inBytes[b+1]!=headerSeq[b+3-i]){
          sameSame = false;
        }
      }
    } 
    if(sameSame){
      if(header == 0){//if it's the first header
        clearArray(inBytes);
        i=-1;
        header++;
        //Serial.println("first valid header found");
      }
      else{//otherwise reset the buffer and parse the data between the 2 headers
        header = 0;//reset the header counter
        i=-1;
        parse(inBytes);
      }    
    }
    i++;
  }
  if (loopCounter-lastLoop == 100000) {
    int freq = 100L*(1000000000L / (micros() - startTime));
#if printUpdateFreq
    Serial.print("Update Frequency: ");
    Serial.println(freq);
#endif
    startTime = micros();
    lastLoop = loopCounter;
  }
  loopCounter++;
}
void parse(char bytes[]){
#if debugInput 
  Serial.println("8=DFull Message From Raspi:");
  for(int i=0;i<32;i++){
    Serial.write(bytes[i]);
  }
  Serial.println();
  //Serial.println("8=DLoL8=D");
#endif
  int i = 0; //i is the channel incrementor and is not to be fucked with
  for(i=0;i<motorNumber;i++){
    char motAsServo = map(bytes[i], -128, 127, 0, 180);//map receiver bytes
#if debugInput 
    /*Serial.print("Motor: ");
     Serial.print(i+1,DEC);
     Serial.print(" Value: ");
     Serial.print(bytes[i],DEC);
     Serial.println();*/
#endif
    mots[i+2].write(motAsServo); //set servos on pins 2-9 to correct vals.
  }
  //using the channel incrementor write to the rest of the channels here 
  //i.e. if(bytes[8]==127) then set lights to high
}
void clearArray(char array[]){
  for(int i=0;i<64;i++){
    array[i] = '/0';
  }
}










