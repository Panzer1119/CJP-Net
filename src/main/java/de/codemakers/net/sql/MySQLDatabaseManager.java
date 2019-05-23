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

import de.codemakers.io.sql.DatabaseManager;

import java.sql.Connection;

public class MySQLDatabaseManager extends DatabaseManager {
    
    private final String connectionString;
    
    public MySQLDatabaseManager(String host, String database, String username, String password) {
        super(host, database, username, password);
        this.connectionString = MySQLUtil.getConnectionString(host, database, username, password);
    }
    
    protected String getConnectionString() {
        return connectionString;
    }
    
    @Override
    public Connection createConnection() throws Exception {
        return MySQLUtil.connect(connectionString);
    }
    
}