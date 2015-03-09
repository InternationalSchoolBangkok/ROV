/* 
 * File:   SampleUsage.cpp
 * Author: nicboi
 *
 * Created on March 7, 2015, 4:28 PM
 */

#include <cstdlib>
#include <iostream>
#include <cstring>

#include "Serial.h"

using namespace std;

int main(int argc, char** argv) {
    Serial serial = Serial("/dev/ttyACM0", 9600, 32);
    int updateRate;
    char *updateRateArg = argv[1];
    if (updateRateArg == NULL) {
        updateRate = 500000;
    } else {
        updateRate = atoi(((string) updateRateArg).c_str());
    }
    while (true) {
        //doke so the 'send' unsigned char array is where the vals for ROV channels go
        unsigned char send[] = "abcdefghijklmnopqrstuvwxyzabcdef";
        //call serial.spew to spew out your array
        serial.spew(send, 32); 
        //to getSpew from ardee just reference an array of at least 32 length
        unsigned char hue[33];
        hue[32]='\0'; //dis just to make it print nicer - array could be 32 long and still work
        serial.getSpew(hue);
        cout<<"GET SPEW:\n "<<hue<<endl;
        usleep(updateRate);
    }
    //when serial no longer needed close the connection)
    serial.closeSerial();
    return 0;
}
