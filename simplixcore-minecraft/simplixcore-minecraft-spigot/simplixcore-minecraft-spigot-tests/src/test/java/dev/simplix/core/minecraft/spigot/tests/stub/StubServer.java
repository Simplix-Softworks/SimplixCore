package dev.simplix.core.minecraft.spigot.tests.stub;

import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

@NoArgsConstructor
@AllArgsConstructor
public class StubServer extends FakeServer {

  private String name = "Mock-Server";
  private String version = "1.0.0";

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public Collection<? extends Player> getOnlinePlayers() {
    return new ArrayList<>();
  }
}
