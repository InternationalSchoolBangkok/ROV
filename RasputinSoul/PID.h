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
    double getSP();
    void enableP(bool enable);
    void enableI(bool enable);
    void enableD(bool enable);
    void enablePID(bool enable);
    void setPIDGainz(double Kp, double Ki, double Kd);
private:
    bool doP, doI, doD;
    double Kp, Ki, Kd;
    double PV, SP, previousError, integral;
};

#endif	/* PID_H */

