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

public class NetCommand extends NetObject {
    
    private final Command command;
    private final Object object;
    
    public NetCommand(Command command, Object object) {
        super();
        this.command = command;
        this.object = object;
    }
    
    public NetCommand(long id, Command command, Object object) {
        super(id);
        this.command = command;
        this.object = object;
    }
    
    public Command getCommand() {
        return command;
    }
    
    public <T> T getObject() {
        return (T) object;
    }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !getClass().isAssignableFrom(object.getClass())) {
            return false;
        }
        final NetCommand that = (NetCommand) object;
        return command == that.command && Objects.equals(this.object, that.object);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(command, object);
    }
    
    @Override
    public String toString() {
        return "NetCommand{" + "command=" + command + ", object=" + object + ", id=" + id + '}';
    }
    
    public enum Command {
        PING,
        PONG,
        START,
        STOP,
        CONNECT,
        DISCONNECT,
        CUSTOM,
        UNKNOWN;
    }
    
}
