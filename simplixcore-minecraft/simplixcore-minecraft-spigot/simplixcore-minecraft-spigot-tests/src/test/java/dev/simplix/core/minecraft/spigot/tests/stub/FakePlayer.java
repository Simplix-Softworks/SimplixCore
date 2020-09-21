package dev.simplix.core.minecraft.spigot.tests.stub;

import java.net.InetSocketAddress;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class FakePlayer implements Player {

  @Override
  public String getDisplayName() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setDisplayName(String name) {

  }

  @Override
  public String getPlayerListName() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setPlayerListName(String name) {

  }

  @Override
  public Location getCompassTarget() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setCompassTarget(Location loc) {

  }

  @Override
  public InetSocketAddress getAddress() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean isConversing() {
    return false;
  }

  @Override
  public void acceptConversationInput(String input) {

  }

  @Override
  public boolean beginConversation(Conversation conversation) {
    return false;
  }

  @Override
  public void abandonConversation(Conversation conversation) {

  }

  @Override
  public void abandonConversation(
      Conversation conversation, ConversationAbandonedEvent details) {

  }

  @Override
  public void sendRawMessage(String message) {

  }

  @Override
  public void kickPlayer(String message) {

  }

  @Override
  public void chat(String msg) {

  }

  @Override
  public boolean performCommand(String command) {
    return false;
  }

  @Override
  public boolean isSneaking() {
    return false;
  }

  @Override
  public void setSneaking(boolean sneak) {

  }

  @Override
  public boolean isSprinting() {
    return false;
  }

  @Override
  public void setSprinting(boolean sprinting) {

  }

  @Override
  public void saveData() {

  }

  @Override
  public void loadData() {

  }

  @Override
  public boolean isSleepingIgnored() {
    return false;
  }

  @Override
  public void setSleepingIgnored(boolean isSleeping) {

  }

  @Override
  public void playNote(Location loc, byte instrument, byte note) {

  }

  @Override
  public void playNote(Location loc, Instrument instrument, Note note) {

  }

  @Override
  public void playSound(Location location, Sound sound, float volume, float pitch) {

  }

  @Override
  public void playSound(Location location, String sound, float volume, float pitch) {

  }

  @Override
  public void playEffect(Location loc, Effect effect, int data) {

  }

  @Override
  public <T> void playEffect(Location loc, Effect effect, T data) {

  }

  @Override
  public void sendBlockChange(Location loc, Material material, byte data) {

  }

  @Override
  public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
    return false;
  }

  @Override
  public void sendBlockChange(Location loc, int material, byte data) {

  }

  @Override
  public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException {

  }

  @Override
  public void sendMap(MapView map) {

  }

  @Override
  public void updateInventory() {

  }

  @Override
  public void awardAchievement(Achievement achievement) {

  }

  @Override
  public void removeAchievement(Achievement achievement) {

  }

  @Override
  public boolean hasAchievement(Achievement achievement) {
    return false;
  }

  @Override
  public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {

  }

  @Override
  public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {

  }

  @Override
  public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {

  }

  @Override
  public int getStatistic(Statistic statistic) throws IllegalArgumentException {
    return 0;
  }

  @Override
  public void incrementStatistic(Statistic statistic, Material material)
      throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(Statistic statistic, Material material)
      throws IllegalArgumentException {

  }

  @Override
  public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
    return 0;
  }

  @Override
  public void incrementStatistic(Statistic statistic, Material material, int amount)
      throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(Statistic statistic, Material material, int amount)
      throws IllegalArgumentException {

  }

  @Override
  public void setStatistic(Statistic statistic, Material material, int newValue)
      throws IllegalArgumentException {

  }

  @Override
  public void incrementStatistic(Statistic statistic, EntityType entityType)
      throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(Statistic statistic, EntityType entityType)
      throws IllegalArgumentException {

  }

  @Override
  public int getStatistic(Statistic statistic, EntityType entityType)
      throws IllegalArgumentException {
    return 0;
  }

  @Override
  public void incrementStatistic(Statistic statistic, EntityType entityType, int amount)
      throws IllegalArgumentException {

  }

  @Override
  public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {

  }

  @Override
  public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {

  }

  @Override
  public void setPlayerTime(long time, boolean relative) {

  }

  @Override
  public long getPlayerTime() {
    return 0;
  }

  @Override
  public long getPlayerTimeOffset() {
    return 0;
  }

  @Override
  public boolean isPlayerTimeRelative() {
    return false;
  }

  @Override
  public void resetPlayerTime() {

  }

  @Override
  public WeatherType getPlayerWeather() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setPlayerWeather(WeatherType type) {

  }

  @Override
  public void resetPlayerWeather() {

  }

  @Override
  public void giveExp(int amount) {

  }

  @Override
  public void giveExpLevels(int amount) {

  }

  @Override
  public float getExp() {
    return 0;
  }

  @Override
  public void setExp(float exp) {

  }

  @Override
  public int getLevel() {
    return 0;
  }

  @Override
  public void setLevel(int level) {

  }

  @Override
  public int getTotalExperience() {
    return 0;
  }

  @Override
  public void setTotalExperience(int exp) {

  }

  @Override
  public float getExhaustion() {
    return 0;
  }

  @Override
  public void setExhaustion(float value) {

  }

  @Override
  public float getSaturation() {
    return 0;
  }

  @Override
  public void setSaturation(float value) {

  }

  @Override
  public int getFoodLevel() {
    return 0;
  }

  @Override
  public void setFoodLevel(int value) {

  }

  @Override
  public boolean isOnline() {
    return false;
  }

  @Override
  public boolean isBanned() {
    return false;
  }

  @Override
  public void setBanned(boolean banned) {

  }

  @Override
  public boolean isWhitelisted() {
    return false;
  }

  @Override
  public void setWhitelisted(boolean value) {

  }

  @Override
  public Player getPlayer() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public long getFirstPlayed() {
    return 0;
  }

  @Override
  public long getLastPlayed() {
    return 0;
  }

  @Override
  public boolean hasPlayedBefore() {
    return false;
  }

  @Override
  public Location getBedSpawnLocation() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setBedSpawnLocation(Location location) {

  }

  @Override
  public void setBedSpawnLocation(Location location, boolean force) {

  }

  @Override
  public boolean getAllowFlight() {
    return false;
  }

  @Override
  public void setAllowFlight(boolean flight) {

  }

  @Override
  public void hidePlayer(Player player) {

  }

  @Override
  public void showPlayer(Player player) {

  }

  @Override
  public boolean canSee(Player player) {
    return false;
  }

  @Override
  public Location getLocation() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Location getLocation(Location loc) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Vector getVelocity() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setVelocity(Vector velocity) {

  }

  @Override
  public boolean isOnGround() {
    return false;
  }

  @Override
  public World getWorld() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean teleport(Location location) {
    return false;
  }

  @Override
  public boolean teleport(
      Location location, TeleportCause cause) {
    return false;
  }

  @Override
  public boolean teleport(Entity destination) {
    return false;
  }

  @Override
  public boolean teleport(
      Entity destination, TeleportCause cause) {
    return false;
  }

  @Override
  public List<Entity> getNearbyEntities(double x, double y, double z) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getEntityId() {
    return 0;
  }

  @Override
  public int getFireTicks() {
    return 0;
  }

  @Override
  public void setFireTicks(int ticks) {

  }

  @Override
  public int getMaxFireTicks() {
    return 0;
  }

  @Override
  public void remove() {

  }

  @Override
  public boolean isDead() {
    return false;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public void sendMessage(String message) {

  }

  @Override
  public void sendMessage(String[] messages) {

  }

  @Override
  public Server getServer() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Entity getPassenger() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean setPassenger(Entity passenger) {
    return false;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean eject() {
    return false;
  }

  @Override
  public float getFallDistance() {
    return 0;
  }

  @Override
  public void setFallDistance(float distance) {

  }

  @Override
  public EntityDamageEvent getLastDamageCause() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setLastDamageCause(EntityDamageEvent event) {

  }

  @Override
  public UUID getUniqueId() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getTicksLived() {
    return 0;
  }

  @Override
  public void setTicksLived(int value) {

  }

  @Override
  public void playEffect(EntityEffect type) {

  }

  @Override
  public EntityType getType() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean isInsideVehicle() {
    return false;
  }

  @Override
  public boolean leaveVehicle() {
    return false;
  }

  @Override
  public Entity getVehicle() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getCustomName() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setCustomName(String name) {

  }

  @Override
  public boolean isCustomNameVisible() {
    return false;
  }

  @Override
  public void setCustomNameVisible(boolean flag) {

  }

  @Override
  public boolean isFlying() {
    return false;
  }

  @Override
  public void setFlying(boolean value) {

  }

  @Override
  public float getFlySpeed() {
    return 0;
  }

  @Override
  public void setFlySpeed(float value) throws IllegalArgumentException {

  }

  @Override
  public float getWalkSpeed() {
    return 0;
  }

  @Override
  public void setWalkSpeed(float value) throws IllegalArgumentException {

  }

  @Override
  public void setTexturePack(String url) {

  }

  @Override
  public void setResourcePack(String url) {

  }

  @Override
  public Scoreboard getScoreboard() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setScoreboard(Scoreboard scoreboard)
      throws IllegalArgumentException, IllegalStateException {

  }

  @Override
  public boolean isHealthScaled() {
    return false;
  }

  @Override
  public void setHealthScaled(boolean scale) {

  }

  @Override
  public double getHealthScale() {
    return 0;
  }

  @Override
  public void setHealthScale(double scale) throws IllegalArgumentException {

  }

  @Override
  public Entity getSpectatorTarget() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setSpectatorTarget(Entity entity) {

  }

  @Override
  public void sendTitle(String title, String subtitle) {

  }

  @Override
  public void resetTitle() {

  }

  @Override
  public Spigot spigot() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Map<String, Object> serialize() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public String getName() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public PlayerInventory getInventory() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Inventory getEnderChest() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean setWindowProperty(Property prop, int value) {
    return false;
  }

  @Override
  public InventoryView getOpenInventory() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public InventoryView openInventory(Inventory inventory) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public InventoryView openWorkbench(Location location, boolean force) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public InventoryView openEnchanting(Location location, boolean force) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void openInventory(InventoryView inventory) {

  }

  @Override
  public void closeInventory() {

  }

  @Override
  public ItemStack getItemInHand() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setItemInHand(ItemStack item) {

  }

  @Override
  public ItemStack getItemOnCursor() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setItemOnCursor(ItemStack item) {

  }

  @Override
  public boolean isSleeping() {
    return false;
  }

  @Override
  public int getSleepTicks() {
    return 0;
  }

  @Override
  public GameMode getGameMode() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void setGameMode(GameMode mode) {

  }

  @Override
  public boolean isBlocking() {
    return false;
  }

  @Override
  public int getExpToLevel() {
    return 0;
  }

  @Override
  public double getEyeHeight() {
    return 0;
  }

  @Override
  public double getEyeHeight(boolean ignoreSneaking) {
    return 0;
  }

  @Override
  public Location getEyeLocation() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public List<Block> getLineOfSight(
      HashSet<Byte> transparent, int maxDistance) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Egg throwEgg() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Snowball throwSnowball() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public Arrow shootArrow() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public int getRemainingAir() {
    return 0;
  }

  @Override
  public void setRemainingAir(int ticks) {

  }

  @Override
  public int getMaximumAir() {
    return 0;
  }

  @Override
  public void setMaximumAir(int ticks) {

  }

  @Override
  public int getMaximumNoDamageTicks() {
    return 0;
  }

  @Override
  public void setMaximumNoDamageTicks(int ticks) {

  }

  @Override
  public double getLastDamage() {
    return 0;
  }

  @Override
  public void setLastDamage(double damage) {

  }

  @Override
  public int _INVALID_getLastDamage() {
    return 0;
  }

  @Override
  public void _INVALID_setLastDamage(int damage) {

  }

  @Override
  public int getNoDamageTicks() {
    return 0;
  }

  @Override
  public void setNoDamageTicks(int ticks) {

  }

  @Override
  public Player getKiller() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean addPotionEffect(PotionEffect effect) {
    return false;
  }

  @Override
  public boolean addPotionEffect(PotionEffect effect, boolean force) {
    return false;
  }

  @Override
  public boolean addPotionEffects(Collection<PotionEffect> effects) {
    return false;
  }

  @Override
  public boolean hasPotionEffect(PotionEffectType type) {
    return false;
  }

  @Override
  public void removePotionEffect(PotionEffectType type) {

  }

  @Override
  public Collection<PotionEffect> getActivePotionEffects() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean hasLineOfSight(Entity other) {
    return false;
  }

  @Override
  public boolean getRemoveWhenFarAway() {
    return false;
  }

  @Override
  public void setRemoveWhenFarAway(boolean remove) {

  }

  @Override
  public EntityEquipment getEquipment() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean getCanPickupItems() {
    return false;
  }

  @Override
  public void setCanPickupItems(boolean pickup) {

  }

  @Override
  public boolean isLeashed() {
    return false;
  }

  @Override
  public Entity getLeashHolder() throws IllegalStateException {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean setLeashHolder(Entity holder) {
    return false;
  }

  @Override
  public void damage(double amount) {

  }

  @Override
  public void _INVALID_damage(int amount) {

  }

  @Override
  public void damage(double amount, Entity source) {

  }

  @Override
  public void _INVALID_damage(int amount, Entity source) {

  }

  @Override
  public double getHealth() {
    return 0;
  }

  @Override
  public void setHealth(double health) {

  }

  @Override
  public int _INVALID_getHealth() {
    return 0;
  }

  @Override
  public void _INVALID_setHealth(int health) {

  }

  @Override
  public double getMaxHealth() {
    return 0;
  }

  @Override
  public void setMaxHealth(double health) {

  }

  @Override
  public int _INVALID_getMaxHealth() {
    return 0;
  }

  @Override
  public void _INVALID_setMaxHealth(int health) {

  }

  @Override
  public void resetMaxHealth() {

  }

  @Override
  public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {

  }

  @Override
  public List<MetadataValue> getMetadata(String metadataKey) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean hasMetadata(String metadataKey) {
    return false;
  }

  @Override
  public void removeMetadata(String metadataKey, Plugin owningPlugin) {

  }

  @Override
  public boolean isPermissionSet(String name) {
    return false;
  }

  @Override
  public boolean isPermissionSet(Permission perm) {
    return false;
  }

  @Override
  public boolean hasPermission(String name) {
    return false;
  }

  @Override
  public boolean hasPermission(Permission perm) {
    return false;
  }

  @Override
  public PermissionAttachment addAttachment(
      Plugin plugin, String name, boolean value) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public PermissionAttachment addAttachment(Plugin plugin) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public void removeAttachment(PermissionAttachment attachment) {

  }

  @Override
  public void recalculatePermissions() {

  }

  @Override
  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public boolean isOp() {
    return false;
  }

  @Override
  public void setOp(boolean value) {

  }

  @Override
  public void sendPluginMessage(Plugin source, String channel, byte[] message) {

  }

  @Override
  public Set<String> getListeningPluginChannels() {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
    throw new AbstractMethodError("Not implemented");
  }

  @Override
  public <T extends Projectile> T launchProjectile(
      Class<? extends T> projectile, Vector velocity) {
    throw new AbstractMethodError("Not implemented");
  }
}
