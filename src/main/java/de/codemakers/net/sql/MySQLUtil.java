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

package de.codemakers.net.sql;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLUtil {
    
    public static final String FORMAT_CONNECTION_H_D_U_P = "jdbc:mysql://%s/%s?user=%s&password=%s";
    public static final String FORMAT_CONNECTION_H_D = "jdbc:mysql://%s/%s";
    
    public static Connection connect(String host, String database, String username, byte[] password) throws Exception {
        return connect(host, database, username, new String(password));
    }
    
    public static Connection connect(String host, String database, String username, String password) throws Exception {
        return connect(String.format(FORMAT_CONNECTION_H_D_U_P, host, database, username, password));
    }
    
    public static Connection connect(String connectionString) throws Exception {
        return DriverManager.getConnection(connectionString);
    }
    
    public static Connection connect(String connectionString, String username, String password) throws Exception {
        return DriverManager.getConnection(connectionString, username, password);
    }
    
}
