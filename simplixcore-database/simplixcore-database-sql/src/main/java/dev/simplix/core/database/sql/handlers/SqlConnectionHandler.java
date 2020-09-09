package dev.simplix.core.database.sql.handlers;

import dev.simplix.core.database.sql.SqlDatabaseConnection;
import dev.simplix.core.database.sql.handler.ConnectionHandler;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class SqlConnectionHandler implements ConnectionHandler {

  private SqlDatabaseConnection sqlDatabaseConnection;
  private DataSource dataSource;

  @Override
  public void init(@NonNull SqlDatabaseConnection mysqlConnection) {
    this.sqlDatabaseConnection = mysqlConnection;
    this.dataSource = mysqlConnection.getDataSource();
  }

  @Override
  public void preConnect() {
    // Intentional blank
  }

  @Override
  public Connection openConnection() throws SQLException {
    return this.dataSource.getConnection(
        this.sqlDatabaseConnection.getUser(),
        this.sqlDatabaseConnection.getPass());
  }

  @Override
  public void updateConnection() {
    // Intentional blank
  }

  @Override
  public Connection connection() {
    try {
      return openConnection();
    } catch (SQLException sqlException) {
      throw new RuntimeException(sqlException);
    }
  }

  @Override
  public void finishConnection(@Nullable Connection connection) {
    SqlDatabaseConnection.close(connection);
  }
}
