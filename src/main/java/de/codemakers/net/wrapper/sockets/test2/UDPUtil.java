/*
 *     Copyright 2018 - 2020 Paul Hagedorn (Panzer1119)
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

import de.codemakers.base.logger.Logger;

import java.nio.channels.AlreadyBoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UDPUtil {
    
    protected static final Map<Integer, UDPServerSocket> UDP_SERVER_SOCKETS = new ConcurrentHashMap<>();
    
    public static boolean addUDPServerSocket(UDPServerSocket udpServerSocket) {
        if (UDP_SERVER_SOCKETS.containsValue(udpServerSocket)) {
            Logger.logWarning("Port " + udpServerSocket.getPort() + " could be already bound");
            return false;
        }
        UDP_SERVER_SOCKETS.put(udpServerSocket.getPort(), udpServerSocket);
        return UDP_SERVER_SOCKETS.containsValue(udpServerSocket);
    }
    
    public static UDPServerSocket replaceUDPServerSocket(UDPServerSocket udpServerSocket) {
        final UDPServerSocket old = UDP_SERVER_SOCKETS.put(udpServerSocket.getPort(), udpServerSocket);
        if (old != null) {
            old.closeWithoutException();
        }
        return old;
    }
    
    public static boolean exists(int port) {
        return UDP_SERVER_SOCKETS.containsKey(port);
    }
    
    public static boolean exists(UDPServerSocket udpServerSocket) {
        return UDP_SERVER_SOCKETS.containsValue(udpServerSocket);
    }
    
    public static UDPServerSocket getUDPServerSocket(int port) {
        return UDP_SERVER_SOCKETS.get(port);
    }
    
    public static UDPServerSocket createUDPServerSocket(int port) {
        if (getUDPServerSocket(port) != null) {
            Logger.logWarning("Port " + port + " is already bound");
            throw new AlreadyBoundException();
        }
        final UDPServerSocket udpServerSocket = new UDPServerSocket(port);
        UDP_SERVER_SOCKETS.put(port, udpServerSocket);
        return udpServerSocket;
    }
    
    public static UDPServerSocket getOrCreateUDPServerSocket(int port) {
        UDPServerSocket udpServerSocket = getUDPServerSocket(port);
        if (udpServerSocket == null) {
            udpServerSocket = createUDPServerSocket(port);
        }
        return udpServerSocket;
    }
    
    public static boolean removeUDPServerSocket(int port) {
        UDP_SERVER_SOCKETS.remove(port);
        return !UDP_SERVER_SOCKETS.containsKey(port);
    }
    
    public static boolean removeUDPServerSocket(UDPServerSocket udpServerSocket) {
        UDP_SERVER_SOCKETS.remove(udpServerSocket);
        return !UDP_SERVER_SOCKETS.containsValue(udpServerSocket);
    }
    
}
