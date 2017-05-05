package service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tool.SharedPreferenceHelper;

import static tool.ServerIP.DELETE_ALL_BILL_URL;

/**
 * Created by Administrator on 2017/5/5.
 */

public class DeleteBillService extends Service
{

    private String USERNAME;
    private int billNameCount=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        USERNAME = SharedPreferenceHelper.getTableNameBySP(this);
        final Set<String> billNameSet = SharedPreferenceHelper.getDeleteBillNameFromSP(this, USERNAME);
        Log.d("service", "Service 获得了" + billNameSet.toString());

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    Log.d("service", "Service 开始连接服务器");
                    if (connectServerToDelete(billNameSet))//如果没删除成功，则过五秒再试
                    {
                        Log.d("service", "删除成功");
                        break;
                    } else
                    {
                        try
                        {
                            Log.d("service", "删除未完全成功,等待5秒再试");
                            Thread.sleep(5000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();
        Log.d("service", "Service 退出");
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean connectServerToDelete(Set<String> billNameSet)
    {
        final int[] isSuccess = {-1};
        final int[] sum = {billNameSet.size()};
        final int[] count={0};
        billNameCount = sum[0];
        if (billNameSet.size() == 0)
        {
            return true;
        }
        if (USERNAME == null)
        {
            return false;
        }
        for (final String billName : billNameSet)
        {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(2, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(2, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(2, TimeUnit.SECONDS)//设置连接超时时间
                    .build();

            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("username", USERNAME);//USERNAME就是username
            formBuilder.add("billname", billName);
            final Request request = new Request.Builder().url(DELETE_ALL_BILL_URL).post(formBuilder.build()).build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    count[0]++;
                    Log.d("service", "*******删除" + billName + "失败");
                    Log.d("service", "*******count is " + count[0] + "  sum is " + sum[0]);
                    if (count[0] == sum[0])//不成功直接退出
                    {
                        isSuccess[0] =0;
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    count[0]++;
                    billNameCount--;
                    String responseString = response.body().string();
                    Log.d("service", responseString);
                    if (responseString.equals("success"))//如果成功就
                    {
                        if (SharedPreferenceHelper.delSingleDeletedBillNameFromSP(DeleteBillService.this, USERNAME, billName))
                        {
                            Log.d("service", "删除" + billName + "成功");
                        }else
                        {
                            Log.d("service", "删除" + billName + "失败");
                        }
                    }else
                    {
                        isSuccess[0]=0;
                        return;
                    }
                    Log.d("service", "count is " + count[0] + "  sum is " + sum[0]);
                    if (count[0] == sum[0])
                    {
                        isSuccess[0] =1;
                    }
                }
            });
        }
        while (isSuccess[0] == -1)
        {

        }
        if (isSuccess[0] == 1)
        {
            return true;
        }else
        {
            return false;
        }
    }
}
