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

public class OutputStreamManagerTest {
    
    public static final void main(String[] args) throws Exception {
        //Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().appendNewLine().appendThread().appendLocation().finishWithoutException();
        Logger.getDefaultAdvancedLeveledLogger().createLogFormatBuilder().appendLogLevel().appendText(": ").appendObject().finishWithoutException();
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        Standard.async(() -> {
            final InputStreamManager inputStreamManager = new InputStreamManager(pipedInputStream);
            Logger.log("[RECEIVER] inputStreamManager=" + inputStreamManager);
            final EndableInputStream endableInputStream_1 = inputStreamManager.createEndableInputStream();
            Logger.log("[RECEIVER] endableInputStream_1=" + endableInputStream_1);
            final EndableInputStream endableInputStream_2 = inputStreamManager.createEndableInputStream();
            Logger.log("[RECEIVER] endableInputStream_2=" + endableInputStream_2);
            int b = 0;
            while ((b = endableInputStream_1.read()) >= 0) {
                Logger.log("[RECEIVER] 1: " + ((byte) (b & 0xFF)));
            }
            Logger.log("[RECEIVER] 1 ENDED WITH " + ((byte) (b & 0xFF)));
            endableInputStream_1.close();
            final EndableInputStream endableInputStream_3 = inputStreamManager.createEndableInputStream();
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
            final EndableInputStream endableInputStream_4 = inputStreamManager.createEndableInputStream((byte) (4 & 0xFF));
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
        final EndableOutputStream endableOutputStream_1 = outputStreamManager.createEndableOutputStream();
        Logger.log("[SENDER] endableOutputStream_1=" + endableOutputStream_1);
        final EndableOutputStream endableOutputStream_2 = outputStreamManager.createEndableOutputStream();
        Logger.log("[SENDER] endableOutputStream_2=" + endableOutputStream_2);
        Logger.log("[SENDER] 1 " + Arrays.toString("Test 1".getBytes()));
        endableOutputStream_1.write("Test 1".getBytes());
        endableOutputStream_1.flush();
        Thread.sleep(500);
        endableOutputStream_1.close();
        final EndableOutputStream endableOutputStream_3 = outputStreamManager.createEndableOutputStream();
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
        final EndableOutputStream endableOutputStream_4 = outputStreamManager.createEndableOutputStream((byte) (4 & 0xFF));
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
    
}
