#include "IMU.h"

IMU::IMU(int baudRate) {
    doUpdate = true;
    cursor = 0;
    //----- SETUP USART 0 -----
    //-------------------------
    //At bootup, pins 8 and 10 are already set to UART0_TXD, UART0_RXD (ie the alt0 function) respectively
    uart0_filestream = -1;
    //OPEN THE UART
    //The flags (defined in fcntl.h):
    //	Access modes (use 1 of these):
    //		O_RDONLY - Open for reading only.
    //		O_RDWR - Open for reading and writing.
    //		O_WRONLY - Open for writing only.
    //
    //	O_NDELAY / O_NONBLOCK (same function) - Enables nonblocking mode. When set read requests on the file can return immediately with a failure status
    //											if there is no input immediately available (instead of blocking). Likewise, write requests can also return
    //											immediately with a failure status if the output can't be written immediately.
    //
    //	O_NOCTTY - When set and path identifies a terminal device, open() shall not cause the terminal device to become the controlling terminal for the process.
    uart0_filestream = open("/dev/ttyAMA0", O_RDWR | O_NOCTTY | O_NDELAY);
    if (uart0_filestream == -1) {
        //ERROR - CAN'T OPEN SERIAL PORT
        printf("Error - Unable to open UART for IMU.  Ensure it is not in use by another application\n");
    } else {
        printf("Opened IMU Connection\n");
    }
    //CONFIGURE THE UART
    //The flags (defined in /usr/include/termios.h - see http://pubs.opengroup.org/onlinepubs/007908799/xsh/termios.h.html):
    //	Baud rate:- B1200, B2400, B4800, B9600, B19200, B38400, B57600, B115200, B230400, B460800, B500000, B576000, B921600, B1000000, B1152000, B1500000, B2000000, B2500000, B3000000, B3500000, B4000000
    //	CSIZE:- CS5, CS6, CS7, CS8
    //	CLOCAL - Ignore modem status lines
    //	CREAD - Enable receiver
    //	IGNPAR = Ignore characters with parity errors
    //	ICRNL - Map CR to NL on input (Use for ASCII comms where you want to auto correct end of line characters - don't use for bianry comms!)
    //	PARENB - Parity enable
    //	PARODD - Odd parity (else even)
    tcgetattr(uart0_filestream, &options);
    unsigned int baud;
    switch (baudRate) {
        case 9600:
            baud = B9600;
            break;
        case 19200:
            baud = B19200;
            break;
        case 38400:
            baud = B38400;
            break;
        case 115200:
            baud = B115200;
            break;
        default:
            baud = B9600;
            break;
    }
    printf("Set baud rate to: %i\n", baudRate);
    options.c_cflag = baud | CS8 | CLOCAL | CREAD; //<Set baud rate
    options.c_iflag = IGNPAR;
    options.c_oflag = 0;
    options.c_lflag = 0;
    tcflush(uart0_filestream, TCIFLUSH);
    tcsetattr(uart0_filestream, TCSANOW, &options);
    initTime = time(NULL);
    //create the rx thread
    LolStructIMU *threadData = new LolStructIMU();
    threadData->obj = this;
    if (pthread_create(&tableUpdaterThread, NULL, staticUpdater, threadData) < 0) {
        cout << "Error:unable to create thread," << endl;
    }
}

void IMU::closeSerial() {
    doUpdate = false;
    pthread_cancel(tableUpdaterThread);
    close(uart0_filestream);
}

