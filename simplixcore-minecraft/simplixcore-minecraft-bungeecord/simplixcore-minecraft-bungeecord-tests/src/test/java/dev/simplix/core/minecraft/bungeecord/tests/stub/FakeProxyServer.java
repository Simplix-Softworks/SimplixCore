package dev.simplix.core.minecraft.bungeecord.tests.stub;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class FakeProxyServer extends ProxyServer {

  Logger logger = Logger.getLogger("");

  @Override
  public String getName() {
    return "FakeProxyServer";
  }

  @Override
  public String getVersion() {
    return "1.0.0";
  }

  @Override
  public String getTranslation(String s, Object... objects) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public Logger getLogger() {
    return logger ;
  }

  @Override
  public Collection<ProxiedPlayer> getPlayers() {
    return new ArrayList<>();
  }

  @Override
  public ProxiedPlayer getPlayer(String s) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public ProxiedPlayer getPlayer(UUID uuid) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public Map<String, ServerInfo> getServers() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public ServerInfo getServerInfo(String s) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public PluginManager getPluginManager() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public ConfigurationAdapter getConfigurationAdapter() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void setConfigurationAdapter(ConfigurationAdapter configurationAdapter) {

  }

  @Override
  public ReconnectHandler getReconnectHandler() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public void setReconnectHandler(ReconnectHandler reconnectHandler) {

  }

  @Override
  public void stop() {

  }

  @Override
  public void stop(String s) {

  }

  @Override
  public void registerChannel(String s) {

  }

  @Override
  public void unregisterChannel(String s) {

  }

  @Override
  public Collection<String> getChannels() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public String getGameVersion() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public int getProtocolVersion() {
    return 0;
  }

  @Override
  public ServerInfo constructServerInfo(
      String s, InetSocketAddress inetSocketAddress, String s1, boolean b) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public ServerInfo constructServerInfo(
      String s, SocketAddress socketAddress, String s1, boolean b) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CommandSender getConsole() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public File getPluginsFolder() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public TaskScheduler getScheduler() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public int getOnlineCount() {
    return 0;
  }

  @Override
  public void broadcast(String s) {

  }

  @Override
  public void broadcast(BaseComponent... baseComponents) {

  }

  @Override
  public void broadcast(BaseComponent baseComponent) {

  }

  @Override
  public Collection<String> getDisabledCommands() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public ProxyConfig getConfig() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public Collection<ProxiedPlayer> matchPlayer(String s) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public Title createTitle() {
    throw new IllegalStateException("Not implemented");
  }
}
