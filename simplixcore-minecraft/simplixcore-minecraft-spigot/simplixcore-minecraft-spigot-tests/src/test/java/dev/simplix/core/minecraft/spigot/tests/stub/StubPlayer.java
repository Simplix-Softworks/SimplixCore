package dev.simplix.core.minecraft.spigot.tests.stub;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Server;

@Data
@NoArgsConstructor
public class StubPlayer extends FakePlayer {

  private final List<String> lastMessages = new ArrayList<>();

  private String name = "KotlinFactory";
  private UUID uuid = UUID.randomUUID();

  public StubPlayer(String name, UUID uuid) {
    this.name = name;
    this.uuid = uuid;
  }

  @Override
  public UUID getUniqueId() {
    return uuid;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public InetSocketAddress getAddress() {
    try {
      return new InetSocketAddress(InetAddress.getLocalHost(), 3306);
    } catch (UnknownHostException unknownHostException) {
      unknownHostException.printStackTrace();
      return null;
    }
  }

  @Override
  public Server getServer() {
    return new StubServer();
  }

  @Override
  public boolean hasPermission(String name) {
    return true; //By pass command permissions
  }

  @Override
  public void sendMessage(String message) {
    lastMessages.add(message);
    super.sendMessage(message);
  }
}
