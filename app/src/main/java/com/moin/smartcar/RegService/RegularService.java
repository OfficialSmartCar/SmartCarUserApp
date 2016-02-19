package com.moin.smartcar.RegService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.R;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class RegularService extends AppCompatActivity {

    int first = 0;
    int check = 0;
    private View rootLayout;
    private View car1View,car2View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_service);

        try{
            check = Integer.parseInt(getIntent().getStringExtra("check"));
        }catch (Exception e){
            check = 0;
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Regular Service");

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, "Regular Service");

        rootLayout = findViewById(R.id.root_layout);
        if (check == 1){
            rootLayout.setVisibility(View.VISIBLE);
        }else{
            rootLayout.setVisibility(View.INVISIBLE);
            startAnimation();
        }

        car1View = findViewById(R.id.car1Selection);
        car2View = findViewById(R.id.car2Selection);

        car1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegularService.this,RegularServiceListing.class);
                navigateToIntent(myIntent);
            }
        });

        car1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegularService.this,RegularServiceListing.class);
                navigateToIntent(myIntent);
            }
        });
    }

    private void navigateToIntent(Intent intent){
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    private void startAnimation(){
        if (first == 0) {
//            view.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (first == 0) {
                            first = 1;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    circularRevealActivity();
                                }
                            }).run();
                        }
                    }
                });
            }
        }
    }

    private void circularRevealActivity(){
        int cx = rootLayout.getLeft();
        int cy = rootLayout.getTop();

        int finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight()) + 300;

        final SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        rootLayout.setVisibility(View.VISIBLE);
        animator.start();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu,menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(RegularService.this, LoginNew.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideCircularReveal(){
        int cx = rootLayout.getLeft();
        int cy = rootLayout.getTop();

        int finalRadius = rootLayout.getWidth() *2 ;

        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, finalRadius,0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);

        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (check == 1){
                finish();
            }
            else {
                hideCircularReveal();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
