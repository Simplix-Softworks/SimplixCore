package dev.simplix.core.common.deploader;

import dev.simplix.core.common.libloader.LibraryTypeHandler;
import dev.simplix.core.common.libloader.SharedLibraryTypeHandler;
import java.io.File;
import java.util.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

@Slf4j
public final class ArtifactDependencyLoader implements DependencyLoader {

  private final static Map<String, DependencyTypeHandler> TYPE_HANDLER = new HashMap<>();

  static {
    TYPE_HANDLER.put("library", new LibraryTypeHandler());
    TYPE_HANDLER.put("shared-library", new SharedLibraryTypeHandler());
  }

  private final File localRepositoryFile = new File(
      System.getProperty("user.home"),
      ".m2/repository");

  public static void registerTypeHandler(
      @NonNull String type,
      @NonNull DependencyTypeHandler handler) {
    TYPE_HANDLER.put(type, handler);
  }

  @Override
  public Optional<DependencyLoadingException> load(
      @NonNull Dependency dependency,
      @NonNull Iterable<Repository> repositories) {
    DependencyTypeHandler handler = TYPE_HANDLER.get(dependency.type());
    if (handler == null) {
      return Optional.of(new DependencyLoadingException(
          dependency,
          "[Simplix | DependencyLoader] Unknown type "
          + dependency.type()
          + " for dependency "
          + dependency));
    }
    if (!handler.shouldInstall(dependency)) {
      return Optional.empty();
    }

    RepositorySystem repositorySystem = newRepositorySystem();
    RepositorySystemSession session = newSession(repositorySystem, this.localRepositoryFile);

    Artifact artifact = new DefaultArtifact(
        dependency.groupId(),
        dependency.artifactId(),
        "jar",
        dependency.version());
    ArtifactRequest request = new ArtifactRequest();
    request.setArtifact(artifact);
    request.setRepositories(createRemoteRepositories(repositories));

    try {
      ArtifactResult result = repositorySystem.resolveArtifact(session, request);
      artifact = result.getArtifact();
      if (artifact != null) {
        handler.handle(dependency, artifact.getFile());
        return Optional.empty();
      } else {
        if (!result.isResolved()) {
          return Optional.of(new DependencyLoadingException(
              dependency,
              "[Simplix | DependencyLoader] Unable to resolve dependency " + dependency));
        }
        if (!result.getExceptions().isEmpty()) {
          return result
              .getExceptions()
              .stream()
              .findFirst()
              .map(exception -> new DependencyLoadingException(dependency, exception));
        }
      }
    } catch (Exception exception) {
      return Optional.of(new DependencyLoadingException(dependency, exception));
    }
    return Optional.empty();
  }

  public List<RemoteRepository> createRemoteRepositories(@NonNull Iterable<Repository> repositories) {
    List<RemoteRepository> out = new ArrayList<>();
    for (Repository repository : repositories) {
      out.add(new RemoteRepository.Builder(repository.id(), "default", repository.url()).build());
    }
    return out;
  }

  public RepositorySystem newRepositorySystem() {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    return locator.getService(RepositorySystem.class);
  }

  public RepositorySystemSession newSession(
      @NonNull RepositorySystem system,
      @NonNull File localRepository) {
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    LocalRepository localRepo = new LocalRepository(localRepository.toString());
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
    return session;
  }

  public File localRepository() {
    return this.localRepositoryFile;
  }

}
