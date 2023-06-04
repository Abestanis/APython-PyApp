package com.apython.python.apython_pyapp; /* REPLACE(9,41): appId */

import android.app.Activity;
import android.os.Bundle;

/**
 * This is a temporary solution to enforce the Python host permission on the Python host.
 */
public class PyHostVerifyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setPyHostResultIntent(getIntent());
        setResult(Activity.RESULT_OK);
        finish();
    }
}
