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

import java.net.InetAddress;

public class UDPSocketTest {
    
    public static final int PORT_RECEIVER = UDPServerSocketTest.PORT_RECEIVER;
    public static final int PORT_SENDER = UDPServerSocketTest.PORT_SENDER;
    
    public static void main(String[] args) throws Exception {
        Logger.getDefaultAdvancedLeveledLogger().setMinimumLogLevel(LogLevel.FINE);
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendThread().appendLogLevel().appendText(": ").appendObject().appendNewLine().appendSource().finishWithoutException();
        final UDPSocket udpSocket = new UDPSocket(InetAddress.getLocalHost(), PORT_RECEIVER);
        Logger.logDebug("udpSocket=" + udpSocket);
        udpSocket.setPortSender(PORT_SENDER);
        Logger.logDebug("udpSocket=" + udpSocket);
        udpSocket.start();
        Logger.logDebug("udpSocket=" + udpSocket);
        Standard.addShutdownHook(udpSocket::stop);
        Standard.addShutdownHook(udpSocket::close);
        Logger.logDebug("Writing message 1...");
        udpSocket.getOutputStream().write("Test Message 1".getBytes());
        udpSocket.getOutputStream().flush();
        Thread.sleep(2000);
        Logger.logDebug("Writing message 2...");
        udpSocket.getOutputStream().write("Test Message 2".getBytes());
        udpSocket.getOutputStream().flush();
        Thread.sleep(2000);
        Logger.logDebug("Writing message 3...");
        udpSocket.getOutputStream().write("Test Message 3".getBytes());
        udpSocket.getOutputStream().flush();
        Standard.async(() -> {
            Thread.sleep(6000);
            Logger.log("Shutting down");
            udpSocket.stop();
            udpSocket.close();
            Logger.log("Exiting");
            System.exit(0);
        });
    }
    
}
