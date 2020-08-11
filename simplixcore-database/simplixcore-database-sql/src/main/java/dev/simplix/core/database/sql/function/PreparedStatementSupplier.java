package dev.simplix.core.database.sql.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;

public interface PreparedStatementSupplier {

  PreparedStatement get(@NonNull Connection connection) throws SQLException;

}