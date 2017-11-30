package org.slf4j.impl;

import sbt.util.Logger;

public final class SbtLoggerAdapter extends AbstractSbtLoggerAdapter {
    public SbtLoggerAdapter(Logger log) {
        super(log);
    }

    @Override
    public void trace(String s, Object[] objects) {
        debug(s, objects);
    }

    @Override
    public void debug(String s, Object[] objects) {
        debug(s, toSeq(objects));
    }

    @Override
    public void info(String s, Object[] objects) {
        info(s, toSeq(objects));
    }

    @Override
    public void warn(String s, Object[] objects) {
        warn(s, toSeq(objects));
    }

    @Override
    public void error(String s, Object[] objects) {
        error(s, toSeq(objects));
    }
}
