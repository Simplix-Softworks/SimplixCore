package dev.simplix.core.database.sql;

import dev.simplix.core.database.sql.function.*;
import dev.simplix.core.database.sql.handler.ConnectionHandler;
import dev.simplix.core.database.sql.handlers.HikariConnectionHandler;
import dev.simplix.core.database.sql.model.FieldInformation;
import dev.simplix.core.database.sql.model.TableInformation;
import java.io.Closeable;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@ToString(of = "connectionHandler")
@Getter
public final class SqlDatabaseConnection {

  public static Set<String> QUERIES = new HashSet<>();
  public static final PreparedStatementFiller EMPTY_FILLER = (ps) -> {
  };
  private DataSource dataSource;
  private String host;
  private String data;
  private String user;
  private String pass;
  private String port;
  private final ConnectionHandler connectionHandler;
  @Setter
  private Consumer<String> statementDebugger;

  public SqlDatabaseConnection(
      @NonNull DataSource dataSource,
      @NonNull String host,
      @NonNull String user,
      @NonNull String pass,
      @NonNull String port,
      @NonNull String data,
      @NonNull ConnectionHandler connectionHandler) {
    this.host = host;
    this.user = user;
    this.pass = pass;
    this.port = port;
    this.data = data;
    this.dataSource = dataSource;
    this.connectionHandler = connectionHandler;
    connectionHandler.init(this);
  }

  public SqlDatabaseConnection(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
    connectionHandler.init(this);
  }

  public boolean hasEntries(String statement, PreparedStatementFiller filler) {
    return rawResultSet(statement, filler, (ps, rs) -> rs.next());
  }

  public int queryKeyInsert(
      @NonNull String select,
      @NonNull String insert,
      @NonNull PreparedStatementFiller selectFiller,
      @NonNull PreparedStatementFiller insertFiller) {
    return queryKeyInsert(
        select,
        insert,
        selectFiller,
        insertFiller,
        rs -> rs.getInt(1),
        rs -> rs.getInt(1));
  }

  public int keyInsert(@NonNull String insert, @NonNull PreparedStatementFiller filler) {
    return keyInsert(insert, filler, rs -> rs.getInt(1));
  }

  public List<String> getEnumValues(@NonNull String table, @NonNull String column) {
    return query(
        "SELECT column_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name = ?",
        ps -> {
          ps.setString(1, this.data);
          ps.setString(2, table);
          ps.setString(3, column);
        },
        rs -> parseEnums(rs.getString(1)));
  }

  /**
   * Overrides existing enum values! CAUTION!
   *
   * @param table
   * @param column
   * @param newValues
   */
  public void updateEnumValues(String table, String column, List<String> newValues) {
    prepare(enumsToString(table, column, newValues));
  }

  public int queryKeyInsert(
      String selectStatement,
      String insertStatement,
      PreparedStatementFiller selectFiller,
      PreparedStatementFiller insertFiller,
      ResultSetTransformer<Integer> selectTransformer,
      ResultSetTransformer<Integer> insertTransformer) {
    return rawResultSet(selectStatement, selectFiller, (ps, rs) -> {
      if (rs.next()) {
        return selectTransformer.transform(rs);
      } else {
        return keyInsert(insertStatement, insertFiller, insertTransformer);
      }
    });
  }

