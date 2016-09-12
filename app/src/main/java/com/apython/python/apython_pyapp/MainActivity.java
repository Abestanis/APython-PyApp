package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends Activity {
    
    // Our handle to the interpreter host app
    private InterpreterHost interpreterHost;

    // The Python app thread
    @SuppressWarnings("FieldCanBeLocal")
    private Thread appThread;
    
    // A handle to the Ui provided by the Python host
    private Object uiHandle;

    // The request ID of the interpreter host activity
    public static final int INTERPRETER_REQUEST_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File codeDir = PyAppUtil.getCodeDir(this.getApplicationContext());
        // TODO: Make a versioning system
        PyAppUtil.deleteDirectory(codeDir);
        if (!codeDir.isDirectory()) {
            if (!(codeDir.mkdirs() || codeDir.isDirectory())) {
                Log.e(PyBuild.APP_LOG_TAG, "Failed to create the 'code' directory!");
                // TODO: Handle this.
                finish();
                return;
            }
            if (!PyAppUtil.extractPythonCode(this, codeDir)) {
                Log.e(PyBuild.APP_LOG_TAG, "Failed to extract our python code!");
                // TODO: Handle this.
                finish();
                return;
            }
        }
        this.interpreterHost = new InterpreterHost(this, PyBuild.APP_LOG_TAG);
    }

    @Override
    protected void onStart() {
        Log.w(PyBuild.APP_LOG_TAG, "<<< ON START >>>");
        super.onStart();
        if (!interpreterHost.connectToHost(INTERPRETER_REQUEST_ID, PyBuild.MIN_PY_VERSION,
                                           PyBuild.PYTHON_MODULE_REQUIREMENTS, PyHostVerifyActivity.class)) {
            // TODO: Handle this better!
            errorExit("Failed to start Python app: No Python interpreter found on system!");
        }
    }
    
    // TODO: Remove this hack
    static Intent pyHostResultIntent;
    
    static void setPyHostResultIntent(Intent resultIntent) {
        pyHostResultIntent = resultIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTERPRETER_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                if (pyHostResultIntent != null) {
                    startApp(pyHostResultIntent);
                    pyHostResultIntent = null;
                } else {
                    Log.e(PyBuild.APP_LOG_TAG, "The Python host did not call the PythonHostVerifyActivity!");
                    errorExit("The Python Host did not verify itself as trusted by the user!");
                }
            } else {
                String protocolVersion = "unknown";
                if (data != null && data.hasExtra("protocolVersion")) {
                    protocolVersion = String.valueOf(data.getIntExtra("protocolVersion", -1));
                }
                Log.e(PyBuild.APP_LOG_TAG, "Python Host error (protocol version " + protocolVersion + ")!");
                errorExit("The Python Host returned an error!");
            }
        }
    }

    private void startApp(final Intent data) {
        if (interpreterHost.loadInterpreterFromHostData(data)) {
            setContentView(R.layout.python_window);
            this.uiHandle = interpreterHost.setWindow(PyBuild.APP_WINDOW_TYPE.ordinal(),
                                                      (ViewGroup) findViewById(R.id.fragmentContainer));
            appThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = interpreterHost.startInterpreter(
                        new String[] {new File(PyAppUtil.getCodeDir(getApplicationContext()), "main.py").getAbsolutePath()}
                    );
                    Log.w(PyBuild.APP_LOG_TAG, "<<< FINISH (exit code " + result + ") >>>");
                    setResult(result);
                    finish();
                }
            });
            appThread.start();
        } else {
            errorExit("Failed to load the Python host interpreter!");
        }
    }

    private void errorExit(String message) {
        errorExit(message, null);
    }

    private void errorExit(final String message, Throwable e) {
        Log.e(PyBuild.APP_LOG_TAG, "Python App exiting: " + message, e);
        Intent result = new Intent();
        result.putExtra("error", e);
        result.putExtra("errorMsg", message);
        setResult(RESULT_CANCELED, result);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return uiHandle instanceof Window.Callback &&
                ((Window.Callback) uiHandle).dispatchKeyEvent(event) ||
                super.dispatchKeyEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (uiHandle instanceof Window.Callback)
            ((Window.Callback) uiHandle).onWindowFocusChanged(hasFocus);
    }
}
