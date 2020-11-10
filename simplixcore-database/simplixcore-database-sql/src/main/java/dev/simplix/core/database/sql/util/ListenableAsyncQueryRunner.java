package dev.simplix.core.database.sql.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.sql.Connection;
import java.util.concurrent.Callable;
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class ListenableAsyncQueryRunner extends AsyncQueryRunner {

  private final ListeningExecutorService executorService;
  private final QueryRunner queryRunner;

  public ListenableAsyncQueryRunner(
      ListeningExecutorService executorService,
      QueryRunner queryRunner) {
    super(executorService, queryRunner);

    this.executorService = executorService;
    this.queryRunner = queryRunner;
  }

  public ListenableFuture<int[]> batch(Connection conn, String sql, Object[][] params) {
    return asFuture(() -> queryRunner.batch(conn, sql, params));
  }

  public ListenableFuture<int[]> batch(String sql, Object[][] params) {
    return asFuture(() -> queryRunner.batch(sql, params));
  }

  public <T> ListenableFuture<T> query(
      Connection conn,
      String sql,
      ResultSetHandler<T> rsh,
      Object... params) {
    return asFuture(() -> queryRunner.query(conn, sql, rsh, params));
  }

  public <T> ListenableFuture<T> query(Connection conn, String sql, ResultSetHandler<T> rsh) {
    return asFuture(() -> queryRunner.query(conn, sql, rsh));
  }

  public <T> ListenableFuture<T> query(String sql, ResultSetHandler<T> rsh, Object... params) {
    return asFuture(() -> queryRunner.query(sql, rsh, params));
  }

  public <T> ListenableFuture<T> query(String sql, ResultSetHandler<T> rsh) {
    return asFuture(() -> queryRunner.query(sql, rsh));
  }

  public ListenableFuture<Integer> update(Connection conn, String sql) {
    return asFuture(() -> queryRunner.update(conn, sql));
  }

  public ListenableFuture<Integer> update(Connection conn, String sql, Object param) {
    return asFuture(() -> queryRunner.update(conn, sql, param));
  }

  public ListenableFuture<Integer> update(Connection conn, String sql, Object... params) {
    return asFuture(() -> queryRunner.update(conn, sql, params));
  }

  public ListenableFuture<Integer> update(String sql) {
    return asFuture(() -> queryRunner.update(sql));
  }

  public ListenableFuture<Integer> update(String sql, Object param) {
    return asFuture(() -> queryRunner.update(sql, param));
  }

  public ListenableFuture<Integer> update(String sql, Object... params) {
    return asFuture(() -> queryRunner.update(sql, params));
  }

  public <T> ListenableFuture<T> insert(String sql, ResultSetHandler<T> rsh) {
    return asFuture(() -> queryRunner.insert(sql, rsh));
  }

  public <T> ListenableFuture<T> insert(String sql, ResultSetHandler<T> rsh, Object... params) {
    return asFuture(() -> queryRunner.insert(sql, rsh, params));
  }

  public <T> ListenableFuture<T> insert(Connection conn, String sql, ResultSetHandler<T> rsh) {
    return asFuture(() -> queryRunner.insert(conn, sql, rsh));
  }

  public <T> ListenableFuture<T> insert(
      Connection conn,
      String sql,
      ResultSetHandler<T> rsh,
      Object... params) {
    return asFuture(() -> queryRunner.insert(conn, sql, rsh, params));
  }

  public <T> ListenableFuture<T> insertBatch(
      String sql,
      ResultSetHandler<T> rsh,
      Object[][] params) {
    return asFuture(() -> queryRunner.insertBatch(sql, rsh, params));
  }

  public <T> ListenableFuture<T> insertBatch(
      Connection conn,
      String sql,
      ResultSetHandler<T> rsh,
      Object[][] params) {
    return asFuture(() -> queryRunner.insertBatch(conn, sql, rsh, params));
  }

  private <T> ListenableFuture<T> asFuture(Callable<T> callable) {
    return executorService.submit(() -> {
      try {
        return callable.call();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
}
