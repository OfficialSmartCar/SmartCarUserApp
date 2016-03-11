package com.moin.smartcar.Booking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.MyBookings.DataStr.UpCommingStr;
import com.moin.smartcar.MyBookings.RescheduleBooking;
import com.moin.smartcar.Network.MyApplication;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.Notification.SetUp.NewMessageStr;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class BookingDetailPartOne extends AppCompatActivity {

    @Bind(R.id.app_bar) Toolbar myToolbar;
    @Bind(R.id.myCode1TextView) TextView myCode1;
    @Bind(R.id.myCode2TextView) TextView myCode2;
    @Bind(R.id.myCode3TextView) TextView myCode3;
    @Bind(R.id.serviceTitleTextView) TextView serviceTitleTextView;
    @Bind(R.id.dateTextView) TextView dateTextView;
    @Bind(R.id.timeTextView) TextView timeTextView;
    @Bind(R.id.serviceToBeDisplayedTextView) TextView serviceToBeDisplayedTextView;
    @Bind(R.id.costTextView) TextView costTextVew;
    @Bind(R.id.buttonLayout) View buttonLayout;
    @Bind(R.id.knowMore) View knowMoreView;

    @Bind(R.id.scratchImage1) ImageView scratchImage1;
    @Bind(R.id.scratchImage2) ImageView scratchImage2;
    @Bind(R.id.scratchImage3) ImageView scratchImage3;

    @Bind(R.id.loadingIndicator) AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.loadignView) View loadingView;
    @Bind(R.id.rescheduleButton) Button rescheduleButton;
    @Bind(R.id.cancelButton) Button cancelButton;

    @Bind(R.id.code1TextView1)TextView code1TextView1;
    @Bind(R.id.code2TextView)TextView code2TextView;
    @Bind(R.id.code3TextView)TextView code3TextView;

    @Bind(R.id.messageTextView)TextView messageTextView;
    @Bind(R.id.howToUseCOdesTextView)TextView howToUseCOdesTextView;



    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    private void setFonts(){
        code1TextView1.setTypeface(mySingelton.myCustomTypeface);
        myCode1.setTypeface(mySingelton.myCustomTypeface);

        code2TextView.setTypeface(mySingelton.myCustomTypeface);
        myCode2.setTypeface(mySingelton.myCustomTypeface);

        code3TextView.setTypeface(mySingelton.myCustomTypeface);
        myCode3.setTypeface(mySingelton.myCustomTypeface);

        messageTextView.setTypeface(mySingelton.myCustomTypeface);
        howToUseCOdesTextView.setTypeface(mySingelton.myCustomTypeface);

        serviceTitleTextView.setTypeface(mySingelton.myCustomTypeface);
        serviceToBeDisplayedTextView.setTypeface(mySingelton.myCustomTypeface);
        dateTextView.setTypeface(mySingelton.myCustomTypeface);
        timeTextView.setTypeface(mySingelton.myCustomTypeface);
        costTextVew.setTypeface(mySingelton.myCustomTypeface);

        rescheduleButton.setTypeface(mySingelton.myCustomTypeface);
        cancelButton.setTypeface(mySingelton.myCustomTypeface);

    }


    @OnClick(R.id.imageForMoreInfo)
    void showMoreInfo(View view) {
        startActivity(new Intent(BookingDetailPartOne.this, CodeUsage.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    @OnClick(R.id.rescheduleButton)
    void rescheduleAppointment(View view) {

        if (mySingelton.MyBookingData.completionStatus != 0) {
            showMessage("Appointment already initiated");
            return;
        }

        String dateString = mySingelton.MyBookingData.date;
        String arr[] = dateString.split("/");
        int month = Integer.parseInt(arr[1]) - 1;

        String time[] = mySingelton.MyBookingData.time.split(":");
        int hours = Integer.parseInt(time[0]);
        String time1[] = time[1].split(" ");
        if (time1[1].equalsIgnoreCase("PM")) {
            if (hours != 12) {
                hours += 12;
            }
        }

        JSONObject params = new JSONObject();
        try {
            params.put("bookingId", mySingelton.MyBookingData.id);
            params.put("year", arr[2]);
            params.put("month", (month + ""));
            params.put("date", arr[0]);
            params.put("hours", (hours + ""));
            params.put("min", (time1[0] + ""));
            params.put("ampm", (time1[1] + ""));

            params.put("type", mySingelton.MyBookingData.title);
            params.put("userCarName", mySingelton.MyBookingData.carName);
            params.put("userNotificationId", mySingelton.UserNotificationToken);
            params.put("userId", mySingelton.userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest cancelbookingRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.getMy_SingeltonData_Reference().checkIfCanReschedule, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                navigateToReschedulePage();
                            } else {
                                new AlertDialog.Builder(BookingDetailPartOne.this)
                                        .setTitle("Failure")
                                        .setMessage(message)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
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
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        cancelbookingRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(cancelbookingRequest);


    }

    private void navigateToReschedulePage() {
        startActivity(new Intent(BookingDetailPartOne.this, RescheduleBooking.class));
        overridePendingTransition(R.anim.slide_right_in, R.anim.scalereduce);
    }

    @OnClick(R.id.cancelButton)
    void cancelAppointment(View view) {

        if (mySingelton.MyBookingData.completionStatus != 0) {
            showMessage("Appointment already initiated");
            return;
        }

        new AlertDialog.Builder(BookingDetailPartOne.this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to cancel the appointment")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelBooking();
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void cancelBooking() {
        String dateString = mySingelton.MyBookingData.date;
        String arr[] = dateString.split("/");
        int month = Integer.parseInt(arr[1]) - 1;

        String time[] = mySingelton.MyBookingData.time.split(":");
        int hours = Integer.parseInt(time[0]);
        String time1[] = time[1].split(" ");
        if (time1[1].equalsIgnoreCase("PM")) {
            if (hours != 12) {
                hours += 12;
            }
        }

        JSONObject params = new JSONObject();
        try {
            params.put("isParent", mySingelton.MyBookingData.isParent);
            params.put("parentId", mySingelton.MyBookingData.parentId);
            params.put("type", serviceTitleTextView.getText().toString());
            params.put("bookingId", mySingelton.MyBookingData.id);
            params.put("year", arr[2]);
            params.put("month", (month + ""));
            params.put("date", arr[0]);
            params.put("hours", (hours + ""));
            params.put("min", (time1[0] + ""));
            params.put("ampm", (time1[1] + ""));
            params.put("userNotificationId", mySingelton.UserNotificationToken);
            params.put("userId", mySingelton.userId);
            params.put("userCarName", mySingelton.MyBookingData.carName);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest cancelbookingRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.getMy_SingeltonData_Reference().cancelBooking, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
//                                hideLoadingWithMessage("Appointment Cancelled");
                                mySingelton.cancelledOrRescheduled = 1;
                                finish();
                            } else {
                                hideLoadingWithMessage(message);
                            }
//                            hideLoadingView();
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
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        cancelbookingRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(cancelbookingRequest);
    }

    private void showMessage(String msg) {
        Toast.makeText(BookingDetailPartOne.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail_part_one);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fillAllData();
        hideLoadingView();

        knowMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingDetailPartOne.this, CodeUsage.class));
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

        setFonts();
    }

    private void fillAllData() {
        myCode1.setText(mySingelton.MyBookingData.code1);
        myCode2.setText(mySingelton.MyBookingData.code2);
        myCode3.setText(mySingelton.MyBookingData.code3);

        serviceTitleTextView.setText(mySingelton.MyBookingData.title);
        String temp = "";
        for (int i = 0; i < mySingelton.MyBookingData.servicesSelected.size(); i++) {
            if (i == 0) {
                temp = mySingelton.MyBookingData.servicesSelected.get(i);
            } else {
                temp += ", " + mySingelton.MyBookingData.servicesSelected.get(i);
            }
        }
        serviceToBeDisplayedTextView.setText("Services To Be Included : " + temp);

        dateTextView.setText("Date : " + mySingelton.MyBookingData.date);
        timeTextView.setText("Time : " + mySingelton.MyBookingData.time);
        costTextVew.setText("Cost : " + mySingelton.MyBookingData.cost);

        if (!mySingelton.UpCommingOrOther) {
            buttonLayout.setVisibility(View.GONE);
        }

        if (mySingelton.MyBookingData.isParent == 0) {
            cancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scratchImage1.setAlpha(1.0f);
        scratchImage2.setAlpha(1.0f);
        scratchImage3.setAlpha(1.0f);
        switch (mySingelton.MyBookingData.completionStatus) {
            case 0:
                scratchImage1.setAlpha(0.0f);
                break;
            case 1:
                scratchImage2.setAlpha(0.0f);
                break;
            case 2:
                scratchImage3.setAlpha(0.0f);
                break;
            default:
        }
        if (mySingelton.cancelledOrRescheduled == 2) {
            finish();
        }

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
        MoinUtils.getReference().showMessage(BookingDetailPartOne.this, msg);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void showAlert(String message){
        new AlertDialog.Builder(BookingDetailPartOne.this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void onEvent(NewMessageStr event){
//        AllData.add()
        scratchImage1.setAlpha(1.0f);
        scratchImage2.setAlpha(1.0f);
        scratchImage3.setAlpha(1.0f);


        try{
            switch (event.status){
                case 1 :
//                    showAlert("Executive Verified");
                    mySingelton.MyBookingData.completionStatus = 1;
                    scratchImage2.setAlpha(0.0f);
                    rescheduleButton.setAlpha(0.0f);
                    cancelButton.setAlpha(0.0f);
                    rescheduleButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    break;
                case 2 :
//                    showAlert("Car Ready For PickUp");
                    mySingelton.MyBookingData.completionStatus = 2;
                    scratchImage3.setAlpha(0.0f);
                    break;
                case 3:
//                    showAlert("Car Successfully Delivered");
                    mySingelton.MyBookingData.completionStatus = 3;
                    break;
            }
        }catch (Exception e){

        }

    }


}
