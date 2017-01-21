package tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/10/9.
 */

public class BitmapHandler
{
    public static String ConvertBitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    public static Bitmap convertStringToBitMap(String st)
    {
        // OutputStream out;
        Bitmap bitmap = null;
        try
        {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);

            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    public static byte[] convertBitmapToByte(Bitmap bmp)
    {
        if(bmp==null)
        {
            Log.d("haha", "bmp is null");

        }
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 20, os);
        return os.toByteArray();
    }

    public static Bitmap convertByteToBitmap(byte[] data)
    {
        if(data!=null)
        {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
            opts.inTempStorage = new byte[5 * 1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            return bitmap;
        }else
        {
            return null;
        }
    }
    public static Bitmap bitmpCulate(byte[] data){
        BitmapFactory.Options opts = new BitmapFactory.Options();
//      opts.inJustDecodeBounds = true;
//      BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        opts.inSampleSize=2;//图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
        opts.inTempStorage = new byte[5*1024]; //设置16MB的临时存储空间（不过作用还没看出来，待验证）
        opts.inSampleSize = computeSampleSize(opts, -1, 128*128);
        opts.inJustDecodeBounds = false;
        return  BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    }
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
