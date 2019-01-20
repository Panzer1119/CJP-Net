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

import de.codemakers.base.util.tough.ToughRunnable;
import de.codemakers.net.entities.NetCommand;
import de.codemakers.net.entities.NetObject;
import de.codemakers.net.exceptions.NetRuntimeException;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

public class ProcessingSocketTest {
    
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public static final AtomicLong PING = new AtomicLong(Long.MIN_VALUE);
    
    public static ProcessingSocket<ObjectInputStream, ObjectOutputStream, Object> PROCESSING_SOCKET = null;
    
    public static void main(String[] args) throws Exception {
        final InetAddress inetAddress = InetAddress.getLocalHost();
        final int port = 1234;
        final ProcessingSocket<ObjectInputStream, ObjectOutputStream, Object> processingSocket = new ProcessingSocket<ObjectInputStream, ObjectOutputStream, Object>(inetAddress, port) {
            @Override
            protected ObjectOutputStream toInternOutputStream(OutputStream outputStream) throws Exception {
                return new ObjectOutputStream(outputStream);
            }
            
            @Override
            protected ObjectInputStream toInternInputStream(InputStream inputStream) throws Exception {
                return new ObjectInputStream(inputStream);
            }
            
            @Override
            protected ToughRunnable createInputProcessor(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
                return () -> {
                    try {
                        while (!isLocalCloseRequested() && !isStopRequested()) {
                            final Object input = inputStream.readObject();
                            //final long timestamp = System.currentTimeMillis();
                            //final Instant instant = Instant.ofEpochMilli(timestamp);
                            final Instant instant = Instant.now();
                            if (input == null) {
                                break;
                            }
                            final String timestamp_string = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
                            System.out.println(String.format("[CLIENT][%s] RAW: \"%s\"", timestamp_string, input));
                            try {
                                if (input instanceof NetObject) {
                                    final NetObject netObject = (NetObject) input;
                                    if (input instanceof NetCommand) {
                                        final NetCommand netCommand = (NetCommand) input;
                                        switch (netCommand.getCommand()) {
                                            case PING:
                                                System.out.println(String.format("[CLIENT][%s] got ping from SERVER (%s)", timestamp_string, netCommand.getId(), netCommand.getObject()));
                                                outputStream.writeObject(new NetCommand(NetCommand.Command.PONG, instant.toEpochMilli()));
                                                break;
                                            case PONG:
                                                System.out.println(String.format("[CLIENT][%s] got pong from SERVER (%s)", timestamp_string, netCommand.getId(), netCommand.getObject()));
                                                final long pong = instant.toEpochMilli();
                                                final long duration = pong - PING.get();
                                                System.out.println(String.format("[CLIENT][%s] time from ping to pong: %d ms", timestamp_string, duration));
                                                break;
                                            case START:
                                            case STOP:
                                            case CONNECT:
                                            case DISCONNECT:
                                            case CUSTOM:
                                            case UNKNOWN:
                                                System.out.println(String.format("[CLIENT][%s][%d] %s: \"%s\"", timestamp_string, netCommand.getId(), NetCommand.class.getSimpleName(), netCommand));
                                                break;
                                        }
                                    } else {
                                        System.out.println(String.format("[CLIENT][%s][%d] %s: \"%s\"", timestamp_string, netObject.getId(), NetObject.class.getSimpleName(), netObject));
                                    }
                                } else {
                                    System.out.println(String.format("[CLIENT][%s] input: \"%s\"", timestamp_string, input));
                                }
                            } catch (Exception ex) {
                                System.err.println("[CLIENT] input error " + ex);
                            }
                        }
                    } catch (Exception ex) {
                        outputStream.close(); //TODO Good?
                        throw new NetRuntimeException(ex);
                    } outputStream.close(); //TODO Good?
                };
            }
            
        };
        PROCESSING_SOCKET = processingSocket;
        System.out.println("[CLIENT] processingSocket=" + processingSocket);
        if (processingSocket.connect()) {
            System.out.println("[CLIENT] processingSocket=" + processingSocket);
            if (processingSocket.start()) {
                System.out.println("[CLIENT] processingSocket=" + processingSocket);
                System.out.println("[CLIENT] thread started");
            } else {
                System.err.println("[CLIENT] thread failed");
            }
            System.out.println("[CLIENT] connection established");
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        processingSocket.disconnect();
                        Thread.sleep(1000);
                        System.out.println("[CLIENT] EXITING");
                        System.exit(0);
                    } catch (Exception ex) {
                        System.err.println("[CLIENT] timer error " + ex);
                    }
                }
            }, 8000);
            Thread.sleep(1000);
            processingSocket.getOutputStream().writeObject("Test String");
            Thread.sleep(1000);
            processingSocket.getOutputStream().writeObject("echo Test1234");
            Thread.sleep(250);
            ping();
            Thread.sleep(250);
            for (int i = 0; i < 10; i++) {
                processingSocket.getOutputStream().writeObject("echo " + Math.random());
                Thread.sleep(100);
            }
            Thread.sleep(500);
            processingSocket.getOutputStream().writeObject("shutdown");
        } else {
            System.err.println("[CLIENT] connection failed");
        }
    }
    
    public static void ping() throws Exception {
        PING.set(System.currentTimeMillis());
        PROCESSING_SOCKET.getOutputStream().writeObject(new NetCommand(NetCommand.Command.PING, PING.get()));
    }
    
    //protected abstract void onInput(D input, long timestamp) throws Exception;
    
}
