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

package de.codemakers.net.wrapper.sockets.test2.server;

import java.net.ServerSocket;

public abstract class AbstractServerSocket {
    
    protected int port = -1;
    protected ServerSocket serverSocket = null;
    
    public AbstractServerSocket(int port) {
        setPort(port);
    }
    
    public AbstractServerSocket(ServerSocket serverSocket) {
        this(serverSocket.getLocalPort());
        setServerSocket(serverSocket);
    }
    
    public int getPort() {
        return port;
    }
    
    public AbstractServerSocket setPort(int port) {
        this.port = port;
        return this;
    }
    
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    public abstract AbstractServerSocket setServerSocket(ServerSocket serverSocket);
    
    protected abstract ServerSocket createServerSocket() throws Exception;
    
}
