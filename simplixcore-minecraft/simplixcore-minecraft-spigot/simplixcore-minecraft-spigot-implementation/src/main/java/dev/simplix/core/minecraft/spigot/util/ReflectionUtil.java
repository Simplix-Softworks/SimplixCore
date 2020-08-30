package dev.simplix.core.minecraft.spigot.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

@Slf4j
public final class ReflectionUtil {

  private ReflectionUtil() {
  }

  private static final Map<Map.Entry<Class<?>, String>, Field> CACHED_FIELDS = new HashMap<>();
  private static final Map<String, Class<?>> CACHED_CLASSES = new HashMap<>();

  public static Class<?> getClass(String classname) throws ClassNotFoundException {
    String path = classname
        .replace("{nms}", "net.minecraft.server." + serverVersion())
        .replace("{obc}", "org.bukkit.craftbukkit." + serverVersion())
        .replace("{nm}", "net.minecraft." + serverVersion());
    Class<?> out = CACHED_CLASSES.get(path);
    if (out == null) {
      out = Class.forName(path);
      CACHED_CLASSES.put(path, out);
    }
    return out;
  }

  public static String serverVersion() {
    return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }

  public static Object nmsPlayer(Player p)
      throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method getHandle = p.getClass().getMethod("getHandle");
    return getHandle.invoke(p);
  }

  public static Object obcPlayer(Player p)
      throws SecurityException, IllegalArgumentException, ClassNotFoundException {
    return getClass("{obc}.entity.CraftPlayer").cast(p);
  }

  public static Object nmsWorld(World w)
      throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method getHandle = w.getClass().getMethod("getHandle");
    return getHandle.invoke(w);
  }

  public static Object nmsScoreboard(Scoreboard s)
      throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method getHandle = s.getClass().getMethod("getHandle");
    return getHandle.invoke(s);
  }

  public static Object fieldValue(Object instance, String fieldName)
      throws IllegalArgumentException, IllegalAccessException, SecurityException {
    final Map.Entry<Class<?>, String> key = new AbstractMap.SimpleEntry<>(
        instance.getClass(),
        fieldName);
    final Field field = CACHED_FIELDS.computeIfAbsent(key, i -> {
      try {
        return instance.getClass().getDeclaredField(fieldName);
      } catch (final NoSuchFieldException e) {
        log.error("Exception occurred", e);
      }
      return null;
    });
    if (field == null)
      return null;
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }
    return field.get(instance);
  }

  public static <T> T fieldValue(Field field, Object obj) {
    try {
      return (T) field.get(obj);
    } catch (Exception e) {
      log.error("Exception occurred", e);
      return null;
    }
  }

  public static Field field(Class<?> clazz, String fieldName) throws Exception {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }

  public static void value(Object instance, String field, Object value) {
    try {
      Field f = instance.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(instance, value);
    } catch (Exception e) {
      log.error("Exception occurred", e);
    }
  }

  public static void value(Class c, Object instance, String field, Object value) {
    try {
      Field f = c.getDeclaredField(field);
      f.setAccessible(true);
      f.set(instance, value);
    } catch (Exception e) {
      log.error("Exception occurred", e);
    }
  }

  public static void valueSubclass(Class<?> clazz, Object instance, String field, Object value) {
    try {
      Field f = clazz.getDeclaredField(field);
      f.setAccessible(true);
      f.set(instance, value);
    } catch (Exception e) {
      log.error("Exception occurred", e);
    }
  }

  public static void sendAllPacket(Object packet) throws Exception {
    for (Player p : Bukkit.getOnlinePlayers()) {
      Object nmsPlayer = nmsPlayer(p);
      Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
      connection
          .getClass()
          .getMethod("sendPacket", ReflectionUtil.getClass("{nms}.Packet"))
          .invoke(connection, packet);
    }
  }

  public static void sendListPacket(List<String> players, Object packet) {
    try {
      for (String name : players) {
        Object nmsPlayer = nmsPlayer(Bukkit.getPlayer(name));
        Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        connection
            .getClass()
            .getMethod("sendPacket", ReflectionUtil.getClass("{nms}.Packet"))
            .invoke(connection, packet);
      }
    } catch (Exception e) {
      log.error("Exception occurred", e);
    }
  }

  public static void sendPlayerPacket(Player p, Object packet) throws Exception {
    Object nmsPlayer = nmsPlayer(p);
    Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
    connection
        .getClass()
        .getMethod("sendPacket", ReflectionUtil.getClass("{nms}.Packet"))
        .invoke(connection, packet);
  }

  public static void listFields(Object e) {
    System.out.println(e.getClass().getName() + " contains " + e
        .getClass()
        .getDeclaredFields().length + " declared fields.");
    System.out.println(e.getClass().getName() + " contains " + e
        .getClass()
        .getDeclaredClasses().length + " declared classes.");
    Field[] fds = e.getClass().getDeclaredFields();
    for (int i = 0; i < fds.length; i++) {
      fds[i].setAccessible(true);
      try {
        System.out.println(fds[i].getName() + " -> " + fds[i].get(e));
      } catch (IllegalArgumentException | IllegalAccessException e1) {
        log.error("Exception occurred", e1);
      }
    }
  }

  public static Object fieldValue(Class<?> superclass, Object instance, String fieldName)
      throws IllegalAccessException, NoSuchFieldException {
    Field field = superclass.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(instance);
  }
}
