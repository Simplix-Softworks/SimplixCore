package dev.simplix.core.common;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReplacerTest {

  private static Replacer replacer;

  @Test
  @BeforeAll
  static void setUp() {
    replacer = Replacer.of("I am a {replace-me}");
    Assertions.assertEquals("I am a {replace-me}", replacer.messages().get(0));
  }

  @Test
  void replacedMessage() {
    final String[] replacedMessage = replacer
        .replaceAll("replace-me", "Replacer")
        .replacedMessage();
    Assertions.assertTrue(Arrays.equals(replacedMessage, new String[]{"I am a Replacer"}));
  }

  @Test
  void replacedMessageJoined() {
    String replacedMessageJoined = replacer
        .replaceAll("replace-me", "Replacer")
        .replacedMessageJoined();
    Assertions.assertEquals("I am a Replacer", replacedMessageJoined);
  }

}