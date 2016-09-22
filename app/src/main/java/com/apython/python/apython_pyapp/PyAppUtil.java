package com.apython.python.apython_pyapp; /* REPLACE(9,41): appId */

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An utility class for the Python app.
 * 
 * Created by Sebastian on 20.08.2016.
 */
public class PyAppUtil {
    
    public static File getCodeDir(Context context) {
        return new File(context.getFilesDir(), "code");
    }

    public static boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                } else {
                    if (!file.delete()) {
                        Log.w(PyBuild.APP_LOG_TAG, "Failed to delete file '" + file.getAbsolutePath() + "'.");
                        return false;
                    }
                }
            }
        }
        if (directory.exists() && !directory.delete()) {
            Log.w(PyBuild.APP_LOG_TAG, "Failed to delete directory '" + directory.getAbsolutePath() + "'.");
            return false;
        }
        return true;
    }

    public static boolean extractPythonCode(Context context, File destination) {
        Log.d(PyBuild.APP_LOG_TAG, "Extracting code to " + destination); // TODO: Maybe also leave it in the zip format.
        ZipInputStream archive;
        try {
            String filename;
            archive = new ZipInputStream(new BufferedInputStream(context.getResources().openRawResource(R.raw.code)));
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            int count;

            while ((zipEntry = archive.getNextEntry()) != null) {
                filename = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // Create the directory if not exists
                    File directory = new File(destination, filename);
                    if (!(directory.mkdirs() || directory.isDirectory())) {
                        Log.e(PyBuild.APP_LOG_TAG, "Could not save directory '" + filename + "' to path '"
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
            Log.e(PyBuild.APP_LOG_TAG, "Failed to extract the Python modules!");
            e.printStackTrace();
            return false;
        }
    }
}
