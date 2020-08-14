package dev.simplix.core.database.sql.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;

public interface ResultSetConsumer {

  void consume(@NonNull ResultSet resultSet) throws SQLException;

}