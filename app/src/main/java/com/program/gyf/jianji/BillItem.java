package com.program.gyf.jianji;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by Administrator on 2017/1/18.
 */

public class BillItem
{
    private String billName;
    private String picAddress;

    public String getBillName()
    {
        return billName;
    }

    public void setBillName(String billName)
    {
        this.billName = billName;
    }

    public String getBillPic()
    {

        return picAddress;
    }

    public Bitmap getBillBitmapPic(Context context)
    {
        try
        {
            if (picAddress==null)
            {
                return null;
            }
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(),Uri.parse(picAddress));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }



    public void setBillPic(String billPic)
    {
        this.picAddress = billPic;
    }
}
