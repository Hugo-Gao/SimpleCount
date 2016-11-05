package View;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.program.gyf.simplecount.R;

/**
 * Created by Administrator on 2016/9/21.
 */


public class SlidingMenu extends HorizontalScrollView
{

    private LinearLayout mWrapter;
    private ViewGroup mMenu;
    private ViewGroup mContent;

    private int mScreenWidth;
    private int mMenuRightPadding=60;//初始值
    private int mMenuWidth;
    private boolean once = true;//是不是第一次测量
    private boolean isOpenning = false;
    private Button toggleButton;
    private Button billButton;

    public SlidingMenu(Context context)
    {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);
    }

    public SlidingMenu(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);
        int n = array.getIndexCount();
        for(int i=0;i<n;i++)
        {
            int attr=array.getIndex(i);
            switch (attr)
            {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = array.getDimensionPixelSize(attr, (int) TypedValue.
                            applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics()));

            }
        }
        array.recycle();
        //获取屏幕大小
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        if(once)
        {
            mWrapter = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWrapter.getChildAt(0);
            mContent = (ViewGroup) mWrapter.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     *
     * @param changed 看布局是否改动
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed)
        {
            this.scrollTo(mMenuWidth, 0);
            once = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();//这个变量时隐藏在左边的菜单的宽度
                if(scrollX>=mMenuWidth/2)//说明只把菜单移动了一点点
                {
                    this.smoothScrollTo(mMenuWidth,0);//重新把菜单那隐藏
                    isOpenning = false;
                    rotationButton(isOpenning);
                }else//说明菜单已经移出来了很多了
                {

                    isOpenning = true;
                    this.smoothScrollTo(0,0);
                    rotationButton(isOpenning);
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    public void openMenu()//这个方法已被toggleMenu方法封装，不要直接使用
    {
        this.smoothScrollTo(0,0);

    }

    public void closeMenu()
    {
        this.smoothScrollTo(mMenuWidth, 0);//这个方法已被toggleMenu方法封装，不要直接使用

    }

    public void toggleMenu()
    {

        if(isOpenning)
        {

            closeMenu();
            isOpenning = false;
        }
        else
        {

            openMenu();
            isOpenning = true;
        }
    }

    private void rotationButton(boolean isOpenning)//封装处理按钮的旋转事件
    {
        toggleButton = (Button) findViewById(R.id.toogleButton);
        if(isOpenning)
        {
            ObjectAnimator animator=ObjectAnimator.ofFloat(toggleButton,"rotation",90F);
            animator.setDuration(500);
            animator.start();
        }
        else
        {

            ObjectAnimator animator=ObjectAnimator.ofFloat(toggleButton,"rotation",0F);
            animator.setDuration(500);
            animator.start();
        }
    }

    @Override//制作滑动时的动画效果
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        //让按钮隐藏在内容底下的动画效果
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMenu, "translationX", oldl, l);
        animator.setDuration(0);
        animator.start();
    }
}
