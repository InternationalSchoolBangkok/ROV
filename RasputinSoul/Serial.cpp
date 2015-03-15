#include "Serial.h"

using namespace std;

Serial::Serial(int dataWidth) {
    this->dataWidth = dataWidth;
    uart0_filestream = open("/dev/ttyACM0", O_RDWR | O_NOCTTY | O_SYNC);
    if (uart0_filestream < 0) {
        printf("error %d opening %s: %s", errno, "/dev/ttyACM0", strerror(errno));
        return;
    }
    set_interface_attribs(uart0_filestream, B115200, 0); // set speed to 115,200 bps, 8n1 (no parity)
    set_blocking(uart0_filestream, 1); // set blocking
    usleep(1000000); //experiment to find optimum
    //start rxtx thread
    memset(toArdee, 0, dataWidth); //populate to ardee array with 0s
    this->start(); //start txrx
}

void
Serial::sendReceive() {
    while (true) {
        char magicChars[] = {'8', '=', 'D', '\n'};
        //message to send creation
        //char message[dataWidth+1];
        char message[36];
        memcpy(message, toArdee, dataWidth);
        message[dataWidth] = magicChars[0]; //clean up with memcpy in the future
        message[dataWidth + 1] = magicChars[1];
        message[dataWidth + 2] = magicChars[2];
        message[dataWidth + 3] = magicChars[3];
        //end of message creation
        write(uart0_filestream, message, dataWidth + 4);
        //usleep(20000);//wait for response(not necessary for blocking)
        char buf [36]; //should be : dataWidth+4
        memset(buf, '6', dataWidth + 4); //if you see 6s in output then gg
        int n = read(uart0_filestream, buf, sizeof (buf)); // read up to (dataWidth) characters if ready to read
        if (searchForCharSeq(buf, magicChars, 4, 0, sizeof (buf)) != dataWidth) {//if 8=D\n is not at end
            tcflush(uart0_filestream, TCIOFLUSH); //clear port driver's buffer if data not what we want
        }
        memcpy(fromArdee,buf,dataWidth+4);
        //printing of from ardee
        /*cout << "From Ardee: " << flush;
        for (int i = 0; i<dataWidth+4; i++) {
            cout << fromArdee[i] << flush;
        }
        cout << '\0' << flush; //append end of string char and flush out*/
        //end of printing from ardee    
    }
}

void
Serial::set(int channel, char value) {
    toArdee[channel] = value;
}

char Serial::get(int channel) {
    return fromArdee[channel];
}

int
Serial::set_interface_attribs(int fd, int speed, int parity) {
    struct termios tty;
    memset(&tty, 0, sizeof tty);
    if (tcgetattr(fd, &tty) != 0) {
        printf("error %d from tcgetattr", errno);
        return -1;
    }

    cfsetospeed(&tty, speed);
    cfsetispeed(&tty, speed);

    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8; // 8-bit chars
    // disable IGNBRK for mismatched speed tests; otherwise receive break
    // as \000 chars
    tty.c_iflag &= ~IGNBRK; // disable break processing
    tty.c_lflag = 0; // no signaling chars, no echo,
    // no canonical processing
    tty.c_oflag = 0; // no remapping, no delays
    tty.c_cc[VMIN] = 0; // read doesn't block
    tty.c_cc[VTIME] = 0; // 0 seconds read timeout after first character

    tty.c_iflag &= ~(IXON | IXOFF | IXANY); // shut off xon/xoff ctrl

    tty.c_cflag |= (CLOCAL | CREAD); // ignore modem controls,
    // enable reading
    tty.c_cflag &= ~(PARENB | PARODD); // shut off parity
    tty.c_cflag |= parity;
    tty.c_cflag &= ~CSTOPB;
    tty.c_cflag &= ~CRTSCTS;

    if (tcsetattr(fd, TCSANOW, &tty) != 0) {
        printf("error %d from tcsetattr", errno);
        return -1;
    }
    return 0;
}

void
Serial::set_blocking(int fd, int should_block) {
    struct termios tty;
    memset(&tty, 0, sizeof tty);
    if (tcgetattr(fd, &tty) != 0) {
        printf("error %d from tggetattr", errno);
        return;
    }

    tty.c_cc[VMIN] = should_block ? dataWidth + 4 : 0; //need (dataWidth+nl) bytes if blocking
    tty.c_cc[VTIME] = 5; // 0.5 seconds read timeout

    if (tcsetattr(fd, TCSANOW, &tty) != 0)
        printf("error %d setting term attributes", errno);
}

int
Serial::searchForCharSeq(char bigy[], char key[], int keyLength, int searchFrom, int searchTo) {
    bool found = false;
    for (int i = searchFrom; i < searchTo; i++) {
        if (bigy[i] == key[0]) {
            found = true;
            for (int u = 1; u < keyLength; u++) {
                if (bigy[u + i] != key[u]) {
                    found = false;
                }
            }
            if (found) {
                return i;
            } else {
                return -1;
            }
        }
    }
    return -1;
}

void*
Serial::run() {
    sendReceive();
    return NULL;
}
