package dev.simplix.core.minecraft.bungeecord.tests.stub;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.score.Scoreboard;

public class FakeProxiedPlayer implements ProxiedPlayer {

  @Override
  public String getDisplayName() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void setDisplayName(String s) {

  }

  @Override
  public void sendMessage(
      ChatMessageType chatMessageType, BaseComponent... baseComponents) {

  }

  @Override
  public void sendMessage(
      ChatMessageType chatMessageType, BaseComponent baseComponent) {

  }

  @Override
  public void connect(ServerInfo serverInfo) {

  }

  @Override
  public void connect(
      ServerInfo serverInfo, Reason reason) {

  }

  @Override
  public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {

  }

  @Override
  public void connect(
      ServerInfo serverInfo, Callback<Boolean> callback, Reason reason) {

  }

  @Override
  public void connect(ServerConnectRequest serverConnectRequest) {

  }

  @Override
  public Server getServer() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public int getPing() {
    return 0;
  }

  @Override
  public void sendData(String s, byte[] bytes) {

  }

  @Override
  public PendingConnection getPendingConnection() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void chat(String s) {

  }

  @Override
  public ServerInfo getReconnectServer() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void setReconnectServer(ServerInfo serverInfo) {

  }

  @Override
  public String getUUID() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public UUID getUniqueId() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public Locale getLocale() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public byte getViewDistance() {
    return 0;
  }

  @Override
  public ChatMode getChatMode() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public boolean hasChatColors() {
    return false;
  }

  @Override
  public SkinConfiguration getSkinParts() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public MainHand getMainHand() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void setTabHeader(
      BaseComponent baseComponent, BaseComponent baseComponent1) {

  }

  @Override
  public void setTabHeader(
      BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {

  }

  @Override
  public void resetTabHeader() {

  }

  @Override
  public void sendTitle(Title title) {

  }

  @Override
  public boolean isForgeUser() {
    return false;
  }

  @Override
  public Map<String, String> getModList() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public Scoreboard getScoreboard() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public String getName() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void sendMessage(String s) {
    System.out.println(s);
  }

  @Override
  public void sendMessages(String... strings) {
    for (String string : strings) {
      sendMessage(string);
    }
  }

  @Override
  public void sendMessage(BaseComponent... baseComponents) {
    for (BaseComponent baseComponent : baseComponents) {
      sendMessage(baseComponent);
    }
  }

  @Override
  public void sendMessage(BaseComponent baseComponent) {
    sendMessage(baseComponent.toLegacyText());
  }

  @Override
  public Collection<String> getGroups() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void addGroups(String... strings) {

  }

  @Override
  public void removeGroups(String... strings) {

  }

  @Override
  public boolean hasPermission(String s) {
    return false;
  }

  @Override
  public void setPermission(String s, boolean b) {

  }

  @Override
  public Collection<String> getPermissions() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public InetSocketAddress getAddress() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public SocketAddress getSocketAddress() {
    return null;
  }

  @Override
  public void disconnect(String s) {

  }

  @Override
  public void disconnect(BaseComponent... baseComponents) {

  }

  @Override
  public void disconnect(BaseComponent baseComponent) {

  }

  @Override
  public boolean isConnected() {
    return false;
  }

  @Override
  public Unsafe unsafe() {
    throw new IllegalStateException("Not implemented");
  }
}
