package tool;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Administrator on 2016/10/2.
 */

public  class ItemAnimition
{
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
    public static void cardConfirmAndBigger(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 1F, 1.5F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1F, 1.5F);

        AnimatorSet animatorSet = new AnimatorSet();
        animator.setDuration(300);
        animator2.setDuration(300);

        animatorSet.play(animator).with(animator2);
        animatorSet.start();
    }
    public static void handMove(View view)
    {

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",300f);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }
    public static void translationToDisapper(View view)
    {
        float curY = view.getTranslationY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",120f);
        animator.setDuration(500);
        animator.start();
    }
    public static void translationToAppear(View view)
    {
        ObjectAnimator animator=ObjectAnimator.ofFloat(view,"translationY",0f);
        animator.setDuration(500);
        animator.start();
    }
    public static void toolBarDisappear(View view)
    {
        ObjectAnimator animator=ObjectAnimator.ofFloat(view,"translationY",-120f);
        animator.setDuration(500);
        animator.start();
    }
    public static void toolBarAppear(View view)
    {
        ObjectAnimator animator=ObjectAnimator.ofFloat(view,"translationY",0f);
        animator.setDuration(500);
        animator.start();
    }
}
