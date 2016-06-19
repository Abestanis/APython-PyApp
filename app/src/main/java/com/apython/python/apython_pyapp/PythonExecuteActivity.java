package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;


/*
 * This activity will be called by the Python interpreter.
 * The intent that started the activity will provide the Python home path and libs.
 * The calling app also needs the permission "com.python.permission.PYTHONHOST".
 * This way we don't introduce a security hole because we execute code from a remote location.
 * 
 * Challenges:
 * 
 * The interpreter class must be loaded by a classloader extending the classloader which loaded this class, so that the loaded class
 * recognizes this FragmentActivity as a FragmentActivity.
 * But the classloader also needs to know the location of the native libraries.
 * Additionally, native code (while cached) can only be loaded by the same loader again and will crash otherwise.
 *
 * Created by Sebastian on 28.06.2015.
 */

public class PythonExecuteActivity extends FragmentActivity {
    
    private static final Map<String, ClassLoader> classLoaderCache = new HashMap<>();

    private Object      interpreter           = null;
    private Bundle      interpreterArgs       = null;
    private ClassLoader pythonHostClassLoader = null;
    private Thread appThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent result = getIntent();
        String interpreterClassPath = result.getStringExtra("interpreterClass");
        if (interpreterClassPath == null) {
            errorExit("Python Host version didn't provide an interpreter");
            return;
        }
        final String apkPath = result.getStringExtra("apkPath");
        final String nativeDir = result.getStringExtra("nativeDir");
        File dexTemp;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dexTemp = getCodeCacheDir();
        } else {
            dexTemp = new File(getCacheDir(), "dexCodeCache");
        }
        if (!dexTemp.exists() && !dexTemp.mkdirs()) {
            dexTemp = getCacheDir();
        }
        Class<?> interpreterClass;
        try {
            // Check if class loaded
            interpreterClass = Class.forName(interpreterClassPath);
        } catch (ClassNotFoundException _) {
            pythonHostClassLoader = classLoaderCache.get(apkPath);
            if (pythonHostClassLoader == null) {
                pythonHostClassLoader = new DexClassLoader(apkPath, dexTemp.getAbsolutePath(),
                                                           nativeDir, getClassLoader());
                classLoaderCache.put(apkPath, pythonHostClassLoader);
            }
            Log.e(MainActivity.TAG, "Classloader: " + pythonHostClassLoader.hashCode());
            try {
                interpreterClass = pythonHostClassLoader.loadClass(interpreterClassPath);
            } catch (ClassNotFoundException e) {
                errorExit("Cant find the provided interpreter", e);
                return;
            }
        }
        try {
            interpreter = interpreterClass.newInstance();
        } catch (Exception e) {
            errorExit("Error instantiating the provided interpreter", e);
        }
        interpreterArgs = new Bundle();
        interpreterArgs.putStringArray("interpreterArgs", new String[] {
                new File(MainActivity.getCodeDir(getApplicationContext()), "main.py").getAbsolutePath()});
        if (PyBuild.APP_WINDOW_TYPE != PyBuild.WindowType.NO_WINDOW)
            interpreterArgs.putString("windowType", PyBuild.APP_WINDOW_TYPE.name());
    }
    
    private void errorExit(String message) {
        errorExit(message, null);
    }
    
    private void errorExit(final String message, Throwable e) {
        Log.e(MainActivity.TAG, "Python App exiting: " + message, e);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        appThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Method runMethod = interpreter.getClass().getMethod("run", Activity.class, String.class, Bundle.class);
                    runMethod.invoke(interpreter, PythonExecuteActivity.this, getIntent().getStringExtra("pythonVersion"), interpreterArgs);
                } catch (SecurityException | NoSuchMethodException | IllegalAccessException e) {
                    errorExit("The provided interpreter does not conform to the defined interface", e);
                } catch (InvocationTargetException e) {
                    errorExit("An error occurred during execution of the interpreter", e);
                }
                Log.w(MainActivity.TAG, "<<< FINISH >>>");
                finish();
            }
        });
        if (pythonHostClassLoader != null) appThread.setContextClassLoader(pythonHostClassLoader);
        appThread.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interpreter instanceof Activity) {
            if (((Activity) interpreter).dispatchKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (interpreter instanceof Window.Callback) {
            ((Window.Callback) interpreter).onWindowFocusChanged(hasFocus);
        }
    }
}
