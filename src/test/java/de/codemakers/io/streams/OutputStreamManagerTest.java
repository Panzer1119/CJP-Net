/*
 *     Copyright 2018 - 2020 Paul Hagedorn (Panzer1119)
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

package de.codemakers.io.streams;

import de.codemakers.base.Standard;
import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OutputStreamManagerTest {
    
    public static final void main(String[] args) throws Exception {
        Logger.getDefaultAdvancedLeveledLogger().setMinimumLogLevel(LogLevel.FINEST);
        if (true) {
            test();
            return;
        }
        //Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().appendNewLine().appendThread().appendLocation().finishWithoutException();
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().finishWithoutException();
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        Standard.async(() -> {
            final TunnelInputStream tunnelInputStream = new TunnelInputStream(pipedInputStream);
            Logger.log("[RECEIVER] tunnelInputStream=" + tunnelInputStream);
            final EndableInputStream endableInputStream_1 = tunnelInputStream.createInputStream();
            Logger.log("[RECEIVER] endableInputStream_1=" + endableInputStream_1);
            final EndableInputStream endableInputStream_2 = tunnelInputStream.createInputStream();
            Logger.log("[RECEIVER] endableInputStream_2=" + endableInputStream_2);
            int b = 0;
            while ((b = endableInputStream_1.read()) >= 0) {
                Logger.log("[RECEIVER] 1: " + ((byte) (b & 0xFF)));
            }
            Logger.log("[RECEIVER] 1 ENDED WITH " + ((byte) (b & 0xFF)));
            endableInputStream_1.close();
            final EndableInputStream endableInputStream_3 = tunnelInputStream.createInputStream();
            Logger.log("[RECEIVER] endableInputStream_3=" + endableInputStream_3);
            while ((b = endableInputStream_2.read()) >= 0) {
                Logger.log("[RECEIVER] 2: " + ((byte) (b & 0xFF)));
            }
            Logger.log("[RECEIVER] 2 ENDED WITH " + ((byte) (b & 0xFF)));
            while ((b = endableInputStream_3.read()) >= 0) {
                Logger.log("[RECEIVER] 3: " + ((byte) (b & 0xFF)));
            }
            Logger.log("[RECEIVER] 3 ENDED WITH " + ((byte) (b & 0xFF)));
            endableInputStream_2.close();
            endableInputStream_3.close();
            final EndableInputStream endableInputStream_4 = tunnelInputStream.getOrCreateInputStream((byte) (4 & 0xFF));
            Logger.log("[RECEIVER] endableInputStream_4=" + endableInputStream_4);
            final DataInputStream dataInputStream = new DataInputStream(endableInputStream_4);
            Logger.log("[RECEIVER] dataInputStream=" + dataInputStream);
            Logger.log("[RECEIVER] 4 int \"" + dataInputStream.readInt() + "\"");
            Logger.log("[RECEIVER] 4 UTF \"" + dataInputStream.readUTF() + "\"");
            dataInputStream.close();
            //endableInputStream_4.close();
            tunnelInputStream.close();
        });
        final TunnelOutputStream tunnelOutputStream = new TunnelOutputStream(pipedOutputStream);
        Logger.log("[SENDER] tunnelOutputStream=" + tunnelOutputStream);
        final EndableOutputStream endableOutputStream_1 = tunnelOutputStream.createOutputStream();
        Logger.log("[SENDER] endableOutputStream_1=" + endableOutputStream_1);
        final EndableOutputStream endableOutputStream_2 = tunnelOutputStream.createOutputStream();
        Logger.log("[SENDER] endableOutputStream_2=" + endableOutputStream_2);
        Logger.log("[SENDER] 1 " + Arrays.toString("Test 1".getBytes()));
        endableOutputStream_1.write("Test 1".getBytes());
        endableOutputStream_1.flush();
        Thread.sleep(500);
        endableOutputStream_1.close();
        final EndableOutputStream endableOutputStream_3 = tunnelOutputStream.createOutputStream();
        Logger.log("[SENDER] endableOutputStream_3=" + endableOutputStream_3);
        Logger.log("[SENDER] 3 " + Arrays.toString("Test 3".getBytes()));
        endableOutputStream_3.write("Test 3".getBytes());
        endableOutputStream_3.flush();
        Thread.sleep(500);
        Logger.log("[SENDER] 2 " + Arrays.toString("Test 2".getBytes()));
        endableOutputStream_2.write("Test 2".getBytes());
        endableOutputStream_2.flush();
        Thread.sleep(500);
        endableOutputStream_3.close();
        endableOutputStream_2.close();
        Thread.sleep(1000);
        final EndableOutputStream endableOutputStream_4 = tunnelOutputStream.getOrCreateOutputStream((byte) (4 & 0xFF));
        Logger.log("[SENDER] endableOutputStream_4=" + endableOutputStream_4);
        final DataOutputStream dataOutputStream = new DataOutputStream(endableOutputStream_4);
        Logger.log("[SENDER] dataOutputStream=" + dataOutputStream);
        Logger.log("[SENDER] 4 int \"" + 42 + "\"");
        dataOutputStream.writeInt(42);
        Logger.log("[SENDER] 4 UTF \"" + "Test String 4" + "\"");
        dataOutputStream.writeUTF("Test String 4");
        dataOutputStream.flush();
        dataOutputStream.close();
        //endableOutputStream_4.close();
        tunnelOutputStream.flush();
        tunnelOutputStream.close();
    }
    
    public static final void test() throws Exception {
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendTimestamp().appendThread().appendText(": ").appendObject().finishWithoutException();
        Logger.setLogger(Logger.getDefaultAdvancedLogger());
        /*
        Logger.setLogger(new AdvancedLogger() {
            @Override
            protected void logFinal(Object object) {
                Standard.SYSTEM_OUTPUT_STREAM.println(object);
                Standard.SYSTEM_OUTPUT_STREAM.flush();
            }
    
            @Override
            protected void logErrorFinal(Object object, Throwable throwable) {
                if (object != null) {
                    Standard.SYSTEM_ERROR_STREAM.println(object);
                }
                if (throwable != null) {
                    throwable.printStackTrace(Standard.SYSTEM_ERROR_STREAM);
                }
                Standard.SYSTEM_ERROR_STREAM.flush();
            }
        });
        */
        final int times = 10;
        /*
        final PipedOutputStream pipedOutputStream = new PipedOutputStream() {
            @Override
            public void close() throws IOException {
                //Logger.logError("WTF WHO CALLED CLOSE ON THIS " + getClass().getSimpleName() + "?!", new Exception());
                super.close();
            }
        };
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream) {
            @Override
            public void close() throws IOException {
                //Logger.logError("WTF WHO CALLED CLOSE ON THIS " + getClass().getSimpleName() + "?!", new Exception());
                super.close();
            }
        };
        final OutputStream outputStream = pipedOutputStream;
        final InputStream inputStream = pipedInputStream;
        */ //Pipes are broken, because they do not like too many threads
        final int port = 3254;
        final ServerSocket serverSocket = new ServerSocket(port);
        Logger.log("serverSocket=" + serverSocket);
        final Socket socket_1 = new Socket(InetAddress.getLocalHost(), port);
        Logger.log("socket_1=" + socket_1);
        final Socket socket_2 = serverSocket.accept();
        Logger.log("socket_2=" + socket_2);
        final OutputStream outputStream = socket_1.getOutputStream();
        final InputStream inputStream = socket_2.getInputStream();
        final byte STREAM_ONE = 1;
        final byte STREAM_TWO = 2;
        final byte STREAM_THREE = 3;
        final ExecutorService executorService_1 = Executors.newFixedThreadPool(2);
        Logger.log("executorService_1=" + executorService_1);
        executorService_1.submit(() -> {
            final Throwable throwable_1 = Standard.silentError(() -> {
                Thread.currentThread().setName("RECEIVER  ");
                final TunnelInputStream tunnelInputStream = new TunnelInputStream(inputStream);
                Logger.log("tunnelInputStream=" + tunnelInputStream);
                final ExecutorService executorService_2 = Executors.newFixedThreadPool(3);
                Logger.log("executorService_2=" + executorService_2);
                executorService_2.submit(() -> {
                    final Throwable throwable_2 = Standard.silentError(() -> {
                        Thread.currentThread().setName("RECEIVER-" + STREAM_ONE);
                        final EndableInputStream endableInputStream = tunnelInputStream.getOrCreateInputStream(STREAM_ONE);
                        Logger.log("endableInputStream=" + endableInputStream);
                        final DataInputStream dataInputStream = new DataInputStream(endableInputStream);
                        Logger.log("dataInputStream=" + dataInputStream);
                        /*
                        String temp = null;
                        while ((temp = dataInputStream.readUTF()) != null) {
                            Logger.log("received \"" + temp + "\"");
                        }
                        */
                        Logger.log("received 1 \"" + dataInputStream.readUTF() + "\"");
                        Logger.log("received 2 \"" + dataInputStream.readUTF() + "\"");
                        dataInputStream.close();
                        //endableInputStream.close();
                    });
                    if (throwable_2 != null) {
                        Logger.logError("STREAM_ONE", throwable_2);
                    }
                });
                executorService_2.submit(() -> {
                    final Throwable throwable_2 = Standard.silentError(() -> {
                        Thread.currentThread().setName("RECEIVER-" + STREAM_TWO);
                        final EndableInputStream endableInputStream = tunnelInputStream.getOrCreateInputStream(STREAM_TWO);
                        Logger.log("endableInputStream=" + endableInputStream);
                        final ObjectInputStream objectInputStream = new ObjectInputStream(endableInputStream);
                        Logger.log("objectInputStream=" + objectInputStream);
                        final TestObject testObject_1 = (TestObject) objectInputStream.readObject();
                        Logger.log("received 1 " + testObject_1);
                        final TestObject testObject_2 = (TestObject) objectInputStream.readObject();
                        Logger.log("received 2 " + testObject_2);
                        objectInputStream.close();
                        //endableInputStream.close();
                    });
                    if (throwable_2 != null) {
                        Logger.logError("STREAM_TWO", throwable_2);
                    }
                });
                
                executorService_2.submit(() -> {
                    final Throwable throwable_2 = Standard.silentError(() -> {
                        //Thread.sleep(1500);
                        Thread.currentThread().setName("RECEIVER-" + STREAM_THREE);
                        final EndableInputStream endableInputStream = tunnelInputStream.getOrCreateInputStream(STREAM_THREE);
                        Logger.log("endableInputStream=" + endableInputStream);
                        final ObjectInputStream objectInputStream = new ObjectInputStream(endableInputStream);
                        Logger.log("objectInputStream=" + objectInputStream);
                        for (int i = 0; i < times; i++) {
                            final double random = objectInputStream.readDouble();
                            Logger.log("received " + i + " " + random);
                            //Logger.log("endableInputStream.available()=" + endableInputStream.available());
                            //Logger.log("objectInputStream.available() =" + objectInputStream.available());
                        }
                        Logger.logError("WTF");
                        objectInputStream.close();
                        //endableInputStream.close();
                    });
                    if (throwable_2 != null) {
                        Logger.logError("STREAM_THREE", throwable_2);
                    }
                });
                
                executorService_2.shutdown();
                Logger.log("executorService_2=" + executorService_2);
                executorService_2.awaitTermination(10, TimeUnit.MINUTES);
                Logger.log("executorService_2=" + executorService_2);
                Logger.log("closing " + tunnelInputStream);
                //Thread.sleep(1000);
                tunnelInputStream.close();
                //inputStream.close();
            });
            if (throwable_1 != null) {
                Logger.logError("RECEIVER", throwable_1);
            }
        });
        //Thread.sleep(500);
        executorService_1.submit(() -> {
            final Throwable throwable_1 = Standard.silentError(() -> {
                Thread.currentThread().setName("SENDER    ");
                final TunnelOutputStream tunnelOutputStream = new TunnelOutputStream(outputStream);
                Logger.log("tunnelOutputStream=" + tunnelOutputStream);
                final ExecutorService executorService_2 = Executors.newFixedThreadPool(3);
                Logger.log("executorService_2=" + executorService_2);
                executorService_2.submit(() -> {
                    final Throwable throwable_2 = Standard.silentError(() -> {
                        Thread.currentThread().setName("SENDER-" + STREAM_ONE + "  ");
                        final EndableOutputStream endableOutputStream = tunnelOutputStream.getOrCreateOutputStream(STREAM_ONE);
                        Logger.log("endableOutputStream=" + endableOutputStream);
                        final DataOutputStream dataOutputStream = new DataOutputStream(endableOutputStream);
                        Logger.log("endableOutputStream=" + dataOutputStream);
                        final String text_1 = "Test String 1 von " + Thread.currentThread().getName();
                        Logger.log("sending 1 \"" + text_1 + "\"");
                        dataOutputStream.writeUTF(text_1);
                        dataOutputStream.flush();
                        Thread.sleep(1000);
                        final String text_2 = "Test String 2 von " + Thread.currentThread().getName();
                        Logger.log("sending 2 \"" + text_2 + "\"");
                        dataOutputStream.writeUTF(text_2);
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        //endableOutputStream.close();
                    });
                    if (throwable_2 != null) {
                        Logger.logError("STREAM_ONE", throwable_2);
                    }
                });
                executorService_2.submit(() -> {
                    final Throwable throwable_2 = Standard.silentError(() -> {
                        Thread.currentThread().setName("SENDER-" + STREAM_TWO + "  ");
                        final EndableOutputStream endableOutputStream = tunnelOutputStream.getOrCreateOutputStream(STREAM_TWO);
                        Logger.log("endableOutputStream=" + endableOutputStream);
                        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(endableOutputStream);
                        Logger.log("objectOutputStream=" + objectOutputStream);
                        final TestObject testObject_1 = new TestObject("TestObject 1");
                        Logger.log("sending 1 " + testObject_1);
                        objectOutputStream.writeObject(testObject_1);
                        objectOutputStream.flush();
                        Thread.sleep(1000);
                        final TestObject testObject_2 = new TestObject("TestObject 2");
                        Logger.log("sending 2 " + testObject_2);
                        objectOutputStream.writeObject(testObject_2);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        //endableOutputStream.close();
                    });
                    if (throwable_2 != null) {
                        Logger.logError("STREAM_TWO", throwable_2);
                    }
                });
                
                executorService_2.submit(() -> {
                    final Throwable throwable_2 = Standard.silentError(() -> {
                        Thread.currentThread().setName("SENDER-" + STREAM_THREE + "  ");
                        final EndableOutputStream endableOutputStream = tunnelOutputStream.getOrCreateOutputStream(STREAM_THREE);
                        Logger.log("endableOutputStream=" + endableOutputStream);
                        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(endableOutputStream);
                        Logger.log("objectOutputStream=" + objectOutputStream);
                        for (int i = 0; i < times; i++) {
                            final double random = Math.random();
                            Logger.log("sending " + i + " " + random);
                            objectOutputStream.writeDouble(random);
                            //objectOutputStream.flush();
                            Thread.sleep(1000 / times);
                        }
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        //endableOutputStream.close();
                    });
                    if (throwable_2 != null) {
                        Logger.logError("STREAM_THREE", throwable_2);
                    }
                });
                
                executorService_2.shutdown();
                Logger.log("executorService_2=" + executorService_2);
                executorService_2.awaitTermination(10, TimeUnit.MINUTES);
                Logger.log("executorService_2=" + executorService_2);
                Logger.log("closing " + tunnelOutputStream);
                //Thread.sleep(1000);
                tunnelOutputStream.close();
                //outputStream.flush();
                //outputStream.close();
            });
            if (throwable_1 != null) {
                Logger.logError("SENDER  ", throwable_1);
            }
        });
        executorService_1.shutdown();
        Logger.log("executorService_1=" + executorService_1);
        executorService_1.awaitTermination(10, TimeUnit.MINUTES);
        Logger.log("executorService_1=" + executorService_1);
        Logger.log("closing " + socket_1);
        socket_1.close();
        Logger.log("closing " + socket_2);
        socket_2.close();
        Logger.log("closing " + serverSocket);
        serverSocket.close();
    }
    
    public static class TestObject implements Serializable {
        
        protected String name;
        private final double random = Math.random();
        
        public TestObject(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public double getRandom() {
            return random;
        }
        
        @Override
        public String toString() {
            return "TestObject{" + "name='" + name + '\'' + ", random=" + random + '}';
        }
        
    }
    
}
