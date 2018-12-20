package com.im.nettychat.config;

import java.io.IOException;
import java.util.Properties;

/**
 * @author hejianglong
 * @date 2018/12/20.
 */
public interface LoadResource {

    Properties loadParam(String[] args) throws IOException;

    boolean isTerminal();

    boolean isConfig();
}
