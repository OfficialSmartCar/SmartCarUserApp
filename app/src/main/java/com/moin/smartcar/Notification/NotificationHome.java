package com.moin.smartcar.Notification;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationHome extends AppCompatActivity {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    @Bind(R.id.recycler) RecyclerView myRecyclerView;
    @Bind(R.id.refresh_layout) CircleRefreshLayout mRefreshLayout;
    @Bind(R.id.loadignView) View loadingView;
    @Bind(R.id.loadingIndicator) AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.noInNotificationsTextView) TextView noInNotificationsTextView;
    private ArrayList<NotificationStr> data = new ArrayList<>();
    private NotificationAdapter myAdapter;

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_home);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.notification));

        navUserBookings navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, getResources().getString(R.string.notification));

        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        getData();
                    }

                    @Override
                    public void completeRefresh() {
                    }
                });

        myRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationHome.this));
        myAdapter = new NotificationAdapter(NotificationHome.this);
        myRecyclerView.setAdapter(myAdapter);

        getData();

        noInNotificationsTextView.setTypeface(mySingelton.myCustomTypeface);

    }

    private void getData() {
        JSONObject params = new JSONObject();
        try {
            params.put("userId", DataSingelton.getMy_SingeltonData_Reference().userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getNotificationList = new JsonObjectRequest(Request.Method.POST, DataSingelton.getMy_SingeltonData_Reference().getNotificationList, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoading();
                                parseServerResponse(response);
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoading();
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
        getNotificationList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getNotificationList);
    }

    private void parseServerResponse(JSONObject response) throws JSONException {

        data = new ArrayList<>();

        JSONArray arr = response.getJSONArray("arr");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            NotificationStr myStr = new NotificationStr();
            myStr.id = obj.getString("id");
            myStr.subject = obj.getString("subject");
            myStr.message = obj.getString("message");
            myStr.date = obj.getString("date");
            myStr.time = obj.getString("time");
            myStr.carName = obj.getString("carname");
            myStr.reply = obj.getString("reply");
            myStr.category = obj.getString("category");
            myStr.readUnRead = Integer.parseInt(obj.getString("readUnRead"));

            data.add(myStr);
        }

        Collections.reverse(data);

        if (data.size() == 0) {
            noInNotificationsTextView.setAlpha(1.0f);
        } else {
            noInNotificationsTextView.setAlpha(0.0f);
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mySingelton.notificationCount = 0;
    }

    private void hideLoading() {
        try {
            hideLoadingView();

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.finishRefreshing();
                }
            };
            worker.schedule(task, 1000, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
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
        hideLoading();
        MoinUtils.getReference().showMessage(NotificationHome.this, msg);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    private class NotificationAdapter extends RecyclerView.Adapter {

        private LayoutInflater inflator;

        public NotificationAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(int position) {
            if (data.get(position).subject.equalsIgnoreCase("Add")) {
                return 0;
            } else {
                if (data.get(position).subject.equalsIgnoreCase("Cancel")) {
                    return 1;
                } else {
                    if (data.get(position).subject.equalsIgnoreCase("Reschedule")) {
                        return 2;
                    } else {
                        if (data.get(position).subject.equalsIgnoreCase("Offer")) {
                            return 3;
                        } else {
                            return 4;
                        }
                    }
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {
                case 0:
                    View view = inflator.inflate(R.layout.notification_appointment_booked_holder, parent, false);
                    AppointmentBookedHolder holder = new AppointmentBookedHolder(view);
                    return holder;
                case 1:
                    View view1 = inflator.inflate(R.layout.notification_appointment_cancelled_holder, parent, false);
                    AppointmentCancelledHolder holder1 = new AppointmentCancelledHolder(view1);
                    return holder1;
                case 2:
                    View view2 = inflator.inflate(R.layout.notification_appointment_rescheduled_holder, parent, false);
                    AppointmentRescheduledHolder holder2 = new AppointmentRescheduledHolder(view2);
                    return holder2;
                case 3:
                    View view3 = inflator.inflate(R.layout.notification_offers_holder, parent, false);
                    OffersHolder holder3 = new OffersHolder(view3);
                    return holder3;
                case 4:
                    View view4 = inflator.inflate(R.layout.notification_dentpaint_holder, parent, false);
                    DentAndPaintHolder holder4 = new DentAndPaintHolder(view4);
                    return holder4;
            }
            return null;
        }


        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            NotificationStr myStr = data.get(position);
            switch (holder.getItemViewType()) {

                case 0:
                    AppointmentBookedHolder holder0 = (AppointmentBookedHolder) holder;
                    holder0.titletextView.setText("Appointment Booked");
                    holder0.dateTextView.setText(myStr.date);
                    String message0 = myStr.category + " appointment for your car " + "\'" + myStr.carName + "\' has been booked for " + myStr.date + " at " + myStr.time;
                    holder0.mainMessage.setText(message0);
                    if (myStr.readUnRead == 1) {
                        holder0.parentContainer.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                    } else {
                        holder0.parentContainer.setBackgroundColor(getResources().getColor(R.color.offWhite));
                    }
                    break;

                case 1:
                    AppointmentCancelledHolder holder1 = (AppointmentCancelledHolder) holder;
                    holder1.titletextView.setText("Appointment Cancelled");
                    holder1.dateTextView.setText(myStr.date);
                    String message1 = myStr.category + " appointment for your car " + "\'" + myStr.carName + "\' has been booked for " + myStr.date + " at " + myStr.time;
                    holder1.mainMessage.setText(message1);
                    if (myStr.readUnRead == 1) {
                        holder1.parentContainer.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                    } else {
                        holder1.parentContainer.setBackgroundColor(getResources().getColor(R.color.offWhite));
                    }
                    break;
                case 2:
                    AppointmentRescheduledHolder holder2 = (AppointmentRescheduledHolder) holder;
                    holder2.titletextView.setText("Appointment Rescheduled");
                    holder2.dateTextView.setText(myStr.date);
                    String message2 = myStr.category + " appointment for your car " + "\'" + myStr.carName + "\' has been booked for " + myStr.date + " at " + myStr.time;
                    holder2.mainMessage.setText(message2);
                    if (myStr.readUnRead == 1) {
                        holder2.parentContainer.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                    } else {
                        holder2.parentContainer.setBackgroundColor(getResources().getColor(R.color.offWhite));
                    }
                    break;
                case 3:
                    OffersHolder holder3 = (OffersHolder) holder;
                    holder3.titletextView.setText(myStr.category);
                    holder3.mainMessage.setText(myStr.message);
                    if (myStr.readUnRead == 1) {
                        holder3.parentContainer.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                    } else {
                        holder3.parentContainer.setBackgroundColor(getResources().getColor(R.color.offWhite));
                    }
                    break;
                case 4:
                    DentAndPaintHolder holder4 = (DentAndPaintHolder) holder;
                    holder4.titletextView.setText("Dent And Paint");
                    holder4.mainMessage.setText(myStr.message);
                    if (myStr.readUnRead == 1) {
                        holder4.parentContainer.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                    } else {
                        holder4.parentContainer.setBackgroundColor(getResources().getColor(R.color.offWhite));
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class AppointmentBookedHolder extends RecyclerView.ViewHolder {
        TextView titletextView, mainMessage, dateTextView;
        View parentContainer;

        public AppointmentBookedHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.mainTitleTextView);
            mainMessage = (TextView) itemView.findViewById(R.id.mainMessageTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            parentContainer = itemView.findViewById(R.id.parentContainer);

            titletextView.setTypeface(mySingelton.myCustomTypeface);
            mainMessage.setTypeface(mySingelton.myCustomTypeface);
            dateTextView.setTypeface(mySingelton.myCustomTypeface);

        }
    }

    public class AppointmentCancelledHolder extends RecyclerView.ViewHolder {
        TextView titletextView, mainMessage, dateTextView;
        View parentContainer;

        public AppointmentCancelledHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.mainTitleTextView);
            mainMessage = (TextView) itemView.findViewById(R.id.mainMessageTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            parentContainer = itemView.findViewById(R.id.parentContainer);

            titletextView.setTypeface(mySingelton.myCustomTypeface);
            mainMessage.setTypeface(mySingelton.myCustomTypeface);
            dateTextView.setTypeface(mySingelton.myCustomTypeface);
        }
    }

    public class AppointmentRescheduledHolder extends RecyclerView.ViewHolder {
        TextView titletextView, mainMessage, dateTextView;
        View parentContainer;

        public AppointmentRescheduledHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.mainTitleTextView);
            mainMessage = (TextView) itemView.findViewById(R.id.mainMessageTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            parentContainer = itemView.findViewById(R.id.parentContainer);

            titletextView.setTypeface(mySingelton.myCustomTypeface);
            mainMessage.setTypeface(mySingelton.myCustomTypeface);
            dateTextView.setTypeface(mySingelton.myCustomTypeface);

        }
    }

    public class OffersHolder extends RecyclerView.ViewHolder {
        TextView titletextView, mainMessage;
        View parentContainer;

        public OffersHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.mainTitleTextView);
            mainMessage = (TextView) itemView.findViewById(R.id.mainMessageTextView);
            parentContainer = itemView.findViewById(R.id.parentContainer);

            titletextView.setTypeface(mySingelton.myCustomTypeface);
            mainMessage.setTypeface(mySingelton.myCustomTypeface);
        }
    }

    public class DentAndPaintHolder extends RecyclerView.ViewHolder {
        TextView titletextView, mainMessage;
        View parentContainer;

        public DentAndPaintHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.mainTitleTextView);
            mainMessage = (TextView) itemView.findViewById(R.id.mainMessageTextView);
            parentContainer = itemView.findViewById(R.id.parentContainer);

            titletextView.setTypeface(mySingelton.myCustomTypeface);
            mainMessage.setTypeface(mySingelton.myCustomTypeface);
        }
    }

}
