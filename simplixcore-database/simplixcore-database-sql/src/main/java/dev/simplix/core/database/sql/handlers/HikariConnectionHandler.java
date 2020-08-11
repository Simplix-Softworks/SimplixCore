package dev.simplix.core.database.sql.handlers;

import java.sql.Connection;
import java.sql.SQLException;

public final class HikariConnectionHandler extends SqlConnectionHandler {

  @Override
  public Connection openConnection() throws SQLException {
    return getDataSource().getConnection();
  }
}
