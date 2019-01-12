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

import de.codemakers.net.entities.NetCommand;
import de.codemakers.net.entities.NetObject;
import de.codemakers.security.entities.SecureData;
import de.codemakers.security.interfaces.Decryptor;
import de.codemakers.security.interfaces.Encryptor;

import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public class ServerSocketTest {
    
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public static final AtomicLong PING = new AtomicLong(Long.MIN_VALUE);
    
    public static final SecretKey SECRET_KEY = SecureDataTest.resolveAESSecretKey();
    public static final Encryptor ENCRYPTOR = SecureDataTest.resolveAESEncryptor(SECRET_KEY);
    public static final Decryptor DECRYPTOR = SecureDataTest.resolveAESDecryptor(SECRET_KEY);
    
    public static AdvancedSocket ADVANCED_SOCKET = null;
    
    public static final void main(String[] args) throws Exception {
        final int port = 1234;
        final ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[SERVER] serverSocket=" + serverSocket);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    serverSocket.close();
                    Thread.sleep(1000);
                    System.out.println("[SERVER] EXITING");
                    System.exit(0);
                } catch (Exception ex) {
                    System.err.println("[SERVER] timer error " + ex);
                }
            }
        }, 16000);
        try {
            boolean shutdownRequested = false;
            Socket socket = null;
            outer:
            while ((socket = serverSocket.accept()) != null) {
                System.out.println("[SERVER] accepted socket: " + socket);
                final AdvancedSocket advancedSocket = new AdvancedSocket(socket);
                System.out.println("[SERVER] advancedSocket 1=" + advancedSocket);
                advancedSocket.processOutputStream(ObjectOutputStream::new);
                System.out.println("[SERVER] advancedSocket 2=" + advancedSocket);
                advancedSocket.processInputStream(ObjectInputStream::new);
                System.out.println("[SERVER] advancedSocket 3=" + advancedSocket);
                ADVANCED_SOCKET = advancedSocket;
                sayHiWithNetObject(advancedSocket);
                pingSocketRandom(advancedSocket);
                Object input = null;
                while ((input = advancedSocket.getInputStream(ObjectInputStream.class).readObject()) != null) {
                    final Instant instant = Instant.now();
                    if (input == null) {
                        break;
                    }
                    final String timestamp_string = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
                    System.out.println(String.format("[SERVER][%s] RAW: \"%s\"", timestamp_string, input));
                    if (input instanceof NetObject) {
                        final NetObject netObject = (NetObject) input;
                        if (input instanceof NetCommand) {
                            final NetCommand netCommand = (NetCommand) input;
                            switch (netCommand.getCommand()) {
                                case PING:
                                    System.out.println(String.format("[SERVER][%s] got ping from CLIENT (%s)", timestamp_string, netCommand.getId(), netCommand.getObject()));
                                    advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject(new NetCommand(NetCommand.Command.PONG, instant.toEpochMilli()));
                                    break;
                                case PONG:
                                    System.out.println(String.format("[SERVER][%s] got pong from CLIENT (%s)", timestamp_string, netCommand.getId(), netCommand.getObject()));
                                    final long pong = instant.toEpochMilli();
                                    final long duration = pong - PING.get();
                                    System.out.println(String.format("[SERVER][%s] time from ping to pong: %d ms", timestamp_string, duration));
                                    break;
                                case START:
                                case STOP:
                                case CONNECT:
                                case DISCONNECT:
                                case CUSTOM:
                                case UNKNOWN:
                                    System.out.println(String.format("[SERVER][%s][%d] %s: \"%s\"", timestamp_string, netCommand.getId(), NetCommand.class.getSimpleName(), netCommand));
                                    break;
                            }
                        } else {
                            System.out.println(String.format("[SERVER][%s][%d] %s: \"%s\"", timestamp_string, netObject.getId(), NetObject.class.getSimpleName(), netObject));
                        }
                    } else if (input instanceof SecureData) {
                        final SecureData secureData = (SecureData) input;
                        final byte[] bytes = secureData.decrypt(DECRYPTOR);
                        final String string = new String(bytes);
                        System.out.println(String.format("[SERVER][%s] got secure (undecrypted): \"%s\"", timestamp_string, new String(secureData.toBytes())));
                        System.out.println(String.format("[SERVER][%s] got secure   (decrypted): \"%s\"", timestamp_string, string));
                    } else if (input instanceof String) {
                        String string = (String) input;
                        if (string.equals("shutdown")) {
                            shutdownRequested = true;
                            break outer;
                        } else if (string.startsWith("echo")) {
                            string = string.substring("echo".length()).trim();
                            advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject(string);
                        }
                        
                    }
                }
            }
            System.out.println("[SERVER] shutdownRequested=" + shutdownRequested);
        } catch (Exception ex) {
            System.err.println("[SERVER] " + ex);
            ex.printStackTrace();
        }
    }
    
    private static void sayHiWithNetObject(AdvancedSocket advancedSocket) {
        try {
            advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject(new Test1234());
        } catch (Exception ex) {
            System.err.println("[SERVER] say hi error " + ex);
        }
    }
    
    private static void pingSocketRandom(AdvancedSocket advancedSocket) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ping();
                } catch (Exception ex) {
                    System.err.println("[SERVER] ping error " + ex);
                }
            }
        }, (int) (Math.random() * 2000) + 1000);
    }
    
    public static class Test1234 extends NetObject {
        
        public Test1234() {
        }
        
        public Test1234(long id) {
            super(id);
        }
        
        @Override
        public String toString() {
            return "Hello from Test1234{" + "id=" + id + '}';
        }
        
    }
    
    public static void ping() throws Exception {
        PING.set(System.currentTimeMillis());
        ADVANCED_SOCKET.getOutputStream(ObjectOutputStream.class).writeObject(new NetCommand(NetCommand.Command.PING, PING.get()));
    }
    
}