  public int keyInsert(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull ResultSetTransformer<Integer> transformer) {
    return rawExecutor0(
        statement,
        (c) -> c.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS),
        filler,
        PreparedStatement::execute,
        (ps, ig) -> {
          ResultSet rs = ps.getGeneratedKeys();
          if (rs.next()) {
            return transformer.transform(rs);
          }
          throw new RuntimeException("Could not get keys!");
        },
        true);
  }

  public List<Integer> batchInsert(String statement, PreparedStatementFiller filler) {
    return rawExecutor0(
        statement,
        (c) -> c.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS),
        filler,
        PreparedStatement::executeBatch,
        (ps, ig) -> {
          ResultSet rs = ps.getGeneratedKeys();
          List<Integer> ints = new ArrayList<>();
          while (rs.next()) {
            ints.add(rs.getInt(1));
          }
          return ints;
        },
        true);
  }

  public <T> List<Integer> batchInsert(
      @NonNull String statement,
      @NonNull Stream<T> stream,
      @NonNull BatchFiller<T> batchFiller) {
    return batchInsert(statement, ps -> stream.forEach(t -> {
      try {
        batchFiller.fill(ps, t);
        ps.addBatch();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }));
  }

  public boolean prepareLazy(@NonNull String statement, @NonNull Object... filler) {
    return prepare(statement, lazy(filler));
  }

  public boolean prepare(@NonNull String statement) {
    return prepare(statement, EMPTY_FILLER);
  }

  public boolean prepare(@NonNull String statement, PreparedStatementFiller filler) {
    Object o = rawExecutor(statement, filler, PreparedStatement::execute, (ps, rs) -> rs);
    if (o != null) {
      return (boolean) o;
    }
    return false;
  }

  public <T, K> T rawExecutor(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull PreparedStatementExecutor<K> executor,
      @NonNull PreparedStatementTransformer<T, K> consumer) {
    return rawExecutor(statement, filler, executor, consumer, true);
  }

  public <T, K> T rawExecutor0(
      @NonNull String statement,
      @NonNull PreparedStatementSupplier supplier,
      @NonNull PreparedStatementFiller filler,
      @NonNull PreparedStatementExecutor<K> executor,
      @NonNull PreparedStatementTransformer<T, K> consumer,
      boolean retry) {
    K k = null;
    this.connectionHandler.preConnect();
    Connection connection = this.connectionHandler.connection();
    QUERIES.add(statement);
    if (this.statementDebugger != null) {
      this.statementDebugger.accept(statement);
    }
    try (PreparedStatement preparedStatement = supplier.get(connection)) {
      filler.fill(preparedStatement);
      k = executor.execute(preparedStatement);
      return consumer.transform(preparedStatement, k);
    } catch (SQLException e) {
      if (retry) {
        updateConnection();
        return rawExecutor0(statement, supplier, filler, executor, consumer, false);
      } else {
        throw new RuntimeException(e);
      }
    } finally {
      if (k instanceof Closeable) {
        close((Closeable) k);
      }
      this.connectionHandler.finishConnection(connection);
    }
  }

  public <T, K> T rawExecutor(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull PreparedStatementExecutor<K> executor,
      @NonNull PreparedStatementTransformer<T, K> consumer,
      boolean retry) {
    return rawExecutor0(
        statement,
        (c) -> c.prepareStatement(statement),
        filler,
        executor,
        consumer,
        retry);
  }

  public <T> T rawResultSet(
      String statement,
      PreparedStatementFiller filler,
      PreparedStatementTransformer<T, ResultSet> transformer) {
    return rawExecutor(statement, filler, PreparedStatement::executeQuery, transformer);
  }

  public <T> T query(
      String statement,
      PreparedStatementFiller filler,
      ResultSetTransformer<T> transFormer) {
    return rawResultSet(statement, filler, (ps, rs) -> {
      if (rs.next()) {
        return transFormer.transform(rs);
      } else {
        return null;
      }
    });
  }

  @Deprecated
  public void consume(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull ResultSetConsumer consumer) {
    rawResultSet(statement, filler, (preparedStatement, resultSet) -> {
      if (resultSet.next()) {
        consumer.consume(resultSet);
      }
      return null;
    });
  }

  public void consumeSingle(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull ResultSetConsumer consumer) {
    rawResultSet(statement, filler, (preparedStatement, resultSet) -> {
      if (resultSet.next()) {
        consumer.consume(resultSet);
      }
      return null;
    });
  }

  public void consumeRaw(
      String statement,
      PreparedStatementFiller filler,
      ResultSetConsumer consumer) {
    rawResultSet(statement, filler, (preparedStatement, resultSet) -> {
      consumer.consume(resultSet);
      return null;
    });
  }

  public void consumeAll(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull ResultSetConsumer consumer) {
    rawResultSet(statement, filler, (preparedStatement, resultSet) -> {
      while (resultSet.next()) {
        consumer.consume(resultSet);
      }
      return null;
    });
  }

  public <T, C extends Collection<T>> C queryCollection(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull ResultSetTransformer<T> consumer,
      @NonNull Supplier<C> supplier) {
    C a = supplier.get();
    return rawResultSet(statement, filler, (ps, rs) -> {
      while (rs.next()) {
        T transform = consumer.transform(rs);
        a.add(transform);
      }
      return a;
    });
  }

  public <T> List<T> queryList(
      @NonNull String statement,
      @NonNull PreparedStatementFiller filler,
      @NonNull ResultSetTransformer<T> consumer) {
    return queryCollection(statement, filler, consumer, ArrayList::new);
  }

  public List<TableInformation> getTableInformation(@NonNull String database) {
    return queryList(
        "SELECT * FROM information_schema.tables WHERE table_schema LIKE ?",
        ps -> ps.setString(1, database),
        TableInformation.getTransformer());
  }

  public List<FieldInformation> getFieldInformation(
      @NonNull String database,
      @NonNull String table) {
    return queryList(
        "SELECT * FROM information_schema.columns WHERE table_schema LIKE ? AND table_name LIKE ?",
        ps -> {
          ps.setString(1, database);
          ps.setString(2, table);
        },
        FieldInformation.getTransformer());
  }

  public static PreparedStatementFiller lazy(@NonNull Object... filler) {
    return ps -> {
      for (int i = 0; i < filler.length; i++) {
        ps.setString(i + 1, filler[i].toString());
      }
    };
  }

  public void updateConnection() {
    this.connectionHandler.updateConnection();
  }

  public static List<String> parseEnums(@NonNull String val) {
    String replaceAll = val.replaceAll("enum|'|\\(|\\)", "");
    String[] split = replaceAll.split(",");
    return new ArrayList<>(Arrays.asList(split));
  }

  public static String enumsToString(
      @NonNull String table,
      @NonNull String column,
      @NonNull List<String> stringList) {
    StringBuilder statement = new StringBuilder("ALTER TABLE "
                                                + table
                                                + " MODIFY COLUMN "
                                                + column
                                                + " ENUM(");
    stringList.forEach(it -> statement.append("'").append(it).append("',"));
    return statement.substring(0, statement.length() - 1) + ");";
  }

  public static void close(@Nullable AutoCloseable closeable) {
    if (closeable == null) {
      return;
    }
    try {
      closeable.close();
    } catch (Throwable throwable) {
    }
  }

  public static SqlDatabaseConnection hikari(
      @NonNull String host,
      @NonNull String user,
      @NonNull String pass,
      @NonNull String port,
      @NonNull String database) {
    return new SqlDatabaseConnection(
        HikariDataSourceCreator.createSource(host, port, database, user, pass),
        host,
        user,
        pass,
        port,
        database,
        new HikariConnectionHandler());
  }

}
