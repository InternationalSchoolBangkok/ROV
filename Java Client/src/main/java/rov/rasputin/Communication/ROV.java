/*
 * The MIT License
 *
 * Copyright 2015 Kolatat Thangkasemvathana <kolatat.t@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rov.rasputin.Communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the state between the client and the ROV.
 *
 * @author Kolatat Thangkasemvathana {@literal <kolatat.t@gmail.com>}
 */
public class ROV
{

    private final int datawidth;

    private final InetAddress ROVAddr;
    private final int ROVPort;
    private final int serverPort;

    private volatile byte clientState[];
    private volatile byte ROVState[];

    private final int TXInterval;

    private Thread TXThread;
    private Thread RXThread;

    private volatile boolean sending;
    private volatile boolean receiving;

    private final DatagramSocket TXSocket;
    private final DatagramSocket RXSocket;

    /**
     * Sets the value of a channel.
     *
     * @param channel The channel number.
     * @param value The value of the channel ranging from -128 to 127.
     */
    public void set(int channel, int value)
    {
        clientState[channel] = (byte) value;
    }

    /**
     * Gets the value of a channel.
     *
     * @param channel The channel number.
     * @return The value of the channel received from the server.
     */
    public int get(int channel)
    {
        return ROVState[channel];
    }

    private class TX implements Runnable
    {

        @Override
        public void run()
        {
            sending = true;
            while(sending) {
                DatagramPacket packet = new DatagramPacket(clientState, datawidth, ROVAddr, ROVPort);
                try {
                    TXSocket.send(packet);
                } catch(IOException ex) {
                    Logger.getLogger(ROV.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    Thread.sleep(TXInterval);
                } catch(InterruptedException ex) {
                    Logger.getLogger(ROV.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private class RX implements Runnable
    {

        @Override
        public void run()
        {
            byte[] buffer = new byte[datawidth];
            DatagramPacket packet = new DatagramPacket(buffer, datawidth);

            receiving = true;
            while(receiving) {
                try {
                    RXSocket.receive(packet);
                    if(packet.getAddress().equals(ROVAddr)) {
                        ROVState = packet.getData();
                    } else {
                        // RX from unknown source
                    }
                } catch(IOException ex) {
                    Logger.getLogger(ROV.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * Constructs a new {@link State} to maintain connection between the client
     * and a ROV.
     *
     * @param ROVAddress The IP address or host name of the ROV.
     * @param ROVPort The port the ROV is listening on.
     * @param serverPort The port to receive data from the ROV.
     * @param datawidth The number of channels.
     * @param TXInterval The time (in milliseconds) between each TX.
     * @throws UnknownHostException If no IP for the host could be found.
     * @throws SocketException If the socket could not be opened or binded to.
     */
    public ROV(String ROVAddress, int ROVPort, int serverPort, int datawidth, int TXInterval) throws UnknownHostException, SocketException
    {
        this.datawidth = datawidth;
        this.TXInterval = TXInterval;
        clientState = new byte[datawidth];
        ROVState = new byte[datawidth];
        ROVAddr = InetAddress.getByName(ROVAddress);
        this.ROVPort = ROVPort;
        this.serverPort = serverPort;
        TXSocket = new DatagramSocket();
        RXSocket = new DatagramSocket(serverPort);
    }

    /**
     * Starts the transmitter.
     */
    public void startTX()
    {
        TXThread = new Thread(new TX());
        TXThread.start();
    }

    /**
     * Starts the receiver.
     */
    public void startRX()
    {
        RXThread = new Thread(new RX());
        RXThread.start();
    }

    /**
     * Starts the transmitter and the receiver.
     */
    public void startTXRX()
    {
        startTX();
        startRX();
    }

    /**
     * Stops the transmitter.
     */
    public void stopTX()
    {
        sending = false;
        if(TXThread.isAlive()) {
            TXThread.stop();
        }
    }

    /**
     * Stops the receiver.
     */
    public void stopRX()
    {
        receiving = false;
        if(RXThread.isAlive()) {
            RXThread.stop();
        }
    }

    /**
     * Stops the transmitter and the receiver.
     */
    public void stopTXRX()
    {
        stopTX();
        stopRX();
    }
}
