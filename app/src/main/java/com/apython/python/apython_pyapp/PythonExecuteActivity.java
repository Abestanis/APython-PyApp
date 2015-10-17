package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;
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

    private PythonExecute pyApp;
    private String libPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent result = getIntent();
        System.setProperty("python.android.entry.class", PythonExecute.class.getCanonicalName().replace('.', '/'));
        ArrayList<String> pythonLibs = result.getStringArrayListExtra("pythonLibs");
        this.libPath = new File(pythonLibs.get(0)).getParent();
        this.pyApp = new PythonExecute(pythonLibs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.pyApp.startApp(System.mapLibraryName("python" + getIntent().getStringExtra("pythonVersion")),
                            getIntent().getStringExtra("pythonExecutablePath"),
                            this.libPath,
                            getIntent().getStringExtra("pythonHome"),
                            getCacheDir().getAbsolutePath(),
                            getIntent().getStringExtra("xdgBasePath"),
                            MainActivity.getCodeDir(getApplicationContext()).getAbsolutePath(),
                            MainActivity.TAG,
                            false);
        finish();
    }
}
