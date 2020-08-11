package dev.simplix.core.database.sql.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementExecutor<T> {

  T execute(PreparedStatement preparedStatement) throws SQLException;

}