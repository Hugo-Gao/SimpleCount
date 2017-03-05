package tool;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.program.gyf.simplecount.R;

/**
 * Created by Administrator on 2017/2/28.
 */

public class WonderfulDialog extends Dialog
{

    private Context context;
    private int resId;
    private int layoutId;
    private String title;
    public WonderfulDialog(Context context)
    {
        super(context);
        this.context = context;

    }

    public WonderfulDialog(Context context, int themeResId)
    {
        super(context, themeResId);
        this.context = context;
        resId = themeResId;
    }

    public WonderfulDialog(Context context, int  themeResId, int  layoutId)
    {
        super(context, themeResId);
        this.layoutId = layoutId;
        this.context = context;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, layoutId, null);
        TextView titleText = (TextView) view.findViewById(R.id.title);
        titleText.setText(title);
        setContentView(view);
        initSize();


    }



    private void initSize()
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = DensityUtils.dp2px(context, 300);
        lp.height = DensityUtils.dp2px(context, 250);
        window.setAttributes(lp);
    }

    public void setMyTitle(String title)
    {

        this.title = title;
        View view = View.inflate(context, layoutId, null);
        TextView titleText = (TextView) view.findViewById(R.id.title);
        titleText.setText(title);

    }
}
