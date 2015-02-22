/* 
 * File:   State.cpp
 * Author: kolatat
 * 
 * Created on February 22, 2015, 4:50 PM
 */

#include "State.h"

State::State(int datawidth, int ROVPort, int clientPort) : datawidth(datawidth), ROVPort(ROVPort), clientPort(clientPort) {

    clientState = new int8_t[datawidth];
    ROVState = new int8_t[datawidth];

    if ((sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) {
        // socket error
    }

    bzero((char *) &ROVAddress, sizeof (ROVAddress));

    ROVAddress.sin_addr.s_addr = INADDR_ANY;
    ROVAddress.sin_family = AF_INET;
    ROVAddress.sin_port = htons(ROVPort);

    if (bind(sockfd, (struct sockaddr*) &ROVAddress, sizeof (ROVAddress)) < 0) {
        // binding error
    }
}

State::~State() {
    delete[](clientState);
    delete[](ROVState);
}

void State::set(int channel, int value) {
    ROVState[channel] = value;
}

int State::get(int channel) {
    return clientState[channel];
}

void State::startTXRX() {
    StateThreadData *threadData = new StateThreadData();
    threadData->obj = this;
    if (pthread_create(&TXRXThread, NULL, staticTXRX, threadData) < 0) {
        // error starting TXRX thread
    }
}

void State::stopTXRX() {
    doTXRX = false;
    pthread_cancel(TXRXThread);
}

void State::TXRX() {
    doTXRX = true;
    int8_t buffer[datawidth];
    InetAddress clientAddr;
    socklen_t addrLen = sizeof (clientAddr);
    while (doTXRX) {
        if (recvfrom(sockfd, buffer, datawidth, 0, (struct sockaddr*) &clientAddr, &addrLen) < 0) {
            // error receiving
        }
        memcpy(clientState, buffer, datawidth);
        memcpy(buffer, ROVState, datawidth);
        if (sendto(sockfd, buffer, datawidth, 0, (struct sockaddr*) &clientAddr, addrLen) < 0) {
            // error sending
        }
    }
    pthread_exit(NULL);
}

void* State::staticTXRX(void* args) {
    StateThreadData* tData = static_cast<StateThreadData*> (args);
    tData->obj->TXRX();
    delete tData;
    return NULL;
}
