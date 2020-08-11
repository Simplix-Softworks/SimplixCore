package dev.simplix.core.database.sql.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchFiller<T> {

  void fill(PreparedStatement ps, T t) throws SQLException;
}