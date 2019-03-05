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

package de.codemakers.io.streams;

import de.codemakers.base.Standard;
import de.codemakers.base.logger.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OutputStreamManagerTest {
    
    public static final void main(String[] args) throws Exception {
        if (true) {
            test();
            return;
        }
        //Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().appendNewLine().appendThread().appendLocation().finishWithoutException();
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().finishWithoutException();
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        Standard.async(() -> {
            final InputStreamManager inputStreamManager = new InputStreamManager(pipedInputStream);
            Logger.log("[RECEIVER] inputStreamManager=" + inputStreamManager);
            final EndableInputStream endableInputStream_1 = inputStreamManager.createInputStream();
            Logger.log("[RECEIVER] endableInputStream_1=" + endableInputStream_1);
            final EndableInputStream endableInputStream_2 = inputStreamManager.createInputStream();
            Logger.log("[RECEIVER] endableInputStream_2=" + endableInputStream_2);
            int b = 0;
            while ((b = endableInputStream_1.read()) >= 0) {
                Logger.log("[RECEIVER] 1: " + ((byte) (b & 0xFF)));
            }
            Logger.log("[RECEIVER] 1 ENDED WITH " + ((byte) (b & 0xFF)));
            endableInputStream_1.close();
            final EndableInputStream endableInputStream_3 = inputStreamManager.createInputStream();
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
            final EndableInputStream endableInputStream_4 = inputStreamManager.createInputStream((byte) (4 & 0xFF));
            Logger.log("[RECEIVER] endableInputStream_4=" + endableInputStream_4);
            final DataInputStream dataInputStream = new DataInputStream(endableInputStream_4);
            Logger.log("[RECEIVER] dataInputStream=" + dataInputStream);
            Logger.log("[RECEIVER] 4 int \"" + dataInputStream.readInt() + "\"");
            Logger.log("[RECEIVER] 4 UTF \"" + dataInputStream.readUTF() + "\"");
            dataInputStream.close();
            //endableInputStream_4.close();
            inputStreamManager.close();
        });
        final OutputStreamManager outputStreamManager = new OutputStreamManager(pipedOutputStream);
        Logger.log("[SENDER] outputStreamManager=" + outputStreamManager);
        final EndableOutputStream endableOutputStream_1 = outputStreamManager.createOutputStream();
        Logger.log("[SENDER] endableOutputStream_1=" + endableOutputStream_1);
        final EndableOutputStream endableOutputStream_2 = outputStreamManager.createOutputStream();
        Logger.log("[SENDER] endableOutputStream_2=" + endableOutputStream_2);
        Logger.log("[SENDER] 1 " + Arrays.toString("Test 1".getBytes()));
        endableOutputStream_1.write("Test 1".getBytes());
        endableOutputStream_1.flush();
        Thread.sleep(500);
        endableOutputStream_1.close();
        final EndableOutputStream endableOutputStream_3 = outputStreamManager.createOutputStream();
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
        final EndableOutputStream endableOutputStream_4 = outputStreamManager.createOutputStream((byte) (4 & 0xFF));
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
        outputStreamManager.flush();
        outputStreamManager.close();
    }
    
    public static final void test() throws Exception {
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendThread().appendText(": ").appendObject().finishWithoutException();
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        final byte STREAM_ONE = 1;
        final byte STREAM_TWO = 2;
        Standard.async(() -> {
            Thread.currentThread().setName("SENDER    ");
            final OutputStreamManager outputStreamManager = new OutputStreamManager(pipedOutputStream);
            Logger.log("outputStreamManager=" + outputStreamManager);
            final ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(() -> Standard.silentError(() -> {
                Thread.currentThread().setName("SENDER-" + STREAM_ONE + "  ");
                final EndableOutputStream endableOutputStream = outputStreamManager.createOutputStream(STREAM_ONE);
                Logger.log("endableOutputStream=" + endableOutputStream);
                final DataOutputStream dataOutputStream = new DataOutputStream(endableOutputStream);
                Logger.log("endableOutputStream=" + dataOutputStream);
                final String text_1 = "Test String 1 von " + Thread.currentThread().getName();
                Logger.log("sending 1 \"" + text_1 + "\"");
                dataOutputStream.writeUTF(text_1);
                Thread.sleep(1000);
                final String text_2 = "Test String 2 von " + Thread.currentThread().getName();
                Logger.log("sending 2 \"" + text_2 + "\"");
                dataOutputStream.writeUTF(text_2);
                dataOutputStream.close();
                //endableOutputStream.close();
            }));
            executorService.submit(() -> Standard.silentError(() -> {
                Thread.currentThread().setName("SENDER-" + STREAM_TWO + "  ");
                final EndableOutputStream endableOutputStream = outputStreamManager.createOutputStream(STREAM_TWO);
                Logger.log("endableOutputStream=" + endableOutputStream);
        
                endableOutputStream.close();
            }));
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
            outputStreamManager.close();
            //pipedOutputStream.flush();
            //pipedOutputStream.close();
        });
        Standard.async(() -> {
            Thread.currentThread().setName("RECEIVER  ");
            final InputStreamManager inputStreamManager = new InputStreamManager(pipedInputStream);
            Logger.log("inputStreamManager=" + inputStreamManager);
            final ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.submit(() -> Standard.silentError(() -> {
                Thread.currentThread().setName("RECEIVER-" + STREAM_ONE);
                final EndableInputStream endableInputStream = inputStreamManager.createInputStream(STREAM_ONE);
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
            }));
            executorService.submit(() -> Standard.silentError(() -> {
                Thread.currentThread().setName("RECEIVER-" + STREAM_TWO);
                final EndableInputStream endableInputStream = inputStreamManager.createInputStream(STREAM_TWO);
                Logger.log("endableInputStream=" + endableInputStream);
    
                endableInputStream.close();
            }));
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
            inputStreamManager.close();
            //pipedInputStream.close();
        });
    }
    
}
