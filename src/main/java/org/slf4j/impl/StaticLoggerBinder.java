package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;
import sbt.util.Logger;

// This must be named StaticLoggerBinder. That's what SLF4J looks for.
public class StaticLoggerBinder implements LoggerFactoryBinder {
    // This needs to match what frontend-plugin-core uses for SLF4J
    public static String REQUESTED_API_VERSION = "1.7"; // !final

    public static Logger sbtLogger = null;

    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    private final ILoggerFactory loggerFactory;

    private StaticLoggerBinder(){
        loggerFactory = new SbtLoggerFactory();
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return SbtLoggerFactory.class.getName();
    }

    public static StaticLoggerBinder getSingleton(){
        return SINGLETON;
    }
}
