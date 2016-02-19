package com.moin.smartcar.User;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewAnimator;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Custom.SheetLayout;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.Pager.CustomPager;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Tabs.SlidingTabLayout;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Profile extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener,ProfileCarFragment.carSectionInterface {

    int check = 0;
    ProfileCarFragment frg1;
    ProfileUserFragment frg2;
    @Bind(R.id.bottom_sheet)
    SheetLayout mSheetLayout;
    @Bind(R.id.fabAddCar)
    FloatingActionButton mFab;
    private CustomPager mPager;
    private SlidingTabLayout myTabLayout;
    private ViewGroup synchronizeButton;
    private ViewGroup.LayoutParams buttonLayoutParams;
    private ViewAnimator viewAnimator;
    private int REQUEST_CODE = 1;

    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    private Button changesUpdateButton;

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    private JSONArray addArrJSON = null;
    private JSONArray updateArrJSON = null;
    private JSONArray deleteArrJSON = null;

    private ArrayList<CarInfoStr> backUpData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mySingelton.deletedCars = new ArrayList<>();

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));

        viewAnimator = (ViewAnimator)findViewById(R.id.viewFlipperProfile);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;


        TypedValue tv = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        int x = width/2;
        int y = actionBarHeight + 30;


        mSheetLayout.setFab(mFab, x, y);
        mSheetLayout.setFabAnimationEndListener(this);

        try{
            check = Integer.parseInt(getIntent().getStringExtra("check"));
        }catch (Exception e){
            check = 0;
        }



        // drawer setup
        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, getResources().getString(R.string.profile));

        mPager = (CustomPager) findViewById(R.id.viewPagerHome);
        myTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtabLayout);
//        myTabLayout.setBackgroundColor(getColor(R.color.primaryColor));

        mPager.setPagingEnabled(false);
        mPager.setScrollDurationFactor(3);

        frg1 = new ProfileCarFragment();
        frg1.setMyContext(this);
        frg1.setmFragmentManager(getSupportFragmentManager());
        frg1.setMy_carSectionInterface(this);

        frg2 = new ProfileUserFragment();
        frg2.setMyContext(this);
        frg2.setmFragmentManager(getSupportFragmentManager());


        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        myTabLayout.setDistributeEvenly(false);
        myTabLayout.setTabWeights(new int[]{1, 1});
        myTabLayout.setViewPager(mPager);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT);
                        break;
                    case 1:
                        AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT);
                        break;
                    default:
                        AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        synchronizeButton = (ViewGroup)findViewById(R.id.changesConfirmedLayout);

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();

        changesUpdateButton = (Button)findViewById(R.id.changesConfirmedButton);
        changesUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });

        backUpData();
    }

    private void getAllArrays() throws JSONException {
        addArrJSON = new JSONArray();
        updateArrJSON = new JSONArray();
        deleteArrJSON = new JSONArray();

        ArrayList<String> deleteArr = new ArrayList<>();


        for (int i=0;i<mySingelton.deletedCars.size();i++){
            deleteArrJSON.put(mySingelton.deletedCars.get(i).carId);
        }

        for (int i=0;i<mySingelton.userCarList.size();i++){
            if (mySingelton.userCarList.get(i).status == 1){

                JSONObject params = new JSONObject();
                params.put("Name",mySingelton.userCarList.get(i).carName);
                params.put("Brand",mySingelton.userCarList.get(i).carBrand);
                params.put("Model",mySingelton.userCarList.get(i).carModel);
                params.put("YearOfManufacture",mySingelton.userCarList.get(i).yearOfMaufacture);
                params.put("RegNumber",mySingelton.userCarList.get(i).carRegNo);
                params.put("Variant",mySingelton.userCarList.get(i).carVariant);
                addArrJSON.put(params);

            }
            if (mySingelton.userCarList.get(i).status == 2){
                JSONObject params = new JSONObject();
                params.put("Name",mySingelton.userCarList.get(i).carName);
                params.put("Brand",mySingelton.userCarList.get(i).carBrand);
                params.put("Model",mySingelton.userCarList.get(i).carModel);
                params.put("YearOfManufacture",mySingelton.userCarList.get(i).yearOfMaufacture);
                params.put("RegNumber",mySingelton.userCarList.get(i).carRegNo);
                params.put("Variant",mySingelton.userCarList.get(i).carVariant);
                params.put("CarId",mySingelton.userCarList.get(i).carId);
                updateArrJSON.put(params);
            }
        }
    }

    private void backUpData(){
        backUpData = new ArrayList<>();
        for (int i=0;i<mySingelton.userCarList.size();i++){
            backUpData.add(mySingelton.userCarList.get(i));
            backUpData.get(i).status = 0;
        }
    }

    private void updateUserDetails(){

        try {
            getAllArrays();
        }catch (Exception e){
            showMessage("Error In Getting Data");
        }

        int check = frg2.getData();

        if (check < 0){
            switch (check){
                case -1 : showMessage("Please enter user name");break;
                case -2 : showMessage("Please enter mobile number");break;
                case -3 : showMessage("Please enter address");break;
                case -4 : showMessage("Please enter valid mobile number");break;
                case -5 : showMessage("mobile number should be of 10 digits only");break;
                default: showMessage("User details are not valid");
            }
            return;
        }

        showLoadingView();

        JSONObject params = new JSONObject();
        try {
            params.put("UserName", mySingelton.userName);
            params.put("Address", mySingelton.address);
            params.put("MobileNumber", mySingelton.mobileNumber);
            params.put("UserId", mySingelton.userId);
            params.put("addArr",addArrJSON);
            params.put("updateArr",updateArrJSON);
            params.put("deleteArr",deleteArrJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.UpdateUserDetails, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoadingWithMessage("User Details Updated");
                                parseResponse(response);
                            } else {
                                goBackToBackedUpData();
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            goBackToBackedUpData();
                            hideLoadingWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        goBackToBackedUpData();
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
            myStr.status = 0;
            mySingelton.userCarList.add(myStr);
        }

        backUpData = new ArrayList<>();
        for (int i=0;i<mySingelton.userCarList.size();i++){
            backUpData.add(mySingelton.userCarList.get(i));
        }

        mySingelton.deletedCars = new ArrayList<>();
        frg1.reloadtable();
        frg2.reloadData();

    }

    private void goBackToBackedUpData(){
        mySingelton.userCarList = new ArrayList<>();
        for (int i=0;i<backUpData.size();i++){
            mySingelton.userCarList.add(backUpData.get(i));
        }
        frg1.reloadtable();
        frg2.reloadData();
    }

    @OnClick(R.id.fabAddCar)
    void onFabClick() {
        mSheetLayout.expandFab();
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(this, AddCarInfo.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }

    @Override
    public void carSelected(CarInfoStr someStr,int index) {
        Intent myIntent = new Intent(Profile.this,AddCarInfo.class);
        myIntent.putExtra("carSelection",someStr);
//        myIntent.putExtra("indexOfCar",index);
        mySingelton.carSelectionIndex = index;
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(Profile.this, LoginNew.class));
        }

        return super.onOptionsItemSelected(item);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu,menu);
//        return true;
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
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
        MoinUtils.getReference().showMessage(Profile.this, msg);
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(Profile.this, msg);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.profiletabs);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    return frg2;
                case 1:
                    return frg1;
                default:
                    return frg2;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }


        @Override
        public int getCount() {
            return 2;
        }
    }
}
