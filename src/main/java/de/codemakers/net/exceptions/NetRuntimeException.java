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

package de.codemakers.net.exceptions;

import de.codemakers.base.exceptions.CJPRuntimeException;

public class NetRuntimeException extends CJPRuntimeException {
    
    public NetRuntimeException() {
        super();
    }
    
    public NetRuntimeException(String message) {
        super(message);
    }
    
    public NetRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NetRuntimeException(Throwable cause) {
        super(cause);
    }
    
    public NetRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
