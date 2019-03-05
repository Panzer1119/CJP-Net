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

import de.codemakers.base.logger.Logger;

public class OutputStreamManagerTest {
    
    public static final void main(String[] args) throws Exception {
        final OutputStreamManager outputStreamManager = new OutputStreamManager(null);
        Logger.log("outputStreamManager=" + outputStreamManager);
        Logger.log("outputStreamManager.getLowestId()=" + outputStreamManager.getLowestId());
        outputStreamManager.getOutputStreams().put((byte) 34, null);
        Logger.log("outputStreamManager.getLowestId()=" + outputStreamManager.getLowestId());
    }
    
}
