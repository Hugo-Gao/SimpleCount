package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/4/5.
 */

public class RemoveBroadCastReceiver extends BroadcastReceiver
{
    private final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/ASimpleCount/";
    @Override
    public void onReceive(Context context, Intent intent)
    {

       /* if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) )
        {
            File file = new File(PHOTO_PATH);
            if(file.exists())
            {
                Log.d("haha", "开始删除文件夹");
                delete(file);
            }
        }*/
    }
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }
}
