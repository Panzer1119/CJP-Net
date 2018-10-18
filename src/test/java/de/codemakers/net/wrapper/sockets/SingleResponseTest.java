/*
 *     Copyright 2018 Paul Hagedorn (Panzer1119)
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

package de.codemakers.net.wrapper.sockets;

import de.codemakers.net.entities.Request;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SingleResponseTest {
    
    public static final int PORT = 2359;
    
    public static final void main(String[] args) throws UnknownHostException {
        final SingleResponseServerSocket singleResponseServerSocket = new SingleResponseServerSocket(PORT) {
            @Override
            public Object processRequest(Socket socket, Request request) throws Exception {
                System.out.println(String.format("[SERVER] %s requested: %s", socket, request));
                if ("error".equals(request)) {
                    throw new Exception("Debug Error");
                }
                return System.currentTimeMillis();
            }
        };
        singleResponseServerSocket.startWithoutException();
        System.out.println("[SERVER] " + SingleResponseServerSocket.class.getSimpleName() + " started");
        final SingleResponseSocket singleResponseSocket = new SingleResponseSocket(InetAddress.getLocalHost(), PORT);
        System.out.println("running: " + singleResponseSocket.isRunning());
        System.out.println(String.format("[CLIENT] response: %s", singleResponseSocket.requestResponse("test").direct()));
        System.out.println("running: " + singleResponseSocket.isRunning());
        System.out.println(String.format("[CLIENT] response: %s", singleResponseSocket.requestResponse("error").direct()));
        System.out.println("running: " + singleResponseSocket.isRunning());
        singleResponseServerSocket.stopWithoutException();
        System.exit(0);
    }
    
}
