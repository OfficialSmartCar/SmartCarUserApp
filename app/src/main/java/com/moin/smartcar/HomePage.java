package com.moin.smartcar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.moin.smartcar.AMC.AMCListing;
import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.DentPaint.DentingAndPainting;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.UserBookings;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.OwnServices.MyOwnService;
import com.moin.smartcar.RegService.RegularServiceListing;
import com.moin.smartcar.ReportBreakdown.BreakdownCategory;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Support.SupportHome;
import com.moin.smartcar.User.Profile;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HomePage extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    int first = 0;
    private ImageView backgroundImage;
    private Runnable task;
    private navUserBookings navFragment;
    private View rootLayout;

    private TextView userNameTextView;
    private TextView userCar;

    private SliderLayout mDemoSlider;


    @Override
    protected void onResume() {
        super.onResume();
        navFragment.refreshHeader();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar)findViewById(R.id.homeappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        userNameTextView = (TextView) findViewById(R.id.textView);
        userNameTextView.setText(DataSingelton.getMy_SingeltonData_Reference().userName);

        userCar = (TextView) findViewById(R.id.userFirstCarName);

        userCar.setText(DataSingelton.getMy_SingeltonData_Reference().userCarList.get(0).carName);
//        rootLayout = findViewById(R.id.root_layout);
//        rootLayout.setVisibility(View.INVISIBLE);

        navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, "HOME");


        backgroundImage = (ImageView)findViewById(R.id.backgroundImage);

//        startAnimation();
        animateImage();

        mDemoSlider = (SliderLayout) findViewById(R.id.carouselSlider);
        loadSlider();

        getTheScreenHeight();
    }

    private void getTheScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        DataSingelton.getMy_SingeltonData_Reference().screenHeight = size.y;
    }

    private void loadSlider() {
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("25% Off", R.drawable.banner1);
        file_maps.put("25% + 10% Off", R.drawable.banner2);
        file_maps.put("30% Off", R.drawable.banner3);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.setPresetTransformer("ZoomOut");

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

    public void navigateToAMCListingPage(View view) {
        Intent myIntent = new Intent(HomePage.this, AMCListing.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    public void navigateToNotifications(View view) {
//        Intent myIntent = new Intent(HomePage.this,BookingSuccess.class);
//        startActivity(myIntent);
//        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            new AlertDialog.Builder(HomePage.this)
                    .setTitle("")
                    .setMessage("Are you sure you want to logout ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            finish();
//                            startActivity(new Intent(HomePage.this,MainActivity.class));
//                            overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(HomePage.this, "Banner Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
