package dev.simplix.core.database.sql.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

public interface PreparedStatementFiller {

  void fill(@NonNull PreparedStatement preparedStatement) throws SQLException;
}