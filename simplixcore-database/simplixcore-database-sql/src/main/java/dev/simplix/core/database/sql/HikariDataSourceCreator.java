package dev.simplix.core.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HikariDataSourceCreator {

  public DataSource createSource(
      @NonNull String host,
      @NonNull String port,
      @NonNull String data,
      @NonNull String user,
      @NonNull String pass) {
    HikariConfig config = new HikariConfig();
    config.setDataSource(JdbcDataSourceCreator.createSource(host, port, data));
    config.setUsername(user);
    config.setPassword(pass);
    return new HikariDataSource(config);
  }

  public DataSource createSource(
      @NonNull String host,
      @NonNull String port,
      @NonNull String data,
      @NonNull String user,
      @NonNull String pass,
      int minimumIdle,
      int maxPoolSize) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDataSource(JdbcDataSourceCreator.createSource(host, port, data));
    hikariConfig.setUsername(user);
    hikariConfig.setPassword(pass);
    hikariConfig.setMinimumIdle(minimumIdle);
    hikariConfig.setMaximumPoolSize(maxPoolSize);
    return new HikariDataSource(hikariConfig);
  }

}
