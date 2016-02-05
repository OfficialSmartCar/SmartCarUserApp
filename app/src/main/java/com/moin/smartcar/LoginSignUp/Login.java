package com.moin.smartcar.LoginSignUp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.Utility.MoinUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class Login extends AppCompatActivity {

    private int backPressed = 0;
    private Runnable task;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private EditText loginUserName,loginPassword;
    private EditText signUpusername,signUpPassword,signUpConfrmPassword;

    private View rootLayout,mainLoginContainer;
    private int first = 0;
    private int signUpDisplayed = 0;

    private View backView,loginBackground;

    private View signUpView;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        rootLayout = findViewById(R.id.root_layout);
        rootLayout.setVisibility(View.INVISIBLE);
        mainLoginContainer = findViewById(R.id.mainLoginContainer);

        loginUserName = (EditText)findViewById(R.id.usernameLoginEditText);
        loginPassword = (EditText)findViewById(R.id.passwordEditText);

        signUpusername = (EditText)findViewById(R.id.usernameSignUpEditText);
        signUpPassword = (EditText)findViewById(R.id.passwordSignUpEditText);
        signUpConfrmPassword = (EditText)findViewById(R.id.confirmPasswordSignUpEditText);

        backView = findViewById(R.id.view);
        loginBackground = findViewById(R.id.loginBackground);

        signUpView = findViewById(R.id.internalSignUpToReveal);
        signUpView.setVisibility(View.INVISIBLE);


        fab = (FloatingActionButton) findViewById(R.id.fabLogin);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonToCenter();
            }
        });

        signUpusername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    signUpPassword.requestFocus();
                    signUpPassword.append("");
                    return true;
                }
                return false;
            }
        });
    }

    private void animateButtonToCenter() {
        int deltaX = (rootLayout.getRight()) / 2;
        int deltaY = (rootLayout.getBottom()) / 2;

        deltaX = (fab.getLeft() + fab.getRight())/2 - deltaX;
        deltaY = (mainLoginContainer.getTop() + mainLoginContainer.getBottom())/2 - deltaY + 50;

        final ObjectAnimator translateX = ObjectAnimator.ofFloat(fab, "translationX", -deltaX);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(fab, "translationY", -deltaY);

        translateX.setDuration(500);
        translateY.setDuration(500);

//        translateX.

        translateX.start();
        translateY.start();


        final int finalDeltaX = deltaX;
        translateX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                ObjectAnimator translateX1 = ObjectAnimator.ofFloat(fab, "translationX", 0);
                ObjectAnimator translateY1 = ObjectAnimator.ofFloat(fab, "translationY", 0);
                translateX1.setDuration(500);
                translateY1.setDuration(500);

                translateX1.setStartDelay(500);
                translateY1.setStartDelay(500);

                translateX1.start();
                translateY1.start();

                if (signUpDisplayed == 0) {
                    fab.setImageResource(R.drawable.fab_minus);
                    moveUp();
                    showSignUp();
                    signUpDisplayed = 1;
                } else {
                    fab.setImageResource(R.drawable.fab_plus);
                    hideSignUp();
                    signUpDisplayed = 0;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showSignUp() {

//        moveUp();

        loginUserName.setEnabled(false);
        loginPassword.setEnabled(false);

        signUpusername.setEnabled(true);
        signUpPassword.setEnabled(true);
        signUpConfrmPassword.setEnabled(true);
        signUpusername.requestFocus();
        signUpusername.append("");

        if (signUpView.getVisibility() == View.INVISIBLE)
        {

            int cx = (rootLayout.getLeft() + rootLayout.getRight())/2;
            int cy = (rootLayout.getTop() + rootLayout.getBottom())/2;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cx-=35;
                cy-=80;
            }


            int finalRadius = Math.max(signUpView.getWidth(), signUpView.getHeight());

            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(signUpView, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(1000);
            signUpView.setVisibility(View.VISIBLE);
            animator.start();

        }
    }

    private void hideSignUp() {

        moveDown();

        loginUserName.setEnabled(true);
        loginPassword.setEnabled(true);

        loginUserName.requestFocus();
        loginUserName.append("");

        signUpusername.setEnabled(false);
        signUpPassword.setEnabled(false);
        signUpConfrmPassword.setEnabled(false);

        if (signUpView.getVisibility() == View.VISIBLE)
        {
            int cx = (rootLayout.getLeft() + rootLayout.getRight())/2;
            int cy = (rootLayout.getTop() + rootLayout.getBottom())/2;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cx-=35;
                cy-=80;
            }
//            cx-=35;
//            cy-=80;

            int finalRadius = signUpView.getWidth() *2 ;

            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(signUpView, cx, cy, finalRadius,0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(500);
            signUpView.setVisibility(View.VISIBLE);

            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    signUpView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator.start();

        }
    }

    private void circularRevealActivity() {

        int cx = (rootLayout.getLeft() + rootLayout.getRight()) / 2;
        int cy = (rootLayout.getTop() + rootLayout.getBottom()) / 2;

        int finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight()) + 100;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        rootLayout.setVisibility(View.VISIBLE);
        animator.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (first == 0) {
//            view.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (first == 0) {
                            first = 1;
                            circularRevealActivity();
                        }
                    }
                });
            }
        }
    }

    public void navigateToForgotPasswordPage(View view) {
        startActivity(new Intent(Login.this,ForgotPassword.class));
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    public void validateAndConfirmSignUp(View view) {

        if (signUpusername.getText().toString().length() == 0){
            MoinUtils.getReference().showMessage(Login.this,"Enter Email Id");
            signUpusername.requestFocus();
            signUpusername.append("");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(signUpusername.getText().toString()).matches()){
            MoinUtils.getReference().showMessage(Login.this, "Entered Email Id Is Invalid");
            return;
        }

        if (signUpPassword.getText().length() == 0){
            MoinUtils.getReference().showMessage(Login.this,"Enter Password");
            signUpPassword.requestFocus();
            signUpPassword.append("");
            return;
        }

        if (!signUpPassword.getText().toString().equalsIgnoreCase(signUpConfrmPassword.getText().toString())){
            MoinUtils.getReference().showMessage(Login.this, "Passwords Dont Match");
            signUpConfrmPassword.requestFocus();
            signUpConfrmPassword.append("");
            return;
        }

        MoinUtils.getReference().showMessage(Login.this,"Valid Data ");

    }

    public void validateEmailIdAndLogin(View view) {

        if (loginUserName.getText().toString().length() == 0){
            MoinUtils.getReference().showMessage(Login.this,"Enter Email Id");
            loginUserName.requestFocus();
            loginUserName.append("");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginUserName.getText().toString()).matches()){
            MoinUtils.getReference().showMessage(Login.this, "Entered Email Id Is Invalid");
            return;
        }

        if (loginPassword.getText().length() == 0){
            MoinUtils.getReference().showMessage(Login.this,"Enter Password");
            loginPassword.requestFocus();
            loginPassword.append("");
            return;
        }

        MoinUtils.getReference().showMessage(Login.this,"Valid Data ");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (backPressed == 0) {
                backPressed = 1;
                MoinUtils.getReference().showMessage(Login.this, "Press Back Again To Exit");
//                Toast.makeText(this,"Press Back Again To Exit",Toast.LENGTH_SHORT).show();
                task = new Runnable() {
                    public void run() {
                        backPressed = 0;
                    }
                };
                worker.schedule(task, 2, TimeUnit.SECONDS);
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    private void moveUp(){
        ObjectAnimator translateY = ObjectAnimator.ofFloat(backView, "translationY", -10);
        translateY.setDuration(500);
        translateY.start();

        ObjectAnimator translateY1 = ObjectAnimator.ofFloat(loginBackground, "translationY", -10);
        translateY1.setDuration(500);
        translateY1.start();
    }

    private void moveDown(){
        ObjectAnimator translateY = ObjectAnimator.ofFloat(backView, "translationY", 10);
        translateY.setDuration(500);
        translateY.setStartDelay(500);
        translateY.start();

        ObjectAnimator translateY1 = ObjectAnimator.ofFloat(loginBackground, "translationY", 10);
        translateY1.setDuration(500);
        translateY1.setStartDelay(500);
        translateY1.start();
    }
}
