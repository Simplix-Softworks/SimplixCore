package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.deploader.ArtifactDependencyLoader;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.inject.SimplixInstaller;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

@Slf4j
public final class SimpleUpdater implements Updater {

  private final ArtifactDependencyLoader dependencyLoader =
      (ArtifactDependencyLoader) SimplixInstaller.instance().dependencyLoader();
  private final File cacheDirectory = new File(".updateCache");

  {
    cacheDirectory.mkdir();
  }

  @Override
  public void installCachedUpdates() {
    for(File file : cacheDirectory.listFiles()) {
      if(file.isDirectory()) {
        installUpdates(file, file.getName());
      }
    }
  }

  private void installUpdates(File directory, String path) {
    for(File file : directory.listFiles()) {
      if(file.isDirectory()) {
        continue;
      }
      if(!file.renameTo(new File(path, file.getName().substring(0, file.getName().length()-7)))) {
        log.warn("[Simplix | Updater] Cannot update "+file.getName());
      }
    }
  }

  @Override
  public void checkForUpdates(ApplicationInfo applicationInfo, UpdatePolicy updatePolicy) {
    Class<?> appClass = SimplixInstaller.instance().applicationClass(applicationInfo.name());
    try {
      File toReplace = new File(appClass.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      // will never happen
    }
    RepositorySystem repositorySystem = dependencyLoader.newRepositorySystem();
    RepositorySystemSession session = dependencyLoader.newSession(repositorySystem,
        dependencyLoader.localRepository());

    Dependency dependency = updatePolicy.dependency();
    Artifact artifact = new DefaultArtifact(dependency.groupId(), dependency.artifactId(), "jar",
        dependency.version());
    ArtifactRequest request = new ArtifactRequest();
    request.setArtifact(artifact);
    request.setRepositories(dependencyLoader
        .createRemoteRepositories(Collections.singletonList(updatePolicy.repository())));

  }

}
