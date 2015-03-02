#include <cstdlib>
#include <fstream>
#include <iostream>
//#include "State.h"
//lol dunno what the following do
#include <fcntl.h>
#include <termios.h>
#include <cstdlib>
#include <unistd.h>


using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
    int fd = open("/dev/ttyACM0", O_RDWR | O_NONBLOCK);
    if (fd < 0) {
        cout << "Could not open" << endl;
    }else{
        cout << "Opened" << endl;
    }
    sleep(3);
    ssize_t written = write(fd, "data", 4);
    if (written >= 0){
        cout << "Successful write!" << endl;
    }
    /*else if (errno == EWOULDBLOCK){// handle case where the write would block
         cout << "naw boi!" << endl;
    }*/
    else{
        cout << "lol" << endl;
    }
}
