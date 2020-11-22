package dev.simplix.core.minecraft.spigot.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
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
import dev.simplix.core.common.platform.Platform;
import dev.simplix.core.minecraft.api.providers.PluginManager;
import dev.simplix.core.minecraft.spigot.SpigotSimplixModule;
import dev.simplix.core.minecraft.spigot.plugin.SimplixPlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GuiceSpigotTest {

  private static SimplixInstaller SIMPLIX_INSTALLER;

  @BeforeAll
  static void setUp() {
    // We need a new instance of the SimplixInstaller since it might already be installed by the plugin test
    SIMPLIX_INSTALLER = new SimplixInstaller();
    if (!MockBukkit.isMocked()) {
      MockBukkit.mock();
    }
    MockBukkit.load(SimplixPlugin.class);
  }

  @Test
  public void testInstall() {
    SIMPLIX_INSTALLER.register(DependTestApplication.class);
    SIMPLIX_INSTALLER.register(TestApplication.class, new PrivacyTestModule(),
        new CommonSimplixModule(), new SpigotSimplixModule());

    SIMPLIX_INSTALLER.install(Platform.SPIGOT);

    Injector unitTestInjector = SIMPLIX_INSTALLER.injector(TestApplication.class);
    Assertions.assertNotNull(unitTestInjector);

    Injector dependTestInjector = SIMPLIX_INSTALLER.injector(DependTestApplication.class);
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
