/* 
 * File:   PID.h
 * Author: kolatat
 *
 * Created on March 29, 2015, 1:43 PM
 */

#ifndef PID_H
#define	PID_H

class PID {
public:
    PID(double Kp, double Ki, double Kd);
    PID(const PID& orig);
    virtual ~PID();
    
    void setPV(double value);
    void setSP(double value);
    double step(double dt);
    void enableP(bool enable);
    void enableI(bool enable);
    void enableD(bool enable);
private:
    bool doP=true,doI=true,doD=true;
    double Kp,Ki,Kd;
    double PV=0, SP=0, previousError=0, integral=0;
};

#endif	/* PID_H */

