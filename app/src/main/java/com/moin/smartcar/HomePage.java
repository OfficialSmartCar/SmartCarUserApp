package com.moin.smartcar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.DentPaint.DentPaintHome;
import com.moin.smartcar.DentPaint.DentingAndPainting;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.UserBookings;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.OwnServices.MyOwnService;
import com.moin.smartcar.RegService.RegularService;
import com.moin.smartcar.RegService.RegularServiceListing;
import com.moin.smartcar.ReportBreakdown.BreakdownCategory;
import com.moin.smartcar.ReportBreakdown.BreakdownHome;
import com.moin.smartcar.Support.SupportHome;
import com.moin.smartcar.User.Profile;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class HomePage extends AppCompatActivity {

    private ImageView backgroundImage;

    private Runnable task;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    int first = 0;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar)findViewById(R.id.homeappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

//        rootLayout = findViewById(R.id.root_layout);
//        rootLayout.setVisibility(View.INVISIBLE);

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, " ");


        backgroundImage = (ImageView)findViewById(R.id.backgroundImage);

//        startAnimation();
        animateImage();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseManager db = new DatabaseManager(HomePage.this);
        db.deleteUserInfo();
        db.deleteAllCars();
        startActivity(new Intent(HomePage.this, LoginNew.class));
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);

//        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    private void startAnimation(){
//        if (first == 0) {
////            view.setVisibility(View.INVISIBLE);
//
//            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
//            if (viewTreeObserver.isAlive()) {
//                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        if (first == 0) {
//                            first = 1;
//
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    circularRevealActivity();
//                                }
//                            }).run();
//
////                            task = new Runnable() {
////                                @Override
////                                public void run() {
////                                    animateViews();
////                                }
////                            };
////                            worker.schedule(task,1000, TimeUnit.MILLISECONDS);
//                        }
//                    }
//                });
//            }
//        }
    }


    private void animateViews(){

    }

    private void circularRevealActivity(){
//        int cx = rootLayout.getLeft();
//        int cy = rootLayout.getTop();
//
//        int finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight()) + 300;
//
//        final SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setDuration(1000);
//        rootLayout.setVisibility(View.VISIBLE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                animator.start();
////                animateImage();
//                animator.addListener(new SupportAnimator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart() {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd() {
//                        animateImage();
//                    }
//
//                    @Override
//                    public void onAnimationCancel() {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat() {
//
//                    }
//                });
//            }
//        }).run();
    }

    private void animateImage(){

        Animation animFade;

        animFade = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        animFade.setDuration(100);
        backgroundImage.startAnimation(animFade);
        animFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                backgroundImage.setImageResource(R.drawable.backcover);
                Animation animFadeIn;
                animFadeIn = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_in);
                animFadeIn.setDuration(500);
                backgroundImage.startAnimation(animFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void navigateToRegularService(View view) {
        startActivity(new Intent(HomePage.this, RegularServiceListing.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToUserBookings(View view) {
        navigation(new Intent(HomePage.this, UserBookings.class));
    }

    private void navigation(Intent intent){
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToProfilePage(View view) {
        Intent intent = new Intent(HomePage.this,Profile.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    public void navigateToMyOwnServices(View view) {
        startActivity(new Intent(HomePage.this, MyOwnService.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToSupportPage(View view) {
        startActivity(new Intent(HomePage.this, SupportHome.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToReportBreakdown(View view) {
        startActivity(new Intent(HomePage.this, BreakdownCategory.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToDentPaintPage(View view) {
        Intent myIntent = new Intent(HomePage.this,DentingAndPainting.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }
}
