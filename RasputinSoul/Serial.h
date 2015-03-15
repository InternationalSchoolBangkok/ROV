/* 
 * File:   Serial.h
 * Author: Nicro
 *
 * Created on March 14, 2015, 6:16 PM
 */
#include <cstdlib>
#include <stdio.h>
#include <unistd.h>
#include <iostream>


#include <termios.h>
#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include "Thread.h"

#ifndef SERIAL_H
#define	SERIAL_H

class Serial : public Thread {
private:
    char toArdee[60];
    char fromArdee[60];
    int dataWidth;
    int uart0_filestream;
    int set_interface_attribs(int fd, int speed, int parity);
    void set_blocking(int fd, int should_block);
    int searchForCharSeq(char[], char[], int, int, int);
    void sendReceive();
    void *run();
public:
    bool spew(unsigned char[], int);
    void set(int channel, char value);
    char get(int channel);
    Serial(int dataWidth);
};
#endif	/* MAIN_H */

