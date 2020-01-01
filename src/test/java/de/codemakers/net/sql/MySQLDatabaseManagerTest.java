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

package de.codemakers.net.sql;

import de.codemakers.base.logger.LogLevel;
import de.codemakers.base.logger.Logger;

import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLDatabaseManagerTest {
    
    public static void main(String[] args) throws Exception {
        Logger.getDefaultAdvancedLeveledLogger().setMinimumLogLevel(LogLevel.FINER);
        final MySQLDatabaseManager mySQLDatabaseManager = new MySQLDatabaseManager(args[0], args[1], args[2], args[3]);
        Logger.log("mySQLDatabaseManager=" + mySQLDatabaseManager);
        mySQLDatabaseManager.createAutoClosingConnection().consume(Logger::log);
        mySQLDatabaseManager.createAutoClosingConnection().consume((connection) -> {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(args[4]);
            int a = 0;
            while (resultSet.next()) {
                String temp = "a=" + a;
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    temp += ", i=" + i + ": \"";
                    temp += resultSet.getObject(i) + "\"";
                }
                Logger.log(temp);
            }
            //statement.close(); //Necessary? Because connection will be closed later and maybe closes this too?
        });
    }
    
}
