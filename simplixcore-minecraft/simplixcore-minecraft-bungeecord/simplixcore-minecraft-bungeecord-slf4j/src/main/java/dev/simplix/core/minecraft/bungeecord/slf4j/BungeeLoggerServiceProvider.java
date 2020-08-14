package dev.simplix.core.minecraft.bungeecord.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public final class BungeeLoggerServiceProvider implements SLF4JServiceProvider {

  public static String REQUESTED_API_VERSION = "1.8.99";

  private ILoggerFactory loggerFactory;
  private IMarkerFactory markerFactory;
  private MDCAdapter mdcAdapter;

  @Override
  public ILoggerFactory getLoggerFactory() {
    return this.loggerFactory;
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return this.markerFactory;
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return this.mdcAdapter;
  }

  @Override
  public String getRequesteApiVersion() {
    return REQUESTED_API_VERSION;
  }

  @Override
  public void initialize() {
    this.loggerFactory = new BungeeLoggerFactory();
    this.markerFactory = new BasicMarkerFactory();
    this.mdcAdapter = new BasicMDCAdapter();
  }

}
