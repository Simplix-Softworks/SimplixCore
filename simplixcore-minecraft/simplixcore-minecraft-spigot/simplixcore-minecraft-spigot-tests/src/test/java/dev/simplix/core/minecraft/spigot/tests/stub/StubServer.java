package dev.simplix.core.minecraft.spigot.tests.stub;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.entity.Player;

public class StubServer extends FakeServer {

  private final String name = "Mock-Server";
  private final String version = "1.0.0-SNAPSHOT";

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getVersion() {
    return this.version;
  }

  @Override
  public Collection<? extends Player> getOnlinePlayers() {
    return new ArrayList<>();
  }
}
