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

import de.codemakers.base.util.interfaces.Startable;
import de.codemakers.base.util.interfaces.Stoppable;
import de.codemakers.base.util.tough.ToughRunnable;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class ProcessingSocket<I extends InputStream, O extends OutputStream, D> extends AdvancedSocket implements Startable, Stoppable {
    
    protected volatile Thread thread = null;
    protected volatile boolean stopRequested = false;
    
    public ProcessingSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    public ProcessingSocket(Socket socket) {
        super(socket);
    }
    
    public boolean isRunning() {
        return thread != null && thread.isAlive();
    }
    
    public boolean isStopRequested() {
        return stopRequested;
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
    protected void onConnection(boolean successful) throws Exception {
        super.onConnection(successful);
        if (successful) {
            processOutputStream(this::toInternOutputStream);
            processInputStream(this::toInternInputStream);
        }
    }
    
    abstract O toInternOutputStream(OutputStream outputStream) throws Exception;
    
    abstract I toInternInputStream(InputStream inputStream) throws Exception;
    
    protected abstract ToughRunnable createInputProcessor(I inputStream, O outputStream);
    
    protected abstract void onInput(D input, long timestamp) throws Exception;
    
    public boolean initInputProcessor() {
        if (isRunning()) {
            return false;
        }
        thread = new Thread(() -> createInputProcessor(getInputStream(), getOutputStream()).run((throwable) -> error(throwable)));
        return thread != null;
    }
    
    @Override
    public boolean start() throws Exception {
        if (isRunning()) {
            return false;
        }
        stopRequested = false;
        if (!initInputProcessor()) {
            return false;
        }
        thread.start();
        return isRunning();
    }
    
    @Override
    public boolean stop() throws Exception {
        if (!isRunning()) {
            return false;
        }
        stopRequested = true;
        if (thread != null) {
            thread.interrupt();
        }
        thread = null; //TODO Is this good?
        return !isRunning();
    }
    
    @Override
    public String toString() {
        return "ProcessingSocket{" + "thread=" + thread + ", stopRequested=" + stopRequested + ", connected=" + connected + ", localCloseRequested=" + localCloseRequested + ", isErrored=" + isErrored + ", error=" + error + ", inetAddress=" + inetAddress + ", port=" + port + ", socket=" + socket + ", outputStream=" + outputStream + ", inputStream=" + inputStream + '}';
    }
    
}
