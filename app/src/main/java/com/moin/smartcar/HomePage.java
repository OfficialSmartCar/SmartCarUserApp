package com.moin.smartcar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.moin.smartcar.AMC.AMCListing;
import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.DentPaint.DentingAndPainting;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.DataStr.UpCommingStr;
import com.moin.smartcar.MyBookings.UserBookings;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.Notification.NotificationHome;
import com.moin.smartcar.Notification.SetUp.NewMessageStr;
import com.moin.smartcar.OwnServices.MyOwnService;
import com.moin.smartcar.RegService.RegularServiceListing;
import com.moin.smartcar.ReportBreakdown.BreakdownCategory;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Support.SupportHome;
import com.moin.smartcar.User.CarInfoStr;
import com.moin.smartcar.User.Profile;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class HomePage extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    int first = 0;
    //    private TextView notificationCountTextView;
    @Bind(R.id.notificationCountTextView123)
    TextView notificationCountTextView123;
    private ImageView backgroundImage;
    private Runnable task;
    private navUserBookings navFragment;
    private View rootLayout;
    private TextView userNameTextView;
    private TextView userCar;
    private SliderLayout mDemoSlider;

    @Bind(R.id.textView1)TextView textView1;
    @Bind(R.id.textView2)TextView textView2;
    @Bind(R.id.textView3)TextView textView3;
    @Bind(R.id.textView4)TextView textView4;
    @Bind(R.id.textView5)TextView textView5;
    @Bind(R.id.textView6)TextView textView6;
    @Bind(R.id.textView7)TextView textView7;
    @Bind(R.id.textView8)TextView textView8;
    @Bind(R.id.textView9)TextView textView9;

    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();


    @Override
    protected void onResume() {
        super.onResume();
        DataSingelton.getMy_SingeltonData_Reference().signUpOrAdd = "Add";
        navFragment.refreshHeader();

        notificationCountTextView123.setText(DataSingelton.getMy_SingeltonData_Reference().notificationCount + "");
        if (DataSingelton.getMy_SingeltonData_Reference().notificationCount == 0) {
            notificationCountTextView123.setAlpha(0.0f);
        } else {
            notificationCountTextView123.setAlpha(1.0f);
        }

        try{
            if (mySingelton.imagesSelected != null){
                if (mySingelton.imagesSelected.size()>0){
                    for (int i=0;i<mySingelton.imagesSelected.size();i++){
                        mySingelton.imagesSelected.get(i).recycle();
                    }
                }
            }
        }catch (Exception e){
            Log.d("Some Exception", "images not recycled");
        }

        if (mySingelton.firstLogin_moin == 0){
            mySingelton.firstLogin_moin = 1;
            getUserDetails();
        }

        mySingelton.moinTextView = notificationCountTextView123;

    }

    private void getUserDetails(){
        showLoadingView();
        JSONObject params = new JSONObject();
        try {
            params.put("UserId", mySingelton.userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.getUserDetails, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
//                                hideLoadingWithMessage("User Details Updated");
                                parseResponse(response);
                            } else {
//                                goBackToBackedUpData();
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            goBackToBackedUpData();
                            hideLoadingWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        goBackToBackedUpData();
                        hideLoadingWithMessage("You Are Offline");
                    }
                }
        );
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        updateRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(updateRequest);
    }

    private void parseResponse(JSONObject response) throws JSONException {
        mySingelton.userName = response.getString("UserName");
        mySingelton.address = response.getString("Address");
        mySingelton.mobileNumber = response.getString("MobileNumber");
        mySingelton.userEmailId = response.getString("EmailId");
        mySingelton.userId = response.getString("UserId");

        mySingelton.backUpMobileNUmber = mySingelton.mobileNumber;
        mySingelton.backUpAdress = mySingelton.address;
        mySingelton.backUpName = mySingelton.userName;

        mySingelton.userCarList = new ArrayList<>();
        JSONArray arr = response.getJSONArray("carList");
        for (int i=0;i<arr.length();i++){
            JSONObject obj = arr.getJSONObject(i);
            CarInfoStr myStr = new CarInfoStr();
            myStr.carName = obj.getString("CarName");
            myStr.carId = obj.getString("CarId");
            myStr.carBrand = obj.getString("CarBrand");
            myStr.carModel = obj.getString("CarModel");
            myStr.yearOfMaufacture = obj.getString("YearOfManufacture");
            myStr.carRegNo = obj.getString("CarRegNumber");
            myStr.carVariant = obj.getString("Variant");
            myStr.isPremium = Integer.parseInt(obj.getString("isPremium"));
            myStr.status = 0;
            mySingelton.userCarList.add(myStr);
        }
        int count = Integer.parseInt(response.getString("notificationCount"));
        mySingelton.notificationCount = response.getInt("notificationCount");
        notificationCountTextView123.setText(DataSingelton.getMy_SingeltonData_Reference().notificationCount + "");
        if (DataSingelton.getMy_SingeltonData_Reference().notificationCount == 0) {
            notificationCountTextView123.setAlpha(0.0f);
        } else {
            notificationCountTextView123.setAlpha(1.0f);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ButterKnife.bind(this);
//        DataSingelton.getMy_SingeltonData_Reference().notificationCount = 0;

        Toolbar toolbar = (Toolbar) findViewById(R.id.homeappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();
        //
        mySingelton.successWebView1 = null;
        mySingelton.successWebView1 = new WebView(this);
        mySingelton.successWebView1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mySingelton.successWebView1.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mySingelton.successWebView1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mySingelton.successWebView1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= 11) {
            mySingelton.successWebView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mySingelton.successWebView1.getSettings().setJavaScriptEnabled(true);
        mySingelton.successWebView1.loadUrl("file:///android_asset/myhtml/index.html");

        userNameTextView = (TextView) findViewById(R.id.textView);
        userNameTextView.setText(DataSingelton.getMy_SingeltonData_Reference().userName);

        userCar = (TextView) findViewById(R.id.userFirstCarName);

        userCar.setText(DataSingelton.getMy_SingeltonData_Reference().userCarList.get(0).carName);
//        rootLayout = findViewById(R.id.root_layout);
//        rootLayout.setVisibility(View.INVISIBLE);

        navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, "HOME");

        backgroundImage = (ImageView) findViewById(R.id.backgroundImage);

//        startAnimation();
//        animateImage();

        mDemoSlider = (SliderLayout) findViewById(R.id.carouselSlider);
        loadSlider();

        getTheScreenHeight();

        setFonts();
        startingUp();
    }

    private void setFonts(){
        textView1.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView2.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView3.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView4.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView5.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView6.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView7.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView8.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView9.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);

    }

    private void getTheScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        DataSingelton.getMy_SingeltonData_Reference().screenHeight = size.y;
    }

    private void loadSlider() {
//        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
//        file_maps.put("25% Off", R.drawable.banner1);
//        file_maps.put("25% + 10% Off", R.drawable.banner2);
//        file_maps.put("30% Off", R.drawable.banner3);

        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("We Operate Only In Mumbai", "http://184.95.55.236:8080/SmartCar/resources/mytheme/images/banner1.jpg");
        url_maps.put("We Operate Only In Mumbai", "http://184.95.55.236:8080/SmartCar/resources/mytheme/images/banner2.jpg");
        url_maps.put("We Operate Only In Mumbai", "http://184.95.55.236:8080/SmartCar/resources/mytheme/images/banner3.jpg");

//        for (String name : url_maps.keySet()) {
//            TextSliderView textSliderView = new TextSliderView(this);
//            // initialize a SliderLayout
//            textSliderView
//                    .description(name)
//                    .image(url_maps.get(name))
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
//                    .setOnSliderClickListener(this);
//
//            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra", name);
//
//            mDemoSlider.addSlider(textSliderView);
//        }

        ArrayList<String> urls1 = new ArrayList<>();
        urls1.add("http://184.95.55.236:8080/SmartCar/resources/mytheme/images/banner1.jpg");
        urls1.add("http://184.95.55.236:8080/SmartCar/resources/mytheme/images/banner2.jpg");
        urls1.add("http://184.95.55.236:8080/SmartCar/resources/mytheme/images/banner3.jpg");

        for (int i=0;i<urls1.size();i++){

            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description("We Operate Only In Mumbai")
                    .image(urls1.get(i))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra","We Operate Only In Mumbai");

            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.setPresetTransformer("ZoomOut");
    }

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
//        mySingelton.
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    private void startAnimation() {
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


    private void animateViews() {

    }

    private void circularRevealActivity() {
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

    private void animateImage() {

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
                backgroundImage.setAlpha(1.0f);
//                Animation animFadeIn;
//                animFadeIn = AnimationUtils.loadAnimation(HomePage.this, R.anim.fade_in);
//                animFadeIn.setDuration(500);
//                backgroundImage.startAnimation(animFadeIn);
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

    private void navigation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToProfilePage(View view) {
        Intent intent = new Intent(HomePage.this, Profile.class);
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
        Intent myIntent = new Intent(HomePage.this, DentingAndPainting.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

    }

    public void navigateToAMCListingPage(View view) {
        Intent myIntent = new Intent(HomePage.this, AMCListing.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    public void navigateToNotifications(View view) {
        Intent myIntent = new Intent(HomePage.this, NotificationHome.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
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
//                            finish();
                            startActivity(new Intent(HomePage.this, LoginNew.class));
                            overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
                            killSelf();
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
//        Toast.makeText(HomePage.this, "Banner Selected", Toast.LENGTH_SHORT).show();
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

    private void killSelf() {
        Runnable task23 = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        worker.schedule(task23, 2000, TimeUnit.MILLISECONDS);
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingWithMessage(String msg) {
        hideLoadingView();
//        MoinUtils.getReference().showMessage(HomePage.this, msg);
    }

    private void showMessage(String msg) {
//        MoinUtils.getReference().showMessage(HomePage.this, msg);
    }


    private void startingUp() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    //do your code here
                    //also call the same runnable

                    notificationCountTextView123.setText(DataSingelton.getMy_SingeltonData_Reference().notificationCount + "");
                    if (DataSingelton.getMy_SingeltonData_Reference().notificationCount == 0) {
                        notificationCountTextView123.setAlpha(0.0f);
                    } else {
                        notificationCountTextView123.setAlpha(1.0f);
                    }

                    handler.postDelayed(this, 2000);
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
            }
        };
        handler.postDelayed(runnable, 1000);

    }
}
