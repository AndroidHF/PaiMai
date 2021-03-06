package com.buycolle.aicang.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.buycolle.aicang.R;

import java.util.ArrayList;


public class PromotedActionsLibrary {

    Context context;

    FrameLayout frameLayout;

    ImageView mainImageButton;

    RotateAnimation rotateOpenAnimation;

    RotateAnimation rotateCloseAnimation;

    ArrayList<ImageView> promotedActions;

    ObjectAnimator objectAnimator[];

    private int px;

    private static final int ANIMATION_TIME = 200;

    private boolean isMenuOpened;

    public void setup(Context activityContext, FrameLayout layout) {
        context = activityContext;
        promotedActions = new ArrayList<ImageView>();
        frameLayout = layout;
        px = (int) context.getResources().getDimension(R.dimen.dim56dp) + 10;
        openRotation();
        closeRotation();
    }


    public ImageView addMainItem(Drawable drawable) {

        ImageView button = (ImageView) LayoutInflater.from(context).inflate(R.layout.main_promoted_action_button, frameLayout, false);

        button.setImageDrawable(drawable);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isable){
                    isable = false;
                    if (isMenuOpened) {
                        closePromotedActions().start();
                        isMenuOpened = false;
                    } else {
                        isMenuOpened = true;
                        openPromotedActions().start();
                    }
                }
            }
        });

        frameLayout.addView(button);

        mainImageButton = button;

        return button;
    }


    public void addItem(Drawable drawable, View.OnClickListener onClickListener) {

        ImageView button = (ImageView) LayoutInflater.from(context).inflate(R.layout.promoted_action_button, frameLayout, false);

        button.setImageDrawable(drawable);

        button.setOnClickListener(onClickListener);

        promotedActions.add(button);

        frameLayout.addView(button);

        return;
    }

    public boolean isable = true;

    /**
     * Set close animation for promoted actions
     */
    public AnimatorSet closePromotedActions() {

        if (objectAnimator == null) {
            objectAnimatorSetup();
        }

        AnimatorSet animation = new AnimatorSet();

        for (int i = 0; i < promotedActions.size(); i++) {

            objectAnimator[i] = setCloseAnimation(promotedActions.get(i), i);
        }

        if (objectAnimator.length == 0) {
            objectAnimator = null;
        }

        animation.playTogether(objectAnimator);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mainImageButton.startAnimation(rotateCloseAnimation);
                mainImageButton.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainImageButton.setClickable(true);
                hidePromotedActions();
                isable = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mainImageButton.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return animation;
    }

    public AnimatorSet openPromotedActions() {

        if (objectAnimator == null) {
            objectAnimatorSetup();
        }


        AnimatorSet animation = new AnimatorSet();

        for (int i = 0; i < promotedActions.size(); i++) {

            objectAnimator[i] = setOpenAnimation(promotedActions.get(i), i);
        }

        if (objectAnimator.length == 0) {
            objectAnimator = null;
        }

        animation.playTogether(objectAnimator);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mainImageButton.startAnimation(rotateOpenAnimation);
                mainImageButton.setClickable(false);
                showPromotedActions();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainImageButton.setClickable(true);
                isable =true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mainImageButton.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });


        return animation;
    }

    private void objectAnimatorSetup() {

        objectAnimator = new ObjectAnimator[promotedActions.size()];
    }


    /**
     * Set close animation for single button
     *
     * @param promotedAction
     * @param position
     * @return objectAnimator
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private ObjectAnimator setCloseAnimation(ImageView promotedAction, int position) {
        ObjectAnimator objectAnimator;
        objectAnimator = ObjectAnimator.ofFloat(promotedAction, View.TRANSLATION_X, -px * (promotedActions.size() - position), 0f);
        objectAnimator.setRepeatCount(0);
        objectAnimator.setDuration(ANIMATION_TIME * (promotedActions.size() - position));
        return objectAnimator;
    }

    /**
     * Set open animation for single button
     *
     * @param promotedAction
     * @param position
     * @return objectAnimator
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private ObjectAnimator setOpenAnimation(ImageView promotedAction, int position) {
        ObjectAnimator objectAnimator;
        objectAnimator = ObjectAnimator.ofFloat(promotedAction, View.TRANSLATION_X, 0f, -px * (promotedActions.size() - position));
        objectAnimator.setRepeatCount(0);
        objectAnimator.setDuration(ANIMATION_TIME * (promotedActions.size() - position));
        return objectAnimator;
    }

    private void hidePromotedActions() {

        for (int i = 0; i < promotedActions.size(); i++) {
            promotedActions.get(i).setVisibility(View.GONE);
        }
    }

    private void showPromotedActions() {

        for (int i = 0; i < promotedActions.size(); i++) {
            promotedActions.get(i).setVisibility(View.VISIBLE);
        }
    }

    private void openRotation() {

        rotateOpenAnimation = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateOpenAnimation.setFillAfter(true);
        rotateOpenAnimation.setFillEnabled(true);
        rotateOpenAnimation.setDuration(ANIMATION_TIME);
    }

    private void closeRotation() {

        rotateCloseAnimation = new RotateAnimation(45, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateCloseAnimation.setFillAfter(true);
        rotateCloseAnimation.setFillEnabled(true);
        rotateCloseAnimation.setDuration(ANIMATION_TIME);
    }
}
