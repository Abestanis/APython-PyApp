package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

/*
 * This activity will be called by the Python interpreter.
 * The intent that started the activity will provide the Python home path and libs.
 * The calling app also needs the permission "com.python.permission.PYTHONHOST".
 * This way we don't introduce a security hole because we execute code from a remote location.
 *
 * Created by Sebastian on 28.06.2015.
 */

public class PythonExecuteActivity extends Activity {

    static {
        System.loadLibrary("envSet");
    }

    private PythonExecute pyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent result = getIntent();
        this.setEnv("JAVA_PYTHON_MAIN_CLASSPATH", PythonExecute.class.getCanonicalName().replace('.', '/'));
        ArrayList<String> pythonLibs = result.getStringArrayListExtra("pythonLibs");
        this.pyApp = new PythonExecute(pythonLibs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.pyApp.startApp(MainActivity.getCodeDir(getApplicationContext()).getAbsolutePath(),
                            getIntent().getStringExtra("pythonHome"),
                            getCacheDir().getAbsolutePath(),
                            MainActivity.TAG);
        finish();
    }

    private native void setEnv(String key, String value);
}
