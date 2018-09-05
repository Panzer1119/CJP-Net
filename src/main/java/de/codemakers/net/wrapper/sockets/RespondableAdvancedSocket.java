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

package de.codemakers.net.wrapper.sockets;

import de.codemakers.base.action.ReturningAction;
import de.codemakers.base.exceptions.CJPNullPointerException;
import de.codemakers.base.util.Waiter;
import de.codemakers.base.util.tough.ToughFunction;
import de.codemakers.base.util.tough.ToughSupplier;
import de.codemakers.net.events.RequestResponseEvent;
import de.codemakers.net.events.ResponseEvent;
import de.codemakers.net.exceptions.NotAcceptedResponseRuntimeException;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class RespondableAdvancedSocket extends AdvancedSocket {
    
    private final Map<Long, ResponseEvent> requestedResponses = new ConcurrentHashMap<>();
    
    public RespondableAdvancedSocket(Socket socket) {
        super(socket);
    }
    
    public RespondableAdvancedSocket(InetAddress inetAddress, int port) {
        super(inetAddress, port);
    }
    
    protected abstract ResponseEvent processInput(RequestResponseEvent requestResponseEvent, ToughFunction<Object, ResponseEvent> createResponseEvent, ToughSupplier<ResponseEvent> createNotAcceptedResponseEvent) throws Exception;
    
    @Override
    protected void processInput(long timestamp, Object input) throws Exception {
        if (input instanceof ResponseEvent) {
            final ResponseEvent responseEvent = (ResponseEvent) input;
            requestedResponses.put(responseEvent.getResponseId(), responseEvent);
        } else if (input instanceof RequestResponseEvent) {
            final RequestResponseEvent requestResponseEvent = (RequestResponseEvent) input;
            final ToughSupplier<ResponseEvent> createNotAcceptedResponseEvent = () -> new ResponseEvent(requestResponseEvent.getResponseId(), null, false);
            final ResponseEvent responseEvent = processInput(requestResponseEvent, (data) -> new ResponseEvent(requestResponseEvent.getResponseId(), data, true), createNotAcceptedResponseEvent);
            if (responseEvent != null) {
                send(responseEvent);
            } else {
                send(createNotAcceptedResponseEvent.getWithoutException());
            }
        } else {
            super.processInput(timestamp, input);
        }
    }
    
    private RequestResponseEvent sendRequestResponse(Object data) throws Exception {
        final RequestResponseEvent requestResponseEvent = new RequestResponseEvent(data);
        requestedResponses.put(requestResponseEvent.getResponseId(), null);
        send(requestResponseEvent);
        return requestResponseEvent;
    }
    
    public <T> ReturningAction<T> requestResponse(Object data) {
        return requestResponse(data, -1, null);
    }
    
    public <T> ReturningAction<T> requestResponse(Object data, long timeout, TimeUnit unit) {
        return new ReturningAction<>(() -> {
            final RequestResponseEvent requestResponseEvent = sendRequestResponse(data);
            if (!new Waiter(timeout, unit, () -> requestedResponses.get(requestResponseEvent.getResponseId()) != null).waitFor()) {
                throw new TimeoutException();
            }
            final ResponseEvent responseEvent = requestedResponses.remove(requestResponseEvent.getResponseId());
            if (responseEvent == null) {
                throw new CJPNullPointerException(); //This should never happen
            } else if (responseEvent.isAccepted()) {
                return (T) responseEvent.getData();
            } else {
                throw new NotAcceptedResponseRuntimeException();
            }
        });
    }
    
}
