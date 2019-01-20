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

package de.codemakers.net.entities;

import java.util.Objects;

public class NetCommand extends NetObjectHolder<Object> {
    
    private final Command command;
    
    public NetCommand(Object object, Command command) {
        super(object);
        this.command = command;
    }
    
    public NetCommand(NetEndpoint source, NetEndpoint destination, Object object, Command command) {
        super(source, destination, object);
        this.command = command;
    }
    
    public NetCommand(long id, Object object, Command command) {
        super(id, object);
        this.command = command;
    }
    
    public NetCommand(long id, NetEndpoint source, NetEndpoint destination, Object object, Command command) {
        super(id, source, destination, object);
        this.command = command;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final NetCommand that = (NetCommand) o;
        return command == that.command;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), command);
    }
    
    @Override
    public String toString() {
        return "NetCommand{" + "command=" + command + ", object=" + object + ", id=" + id + ", source=" + source + ", destination=" + destination + '}';
    }
    
    public enum Command {
        PING,
        PONG,
        START,
        STOP,
        CONNECT,
        DISCONNECT,
        CUSTOM,
        UNKNOWN
    }
    
}
