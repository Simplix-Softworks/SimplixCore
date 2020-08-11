package dev.simplix.core.database.sql;

import dev.simplix.core.database.sql.function.ResultSetTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ObjectResultSetTransformer<T> implements ResultSetTransformer<T> {

  private final Class<T> clazz;
  private Constructor<T> constructor;
  private int fields;
  private List<Field> fieldClasses = new ArrayList<>();
  private int[] types;

  public ObjectResultSetTransformer(Class<T> clazz) {
    this.clazz = clazz;
    init();
  }

  private void init() {
    Field[] declaredFields = clazz.getDeclaredFields();
    types = new int[declaredFields.length];
    List<Class<?>> clazzes = new ArrayList<>();
    int i = 0;
    for (Field field : declaredFields) {
      int modifiers = field.getModifiers();
      if (Modifier.isStatic(modifiers))
        continue;
      Class<?> type = field.getType();
      if (type.equals(UUID.class))
        types[i] = 1;
      clazzes.add(type);
      fieldClasses.add(field);
      i++;
    }
    this.fields = clazzes.size();
    try {
      constructor = clazz.getDeclaredConstructor(clazzes.toArray(new Class<?>[0]));
    } catch (NoSuchMethodException noSuchMethodException) {
      noSuchMethodException.printStackTrace();
      throw new RuntimeException("Could not find a proper all argument constructor!");
    }
  }

  @Override
  public T transform(ResultSet resultSet) throws SQLException {
    Object[] objects = new Object[fields];
    //transform to uuid
    for (int i = 0; i < fields; i++)
      if (types[i] == 1)
        objects[i] = UUID.fromString(resultSet.getString(i + 1));
      else
        objects[i] = resultSet.getObject(i + 1);
    try {
      return constructor.newInstance(objects);
    } catch (InstantiationException | InvocationTargetException | IllegalAccessException | IllegalArgumentException exception) {
      exception.printStackTrace();
      if (!(exception instanceof IllegalArgumentException))
        return null;

      System.out.println("Invalid schema for class '" + clazz.getName() + "'");

      for (int i = 0; i < fields; i++) {
        Object object = resultSet.getObject(i + 1);
        System.out.println("field " + i + " type = " + (
            object == null
                ? "null"
                : object.getClass()) + " registered: " + fieldClasses.get(i));
      }
    }
    return null;
  }
}
