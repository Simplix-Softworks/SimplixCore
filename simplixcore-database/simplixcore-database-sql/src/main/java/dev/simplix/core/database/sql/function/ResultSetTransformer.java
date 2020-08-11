package dev.simplix.core.database.sql.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import lombok.NonNull;

public interface ResultSetTransformer<T> {

  T transform(@NonNull ResultSet resultSet) throws SQLException;

  static long ms(@NonNull ResultSet resultSet, @NonNull String name) throws SQLException {
    Timestamp timestamp = resultSet.getTimestamp(name);
    return timestamp == null ? 0 : timestamp.getTime();
  }
}