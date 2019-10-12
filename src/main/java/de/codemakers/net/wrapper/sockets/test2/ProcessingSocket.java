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

import de.codemakers.base.Standard;
import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.base.util.tough.ToughRunnable;
import de.codemakers.net.entities.NetEndpoint;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ProcessingSocket<I extends InputStream, O extends OutputStream> extends AdvancedSocket implements Startable, Stoppable {
    
    protected final AtomicBoolean stopRequested = new AtomicBoolean(false);
    protected final AtomicReference<Thread> thread = new AtomicReference<>(null);
    
    public ProcessingSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public ProcessingSocket(NetEndpoint netEndpoint) {
        super(netEndpoint);
    }
    
    public ProcessingSocket(Socket socket) {
        super(socket);
    }
    
    public boolean isStopRequested() {
        return stopRequested.get();
    }
    
    protected ProcessingSocket setStopRequested(boolean stopRequested) {
        this.stopRequested.set(stopRequested);
        return this;
    }
    
    protected Thread getThread() {
        return thread.get();
    }
    
    private ProcessingSocket setThread(Thread thread) {
        this.thread.set(thread);
        return this;
    }
    
    public boolean isRunning() {
        final Thread thread = getThread();
        return thread != null && thread.isAlive() && !thread.isInterrupted();
    }
    
    @Override
    public I getInputStream() {
        return super.getInputStream();
    }
    
    @Override
    public O getOutputStream() {
        return super.getOutputStream();
    }
    
    @Override
    protected boolean onConnection(boolean successful) throws Exception {
        if (successful) {
            processOutputStream(this::toInternOutputStream);
            processInputStream(this::toInternInputStream);
        }
        return successful;
    }
    
    @Override
    protected boolean onDisconnection() throws Exception {
        return stop();
    }
    
    protected abstract O toInternOutputStream(OutputStream outputStream) throws Exception;
    
    protected abstract I toInternInputStream(InputStream inputStream) throws Exception;
    
    protected abstract ToughRunnable createInputProcessor(I inputStream, O outputStream);
    
    public boolean initInputProcessor() {
        if (isRunning()) {
            return false;
        }
        setThread(Standard.toughThread(() -> createInputProcessor(getInputStream(), getOutputStream()).run(this::error)));
        return getThread() != null;
    }
    
    @Override
    public boolean start() throws Exception {
        if (isRunning()) {
            return false;
        }
        setStopRequested(false);
        if (!initInputProcessor()) {
            return false;
        }
        getThread().start();
        return isRunning();
    }
    
    @Override
    public boolean stop() throws Exception {
        if (!isRunning()) {
            return false;
        }
        setStopRequested(true);
        if (getThread() != null) {
            getThread().interrupt(); //FIXME Hmmm?
        }
        setThread(null); //TODO Is this good?
        return !isRunning();
    }
    
    @Override
    public String toString() {
        return "ProcessingSocket{" + "stopRequested=" + stopRequested + ", thread=" + thread + ", connected=" + connected + ", localCloseRequested=" + localCloseRequested + ", isErrored=" + isErrored + ", error=" + error + ", netEndpoint=" + netEndpoint + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
