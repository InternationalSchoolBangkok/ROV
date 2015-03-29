/* 
 * File:   PID.cpp
 * Author: kolatat
 * 
 * Created on March 29, 2015, 1:43 PM
 */

#include "PID.h"

PID::PID(double Kp, double Ki, double Kd) : Kp(Kp), Ki(Ki), Kd(Kd) {
}

PID::PID(const PID& orig) {
}

PID::~PID() {
}

void PID::setPV(double value) {
    PV=value;
}

void PID::setSP(double value) {
    SP = value;
    previousError=SP-PV;
    integral=0;
}

void PID::enableD(bool enable) {
    doD = enable;
}

void PID::enableI(bool enable) {
    doI = enable;
}

void PID::enableP(bool enable) {
    doP = enable;
}



double PID::step(double dt) {
    double error = SP-PV;
    integral+=error*dt;
    double derivative = (error-previousError)/dt;
    double output = 0;
    if(doP){
        output+=Kp*error;
    }
    if(doI){
        output+=Ki*integral;
    }
    if(doD){
        output+=Kd*derivative;
    }
    previousError = error;
    return output;
}
