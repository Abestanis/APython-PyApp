package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {

    // The tag for this App // TODO: Maybe use the app name instead?
    public static final String TAG = "PythonApp";

    // The manager to manage the connection with the Python host.
    private PythonHostConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        this.connectionManager = new PythonHostConnectionManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager.connectToPythonHost(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        this.connectionManager.handleActivityResult(this, requestCode, resultCode, result);
    }
}
