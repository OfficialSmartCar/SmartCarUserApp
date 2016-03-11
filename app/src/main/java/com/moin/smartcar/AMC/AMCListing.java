package com.moin.smartcar.AMC;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Booking.BookingMain;
import com.moin.smartcar.Booking.PremiumCar;
import com.moin.smartcar.CarSelector.CarSelection;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.RegService.RegularServiceDetail;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;
import com.moin.smartcar.User.ProfileCarFragment;
import com.moin.smartcar.Utility.MoinUtils;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AMCListing extends AppCompatActivity implements CarSelection.carSelectedInterface {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    CarInfoStr selectedCar = new CarInfoStr();
    DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
    @Bind(R.id.loadignView) View loadingView;
    @Bind(R.id.loadingIndicator) AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.refresh_layout) CircleRefreshLayout mRefreshLayout;
    @Bind(R.id.costTextView) TextView costTextView;
    @Bind(R.id.recycler) RecyclerView myRecyclerView;
    @Bind(R.id.fragmentContainer) View fragmentContainer;
    @Bind(R.id.bookLabel) TextView bookingTextView;
    @Bind(R.id.bookingLayout) View bookingLayout;
    @Bind(R.id.offerTextView)TextView offerTextView;

    @Bind(R.id.totalTextView)TextView totalTextView;

    private ArrayList<AMCStr> data = new ArrayList<>();
    private Double total = 0.0;
    private ProfileCarFragment tempView;
    private Double finalCost = 0.0;
    private AMCAdapter myAdapter;
    private boolean bookingDisabled;
    private CarSelection fragmentPopUp;

    private void setFonts(){
        offerTextView.setTypeface(mySingelton.myCustomTypeface);
        totalTextView.setTypeface(mySingelton.myCustomTypeface);
        costTextView.setTypeface(mySingelton.myCustomTypeface);
        bookingTextView.setTypeface(mySingelton.myCustomTypeface);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amclisting);
        ButterKnife.bind(this);

        mySingelton.PremiumSelection = 0;

        bookingDisabled = true;
        bookingLayout.setAlpha(0.0f);

        mySingelton.selectionOfScreen = 3;

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.AMC));

        navUserBookings navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, getResources().getString(R.string.AMC));

        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new AMCAdapter(AMCListing.this);
        myRecyclerView.setAdapter(myAdapter);

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

        showLoadingView();

        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentContainer.setVisibility(View.VISIBLE);

        fragmentPopUp = (CarSelection) getSupportFragmentManager().findFragmentById(R.id.fragment1);
        fragmentPopUp.setMyFragmentManager(getSupportFragmentManager());
        fragmentPopUp.setMy_carSelectedInterface(this);

        if (mySingelton.userCarList.size() == 1) {
            selectedCar = mySingelton.userCarList.get(0);
            mySingelton.CarSelecetd = selectedCar;
            fragmentContainer = findViewById(R.id.fragmentContainer);
            fragmentContainer.setVisibility(View.GONE);
            if (mySingelton.CarSelecetd.isPremium == 1){
                startActivity(new Intent(AMCListing.this,PremiumCar.class));
                overridePendingTransition(R.anim.slide_right_in,R.anim.scalereduce);
                return;
            }
            getData();
        }

        costTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bookingDisabled) {
                    Intent myIntent = new Intent(AMCListing.this, BookingMain.class);
                    mySingelton.paymentSelection = 3;
                    String ammount = costTextView.getText().toString();
                    ammount = ammount.substring(0, ammount.length() - 2);
                    mySingelton.AmmountToPay = Double.parseDouble(ammount);
                    mySingelton.amcServiceSelection = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        mySingelton.amcServiceSelection.add(data.get(i));
                    }
                    startActivity(myIntent);
                    overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                }
            }
        });

        bookingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bookingDisabled) {

                    Intent myIntent = new Intent(AMCListing.this, BookingMain.class);
                    mySingelton.paymentSelection = 3;
                    String ammount = costTextView.getText().toString();
                    ammount = ammount.substring(0, ammount.length() - 2);
                    mySingelton.AmmountToPay = Double.parseDouble(ammount);
                    mySingelton.amcServiceSelection = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        mySingelton.amcServiceSelection.add(data.get(i));
                    }
                    startActivity(myIntent);
                    overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                }
            }
        });

        setFonts();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(AMCListing.this, LoginNew.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentPopUp.refreshHeader();
        if (mySingelton.PremiumSelection == 1){
            mySingelton.PremiumSelection = 0;
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    private void getData() {
        costTextView.setText("");
        finalCost = 0.0;
        JsonObjectRequest getAMCList = new JsonObjectRequest(Request.Method.GET, DataSingelton.getAMC,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");

                            if (!status.equalsIgnoreCase("Error")) {
                                parseServerResponse(response);
                            } else {
                                hideLoadingWithMessage(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingWithMessage("There was some problem please try again");
                    }
                }

        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getAMCList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getAMCList);
    }

    private void parseServerResponse(JSONObject myObj) {
        try {
            total = 0.0;
            data = new ArrayList<>();
            JSONArray jsonArr = myObj.getJSONArray("arr");
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject indexObject = jsonArr.getJSONObject(i);
                AMCStr str = new AMCStr();
                str.TaskId = indexObject.get("TaskId").toString();
                str.NameOfTask = indexObject.get("NameOfTask").toString();
                str.InfoOfTask = indexObject.get("InfoOfTask").toString();
                str.CostOfTask = Double.parseDouble(indexObject.get("CostOfTask").toString());
                str.TaxPercentage = Double.parseDouble(indexObject.get("TaxPercentage").toString());
                total = total + str.CostOfTask;
                str.TaxType = indexObject.get("TaxType").toString();
                data.add(str);
            }
            costTextView.setText("Rs. " + total + " ");
//            hideLoadingView();
            hideLoading();
            myAdapter.notifyDataSetChanged();
            if (data.size() > 0) {
                bookingLayout.setAlpha(1.0f);
                bookingDisabled = false;
            }
//            mRefreshLayout.finishRefreshing();
        } catch (Exception e) {
            hideLoadingWithMessage(getString(R.string.dataInconsistent));
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
        MoinUtils.getReference().showMessage(AMCListing.this, msg);
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

    @Override
    public void carSelectedAtIndex(int index) {
        fragmentContainer.setVisibility(View.GONE);
        selectedCar = mySingelton.userCarList.get(index);
        if (selectedCar.isPremium == 1){
            startActivity(new Intent(AMCListing.this, PremiumCar.class));
            overridePendingTransition(R.anim.slide_right_in,R.anim.scalereduce);
            return;
        }
        getData();

    }

    private void showMoreInfoForView(int position, View v) {
        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);
        AMCStr info = data.get(position);
        Intent subActivity = new Intent(AMCListing.this,
                RegularServiceDetail.class);
        subActivity.
                putExtra(MoinUtils.getReference().Package + ".left", screenLocation[0]).
                putExtra(MoinUtils.getReference().Package + ".top", screenLocation[1]).
                putExtra(MoinUtils.getReference().Package + ".width", v.getWidth()).
                putExtra(MoinUtils.getReference().Package + ".height", v.getHeight());

        AMCStr myStr = data.get(position);
        mySingelton.regServiceTaskId = myStr.TaskId;
        mySingelton.regServicetitle = myStr.NameOfTask;
        mySingelton.regServicesubTitle = myStr.InfoOfTask;
        mySingelton.regServiceCost = myStr.CostOfTask;
        mySingelton.regServiceTaxType = myStr.TaxType;
        mySingelton.regServiceTaxPercentage = myStr.TaxPercentage;
        startActivity(subActivity);
        overridePendingTransition(0, 0);
    }

    private class AMCAdapter extends RecyclerView.Adapter {

        private LayoutInflater inflator;

        public AMCAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(int position) {
            if (data.size() == 0) {
                return 0;
            }
            if (position >= data.size()) {
                return 1;
            }
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {
                case 0:
                    View view = inflator.inflate(R.layout.reciept_layout, parent, false);
                    MainDisplayCell holder = new MainDisplayCell(view);
                    return holder;
                case 1:
                    View view1 = inflator.inflate(R.layout.support_reciept_layout, parent, false);
                    SupportCell holder1 = new SupportCell(view1);
                    return holder1;
            }
            return null;

        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            switch (holder.getItemViewType()) {

                case 0:
                    MainDisplayCell holder1 = (MainDisplayCell) holder;
                    holder1.titletextView.setText(data.get(position).NameOfTask);
                    holder1.subtitleTextView.setText("Rs." + data.get(position).CostOfTask);
                    int max = data.size() - 1;
                    holder1.backView.setBackgroundResource(R.drawable.s_box);
//                    if (position == 0){
//                        holder1.backView.setBackgroundResource(R.drawable.s_up);
//                    }
//                    if (position == (data.size()-1)){
//                        holder1.backView.setBackgroundResource(R.drawable.s_down);
//                    }
                    break;

                case 1:
                    SupportCell holder2 = (SupportCell) holder;
                    Double costWithoutDiscount = 0.0;
                    Double costWithDiscount = 0.0;
                    for (int i = 0; i < data.size(); i++) {
                        Double cost = data.get(i).CostOfTask * 3;
                        cost = cost + ((data.get(i).TaxPercentage / 100) * cost);
                        costWithoutDiscount += cost;
                    }
                    for (int i = 0; i < data.size(); i++) {
                        Double cost = data.get(i).CostOfTask * 3;
                        cost = cost - ((35.0 / 100.0) * cost);
                        cost = cost + ((data.get(i).TaxPercentage / 100) * cost);
                        costWithDiscount += cost;
                    }

                    Double truncatedDouble = new BigDecimal(costWithoutDiscount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    Double truncatedDouble2 = new BigDecimal(costWithDiscount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

//                    truncatedDouble = truncatedDouble * 3;
//                    truncatedDouble2 = truncatedDouble2 * 3;

                    holder2.orignalCostTextView.setText(truncatedDouble + "/-");
                    holder2.discountTextView.setText("35%");
                    holder2.finalCostTextView.setText(truncatedDouble2 + "/-");

                    finalCost = truncatedDouble2;
                    costTextView.setText(truncatedDouble2 + "/-");

                    break;
            }

        }

        @Override
        public int getItemCount() {
            if (data.size() != 0) {
                return data.size() + 1;
            }
            return data.size();

        }
    }

    public class MainDisplayCell extends RecyclerView.ViewHolder {
        TextView titletextView, subtitleTextView, locationTextView, totalTextView;
        View backView;

        public MainDisplayCell(View itemView) {
            super(itemView);
            backView = itemView.findViewById(R.id.backView);
            titletextView = (TextView) itemView.findViewById(R.id.nameOfService);
            subtitleTextView = (TextView) itemView.findViewById(R.id.costOfService);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreInfoForView(getAdapterPosition(), v);
                }
            });

            titletextView.setTypeface(mySingelton.myCustomTypeface);
            subtitleTextView.setTypeface(mySingelton.myCustomTypeface);

        }
    }

    public class SupportCell extends RecyclerView.ViewHolder {
        TextView orignalCostTextView, discountTextView, finalCostTextView;

        public SupportCell(View itemView) {
            super(itemView);
            orignalCostTextView = (TextView) itemView.findViewById(R.id.orignalCostTextView);
            discountTextView = (TextView) itemView.findViewById(R.id.discountTextView);
            finalCostTextView = (TextView) itemView.findViewById(R.id.finalCostTextView);

            orignalCostTextView.setTypeface(mySingelton.myCustomTypeface);
            discountTextView.setTypeface(mySingelton.myCustomTypeface);
            finalCostTextView.setTypeface(mySingelton.myCustomTypeface);


        }
    }

}
