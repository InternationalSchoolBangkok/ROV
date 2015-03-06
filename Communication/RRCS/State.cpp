/* 
 * File:   State.cpp
 * Author: kolatat
 * 
 * Created on February 22, 2015, 4:50 PM
 */

#include "State.h"

void Kola::log(std::string src, std::string msg) {
    time_t now = time(0);
    tm *lctm = localtime(&now);
    printf("%04d-%02d-%02d %02d:%02d:%02d [%s] %s\n",
            1900 + lctm->tm_year,
            1 + lctm->tm_mon,
            lctm->tm_mday,
            lctm->tm_hour,
            lctm->tm_min,
            lctm->tm_sec,
            src.c_str(),
            msg.c_str());
}

std::string Kola::format(const char* fmt, ...) {
    char buffer[1024];
    va_list args;
    va_start(args, fmt);
    vsprintf(buffer, fmt, args);
    va_end(args);
    return buffer;
}

std::vector<std::string> Kola::explode(std::string const & s, char delim)
{
    std::vector<std::string> result;
    std::istringstream iss(s);

    for (std::string token; std::getline(iss, token, delim); )
    {
        result.push_back(std::move(token));
    }

    return result;
}

bool Kola::isNumber(const std::string& s)
{
    return !s.empty() && std::find_if(s.begin(), 
        s.end(), [](char c) { return !std::isdigit(c); }) == s.end();
}

using namespace Kola;

State::State(int datawidth, int ROVPort, int clientPort) : datawidth(datawidth), ROVPort(ROVPort), clientPort(clientPort) {
    src = "State";
    log(src, "Creating server state...");
    log(src, format("Binding server to 0.0.0.0:%d with %dx lanes of data...", ROVPort, datawidth));
    log(src, format("Setting server reply destination to port %d...", clientPort));

    clientState = new int8_t[datawidth];
    memset(clientState,0,datawidth);
    ROVState = new int8_t[datawidth];
    memset(ROVState,0,datawidth);

    log(src, "Creating socket...");
    if ((sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) < 0) {
        // socket error
        log(src, "Error creating socket.");
        return;
    }
    log(src, "Socket created.");

    bzero((char *) &ROVAddress, sizeof (ROVAddress));

    ROVAddress.sin_addr.s_addr = INADDR_ANY;
    ROVAddress.sin_family = AF_INET;
    ROVAddress.sin_port = htons(ROVPort);

    log(src, "Binding to socket...");
    if (bind(sockfd, (struct sockaddr*) &ROVAddress, sizeof (ROVAddress)) < 0) {
        // binding error
        log(src, "Error binding server to socket.");
        return;
    }
    log(src, "Binded to socket.");
}

State::~State() {
    log(src, "Destroying server state...");
    delete[](clientState);
    delete[](ROVState);
    log(src, "Server state destroyed.");
}

int State::getDatawidth(){
    return datawidth;
}

void State::set(int channel, int value) {
    ROVState[channel] = value;
}

int State::get(int channel) {
    return clientState[channel];
}

void State::startTXRX() {
    log(src, "Starting TXRX...");
    StateThreadData *threadData = new StateThreadData();
    threadData->obj = this;
    if (pthread_create(&TXRXThread, NULL, staticTXRX, threadData) < 0) {
        // error starting TXRX thread
        log(src, "Error starting TXRX.");
        return;
    }
    log(src, "TXRX started.");
}

void State::stopTXRX() {
    log(src, "Stopping TXRX...");
    doTXRX = false;
    pthread_cancel(TXRXThread);
    log(src, "TXRX stopped.");
}

void State::TXRX() {
    std::string src = "StateTXRX";
    doTXRX = true;
    int8_t buffer[datawidth];
    InetAddress clientAddr;
    socklen_t addrLen = sizeof (clientAddr);
    log(src, "Ready for TXRX.");
    int i=0;
    while (doTXRX) {
        if (recvfrom(sockfd, buffer, datawidth, 0, (struct sockaddr*) &clientAddr, &addrLen) < 0) {
            // error receiving
            log(src,"Error receiving.");
        }
        memcpy(clientState, buffer, datawidth);
        memcpy(buffer, ROVState, datawidth);
        
        clientAddr.sin_port = htons(clientPort);
        if (sendto(sockfd, buffer, datawidth, 0, (struct sockaddr*) &clientAddr, addrLen) < 0) {
            // error sending
            log(src,"Error sending.");
        }
    }
    log(src, "Closing TXRX...");
    pthread_exit(NULL);
}

void* State::staticTXRX(void* args) {
    StateThreadData* tData = static_cast<StateThreadData*> (args);
    tData->obj->TXRX();
    delete tData;
    return NULL;
}