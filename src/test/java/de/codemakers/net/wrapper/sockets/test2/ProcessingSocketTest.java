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

import de.codemakers.base.util.tough.ToughRunnable;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessingSocketTest {
    
    public static void main(String[] args) throws Exception {
        final InetAddress inetAddress = InetAddress.getLocalHost();
        final int port = 1234;
        final ProcessingSocket<ObjectInputStream, ObjectOutputStream, Object> processingSocket = new ProcessingSocket<ObjectInputStream, ObjectOutputStream, Object>(inetAddress, port) {
            @Override
            ObjectOutputStream toInternOutputStream(OutputStream outputStream) throws Exception {
                return new ObjectOutputStream(outputStream);
            }
            
            @Override
            ObjectInputStream toInternInputStream(InputStream inputStream) throws Exception {
                return new ObjectInputStream(inputStream);
            }
            
            @Override
            protected ToughRunnable createInputProcessor(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
                return () -> {
                    while (!isLocalCloseRequested() && !isStopRequested()) {
                        final Object input = inputStream.readObject();
                        final long timestamp = System.currentTimeMillis();
                        if (input == null) {
                            break;
                        }
                        try {
                            onInput(input, timestamp);
                        } catch (Exception ex) {
                            System.err.println("[CLIENT] input error " + ex);
                        }
                    }
                    outputStream.close(); //TODO Good?
                };
            }
            
            @Override
            protected void onInput(Object input, long timestamp) throws Exception {
                System.out.println("[CLIENT][" + timestamp + "] input: \"" + input + "\"");
            }
        };
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
            Thread.sleep(1000);
            processingSocket.getOutputStream().writeObject("shutdown");
        } else {
            System.err.println("[CLIENT] connection failed");
        }
    }
    
}
