package dev.simplix.core.common.updater;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public class Version implements Comparable<Version> {

  private final String pattern;
  private final List<Integer> values = new ArrayList<>(3);

  private Version(String pattern) {
    this.pattern = pattern;
  }

  public void value(int pos, int value) {
    values.add(pos, value);
  }

  public boolean newerThen(Version version) {
    return compareTo(version) < 0;
  }

  public boolean olderThen(Version version) {
    return compareTo(version) > 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Version version = (Version) o;
    return pattern.equals(version.pattern) &&
           values.equals(version.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern, values);
  }

  @Override
  public int compareTo(@NotNull Version version) {
    if(!version.pattern.equals(pattern)) {
      throw new IllegalArgumentException("Pattern mismatch");
    }
    for (int i = 0; i < version.values.size(); i++) {
      if(i >= values.size()) {
        return 1;
      }
      int j = version.values.get(i);
      int value = values.get(i);
      if(j > value) {
        return 1;
      } else if(j < value) {
        return -1;
      }
    }
    return 0;
  }

  public static Version parse(String version) {
    return parse("^(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)$", version);
  }

  public static Version parse(String pattern, String versionString) {
    Version version = new Version(pattern);
    Pattern regex = Pattern.compile(pattern);
    Matcher matcher = regex.matcher(versionString);
    int group = 0;
    while (matcher.find()) {
      for (int i = 1; i <= matcher.groupCount(); i++) {
        String match = matcher.group(i);
        if(match == null) {
          continue;
        }
        match = match.replaceAll("\\D+", "");
        if(match.matches("\\d+")) {
          version.values.add(Integer.parseInt(match));
        }
        group ++;
      }
    }
    return version;
  }

}
