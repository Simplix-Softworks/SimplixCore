package dev.simplix.core.database.sql.model;

import java.math.BigInteger;
import java.sql.Timestamp;
import lombok.*;
import dev.simplix.core.database.sql.ObjectResultSetTransformer;
import dev.simplix.core.database.sql.function.ResultSetTransformer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInformation {

  @Getter
  public static ResultSetTransformer<TableInformation> transformer = new ObjectResultSetTransformer<>(
      TableInformation.class);
  private String catalog;
  private String schema;
  private String name;
  private String type;
  private String engine;
  private BigInteger version;
  private String rowFormat;
  private BigInteger tableRows;
  private BigInteger avgRowLength;
  private BigInteger dataLength;
  private BigInteger maxDataLength;
  private BigInteger indexLength;
  private BigInteger dataFree;
  private BigInteger autoIncrement;
  private Timestamp createTime;
  private Timestamp updateTime;
  private Timestamp checkTime;
  private String collation;
  private String checkSum;
  private String createOptions;
  private String tableComment;
}
