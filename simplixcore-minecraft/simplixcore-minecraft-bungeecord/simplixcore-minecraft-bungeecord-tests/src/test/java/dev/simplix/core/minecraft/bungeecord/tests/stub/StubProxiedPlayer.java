package dev.simplix.core.minecraft.bungeecord.tests.stub;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;

@AllArgsConstructor
@NoArgsConstructor
public class StubProxiedPlayer extends FakeProxiedPlayer {

  private final List<String> lastMessages = new ArrayList<>();

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

  @Override
  public boolean hasPermission(String s) {
    return true; //By pass command permissions
  }

  @Override
  public void sendMessage(String s) {
    lastMessages.add(s);
    super.sendMessage(s);
  }

  @Override
  public void sendMessage(BaseComponent baseComponent) {
    lastMessages.add(baseComponent.toLegacyText());
    super.sendMessage(baseComponent);
  }
}
