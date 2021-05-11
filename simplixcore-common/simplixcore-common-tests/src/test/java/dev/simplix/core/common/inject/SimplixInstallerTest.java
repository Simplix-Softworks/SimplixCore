package dev.simplix.core.common.inject;

import com.google.inject.Injector;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.deploader.DependencyLoader;
import dev.simplix.core.common.libloader.LibraryLoader;
import dev.simplix.core.common.platform.Platform;
import dev.simplix.core.common.updater.Updater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

@SimplixApplication(name = "TestApplication", version = "1.0.0-SNAPSHOT", authors = "SimplixSoftworks")
class SimplixInstallerTest {

  @Test
  @BeforeAll
  static void instance() {
    SimplixInstaller.init(LoggerFactory.getLogger(SimplixInstaller.class));

    Assertions.assertNotNull(
        SimplixInstaller.instance(),
        "SimplixInstaller instance mustn't be null");
  }

  @Test
  static void injector() {
    final Injector injector = SimplixInstaller.instance().injector(SimplixInstaller.class);
    Assertions.assertNotNull(injector, "Injector mustn't be null");
    Assertions.assertEquals("com.google.inject", injector.getClass().getName());
  }

  @BeforeEach
  void register() {
    SimplixInstaller.instance().register(this.getClass());
  }

  @Test
  void registered() {
    Assertions.assertTrue(SimplixInstaller.instance().registered("TestApplication"));
  }

  @Test
  void dependencyLoader() {
    final DependencyLoader dependencyLoader = SimplixInstaller.instance().dependencyLoader();
    Assertions.assertNotNull(dependencyLoader, "Dependency-Loader mustn't be null");
  }

  @Test
  void updater() {
    final Updater updater = SimplixInstaller.instance().updater();
    Assertions.assertNotNull(updater, "Updater mustn't be null");
  }

  @Test
  void libraryLoader() {
    final LibraryLoader libraryLoader = SimplixInstaller.instance().libraryLoader();
    Assertions.assertNotNull(libraryLoader, "LibraryLoader mustn't be null");
  }

  @Test
  void install() {
    try {
      SimplixInstaller.instance().install(Platform.STANDALONE);
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }

  @Test
  void applicationClass() {
    final Class<?> testApplication = SimplixInstaller
        .instance()
        .applicationClass("TestApplication");
    Assertions.assertNotNull(testApplication, "TestApplication must not be null");
    Assertions.assertEquals(testApplication, this.getClass(), "Invalid class result");
  }
}