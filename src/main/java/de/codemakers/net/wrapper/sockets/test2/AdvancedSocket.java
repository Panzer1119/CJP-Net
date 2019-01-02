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

package de.codemakers.net.wrapper.sockets.test2;

import java.net.InetAddress;
import java.net.Socket;

public class AdvancedSocket extends NormalSocket {
    
    public AdvancedSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public AdvancedSocket(Socket socket) {
        super(socket);
    }
    
    @Override
    protected void onConnection(boolean successful) throws Exception {
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }
    
    @Override
    protected Socket createSocket() throws Exception {
        return new Socket(inetAddress, port);
    }
    
    @Override
    public String toString() {
        return "AdvancedSocket{" + "connected=" + connected + ", localCloseRequested=" + localCloseRequested + ", inetAddress=" + inetAddress + ", port=" + port + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
