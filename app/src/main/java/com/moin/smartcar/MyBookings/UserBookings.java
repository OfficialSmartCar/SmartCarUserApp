package com.moin.smartcar.MyBookings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Booking.BookingDetailPartOne;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.DataStr.UpCommingStr;
import com.moin.smartcar.MyBookings.MyBookingsTabs.BookingsHistory;
import com.moin.smartcar.MyBookings.MyBookingsTabs.InProgress;
import com.moin.smartcar.MyBookings.MyBookingsTabs.UpCommingBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Tabs.CustomPager;
import com.moin.smartcar.Tabs.SlidingTabLayout;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserBookings extends AppCompatActivity implements UpCommingBookings.UpCommingInterface, InProgress.ImProgressInterface, BookingsHistory.HistoryInterface {

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    @Bind(R.id.viewPagerHome)
    CustomPager mPager;
    @Bind(R.id.slidingtabLayout)
    SlidingTabLayout myTabLayout;
    @Bind(R.id.loadingIndicator)
    AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.loadignView)
    View loadingView;
    private UpCommingBookings frg1;
    private InProgress frg2;
    private BookingsHistory frg3;

    private ArrayList<UpCommingStr> list1 = new ArrayList<>();
    private ArrayList<UpCommingStr> list2 = new ArrayList<>();
    private ArrayList<UpCommingStr> list3 = new ArrayList<>();

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings);

        ButterKnife.bind(this);

        mySingelton.cancelledOrRescheduled = 0;


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Bookings");

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, "My Bookings");

        loadFragments();
        loadViewPager();
        showLoadingView();
    }

    private void loadFragments() {
        frg1 = UpCommingBookings.getInstance();
        frg1.setfragmentManager(getSupportFragmentManager());
        frg1.setMyContext(UserBookings.this);
        frg1.setMy_UpCommingInterface(UserBookings.this);

        frg2 = InProgress.getInstance();
        frg2.setfragmentManager(getSupportFragmentManager());
        frg2.setMyContext(UserBookings.this);
        frg2.setMy_ImProgressInterface(UserBookings.this);

        frg3 = BookingsHistory.getInstance();
        frg3.setfragmentManager(getSupportFragmentManager());
        frg3.setMyContext(UserBookings.this);
        frg3.setMy_HistoryInterface(UserBookings.this);

        getData();

    }

    private void loadViewPager() {
        mPager.setScrollDurationFactor(3);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        myTabLayout.setDistributeEvenly(false);
        myTabLayout.setTabWeights(new int[]{3, 3, 3});
        myTabLayout.setViewPager(mPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mySingelton.cancelledOrRescheduled == 1) {
            mySingelton.cancelledOrRescheduled = 0;
            new AlertDialog.Builder(UserBookings.this)
                    .setTitle("Appointment Cancelled")
                    .setMessage("Our 'Service Representative' will get in touch with you for further assistance related to refund")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .show();
            showLoadingView();
            getData();
        }
        if (mySingelton.cancelledOrRescheduled == 2) {
            mySingelton.cancelledOrRescheduled = 0;
            new AlertDialog.Builder(UserBookings.this)
                    .setTitle("Appointment Rescheduled")
                    .setMessage("Your Appointment has been rescheduled")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .show();
            showLoadingView();
            getData();
        }
    }

    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("userId", DataSingelton.getMy_SingeltonData_Reference().userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest bookingRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.getMy_SingeltonData_Reference().getAllbooking, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoadingView();
                                parseResponseIntoSrt(response);
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingWithMessage("You Are Offline");
                    }
                }
        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        bookingRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(bookingRequest);
    }

    private void parseResponseIntoSrt(JSONObject response) throws JSONException {
        JSONArray arr = response.getJSONArray("arr");
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject object = arr.getJSONObject(i);
            UpCommingStr myStr = new UpCommingStr();
            myStr.id = object.getString("id");
            myStr.title = object.getString("type");
            myStr.date = object.getString("date");
            myStr.time = object.getString("time");
            myStr.phoneNumber = object.getString("userPhoneNumber");
            myStr.alternatenumber = object.getString("userAlternateNumber");
            myStr.userAddress = object.getString("userAddress");
            myStr.userCarId = object.getString("userCarId");
            myStr.completionStatus = Integer.parseInt(object.getString("completionStatus"));
            myStr.isCancelled = Integer.parseInt(object.getString("isCancelled"));
            myStr.isActive = Integer.parseInt(object.getString("isActive"));
            myStr.parentId = object.getString("parentId");
            myStr.isParent = Integer.parseInt(object.getString("isParent"));
            myStr.carName = object.getString("carName");
            myStr.serviceIndex = object.getString("serviceIndex");

            String services = object.getString("servicesSelected");

            String[] arr123 = services.split(",");
            myStr.servicesSelected = new ArrayList<>();
            for (int x = 0; x < arr123.length; x++) {
                myStr.servicesSelected.add(arr123[x]);
            }

            String servicesIdentifier = object.getString("servicesId");
            String[] arr1234 = services.split(",");
            myStr.servicesId = new ArrayList<>();
            for (int x = 0; x < arr1234.length; x++) {
                myStr.servicesId.add(arr1234[x]);
            }

            myStr.cost = object.getString("cost");
            myStr.code1 = object.getString("code1");
            myStr.code2 = object.getString("code2");
            myStr.code3 = object.getString("code3");

            myStr.status = object.getString("status");

            if (myStr.status.equalsIgnoreCase("UpComming")) {
                list1.add(myStr);
            } else {
                if (myStr.status.equalsIgnoreCase("InProgress")) {
                    list2.add(myStr);
                } else {
                    list3.add(myStr);
                }
            }
        }
        frg1.updateData(list1);
        frg2.updateData(list2);
        frg3.updateData(list3);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(UserBookings.this, LoginNew.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void doNothing(View view) {
    }

    @Override
    public void requestDataImprogressScreen() {
        getData();
    }

    @Override
    public void selectedInprogressBooking(UpCommingStr someStr) {
        DataSingelton.getMy_SingeltonData_Reference().MyBookingData = someStr;
        startActivity(new Intent(UserBookings.this, BookingDetailPartOne.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    @Override
    public void requestDataUpCommingScreen() {
        getData();
    }

    @Override
    public void selectedUpCommingBooking(UpCommingStr someStr) {
        DataSingelton.getMy_SingeltonData_Reference().MyBookingData = someStr;
        startActivity(new Intent(UserBookings.this, BookingDetailPartOne.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    @Override
    public void requestDataHistoryScreen() {
        getData();
    }

    @Override
    public void selectedHistory(UpCommingStr someStr) {
        DataSingelton.getMy_SingeltonData_Reference().MyBookingData = someStr;
        startActivity(new Intent(UserBookings.this, BookingDetailPartOne.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        frg1.completeRefresh();
        frg2.completeRefresh();
        frg3.completeRefresh();
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingWithMessage(String msg) {
        hideLoadingView();
        MoinUtils.getReference().showMessage(UserBookings.this, msg);
        frg1.completeRefresh();
        frg2.completeRefresh();
        frg3.completeRefresh();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.my_bookings_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return frg1;
                case 1:
                    return frg2;
                case 2:
                    return frg3;
                default:
                    return frg1;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 3;
        }
    }



}
