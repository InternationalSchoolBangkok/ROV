/* 
 * File:   Serial.h
 * Author: Nicro ramicaza
 *
 * Created on March 7, 2015, 5:51 PM
 */

#ifndef SERIAL_H
#define	SERIAL_H

#include <cstdlib>
#include <iostream>

#include <stdio.h>
#include <unistd.h>			//Used for UART
#include <fcntl.h>			//Used for UART
#include <termios.h>		//Used for UART
#include <pthread.h>
#include <ctime>

using namespace std;

class Serial;

typedef struct {
    Serial* obj;
    void* data;
} LolStruct;

class Serial {
private:
    double initTime; 
    unsigned char headerSeq[3];
    int uart0_filestream;
    struct termios options;
    const char *device;
    unsigned char fromArduino[32];
    unsigned char tempBuffer[512];
    void parse(string);
    int channels;
    void tableUpdater();
    static void *staticUpdater(void *args);
    void appendBuffers(unsigned char[],int,unsigned char[],int);
    void clearUnsignedCharArray(unsigned char[],int);
    int searchForCharSeq(unsigned char[],unsigned char[],int,int,int);
    pthread_t tableUpdaterThread;
    bool doUpdate;
    int cursor;
public:
    Serial(string, int, int);
    bool spew(unsigned char[],int);
    void printSerialBuffer();
    void closeSerial();
    void getSpew(unsigned char[]);
};

#endif	/* SERIAL_H */

