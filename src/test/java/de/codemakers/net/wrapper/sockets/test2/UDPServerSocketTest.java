/*
 *     Copyright 2018 - 2019 Paul Hagedorn (Panzer1119)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package de.codemakers.net.wrapper.sockets.test2;

import de.codemakers.base.Standard;
import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;
import de.codemakers.base.multiplets.Doublet;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;

public class UDPServerSocketTest {
    
    public static final int PORT_RECEIVER = 5645;
    public static final int PORT_SENDER = PORT_RECEIVER + 1;
    
    public static void main(String[] args) throws Exception {
        Logger.getDefaultAdvancedLeveledLogger().setMinimumLogLevel(LogLevel.FINE);
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendThread().appendLogLevel().appendText(": ").appendObject().appendNewLine().appendSource().finishWithoutException();
        UDPServerSocket.DEBUG = true;
        final UDPServerSocket udpServerSocket = new UDPServerSocket(PORT_RECEIVER);
        Logger.logDebug("udpServerSocket=" + udpServerSocket);
        udpServerSocket.connect();
        Logger.logDebug("udpServerSocket=" + udpServerSocket);
        udpServerSocket.start();
        Logger.logDebug("udpServerSocket=" + udpServerSocket);
        Standard.addShutdownHook(udpServerSocket::disconnect);
        Standard.addShutdownHook(udpServerSocket::stop);
        Standard.addShutdownHook(udpServerSocket::close);
        Standard.async(() -> {
            Logger.logDebug("Waiting for connection");
            final Doublet<InetAddress, Integer> connection = udpServerSocket.accept();
            Logger.logDebug("connection=" + connection);
            InputStream inputStream = null;
            if (true) {
                inputStream = udpServerSocket.getInputStream(connection.getA(), connection.getB());
            } else {
                int i = 0;
                while (inputStream == null) {
                    inputStream = udpServerSocket.getInputStream(InetAddress.getLocalHost(), PORT_SENDER);
                    Thread.sleep(100);
                }
            }
            Logger.logDebug("Receiving started");
            final byte[] buffer = new byte[16];
            int read = 0;
            while (read != -1) {
                read = inputStream.read(buffer, 0, buffer.length);
                final byte[] temp = new byte[read];
                System.arraycopy(buffer, 0, temp, 0, read);
                Logger.logDebug(String.format("Received %d Bytes: %s", read, Arrays.toString(temp)));
            }
            Logger.logDebug("Receiving ended");
        });
        Logger.logDebug("udpServerSocket=" + udpServerSocket);
        Thread.sleep(1000);
        Logger.logDebug("udpServerSocket=" + udpServerSocket);
        Standard.async(() -> {
            Logger.log("Waiting 20 seconds to exit");
            Thread.sleep(20000);
            Logger.log("Disconnecting");
            //udpServerSocket.disconnect();
            Logger.log("Stopping");
            udpServerSocket.stop();
            Logger.log("Closing");
            udpServerSocket.close();
            Logger.log("Exiting");
            System.exit(0);
        });
    }
    
    
}
