package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {

    // The tag for this App // TODO: Maybe use the app name instead?
    public static final String TAG = "PythonApp";

    private PythonHostConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        this.connectionManager = new PythonHostConnectionManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager.connect();
        // TODO: Maybe display a bootstrap image from the python App here if specified.
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.connectionManager.closeConnection();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        this.connectionManager.handleActivityResult(requestCode, resultCode, result);
    }
}
