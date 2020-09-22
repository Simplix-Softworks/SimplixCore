package dev.simplix.core.common.converter;

import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConvertersTest {

  @BeforeAll
  static void setUp() {
    Converters.register(Car.class, AnotherCar.class,
        (Converter<Car, AnotherCar>) src -> new AnotherCar(src.name, src.age));
  }

  @Test
  @BeforeEach
  void testRegistered() {
    Assertions.assertNotNull(
        Converters.getConverter(Car.class, AnotherCar.class),
        "Converter must be registered");
  }

  @Test
  void convert() {
    AnotherCar convert = Converters.convert(new Car("Example-Name", 1), AnotherCar.class);
    Assertions.assertEquals(convert.anotherName, "Example-Name");
    Assertions.assertEquals(convert.anotherAge, 1);
  }

  @Test
  void getConverter() {
    Converter<Car, AnotherCar> converter = Converters.getConverter(
        Car.class,
        AnotherCar.class);
    Assertions.assertNotNull(converter, "Converter must be registered");
    AnotherCar convert = converter.convert(new Car("Example-Name", 1));
    Assertions.assertEquals(convert.anotherName, "Example-Name");
    Assertions.assertEquals(convert.anotherAge, 1);
  }

  @Data
  private static class Car {

    private final String name;
    private final int age;
  }

  @Data
  private static class AnotherCar {

    private final String anotherName;
    private final int anotherAge;
  }
}