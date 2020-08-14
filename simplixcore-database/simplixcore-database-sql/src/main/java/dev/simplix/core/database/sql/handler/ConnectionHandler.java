package dev.simplix.core.database.sql.handler;

import dev.simplix.core.database.sql.SqlDatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;

public interface ConnectionHandler {

  void init(@NonNull SqlDatabaseConnection sqlDatabaseConnection);

  void preConnect();

  Connection openConnection() throws SQLException;

  void updateConnection();

  Connection connection();

  void finishConnection(Connection connection);
}