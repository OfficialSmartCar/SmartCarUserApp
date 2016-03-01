package com.moin.smartcar.MyBookings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RescheduleBooking extends AppCompatActivity {

    @Bind(R.id.dateTextView)
    TextView dateTextView;
    @Bind(R.id.changeDateTextView)
    TextView changeDateTextView;
    @Bind(R.id.dateSelectionView)
    View dateSelectionView;
    @Bind(R.id.rescheduleButton)
    Button rescheduleButton;

    @Bind(R.id.time_recycler)
    RecyclerView myRecycler;


    @Bind(R.id.loadingIndicator)
    AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.loadignView)
    View loadingView;

    private TimeAdapter myAdapter;
    private ArrayList<TimeStr> data = new ArrayList<>();

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_booking);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reschedule Appointment");

        hideLoadingView();

        changeDateTextView.setAlpha(0.0f);

        dateSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(dateTextView, changeDateTextView, RescheduleBooking.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        getData();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        myRecycler.setLayoutManager(staggeredGridLayoutManager);
        myAdapter = new TimeAdapter(RescheduleBooking.this);
        myRecycler.setAdapter(myAdapter);

        rescheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidations()) {
                    rescheduleService();
                }
            }
        });
    }

    private Boolean checkValidations() {
        if (dateTextView.getText().toString().equalsIgnoreCase("Select Date")) {
            DialogFragment newFragment = new DatePickerFragment(dateTextView, changeDateTextView, RescheduleBooking.this);
            newFragment.show(getSupportFragmentManager(), "datePicker");
            showMessage("Please select a desired date");
            return false;
        }
        return true;
    }

    private void rescheduleService() {
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

        String timeSelected = "";

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).active) {
                timeSelected = data.get(i).time;
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

            params.put("desiredDate", dateTextView.getText().toString());
            params.put("desiredTime", timeSelected);

            params.put("type", mySingelton.MyBookingData.title);
            params.put("userCarName", mySingelton.MyBookingData.carName);
            params.put("userNotificationId", mySingelton.UserNotificationToken);
            params.put("userId", mySingelton.userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest cancelbookingRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.getMy_SingeltonData_Reference().rescheduleAppointment, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                mySingelton.cancelledOrRescheduled = 2;
                                finish();
                            } else {
                                hideLoadingView();
//                                hideLoadingWithMessage(message);
                                new AlertDialog.Builder(RescheduleBooking.this)
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mySingelton.cancelledOrRescheduled == 2) {
            finish();
        }
    }

    private void getData() {
        data = new ArrayList<>();
        TimeStr str = new TimeStr();
        str.time = "9:00 AM";
        str.active = true;
        data.add(str);
        String[] titles = getResources().getStringArray(R.array.timeSlots);
        for (int i = 0; i < titles.length; i++) {
            TimeStr str1 = new TimeStr();
            str1.time = titles[i];
            str1.active = false;
            data.add(str1);
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

    private void timeSlotSelected(int index) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).active = false;
        }
        data.get(index).active = true;
        myAdapter.notifyDataSetChanged();
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(RescheduleBooking.this, msg);
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
        MoinUtils.getReference().showMessage(RescheduleBooking.this, msg);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView passedTextView;
        private TextView backUpTextView;
        private Context myContext;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(TextView someTextView, TextView backUpTextView, Context context) {
            this.passedTextView = someTextView;
            this.backUpTextView = backUpTextView;
            this.myContext = context;
        }

        public DatePickerFragment() {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

//            String settingDate = day+"/"+(month+1)+"/20"+year;
            String dateMoin = day + "/" + (month + 1) + "/" + year;
            String initialSplit = dateMoin;
            String[] arr = initialSplit.split(":");
            String temp123 = arr[0].toString();
            String[] arr2 = temp123.split("/");
            int dateRecieved = Integer.parseInt(arr2[0]);
            int monthRecieved = Integer.parseInt(arr2[1]);
            int yearRecieved = Integer.parseInt(arr2[2]);

            String TodaysDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String[] arr12 = TodaysDate.split("-");
            int todaysDate = Integer.parseInt(arr12[0]);
            int todaysMonth = Integer.parseInt(arr12[1]);
            int todaysYear = Integer.parseInt(arr12[2]);

            passedTextView.setText("Select Date");
            backUpTextView.setAlpha(0.0f);
            if (yearRecieved < todaysYear) {
                MoinUtils.getReference().showMessage(myContext, "Please Select Date Ahead Of Today's Date");
            } else {
                if (monthRecieved < todaysMonth) {
                    MoinUtils.getReference().showMessage(myContext, "Please Select Date Ahead Of Today's Date");
                } else {
                    if (monthRecieved == todaysMonth) {
                        if (dateRecieved <= todaysDate) {
                            MoinUtils.getReference().showMessage(myContext, "Please Select Date Ahead Of Today's Date");
                        } else {
                            backUpTextView.setAlpha(1.0f);
                            passedTextView.setText(dateMoin);
                        }
                    } else {
                        backUpTextView.setAlpha(1.0f);
                        passedTextView.setText(dateMoin);
                    }
                }
            }
        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeCells> {

        private LayoutInflater inflator;

        public TimeAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public TimeCells onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.time_slot_cell, parent, false);
            TimeCells holder = new TimeCells(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(TimeCells holder, int position) {
            TimeStr myStr = data.get(position);
            if (myStr.active) {
                holder.parentView.setBackgroundResource(R.drawable.time_selection_selected);
                holder.timeTextView.setTextColor(Color.WHITE);
                holder.timeTextView.setTypeface(null, Typeface.BOLD);
            } else {
                holder.parentView.setBackgroundColor(Color.TRANSPARENT);
                holder.timeTextView.setTextColor(Color.BLACK);
                holder.timeTextView.setTypeface(null, Typeface.NORMAL);
            }
            holder.timeTextView.setText(myStr.time);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class TimeCells extends RecyclerView.ViewHolder {
        TextView timeTextView;
        View parentView;

        public TimeCells(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            parentView = itemView.findViewById(R.id.time_backGround);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeSlotSelected(getAdapterPosition());
                }
            });
        }
    }

    private class TimeStr {
        public String time;
        public Boolean active;
    }
}
