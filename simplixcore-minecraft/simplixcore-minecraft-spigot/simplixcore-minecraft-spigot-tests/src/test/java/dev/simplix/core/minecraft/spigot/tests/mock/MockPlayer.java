package dev.simplix.core.minecraft.spigot.tests.mock;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MockPlayer extends MockPlayerBase {

  private String name = "KotlinFactory";
  private UUID uuid = UUID.randomUUID();

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
}
