/* 
 * File:   main.cpp
 * Author: admin
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
    //cli stuffs
    char* sendArg = argv[1];
    if (sendArg == NULL) {
        unsigned char send[16] = "8=DHUEHUEHUE8=D";
        //serial.spew(send, 32);
    } else {
        //temp just for testing with CLI
        string meatString = string(sendArg);
        string startSeq = "8=D";
        string bigString = (startSeq).append(meatString).append(startSeq);
        unsigned char finalArray[32];
        strncpy((char*) finalArray, bigString.c_str(), 32);
        //serial.spew(finalArray, 32); //enter -1 to send full length of string
        //hue
    }
    while (true) {
        usleep(atoi(((string) argv[2]).c_str()));
        //unsigned char hues[33];
        //serial.getSpew(hues);
        //cout << hues << endl;
        unsigned char send[32] = "8=DOhhiden8888iggajgdrgjkiyt8=D";
        serial.spew(send, 31); //weird bug where if you send > 32 bytes you mor gg den ogbuda
    }
    serial.closeSerial();
    return 0;
}
