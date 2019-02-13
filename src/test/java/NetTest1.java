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

import de.codemakers.base.logger.Logger;
import de.codemakers.net.events.DisconnectedEvent;
import de.codemakers.net.events.ObjectReceived;
import de.codemakers.net.events.SocketAcceptedEvent;
import de.codemakers.net.wrapper.sockets.AdvancedServerSocket;
import de.codemakers.net.wrapper.sockets.AdvancedSocket;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

public class NetTest1 {
    
    public static final int PORT = 1234;
    
    public static final void main(String[] args) throws Exception {
        final long start = System.currentTimeMillis();
        final Instant instant = Instant.now();
        System.out.println("start  : " + start);
        System.out.println("instant: " + instant.toEpochMilli());
        System.out.println("Test started: " + start);
        final AdvancedServerSocket advancedServerSocket = new AdvancedServerSocket(PORT);
        advancedServerSocket.addEventListener(SocketAcceptedEvent.class, (socketAcceptedEvent) -> {
            System.out.println("[SERVER] SocketAcceptedEvent: " + socketAcceptedEvent);
            System.out.println("[SERVER] SocketAcceptedEvent TIME: " + socketAcceptedEvent.toLocalISOZonedDateTime());
            System.out.println("[SERVER] Socket accepted: " + socketAcceptedEvent.getSocket());
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketAcceptedEvent.getSocket().getOutputStream());
                objectOutputStream.writeObject("[SERVER] -> [CLIENT]: Hi from me! I accepted you!");
            } catch (Exception ex) {
                Logger.handleError(ex);
            }
            return false;
        });
        advancedServerSocket.addEventListener(DisconnectedEvent.class, (disconnectedEvent) -> {
            System.out.println("[SERVER] DisconnectedEvent: " + disconnectedEvent);
            return false;
        });
        advancedServerSocket.start(Throwable::printStackTrace);
        final AdvancedSocket advancedSocket = new AdvancedSocket(InetAddress.getLocalHost(), PORT);
        advancedSocket.addEventListener(ObjectReceived.class, (objectReceivedEvent) -> {
            System.out.println("[CLIENT] ObjectReceivedEvent: " + objectReceivedEvent);
            System.out.println("[CLIENT] ObjectReceivedEvent TIME: " + objectReceivedEvent.toLocalISOZonedDateTime());
            System.out.println("[CLIENT] received: " + objectReceivedEvent.getObject());
            return false;
        });
        advancedSocket.addEventListener(DisconnectedEvent.class, (disconnectedEvent) -> {
            System.out.println("[CLIENT] DisconnectedEvent: " + disconnectedEvent);
            return false;
        });
        //System.out.println("Connected: " + advancedSocket.connect());
        System.out.println("Started: " + advancedSocket.start());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                advancedSocket.processOutputStream(ObjectOutputStream::new);
                advancedSocket.sendAction("Test").queue((success) -> System.out.println("[CLIENT] sending data was successful: " + success), (throwable) -> System.out.println("[CLIENT] sent data not successfully: " + throwable));
            }
        }, 2000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    advancedSocket.stop();
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    Logger.handleError(ex);
                }
                try {
                    advancedServerSocket.stop();
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    Logger.handleError(ex);
                }
                System.exit(0);
                final long duration = System.currentTimeMillis() - start;
                System.out.println("Test finished: " + duration + "ms");
            }
        }, 15000);
    }
    
}