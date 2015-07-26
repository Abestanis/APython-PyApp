package com.apython.python.apython_pyapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class MainActivity extends Activity {

    // The tag for this App // TODO: Maybe use the app name instead?
    public static final String TAG = "PythonApp";

    // The manager to manage the connection with the Python host.
    private PythonHostConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File codeDir = getCodeDir(this.getApplicationContext());
        if (codeDir.exists()) { // TODO: Make a versioning system
            for (String child : codeDir.list()) {
                new File(codeDir, child).delete();
            }
            codeDir.delete();
        }
        if (!codeDir.isDirectory()) {
            if (!(codeDir.mkdirs() || codeDir.isDirectory())) {
                Log.e(MainActivity.TAG, "Failed to create the 'code' directory!");
                // TODO: Handle this.
                finish();
                return;
            }
            this.extractPythonCode(codeDir);
        }
        this.connectionManager = new PythonHostConnectionManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager.connectToPythonHost(this);
    }

    public static File getCodeDir(Context context) {
        return new File(context.getFilesDir(), "code");
    }

    protected boolean extractPythonCode(File destination) {
        Log.d(TAG, "Extracting code to " + destination); // TODO: Maybe also leave it in the zip format.
        ZipInputStream archive;
        try {
            String filename;
            archive = new ZipInputStream(new BufferedInputStream(getResources().openRawResource(R.raw.code)));
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            int count;

            while ((zipEntry = archive.getNextEntry()) != null) {
                filename = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // Create the directory if not exists
                    File directory = new File(destination, filename);
                    if (!(directory.mkdirs() || directory.isDirectory())) {
                        Log.e(TAG, "Could not save directory '" + filename + "' to path '"
                                + directory.getAbsolutePath() + "' while trying to install the Python modules!");
                        archive.close();
                        return false;
                    }
                } else {
                    // Save the file
                    File file = new File(destination, filename);
                    FileOutputStream outputFile = new FileOutputStream(file);
                    while ((count = archive.read(buffer)) != -1) {
                        outputFile.write(buffer, 0, count);
                    }
                    outputFile.close();
                }
                archive.closeEntry();
            }
            archive.close();
            return true;
        }
        catch(IOException e) {
            Log.e(TAG, "Failed to extract the Python modules!");
            e.printStackTrace();
            return false;
        }
    }
}
