/* 
 * File:   main.cpp
 * Author: kolatat
 *
 * Created on March 5, 2015, 5:03 PM
 */

#include "State.h"

#include <cstdlib>
#include <stdio.h>
#include <iostream>
#include <string.h>
#include <ctime>
#include <pthread.h>
#include <stdarg.h>
#include <signal.h>
#include <algorithm>

using namespace Kola;

State *server;
std::string src = "RRCS";
bool runServer;
bool msStarted;
pthread_t msThread;

void stopServer();
void exit();

void startServer(){
    log(src, "ROV Rasputin Communication Server welcomes you.");
    log(src, "Starting server...");
    server = new State(32, 6969, 6969);
    server->startTXRX();
}

void onSigInt(int s){
    log(src,format("Caught signal %d.",s));
    exit();
}

void setSigIntAction(){
    struct sigaction sigIntHandler;
    sigIntHandler.sa_handler=onSigInt;
    sigemptyset(&sigIntHandler.sa_mask);
    sigIntHandler.sa_flags=0;
    sigaction(SIGINT,&sigIntHandler,NULL);
}

void stopServer(){
    log(src,"Stopping server...");
    runServer=false;
    server->stopTXRX();
    server->~State();
    log(src, "Server stopped.");
}

void exit(){
    log(src,"Exiting...");
    stopServer();
    pthread_exit(NULL);
}

void *mainSequence(void *ptr){
    std::string src = "MS";
    log(src,"MS started.");
    msStarted=true;
    while(runServer){
        // do the main stuffs here
        
        usleep(1000000);
    }
    log(src,"MS stopped.");
}

void callCmd(std::string cmd, std::vector<std::string> args){
    if(cmd.compare("exit")==0){
        exit();
    }else if(cmd.compare("help")==0 || cmd.compare("?")==0){
        std::cout<<"Available commands on the ROV Rasputin:"<<std::endl;
        std::cout<<"exit                \tStops the RRCS."<<std::endl;
        std::cout<<"get <channel>|*     \tGets the value of a channel."<<std::endl;
        std::cout<<"help                \tDisplays this help message."<<std::endl;
        std::cout<<"set <channel> <value>\tSets the value of a channel."<<std::endl;
    } else if(cmd.compare("get")==0){
        bool ok = true;
        ok &= args.size()==1;
        if(ok){
            ok&=(isNumber(args.at(0))||args.at(0).compare("*")==0);
        }
        if(ok){
            //ok
            std::cout<<"Value of channel "<<args.at(0)<<" is: ";
            if(args.at(0).compare("*")==0){
                for(int c=0;c<server->getDatawidth();++c){
                    std::cout<<server->get(c)<<" ";
                }
            } else {
                int c=std::stoi(args.at(0),nullptr);
                if(c>=server->getDatawidth()){
                    std::cout<<"CHANNEL_OUT_OF_BOUNDS";
                } else {
                    std::cout<<server->get(c);
                }
            }
            std::cout<<std::endl;
        } else {
            std::cout<<"Displays the value of a channel."<<std::endl;
            std::cout<<"Usage: get <channel>|*"<<std::endl;
        }
    }  else if(cmd.compare("set")==0){
        bool ok = true;
        ok &= args.size()==2;
        if(ok){
            ok&=isNumber(args.at(0))&&isNumber(args.at(1));
        }
        if(ok){
            int c = std::stoi(args.at(0),nullptr);
            int v = std::stoi(args.at(1),nullptr);
            if(c>=server->getDatawidth() || v>=128 || v<-128){
                std::cout<<"VALUE_OUT_OF_BOUNDS";
            } else {
                server->set(c,v);
                std::cout<<"Value of channel "<<c<<" set to "<<v<<".";
            }
            std::cout<<std::endl;
        } else {
            std::cout<<"Sets the value of a channel."<<std::endl;
            std::cout<<"Usage: set <channel> <value>"<<std::endl;
        }
    } else {
        std::cout<<format("%s: command not found.\n",cmd.c_str());
    }
}

void enterMainSequence(){
    runServer = true;
    msStarted=false;
    if(pthread_create(&msThread,NULL,mainSequence,NULL)<0){
        log(src,"Error entering server main sequence.");
        return;
    }
    while(!msStarted){}
    while(runServer){
        std::string input;
        std::cout << "Awooga$ ";
        getline(std::cin,input);
        std::vector<std::string> args = explode(input,' ');
        
        if(args.size()>0){
            std::string cmd = args.front();
            args.erase(args.begin());

            callCmd(cmd,args);
        }
    }
}

/*
 * 
 */
int main(int argc, char** argv) {
    startServer();
    setSigIntAction();
    enterMainSequence();
    stopServer();
    exit();
    return EXIT_SUCCESS;
}

