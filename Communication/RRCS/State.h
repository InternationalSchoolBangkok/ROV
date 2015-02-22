/* 
 * File:   State.h
 * Author: kolatat
 *
 * Created on February 22, 2015, 4:50 PM
 */

#ifndef STATE_H
#define	STATE_H

#include <cstdlib>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <pthread.h>
#include <vector>

typedef struct sockaddr_in InetAddress;

class State;

typedef struct {
    State* obj;
    void* data;
} StateThreadData;

class State {
public:
    State(int datawidth, int ROVPort, int clientPort);
    virtual ~State();

    void startTXRX();
    void stopTXRX();

    void set(int channel, int value);
    int get(int channel);

private:
    const int datawidth, ROVPort, clientPort;
    int sockfd;
    InetAddress ROVAddress;
    int8_t *clientState, *ROVState;
    bool doTXRX;

    pthread_t TXRXThread;

    void TXRX();
    static void *staticTXRX(void *args);
};

#endif	/* STATE_H */

