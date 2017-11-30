package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SbtLoggerFactory implements ILoggerFactory {
    public Logger getLogger(String name) {
        return new SbtLoggerAdapter(StaticLoggerBinder.sbtLogger);
    }
}
