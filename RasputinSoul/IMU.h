/* 
 * File:   IMU.h
 * Author: Nicro ramicaza
 *
 * Created on March 7, 2015, 5:51 PM
 */

#ifndef IMU_H
#define	IMU_H

#include <cstdlib>
#include <iostream>

#include <stdio.h>
#include <unistd.h>     //Used for UART
#include <fcntl.h>      //Used for UART
#include <termios.h>    //Used for UART
#include <pthread.h>
#include <ctime>
#include <cstring>
#include <string>

using namespace std;

class IMU;

typedef struct {
    IMU* obj;
    void* data;
} LolStructIMU;

class IMU {
private:
    double initTime; 
    unsigned char headerSeq[3];
    int uart0_filestream;
    struct termios options;
    const char *device;
    unsigned char fromIMU[100];
    unsigned char tempBuffer[512];
    int channels;
    void tableUpdater();
    static void *staticUpdater(void *args);
    void appendBuffers(unsigned char[],int,unsigned char[],int);
    void clearUnsignedCharArray(unsigned char[],int);
    int searchForCharSeq(unsigned char[],unsigned char[],int,int,int);
    pthread_t tableUpdaterThread;
    bool doUpdate;
    int cursor;
    float roll;
    float pitch;
    float yaw;
public:
    IMU(int);
    bool spew(unsigned char[],int);
    void printSerialBuffer();
    void closeSerial();
    void getSpew(unsigned char[]);
    float getRoll();
    float getPitch();
    float getYaw();
};

#endif	/* IMU_H */

