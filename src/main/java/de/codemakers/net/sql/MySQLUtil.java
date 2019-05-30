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
    
    public static final int STANDARD_PORT = 3306;
    
    public static final String FORMAT_CONNECTION_HOST_PORT_DATABASE_USERNAME_PASSWORD = "jdbc:mysql://%s:%d/%s?user=%s&password=%s";
    public static final String FORMAT_CONNECTION_HOST_DATABASE_USERNAME_PASSWORD = "jdbc:mysql://%s/%s?user=%s&password=%s";
    public static final String FORMAT_CONNECTION_HOST_PORT_DATABASE = "jdbc:mysql://%s:%d/%s";
    public static final String FORMAT_CONNECTION_HOST_DATABASE = "jdbc:mysql://%s/%s";
    
    public static String getConnectionString(String host, String username, byte[] password, String database) {
        return getConnectionString(host, -1, username, new String(password), database);
    }
    
    public static String getConnectionString(String host, int port, String username, byte[] password, String database) {
        return getConnectionString(host, port, username, new String(password), database);
    }
    
    public static String getConnectionString(String host, String username, String password, String database) {
        return getConnectionString(host, -1, username, password, database);
    }
    
    public static String getConnectionString(String host, int port, String username, String password, String database) {
        if (port == -1) {
            return String.format(FORMAT_CONNECTION_HOST_DATABASE_USERNAME_PASSWORD, host, database, username, password);
        } else {
            return String.format(FORMAT_CONNECTION_HOST_PORT_DATABASE_USERNAME_PASSWORD, host, port, database, username, password);
        }
    }
    
    public static String getConnectionString(String host, String database) {
        return getConnectionString(host, -1, database);
    }
    
    public static String getConnectionString(String host, int port, String database) {
        if (port == -1) {
            return String.format(FORMAT_CONNECTION_HOST_DATABASE, host, database);
        } else {
            return String.format(FORMAT_CONNECTION_HOST_PORT_DATABASE, host, port, database);
        }
    }
    
    public static Connection connect(String host, String database, String username, byte[] password) throws Exception {
        return connect(host, database, username, new String(password));
    }
    
    public static Connection connect(String host, String database, String username, String password) throws Exception {
        return connect(getConnectionString(host, database, username, password));
    }
    
    public static Connection connect(String host, int port, String database, String username, byte[] password) throws Exception {
        return connect(host, port, database, username, new String(password));
    }
    
    public static Connection connect(String host, int port, String database, String username, String password) throws Exception {
        return connect(getConnectionString(host, port, database, username, password));
    }
    
    public static Connection connect(String connectionString) throws Exception {
        return DriverManager.getConnection(connectionString);
    }
    
    public static Connection connect(String connectionString, String username, String password) throws Exception {
        return DriverManager.getConnection(connectionString, username, password);
    }
    
}
