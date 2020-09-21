package dev.simplix.core.minecraft.spigot.tests.stub;

import com.avaje.ebean.config.ServerConfig;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.Warning.WarningState;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

public class FakeServer implements Server {

  @Override
  public String getName() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getVersion() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getBukkitVersion() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Player[] _INVALID_getOnlinePlayers() {
    return new Player[0];
  }

  @Override
  public Collection<? extends Player> getOnlinePlayers() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getMaxPlayers() {
    return 0;
  }

  @Override
  public int getPort() {
    return 0;
  }

  @Override
  public int getViewDistance() {
    return 0;
  }

  @Override
  public String getIp() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getServerName() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getServerId() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getWorldType() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean getGenerateStructures() {
    return false;
  }

  @Override
  public boolean getAllowEnd() {
    return false;
  }

  @Override
  public boolean getAllowNether() {
    return false;
  }

  @Override
  public boolean hasWhitelist() {
    return false;
  }

  @Override
  public void setWhitelist(boolean value) {

  }

  @Override
  public Set<OfflinePlayer> getWhitelistedPlayers() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void reloadWhitelist() {

  }

  @Override
  public int broadcastMessage(String message) {
    return 0;
  }

  @Override
  public String getUpdateFolder() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public File getUpdateFolderFile() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public long getConnectionThrottle() {
    return 0;
  }

  @Override
  public int getTicksPerAnimalSpawns() {
    return 0;
  }

  @Override
  public int getTicksPerMonsterSpawns() {
    return 0;
  }

  @Override
  public Player getPlayer(String name) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Player getPlayerExact(String name) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public List<Player> matchPlayer(String name) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Player getPlayer(UUID id) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public PluginManager getPluginManager() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public BukkitScheduler getScheduler() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public ServicesManager getServicesManager() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public List<World> getWorlds() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public World createWorld(WorldCreator creator) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean unloadWorld(String name, boolean save) {
    return false;
  }

  @Override
  public boolean unloadWorld(World world, boolean save) {
    return false;
  }

  @Override
  public World getWorld(String name) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public World getWorld(UUID uid) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public MapView getMap(short id) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public MapView createMap(World world) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void reload() {

  }

  @Override
  public Logger getLogger() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public PluginCommand getPluginCommand(String name) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void savePlayers() {

  }

  @Override
  public boolean dispatchCommand(
      CommandSender sender, String commandLine) throws CommandException {
    return false;
  }

  @Override
  public void configureDbConfig(ServerConfig config) {

  }

  @Override
  public boolean addRecipe(Recipe recipe) {
    return false;
  }

  @Override
  public List<Recipe> getRecipesFor(ItemStack result) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Iterator<Recipe> recipeIterator() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void clearRecipes() {

  }

  @Override
  public void resetRecipes() {

  }

  @Override
  public Map<String, String[]> getCommandAliases() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getSpawnRadius() {
    return 0;
  }

  @Override
  public void setSpawnRadius(int value) {

  }

  @Override
  public boolean getOnlineMode() {
    return false;
  }

  @Override
  public boolean getAllowFlight() {
    return false;
  }

  @Override
  public boolean isHardcore() {
    return false;
  }

  @Override
  public boolean useExactLoginLocation() {
    return false;
  }

  @Override
  public void shutdown() {

  }

  @Override
  public int broadcast(String message, String permission) {
    return 0;
  }

  @Override
  public OfflinePlayer getOfflinePlayer(String name) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public OfflinePlayer getOfflinePlayer(UUID id) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Set<String> getIPBans() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void banIP(String address) {

  }

  @Override
  public void unbanIP(String address) {

  }

  @Override
  public Set<OfflinePlayer> getBannedPlayers() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public BanList getBanList(Type type) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Set<OfflinePlayer> getOperators() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public GameMode getDefaultGameMode() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setDefaultGameMode(GameMode mode) {

  }

  @Override
  public ConsoleCommandSender getConsoleSender() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public File getWorldContainer() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public OfflinePlayer[] getOfflinePlayers() {
    return new OfflinePlayer[0];
  }

  @Override
  public Messenger getMessenger() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public HelpMap getHelpMap() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Inventory createInventory(
      InventoryHolder owner, InventoryType type) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Inventory createInventory(
      InventoryHolder owner, InventoryType type, String title) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Inventory createInventory(InventoryHolder owner, int size)
      throws IllegalArgumentException {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Inventory createInventory(InventoryHolder owner, int size, String title)
      throws IllegalArgumentException {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getMonsterSpawnLimit() {
    return 0;
  }

  @Override
  public int getAnimalSpawnLimit() {
    return 0;
  }

  @Override
  public int getWaterAnimalSpawnLimit() {
    return 0;
  }

  @Override
  public int getAmbientSpawnLimit() {
    return 0;
  }

  @Override
  public boolean isPrimaryThread() {
    return false;
  }

  @Override
  public String getMotd() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getShutdownMessage() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public WarningState getWarningState() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public ItemFactory getItemFactory() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public ScoreboardManager getScoreboardManager() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public CachedServerIcon getServerIcon() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public CachedServerIcon loadServerIcon(BufferedImage image)
      throws IllegalArgumentException, Exception {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getIdleTimeout() {
    return 0;
  }

  @Override
  public void setIdleTimeout(int threshold) {

  }

  @Override
  public ChunkData createChunkData(World world) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public UnsafeValues getUnsafe() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Spigot spigot() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void sendPluginMessage(Plugin source, String channel, byte[] message) {

  }

  @Override
  public Set<String> getListeningPluginChannels() {
    throw new AbstractMethodError("Not implemented");
  }
}
