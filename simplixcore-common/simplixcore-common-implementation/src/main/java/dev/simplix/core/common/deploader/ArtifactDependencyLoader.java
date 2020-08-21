package dev.simplix.core.common.deploader;

import dev.simplix.core.common.libloader.LibraryTypeHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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

  private final static Map<String, BiConsumer<Dependency, File>> TYPE_HANDLER = new HashMap<>();
  private final File localRepositoryFile = new File(
      System.getProperty("user.home"),
      ".m2/repository");

  static {
    TYPE_HANDLER.put("library", new LibraryTypeHandler());
  }

  public static void registerTypeHandler(String type, BiConsumer<Dependency, File> handler) {
    TYPE_HANDLER.put(type, handler);
  }

  @Override
  public boolean load(
      Dependency dependency, Iterable<Repository> repositories) {
    BiConsumer<Dependency, File> handler = TYPE_HANDLER.get(dependency.type());
    if (handler == null) {
      log.error("[Simplix | DependencyLoader] Unknown type "
                + dependency.type()
                + " for dependency "
                + dependency);
      return false;
    }

    RepositorySystem repositorySystem = newRepositorySystem();
    RepositorySystemSession session = newSession(repositorySystem, localRepositoryFile);

    Artifact artifact = new DefaultArtifact(dependency.groupId(), dependency.artifactId(), "jar",
        dependency.version());
    ArtifactRequest request = new ArtifactRequest();
    request.setArtifact(artifact);
    request.setRepositories(createRemoteRepositories(repositories));

    try {
      ArtifactResult result = repositorySystem.resolveArtifact(session, request);
      artifact = result.getArtifact();
      if (artifact != null) {
        handler.accept(dependency, artifact.getFile());
        return true;
      } else {
        if (!result.isResolved()) {
          log.error("[Simplix | DependencyLoader] Unable to resolve dependency " + dependency);
        }
        if (!result.getExceptions().isEmpty()) {
          result
              .getExceptions()
              .forEach(exception -> log.error(
                  "[Simplix | DependencyLoader] An error occurred while loading dependency "
                  + dependency,
                  exception));
        }
        return false;
      }
    } catch (Exception exception) {
      log.error("[Simplix | DependencyLoader] Unable to load dependency " + dependency, exception);
    }
    return false;
  }

  private List<RemoteRepository> createRemoteRepositories(Iterable<Repository> repositories) {
    List<RemoteRepository> out = new ArrayList<>();
    for (Repository repository : repositories) {
      out.add(new RemoteRepository.Builder(repository.id(), "default", repository.url()).build());
    }
    return out;
  }

  private RepositorySystem newRepositorySystem() {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    return locator.getService(RepositorySystem.class);
  }

  private RepositorySystemSession newSession(RepositorySystem system, File localRepository) {
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    LocalRepository localRepo = new LocalRepository(localRepository.toString());
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
    return session;
  }

}