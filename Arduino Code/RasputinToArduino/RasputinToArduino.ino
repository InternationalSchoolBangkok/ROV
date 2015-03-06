void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
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
      for(int b=i-3;b<i;b++){
        sameSame = true;
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
    else{
      sameSame = false;
    }
    if(sameSame){
      if(header == 0){//if it's the first header
        clearArray(inBytes);
        i=-1;
        header++;
        Serial.println("first valid header found");
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
    Serial.print("Update Frequency: ");
    Serial.println(freq);
    startTime = micros();
    lastLoop = loopCounter;
  }
  loopCounter++;
}
void parse(char bytes[]){
  Serial.println("parsing:");
  for(int i=0;i<64;i++){
    Serial.write(bytes[i]);
  }
}
void clearArray(char array[]){
  for(int i=0;i<64;i++){
    array[i] = '/0';
  }
}




