package dev.simplix.core.database.sql.exceptions;

import java.sql.SQLException;
import org.jetbrains.annotations.Nullable;

public class SqlRuntimeException extends RuntimeException {

  public SqlRuntimeException(@Nullable SQLException exception) {
    super(exception);
  }

}
