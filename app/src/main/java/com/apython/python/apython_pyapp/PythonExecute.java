package com.apython.python.apython_pyapp;

/*
 * Execute the python code of this app with the interpreter libraries specified by the Python host.
 *
 * Created by Sebastian on 27.06.2015.
 */

import android.util.Log;

import java.util.ArrayList;

public class PythonExecute {

    public PythonExecute(ArrayList<String> pythonLibs) {
        for (String lib : pythonLibs) {
            Log.d(MainActivity.TAG, "Loading lib from " + lib);
            System.load(lib);
        }
    }

    public native int startApp(String pythonExecutable, String pythonLibs, String pythonHome, String pythonTemp, String xdgBasePath, String appBaseDir, String appTag, boolean redirectOutput);
}
