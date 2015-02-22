
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rov.rasputin.comm.State;

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

/**
 *
 * @author Kolatat Thangkasemvathana <kolatat.t@gmail.com>
 */
public class SampleUsage
{
    public static void main(String[] args){
        try {
            
            // create a new State for the ROV
            State ROV = new State("10.69.69.69", 6969, 6969, 32, 50);
            
            // start the communication with the ROV
            ROV.startTXRX();
            
            // set channel 5 on the ROV to 56
            ROV.set(5, 56);
            
            // get the value of channel 12
            ROV.get(12);
            
            // close the connection
            ROV.stopTXRX();
            
        } catch(UnknownHostException | SocketException ex) {
            Logger.getLogger(SampleUsage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
