package dev.simplix.core.database.sql.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

public interface PreparedStatementTransformer<T, K> {

  T transform(@NonNull PreparedStatement preparedStatement, @NonNull K k) throws SQLException;

}