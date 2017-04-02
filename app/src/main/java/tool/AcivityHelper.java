package tool;

import android.app.Activity;
import android.util.Log;

/**
 * Created by Administrator on 2017/1/18.
 */

public class AcivityHelper
{
    public static void finishThisActivity(final Activity activity)
    {
        Log.d("haha", "调用了finishThisActivity()方法");
        new Thread(new Runnable()//在后台线程中关闭此活动
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(500);
                    activity.finish();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
