package dev.simplix.core.database.sql.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

public interface BatchFiller<T> {

  void fill(@NonNull PreparedStatement preparedStatement, @NonNull T type) throws SQLException;
}