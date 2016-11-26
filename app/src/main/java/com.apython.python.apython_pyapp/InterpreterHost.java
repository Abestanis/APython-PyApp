package com.apython.python.apython_pyapp; /* REPLACE(9,41): appId */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

/**
 * A wrapper of the defined interface of any Python Host.
 * One should be able to just copy this class into ones
 * Python App project and let it handle all communication.
 * 
 * Created by Sebastian on 20.08.2016.
 */
@SuppressWarnings("JniMissingFunction") // They are all provided by the Python host
public class InterpreterHost {
    // The protocol version used by this app
    public static final int PROTOCOL_VERSION = 0;
    
    // The apps activity which hosts the UI for the Python app.
    private final Activity hostingAppActivity;
    
    private boolean isInterpreterLoaded = false;
    
    private String logTag = "PythonApp";
    
    public static final int ON_DESTROY = 0;
    public static final int ON_PAUSE = 1;
    public static final int ON_RESUME = 2;
    
    public InterpreterHost(Activity hostingAppActivity, String logTag) {
        this.hostingAppActivity = hostingAppActivity;
        if (logTag != null) this.logTag = logTag;
    }

    public boolean connectToHost(int requestId, String minPyVersion, String requirements, Class verifyActivity) {
        Intent startIntent = new Intent("com.python.pythonhost.PYTHON_APP_GET_EXECUTION_INFO");
        startIntent.putExtra("protocolVersion", PROTOCOL_VERSION);
        startIntent.putExtra("minPythonVersion", minPyVersion);
        if (requirements.length() > 0) startIntent.putExtra("requirements", requirements);
        startIntent.putExtra("securityIntent", new Intent(hostingAppActivity, verifyActivity));
        try {
            hostingAppActivity.startActivityForResult(startIntent, requestId);
            return true;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }
    
    @SuppressLint("UnsafeDynamicallyLoadedCode") // We trust our permission
    public boolean loadInterpreterFromHostData(Intent data) {
        System.setProperty("python.android.app.wrapper.class",
                           this.getClass().getCanonicalName().replace('.', '/'));
        String libPath = data.getStringExtra("libPath");
        if (libPath == null) return false;
        try {
            System.load(libPath);
            setLogTag(logTag);
        } catch (UnsatisfiedLinkError e) {
            Log.e(logTag, "Failed to load the native library provided py the interpreter host!", e);
            return false;
        }
        isInterpreterLoaded = loadPythonHost(hostingAppActivity, data.getStringExtra("pythonVersion"));
        return isInterpreterLoaded;
    }
    
    public void notifyActivityLifecycleEvent(int eventId) {
        if (isInterpreterLoaded) {
            onActivityLifecycleEvent(eventId);
        }
    }
    
    private native boolean loadPythonHost(Activity activity, String pythonVersion);
    private native void    setLogTag(String tag);
    public  native Object  setWindow(int windowType, ViewGroup parent);
    public  native int     startInterpreter(String[] args);
    private native void    onActivityLifecycleEvent(int eventId);
}
