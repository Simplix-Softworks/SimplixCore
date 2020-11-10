package dev.simplix.core.database.sql.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;

public class QueryFactory {

  private final Supplier<ListeningExecutorService> executorServiceSupplier;
  private Supplier<DataSource> dataSourceSupplier;

  public QueryFactory(
      Supplier<ListeningExecutorService> executorServiceSupplier,
      Supplier<DataSource> dataSourceSupplier) {
    this.executorServiceSupplier = executorServiceSupplier;
    this.dataSourceSupplier = dataSourceSupplier;
  }

  public QueryFactory(
      Supplier<ListeningExecutorService> executorServiceSupplier,
      DataSource dataSource) {
    this.executorServiceSupplier = executorServiceSupplier;
    this.dataSourceSupplier = (() -> dataSource);
  }

  public QueryRunner query() {
    return new QueryRunner(getDataSource());
  }

  public ReThrowableQueryRunner uncheckedQuery() {
    return new ReThrowableQueryRunner(getDataSource());
  }

  public ListenableAsyncQueryRunner listenableQuery() {
    return new ListenableAsyncQueryRunner(
        executorServiceSupplier.get(),
        new QueryRunner(getDataSource()));
  }

  public CompletableAsyncQueryRunner completableQuery() {
    return new CompletableAsyncQueryRunner(
        executorServiceSupplier.get(),
        new QueryRunner(getDataSource()));
  }

  public ListeningExecutorService getExecutorService() {
    return executorServiceSupplier.get();
  }

  public DataSource getDataSource() {
    return dataSourceSupplier.get();
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSourceSupplier = (() -> dataSource);
  }

  public void setDataSourceSupplier(Supplier<DataSource> dataSourceSupplier) {
    this.dataSourceSupplier = dataSourceSupplier;
  }
}
