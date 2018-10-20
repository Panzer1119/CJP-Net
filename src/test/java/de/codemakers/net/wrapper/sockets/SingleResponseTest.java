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
import de.codemakers.net.entities.Response;

import java.net.InetAddress;
import java.net.Socket;

public class SingleResponseTest {
    
    public static final int PORT = 2359;
    
    public static final void main(String[] args) throws Exception {
        final SingleResponseServerSocket singleResponseServerSocket = new SingleResponseServerSocket(PORT) {
            @Override
            public <T> Response<?> processRequest(Socket socket, Request<T> request) throws Exception {
                System.out.println(String.format("[SERVER] %s requested: %s", socket, request));
                if ("error".equals(request.getRequest())) {
                    throw new Exception("Debug Error");
                }
                return request.respond((o) -> System.currentTimeMillis());
            }
        };
        System.out.println(String.format("[SERVER] singleResponseServerSocket started: %s (%s)", singleResponseServerSocket.startWithoutException(), singleResponseServerSocket));
        final SingleResponseSocket singleResponseSocket = new SingleResponseSocket(InetAddress.getLocalHost(), PORT);
        //System.out.println(String.format("[ TEST ] singleResponseSocket running: %s", singleResponseSocket.isRunning()));
        //
        singleResponseSocket.requestResponse("test1234").queue((response) -> System.out.println("[CLIENT] Response: " + response), (throwable) -> System.err.println("[CLIENT] Request failed: " + throwable));
        //
        Thread.sleep(3000);
        System.out.println(String.format("[CLIENT] response: %s", singleResponseSocket.requestResponse("test").direct()));
        //System.out.println(String.format("[ TEST ] singleResponseSocket running: %s", singleResponseSocket.isRunning()));
        System.out.println(String.format("[CLIENT] response: %s", singleResponseSocket.requestResponse("error").direct()));
        //System.out.println(String.format("[ TEST ] singleResponseSocket running: %s", singleResponseSocket.isRunning()));
        System.out.println(String.format("[SERVER] singleResponseServerSocket stopped: %s (%s)", singleResponseServerSocket.stopWithoutException(), singleResponseServerSocket));
        System.exit(0);
    }
    
}
