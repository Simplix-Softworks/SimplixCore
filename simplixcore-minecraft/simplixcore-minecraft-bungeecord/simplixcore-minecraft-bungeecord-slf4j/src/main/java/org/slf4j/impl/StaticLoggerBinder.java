/*
 * This entire file is sublicensed to you under GPLv3 or (at your option) any
 * later version. The original copyright notice is retained below.
 */
/*
 * Portions of this file are
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Copyright (c) 2004-2011 QOS.ch All rights reserved.
 * <p>
 * Permission is hereby granted, free  of charge, to any person obtaining a  copy  of this  software
 * and  associated  documentation files  (the "Software"), to  deal in  the Software without
 * restriction, including without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to permit persons to whom the
 * Software  is furnished to do so, subject to the following conditions:
 * <p>
 * The  above  copyright  notice  and  this permission  notice  shall  be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND, EXPRESS OR  IMPLIED,
 * INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF MERCHANTABILITY,    FITNESS    FOR    A
 * PARTICULAR    PURPOSE    AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.slf4j.impl;

import dev.simplix.core.minecraft.bungeecord.slf4j.BungeeLoggerFactory;
import net.md_5.bungee.api.ProxyServer;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * The binding of {@link LoggerFactory} class with an actual instance of {@link ILoggerFactory} is
 * performed using information returned by this class.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Ronald Jack Jenkins Jr.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

  /**
   * Declare the version of the SLF4J API this implementation is compiled against. The value of this
   * field is modified with each major release.
   */
  // to avoid constant folding by the compiler, this field must *not* be final
  public static String REQUESTED_API_VERSION = "1.6.99";                           // !final

  /**
   * The unique instance of this class.
   */
  private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

  /**
   * The ILoggerFactory instance returned by the {@link #getLoggerFactory} method should always be
   * the same object
   */
  private final ILoggerFactory loggerFactory;

  private StaticLoggerBinder() {
    this.loggerFactory = detectLoggingFrameworkAndCreateFactory();
  }

  private ILoggerFactory detectLoggingFrameworkAndCreateFactory() {
    try {
      Class<?> staticLoggerBinder = Class.forName(
          StaticLoggerBinder.class.getName(),
          true,
          ProxyServer.class.getClassLoader());
      Object loggerBinder = staticLoggerBinder.getMethod("getSingleton").invoke(null);
      Object factory = loggerBinder.getClass().getMethod("getLoggerFactory").invoke(loggerBinder);
      ProxyServer
          .getInstance()
          .getLogger()
          .info("[Simplix] This proxy server comes with on-board slf4j support. Utilizing...");
      return (ILoggerFactory) factory;
    } catch (ClassNotFoundException classNotFoundException) {
      return new BungeeLoggerFactory();
    } catch (Exception e) {
      ProxyServer
          .getInstance()
          .getLogger()
          .warning("[Simplix] Cannot set up slf4j on this platform.");
      return null;
    }
  }

  /**
   * Return the singleton of this class.
   *
   * @return the StaticLoggerBinder singleton
   */
  public static final StaticLoggerBinder getSingleton() {
    return StaticLoggerBinder.SINGLETON;
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return this.loggerFactory;
  }

  @Override
  public String getLoggerFactoryClassStr() {
    return getLoggerFactory().getClass().getName();
  }

}