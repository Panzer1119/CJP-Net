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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ServerSocketTest {
    
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
                Object object = null;
                while ((object = advancedSocket.getInputStream(ObjectInputStream.class).readObject()) != null) {
                    System.out.println("[SERVER] got: \"" + object + "\"");
                    if (object.equals("shutdown")) {
                        shutdownRequested = true;
                        break outer;
                    }
                }
            }
            System.out.println("[SERVER] shutdownRequested=" + shutdownRequested);
        } catch (Exception ex) {
            System.err.println("[SERVER] " + ex);
            ex.printStackTrace();
        }
    }
    
}
