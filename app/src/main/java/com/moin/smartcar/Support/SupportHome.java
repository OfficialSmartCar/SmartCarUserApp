package com.moin.smartcar.Support;

import android.content.Intent;
import android.graphics.Point;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.moin.smartcar.Custom.SheetLayout;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class SupportHome extends AppCompatActivity{


    @Bind(R.id.root_layout_outer) View outerView;
    SupportAnimator hideAnimator;
    @Bind(R.id.topClick)View topView;
    @Bind(R.id.BottomClick)View bottomView;
    int navigationDestination = 0;
    @Bind(R.id.outerImageView)ImageView imgView;
    private int REQUEST_CODE = 109;

    int check = 0;
    int closignAnimationStarted = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_home);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.support));

        navUserBookings navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), myToolbar, getResources().getString(R.string.support));

        if (savedInstanceState == null){
            outerView.setVisibility(View.INVISIBLE);
            startAnimationOuterExpand();
        }

        topView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topView.setEnabled(true);
                bottomView.setEnabled(true);
                navigationDestination = 0;
                topSelection();
            }
        });

        bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topView.setEnabled(true);
                bottomView.setEnabled(true);
                navigationDestination = 1;
                BottomSelection();
            }
        });
    }

    private void startAnimationOuterExpand() {

        ViewTreeObserver viewTreeObserver = outerView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (check == 0){
                                circularRevealOuterView();
                                check = 1;
                            }
                        }
                    }).run();
                }
            });
        }
    }

    private void circularRevealOuterView(){

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int cx = size.x/2;
        int cy = size.y/2 - 50;

        int finalRadius = Math.max(outerView.getWidth(), outerView.getHeight()) + 300;

        final SupportAnimator animator = ViewAnimationUtils.createCircularReveal(outerView, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2000);
        outerView.setVisibility(View.VISIBLE);
        animator.start();
        preloadClosingAnimation();

    }

    private void preloadClosingAnimation(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int cx = size.x/2;
        int cy = size.y/2 - 50;

        int finalRadius = outerView.getWidth() *2 ;

        hideAnimator = ViewAnimationUtils.createCircularReveal(outerView, cx, cy, finalRadius,0);
        hideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hideAnimator.setDuration(2000);

        hideAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
//                finish();
                outerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private void topSelection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                hideCircularReveal();
            }
        }).run();
    }

    private void BottomSelection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                hideCircularReveal();
            }
        }).run();
    }

    private void hideCircularReveal(){
        if (closignAnimationStarted == 0){
            closignAnimationStarted = 1;
            hideAnimator.start();
            hideAnimator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {

                    closignAnimationStarted = 0;
                    if (navigationDestination == 0) {
                        Intent intent = new Intent(SupportHome.this, ContactUs.class);
                        startActivityForResult(intent, REQUEST_CODE);
                        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
                    } else {
                        Intent intent = new Intent(SupportHome.this, FAQHome.class);
                        startActivityForResult(intent, REQUEST_CODE);
                        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
                    }
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(SupportHome.this, LoginNew.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            topView.setEnabled(true);
            bottomView.setEnabled(true);
            circularRevealOuterView();
        }
    }

}
