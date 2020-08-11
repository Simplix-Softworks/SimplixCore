package dev.simplix.core.database.sql.model;

import dev.simplix.core.database.sql.ObjectResultSetTransformer;
import dev.simplix.core.database.sql.function.ResultSetTransformer;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@AllArgsConstructor
public final class FieldInformation {

  @Getter
  public static ResultSetTransformer<FieldInformation> transformer = new ObjectResultSetTransformer<>(
      FieldInformation.class);
  private String catalog;
  private String schema;
  private String name;
  private String columnName;
  private BigInteger ordinalPosition;
  private Object defaultValue;
  private String nullAble;
  private String dataType;
  private BigInteger maxLength;
  private BigInteger octetLength;
  private BigInteger numericPrecision;
  private BigInteger numericScale;
  private BigInteger dateTimePrecision;
  private String characterSetName;
  private String collationName;
  private String columnType;
  private String columnKey;
  private String extra;
  private String privileges;
  private String columnComment;
}