void IMU::tableUpdater() {
    //cout << "Executing tableUpdaterThread, " << endl;
    while (doUpdate) {
        if (uart0_filestream != -1) {
            // Read up to 255 characters from the port if they are there
            unsigned char rx_buffer[256];
            int rx_length = read(uart0_filestream, (void*) rx_buffer, 255); //Filestream, buffer to store in, number of bytes to read (max)
            if (rx_length < 0) {
                //An error occurred (will occur if there are no bytes)
            } else if (rx_length == 0) {
                //No data waiting
            } else {
                //Bytes received
                //rx_buffer[rx_length] = '\0'; //make it string friendly
                //printf("%i bytes read : %s\n", rx_length, rx_buffer);
                //cout << rx_buffer;
                appendBuffers(tempBuffer, cursor, rx_buffer, rx_length); //buff 2 to back of buff 1
                //cout<<"cursor boost";
                //cout<<cursor<<endl;
                //cout << "!!!TEMP BUFF PRINT STARTS HERE\n" << tempBuffer << "\nEND OF TB PRINT" << endl;
                //cout<<rx_buffer<<flush;
                //cout<<rx_length<<endl;
                cursor += rx_length;
                if (cursor >= 512) {
                    cursor = 0;
                    clearUnsignedCharArray(tempBuffer, 512);
                }
                //search for searchSeq in temp buffer
                unsigned char startChars[] = {'!', '!', '!'};
                unsigned char endChars[] = {'*', '*', '*'};
                int pos = searchForCharSeq(tempBuffer, startChars, 3, 0, 512);
                if (pos == -1) {
                    /*cout << "\n000DID NOT FIND PENIS NOOOOOO- TB:\n" << endl;
                    for (int i = 0; i < 512; i++) {//debug
                        cout << tempBuffer[i] << flush;
                    }*/
                    //position not found
                    //cout << "NGSINGS1" << endl;
                    //cout<<bigBoi<<endl;
                    //cout<<searchFor<<endl;
                } else {
                    //cout << "\n111HERE HERE HERE HUEHEUUEHEUEHEUUHE" << endl;
                    if (pos < 442) {
                        int secondPos = searchForCharSeq(tempBuffer, endChars, 3, pos + 35, 512);
                        if (secondPos != -1) {
                            int i;
                            //cout << "Values From IMU: ";
                            for (i = 0; i < secondPos && i < 100; i++) {
                                fromIMU[i] = tempBuffer[i + pos + 3];
                                //cout <<"I: " <<i<<" "<<fromIMU[i] << flush; 
                                //cout << fromIMU[i] << flush;          
                            }
                            fromIMU[i] = '\0';
                            //cout << "\nEND OF FROM IMU" << endl;
                            //cout << "TB PRIOR TO RESET\n" << tempBuffer << "\nEND OF TB" << endl;
                            clearUnsignedCharArray(tempBuffer, 512);
                            cursor = 0;
                            //parse the fromIMU data into roll, pitch, and yaw
                            std::string toBeParsed = std::string((char*) fromIMU);
                            int rs = toBeParsed.find("RLL:");
                            int re = toBeParsed.find(",", rs);
                            std::string rollString = toBeParsed.substr(rs + 4, re - rs - 4);
                            roll = atof(rollString.c_str());
                            int ps = toBeParsed.find("PCH:");
                            int pe = toBeParsed.find(",", ps);
                            std::string pitchString = toBeParsed.substr(ps + 4, pe - ps - 4);
                            pitch = atof(pitchString.c_str());
                            int ys = toBeParsed.find("YAW:");
                            int ye = toBeParsed.find(",", ys);
                            std::string yawString = toBeParsed.substr(ys + 4, ye - ys - 4);
                            yaw = atof(yawString.c_str());
                            //cout<<"Roll: "<<rollString<<" Pitch: "<<pitchString<<" Yaw: "<<yawString<<endl;
                        }
                    } else {//otherwise cursor is so far back that it will start looking after the buffer ends
                        cout << "search string too far back" << endl;
                        cursor = 0;
                        clearUnsignedCharArray(tempBuffer, 512);
                    }
                }
                //cout<<tempBuffer<<endl;
            }

        } else {
            cout << "uartStream ggd" << endl;
            pthread_exit(NULL);
        }
    }
    pthread_exit(NULL);
}

void* IMU::staticUpdater(void* args) {
    LolStructIMU* tData = static_cast<LolStructIMU*> (args);
    tData->obj->tableUpdater();
    delete tData;
    return NULL;
}

void IMU::appendBuffers(unsigned char array1[], int appendTo, unsigned char array2[], int array2Length) {
    for (int i = appendTo; i < appendTo + array2Length; i++) {
        array1[i] = array2[i - appendTo];
        array1[i + 1] = '\0'; //attach string termination character
        //printf("array1: %c array2: %c\n",array1[i],array2[i-appendTo]);
    }
    /*for (int i = 0; i < 512; i++) {
        cout<< array1[i];
    }
    cout<<endl;*/
}

void IMU::clearUnsignedCharArray(unsigned char array[], int length) {
    for (int i = 0; i < length; i++) {
        array[i] = '\0';
    }
}

float IMU::getRoll() {
    return roll;
}

float IMU::getPitch() {
    return pitch;
}

float IMU::getYaw() {
    return yaw;
}

int IMU::searchForCharSeq(unsigned char bigy[], unsigned char key[], int keyLength, int searchFrom, int searchTo) {
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
