package dev.simplix.core.database.sql.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.StatementConfiguration;

public class ReThrowableQueryRunner extends QueryRunner {

  public ReThrowableQueryRunner(boolean pmdKnownBroken) {
    super(pmdKnownBroken);
  }

  public ReThrowableQueryRunner(DataSource ds) {
    super(ds);
  }

  public ReThrowableQueryRunner(StatementConfiguration stmtConfig) {
    super(stmtConfig);
  }

  public ReThrowableQueryRunner(DataSource ds, boolean pmdKnownBroken) {
    super(ds, pmdKnownBroken);
  }

  public ReThrowableQueryRunner(DataSource ds, StatementConfiguration stmtConfig) {
    super(ds, stmtConfig);
  }

  public ReThrowableQueryRunner(
      DataSource ds,
      boolean pmdKnownBroken,
      StatementConfiguration stmtConfig) {
    super(ds, pmdKnownBroken, stmtConfig);
  }

  @Override
  public int[] batch(String sql, Object[][] params) {
    try {
      return super.batch(sql, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) {
    try {
      return super.query(sql, rsh, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T query(String sql, ResultSetHandler<T> rsh) {
    try {
      return super.query(sql, rsh);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int update(String sql) {
    try {
      return super.update(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int update(String sql, Object param) {
    try {
      return super.update(sql, param);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int update(String sql, Object... params) {
    try {
      return super.update(sql, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T insert(String sql, ResultSetHandler<T> rsh) {
    try {
      return super.insert(sql, rsh);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T insert(String sql, ResultSetHandler<T> rsh, Object... params) {
    try {
      return super.insert(sql, rsh, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T insertBatch(String sql, ResultSetHandler<T> rsh, Object[][] params) {
    try {
      return super.insertBatch(sql, rsh, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T insertBatch(
      Connection conn,
      String sql,
      ResultSetHandler<T> rsh,
      Object[][] params) {
    try {
      return super.insertBatch(conn, sql, rsh, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int execute(Connection conn, String sql, Object... params) {
    try {
      return super.execute(conn, sql, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int execute(String sql, Object... params) {
    try {
      return super.execute(sql, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> List<T> execute(String sql, ResultSetHandler<T> rsh, Object... params) {
    try {
      return super.execute(sql, rsh, params);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
