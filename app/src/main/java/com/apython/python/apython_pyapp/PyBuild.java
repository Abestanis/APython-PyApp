package com.apython.python.apython_pyapp;

/**
 * This file contains the configuration of this app.  
 * 
 * Created by Sebastian on 24.05.2016.
 */
public class PyBuild {
    public enum WindowType { NO_WINDOW, TERMINAL, SDL, WINDOW_MANAGER }
    
    public static final WindowType APP_WINDOW_TYPE        = WindowType.TERMINAL; /* REPLACE(72,80): windowType */
    public static final String MIN_PY_VERSION             = "3.4"; /* REPLACE(62,65): minPyVersion */
    public static final String PYTHON_MODULE_REQUIREMENTS = "twisted"; /* REPLACE(62,62): requirements */
}
