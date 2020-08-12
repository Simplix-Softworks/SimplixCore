package dev.simplix.core.database.sql.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

public interface PreparedStatementExecutor<T> {

  T execute(@NonNull PreparedStatement preparedStatement) throws SQLException;

}