package com.np.brickbreaker.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StoreUtils {
    public static void copyOrReplaceBgStorage(File file,InputStream is) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        fos.write(buffer);
        is.close();
        fos.close();
        Log.e("info", "File copied to internal storage!");
    }
}
