package me.fernandodominguez.zenmap.helpers;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by fernando on 28/12/15.
 */
public class FileHelper {

    public static int chmod(File path, int mode) throws Exception {
        Class fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions =
                fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
        return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
    }

    static public void makedir (String dir) {
        File myDir = new File(dir);

        if(!myDir.isDirectory()) {
            myDir.mkdirs();
        }
    }

    static public void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }

}
