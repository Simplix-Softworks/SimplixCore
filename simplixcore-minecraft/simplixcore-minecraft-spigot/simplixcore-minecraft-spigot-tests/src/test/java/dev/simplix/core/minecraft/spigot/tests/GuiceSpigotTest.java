package dev.simplix.core.minecraft.spigot.tests;

import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import dev.simplix.core.common.CommonSimplixModule;
import dev.simplix.core.common.aop.AbstractSimplixModule;
import dev.simplix.core.common.aop.Private;
import dev.simplix.core.common.aop.ScanComponents;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.minecraft.api.providers.PluginManager;
import dev.simplix.core.minecraft.spigot.SpigotSimplixModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GuiceSpigotTest {

  @Test
  public void testInstall() {
    SimplixInstaller.instance().register(DependTestApplication.class);
    SimplixInstaller.instance().register(TestApplication.class, new PrivacyTestModule(),
        new CommonSimplixModule(), new SpigotSimplixModule());
    SimplixInstaller.instance().install();

    Injector unitTestInjector = SimplixInstaller.instance().injector(TestApplication.class);
    Assertions.assertNotNull(unitTestInjector);

    Injector dependTestInjector = SimplixInstaller.instance().injector(DependTestApplication.class);
    Assertions.assertNotNull(dependTestInjector);

    PluginManager pluginManager = unitTestInjector.getInstance(PluginManager.class);
    Assertions.assertNotNull(pluginManager);

    PluginManager pluginManager2 = dependTestInjector.getInstance(PluginManager.class);
    if (pluginManager != pluginManager2) {
      throw new IllegalStateException("PluginManagers are not the same instance!");
    }

    expect(
        () -> dependTestInjector.getInstance(Key.get(Car.class, Private.class)),
        ConfigurationException.class);
    Assertions.assertEquals(
        "Audi A6",
        unitTestInjector.getInstance(Key.get(Car.class, Private.class)).getName());
  }

  private void expect(Runnable runnable, Class<? extends Exception> clazz) {
    try {
      runnable.run();
      throw new IllegalStateException(clazz.getName() + " did not occur");
    } catch (Exception e) {
      if (!e.getClass().equals(clazz)) {
        throw e;
      }
    }
  }

  @SimplixApplication(name = "UnitTest", version = "1.0", authors = "SimplixSoftworks")
  @ScanComponents("dev.simplix.core")
  final static class TestApplication {

  }

  @SimplixApplication(name = "DependTest", version = "1.0", authors = "SimplixSoftworks",
      dependencies = "UnitTest")
  final static class DependTestApplication {

  }

  static class PrivacyTestModule extends AbstractSimplixModule {

    @Provides
    @Private
    public Car somePrivateString() {
      System.out.println("Hello binding");
      Car car = new Car();
      car.name = "Audi A6";
      car.horsePower = 190;
      return car;
    }

  }

  @NoArgsConstructor
  @Getter
  static final class Car {

    private String name;
    private int horsePower;

  }

}
