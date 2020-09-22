package dev.simplix.core.common.deploader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArtifactDependencyLoaderTest {

  private static ArtifactDependencyLoader artifactDependencyLoader;

  @BeforeEach
  void setUp() {
    artifactDependencyLoader = new ArtifactDependencyLoader();
  }

  @AfterEach
  void tearDown() {
    artifactDependencyLoader = null;
  }

  @Test
  void load() {
  }

  @Test
  void createRemoteRepositories() {
  }

  @Test
  void newRepositorySystem() {
  }

  @Test
  void newSession() {
  }

  @Test
  void localRepository() {
  }
}