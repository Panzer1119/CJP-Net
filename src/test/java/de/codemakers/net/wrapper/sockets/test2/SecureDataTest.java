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

import de.codemakers.base.logger.Logger;
import de.codemakers.io.file.AdvancedFile;
import de.codemakers.security.interfaces.Decryptor;
import de.codemakers.security.interfaces.Encryptor;
import de.codemakers.security.util.EasyCryptUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class SecureDataTest {
    
    public static final AdvancedFile KEY_FILE = new AdvancedFile("E:\\Temp\\keys\\aes_256_2342345235235.key");
    
    public static SecretKey resolveAESSecretKey() {
        try {
            if (KEY_FILE.exists()) {
                return new SecretKeySpec(KEY_FILE.readBytes(), "AES");
            } else {
                final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256, SecureRandom.getInstanceStrong());
                final SecretKey secretKey = keyGenerator.generateKey();
                KEY_FILE.writeBytes(secretKey.getEncoded());
                return secretKey;
            }
        } catch (Exception ex) {
            Logger.handleError(ex);
            return null;
        }
    }
    
    public static Encryptor resolveAESEncryptor(SecretKey secretKey) {
        try {
            return EasyCryptUtil.encryptorOfCipher(Cipher.getInstance("AES"), secretKey);
        } catch (Exception ex) {
            Logger.handleError(ex);
            return null;
        }
    }
    
    public static Decryptor resolveAESDecryptor(SecretKey secretKey) {
        try {
            return EasyCryptUtil.decryptorOfCipher(Cipher.getInstance("AES"), secretKey);
        } catch (Exception ex) {
            Logger.handleError(ex);
            return null;
        }
    }
    
}
