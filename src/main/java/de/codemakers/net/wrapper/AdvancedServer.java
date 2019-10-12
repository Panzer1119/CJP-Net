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

package de.codemakers.net.wrapper;

import de.codemakers.net.wrapper.sockets.test2.AdvancedSocket;
import de.codemakers.net.wrapper.sockets.test2.server.DefaultServerSocket;

public abstract class AdvancedServer<S extends AdvancedSocket, SS extends DefaultServerSocket<S>, C extends AdvancedClient<S>> extends AbstractServer<S, SS, C> {
    
    public AdvancedServer(SS serverSocket) {
        super(serverSocket);
    }
    
    @Override
    public String toString() {
        return "AdvancedServer{" + "serverSocket=" + serverSocket + ", clients=" + clients + '}';
    }
    
}