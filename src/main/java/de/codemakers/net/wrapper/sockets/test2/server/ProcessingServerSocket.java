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

package de.codemakers.net.wrapper.sockets.test2.server;

import de.codemakers.base.Standard;
import de.codemakers.base.util.tough.ToughRunnable;
import de.codemakers.net.wrapper.sockets.test2.AbstractSocket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ProcessingServerSocket<S extends AbstractSocket> extends AdvancedServerSocket {
    
    protected final AtomicReference<Thread> thread = new AtomicReference<>(null);
    
    public ProcessingServerSocket(int port) {
        super(port);
    }
    
    public ProcessingServerSocket(ServerSocket serverSocket) {
        super(serverSocket);
    }
    
    protected Thread getThread() {
        return thread.get();
    }
    
    private ProcessingServerSocket setThread(Thread thread) {
        this.thread.set(thread);
        return this;
    }
    
    public boolean isRunning() {
        final Thread thread = getThread();
        return thread != null && thread.isAlive() && !thread.isInterrupted();
    }
    
    protected abstract S onSocket(Socket socket) throws Exception;
    
    protected abstract ToughRunnable createSocketProcessor(ServerSocket serverSocket);
    
    public boolean initSocketProcessor() {
        if (isRunning()) {
            return false;
        }
        setThread(Standard.toughThread(() -> createSocketProcessor(getServerSocket()).run(this::error)));
        return getThread() != null;
    }
    
    @Override
    protected boolean onStart(boolean successful) throws Exception {
        if (isRunning() || !successful) {
            return false;
        }
        if (!initSocketProcessor()) {
            return false;
        }
        getThread().start();
        return isRunning();
    }
    
    @Override
    protected boolean onStop() throws Exception {
        if (!isRunning()) {
            return false;
        }
        if (getThread() != null) {
            getThread().interrupt(); //FIXME Hmmm?
        }
        setThread(null); //TODO Is this good?
        return !isRunning();
    }
    
    @Override
    public String toString() {
        return "ProcessingServerSocket{" + "thread=" + thread + ", started=" + started + ", stopRequested=" + stopRequested + ", errored=" + errored + ", error=" + error + ", port=" + port + ", serverSocket=" + serverSocket + '}';
    }
    
}
