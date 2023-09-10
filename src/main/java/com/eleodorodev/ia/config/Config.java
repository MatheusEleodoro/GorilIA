package com.eleodorodev.ia.config;


import jdk.jshell.spi.ExecutionControl;

import java.util.logging.Logger;

public class Config {
    private Config() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("");
    }
    public static final String AUDIO_PATCH  = System.getenv("AUDIO_PATCH");
    public static final String API_KEY = System.getenv("API_KEY");

    public static class Log {
        private Log(){}
        public static final Logger LOGGER  = Logger.getLogger(Log.class.getName());
    }
}
