package tool;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2016/10/2.
 */

public  class ItemAnimition
{
    private static Action action;
    public static void rotationAndGone(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0F, 405F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 1.5F, 0F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleY", 1.5F, 0F);
        animator.setDuration(500);
        animator2.setDuration(500);
        animator3.setDuration(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animator2, animator3);
        animatorSet.start();
    }

    public static void rotationAndAppear(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 405F, 0F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 0F, 1F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleY", 0F, 1F);
        animator.setDuration(500);
        animator.start();
        animator2.setDuration(500);
        animator2.start();
        animator3.setDuration(500);
        animator3.start();
    }
    public static void confirmAndBigger(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 1F, 1.5F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1F, 1.5F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleX", 1.5F, 1F);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "scaleY", 1.5F, 1F);
        animator.setDuration(300);
        animator.start();
        animator2.setDuration(300);
        animator2.start();
        animator3.setDuration(300);
        animator3.start();
        animator4.setDuration(300);
        animator4.start();

    }
    public static void confirmAndBigger(Context context, View view)
    {
        action.act();
    }

    public interface Action
    {
        public abstract void act();
    }

    public static void setAction(Action maction)
    {
        action = maction;
    }
}
