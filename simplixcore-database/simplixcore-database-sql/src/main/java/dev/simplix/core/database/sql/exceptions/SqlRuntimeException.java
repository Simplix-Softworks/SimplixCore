package dev.simplix.core.database.sql.exceptions;

import java.sql.SQLException;

public class SqlRuntimeException extends RuntimeException {

  public SqlRuntimeException(SQLException exception) {
    super(exception);
  }

}
