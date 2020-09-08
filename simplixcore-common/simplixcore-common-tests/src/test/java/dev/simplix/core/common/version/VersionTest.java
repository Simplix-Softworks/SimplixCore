package dev.simplix.core.common.version;

import dev.simplix.core.common.updater.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionTest {

  @Test
  public void testVersions() {
    Version v1_12_2 = Version.parse("1.12.2");
    Assertions.assertEquals(1, v1_12_2.values().get(0));
    Assertions.assertEquals(12, v1_12_2.values().get(1));
    Assertions.assertEquals(2, v1_12_2.values().get(2));

    Version v1_16 = Version.parse("1.16");
    Assertions.assertEquals(1, v1_16.values().get(0));
    Assertions.assertEquals(16, v1_16.values().get(1));

    Assertions.assertTrue(v1_16.newerThen(v1_12_2));
    Assertions.assertTrue(v1_12_2.olderThen(v1_16));
  }

}
