package dev.simplix.core.database.sql.util;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class CompletableAsyncQueryRunner extends AsyncQueryRunner {

    private final ExecutorService executorService;
    private final QueryRunner queryRunner;

    public CompletableAsyncQueryRunner(ExecutorService executorService, QueryRunner queryRunner) {
        super(executorService, queryRunner);

        this.executorService = executorService;
        this.queryRunner = queryRunner;
    }

    @Override
    public CompletableFuture<int[]> batch(Connection conn, String sql, Object[][] params) {
        return asFuture(() -> queryRunner.batch(conn, sql, params));
    }

    @Override
    public CompletableFuture<int[]> batch(String sql, Object[][] params) {
        return asFuture(() -> queryRunner.batch(sql, params));
    }

    @Override
    public <T> CompletableFuture<T> query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) {
        return asFuture(() -> queryRunner.query(conn, sql, rsh, params));
    }

    @Override
    public <T> CompletableFuture<T> query(Connection conn, String sql, ResultSetHandler<T> rsh) {
        return asFuture(() -> queryRunner.query(conn, sql, rsh));
    }

    @Override
    public <T> CompletableFuture<T> query(String sql, ResultSetHandler<T> rsh, Object... params) {
        return asFuture(() -> queryRunner.query(sql, rsh, params));
    }

    @Override
    public <T> CompletableFuture<T> query(String sql, ResultSetHandler<T> rsh) {
        return asFuture(() -> queryRunner.query(sql, rsh));
    }

    @Override
    public CompletableFuture<Integer> update(Connection conn, String sql) {
        return asFuture(() -> queryRunner.update(conn, sql));
    }

    @Override
    public CompletableFuture<Integer> update(Connection conn, String sql, Object param) {
        return asFuture(() -> queryRunner.update(conn, sql, param));
    }

    @Override
    public CompletableFuture<Integer> update(Connection conn, String sql, Object... params) {
        return asFuture(() -> queryRunner.update(conn, sql, params));
    }

    @Override
    public CompletableFuture<Integer> update(String sql) {
        return asFuture(() -> queryRunner.update(sql));
    }

    @Override
    public CompletableFuture<Integer> update(String sql, Object param) {
        return asFuture(() -> queryRunner.update(sql, param));
    }

    @Override
    public CompletableFuture<Integer> update(String sql, Object... params) {
        return asFuture(() -> queryRunner.update(sql, params));
    }

    @Override
    public <T> CompletableFuture<T> insert(String sql, ResultSetHandler<T> rsh) {
        return asFuture(() -> queryRunner.insert(sql, rsh));
    }

    @Override
    public <T> CompletableFuture<T> insert(String sql, ResultSetHandler<T> rsh, Object... params) {
        return asFuture(() -> queryRunner.insert(sql, rsh, params));
    }

    @Override
    public <T> CompletableFuture<T> insert(Connection conn, String sql, ResultSetHandler<T> rsh) {
        return asFuture(() -> queryRunner.insert(conn, sql, rsh));
    }

    @Override
    public <T> CompletableFuture<T> insert(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) {
        return asFuture(() -> queryRunner.insert(conn, sql, rsh, params));
    }

    @Override
    public <T> CompletableFuture<T> insertBatch(String sql, ResultSetHandler<T> rsh, Object[][] params) {
        return asFuture(() -> queryRunner.insertBatch(sql, rsh, params));
    }

    @Override
    public <T> CompletableFuture<T> insertBatch(Connection conn, String sql, ResultSetHandler<T> rsh, Object[][] params) {
        return asFuture(() -> queryRunner.insertBatch(conn, sql, rsh, params));
    }

    private <T> CompletableFuture<T> asFuture(Callable<T> callable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
}
