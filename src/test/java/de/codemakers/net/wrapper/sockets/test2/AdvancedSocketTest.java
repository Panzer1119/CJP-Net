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

package de.codemakers.net.wrapper.sockets.test2;

import de.codemakers.security.entities.SecureData;
import de.codemakers.security.interfaces.Decryptor;
import de.codemakers.security.interfaces.Encryptor;

import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class AdvancedSocketTest {
    
    public static final SecretKey SECRET_KEY = SecureDataTest.resolveAESSecretKey();
    public static final Encryptor ENCRYPTOR = SecureDataTest.resolveAESEncryptor(SECRET_KEY);
    public static final Decryptor DECRYPTOR = SecureDataTest.resolveAESDecryptor(SECRET_KEY);
    
    public static final void main(String[] args) throws Exception {
        final InetAddress inetAddress = InetAddress.getLocalHost();
        final int port = 1234;
        final AdvancedSocket advancedSocket = new AdvancedSocket(inetAddress, port);
        System.out.println("[CLIENT] advancedSocket 1=" + advancedSocket);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    advancedSocket.disconnect();
                    Thread.sleep(1000);
                    System.out.println("[CLIENT] EXITING");
                    System.exit(0);
                } catch (Exception ex) {
                    System.err.println("[SERVER] timer error " + ex);
                }
            }
        }, 10000);
        System.out.println(System.currentTimeMillis());
        advancedSocket.connect();
        System.out.println(System.currentTimeMillis());
        System.out.println("[CLIENT] advancedSocket 2=" + advancedSocket);
        advancedSocket.processOutputStream(ObjectOutputStream::new);
        System.out.println("[CLIENT] advancedSocket 3=" + advancedSocket);
        advancedSocket.processInputStream(ObjectInputStream::new);
        System.out.println("[CLIENT] advancedSocket 4=" + advancedSocket);
        Thread.sleep(1000);
        advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject("Test String");
        Thread.sleep(1000);
        final String string_1 = "This is a test String, it even has an \"!\" in it!";
        final byte[] bytes_1 = string_1.getBytes();
        final SecureData secureData_1 = new SecureData(bytes_1, ENCRYPTOR);
        advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject(secureData_1);
        Thread.sleep(3000);
        //advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject(null);
        advancedSocket.getOutputStream(ObjectOutputStream.class).writeObject("shutdown");
    }
    
}
