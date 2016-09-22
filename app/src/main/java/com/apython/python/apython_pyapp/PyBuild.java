package com.apython.python.apython_pyapp; /* REPLACE(9,41): appId */

/**
 * This file contains the configuration of this app.  
 * 
 * Created by Sebastian on 24.05.2016.
 */
public class PyBuild {
    @SuppressWarnings("unused")
    public enum WindowType { NO_WINDOW, TERMINAL, SDL, WINDOW_MANAGER, ANDROID }
    
    public static final String APP_LOG_TAG                = "PythonApp"; /* REPLACE(62,71): appLogTag */
    public static final WindowType APP_WINDOW_TYPE        = WindowType.TERMINAL; /* REPLACE(72,80): windowType */
    public static final String MIN_PY_VERSION             = "3.4"; /* REPLACE(62,65): minPyVersion */
    public static final String PYTHON_MODULE_REQUIREMENTS = "twisted"; /* REPLACE(62,69): requirements */
}
