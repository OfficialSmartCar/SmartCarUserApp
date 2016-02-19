package com.moin.smartcar.RegService;

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
import com.moin.smartcar.CarSelector.CarSelection;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
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

public class RegularServiceListing extends AppCompatActivity implements CarSelection.carSelectedInterface {


    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
    CarInfoStr selectedCar = new CarInfoStr();
    private RecyclerView myRecyclerView;
    private ExpressServiceAdapter myAdapter;
    private ArrayList<RegServiceStr> data = new ArrayList<>();
    private ProfileCarFragment tempView;
    private CircleRefreshLayout mRefreshLayout;
    private TextView costTextView;
    private View fragmentContainer;
    private CarSelection fragmentPopUp;
    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;
    private Double total = 0.0;
    private Double finalCost = 0.0;
    private View BottomView;
    private TextView bookTextView;

//    private RecyclerAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_service_listing);

        mySingelton.selectionOfScreen = 1;

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.expressService));

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, getResources().getString(R.string.expressService));

        BottomView = findViewById(R.id.payLayout);
        BottomView.setAlpha(0.0f);

        costTextView = (TextView)findViewById(R.id.costTextView);

        mRefreshLayout = (CircleRefreshLayout)findViewById(R.id.refresh_layout);

        myRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(layoutManager);

        myAdapter = new ExpressServiceAdapter(this);
        myRecyclerView.setAdapter(myAdapter);

        tempView = new ProfileCarFragment().getInstance();
        tempView.setmFragmentManager(getSupportFragmentManager());
        tempView.setMyContext(this);


        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentContainer.setVisibility(View.VISIBLE);

        fragmentPopUp = (CarSelection)getSupportFragmentManager().findFragmentById(R.id.fragment1);
        fragmentPopUp.setMyFragmentManager(getSupportFragmentManager());
        fragmentPopUp.setMy_carSelectedInterface(this);

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView)findViewById(R.id.loadingIndicator);
        hideLoading();

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

        if (mySingelton.userCarList.size() == 1){
            selectedCar = mySingelton.userCarList.get(0);
            mySingelton.CarSelecetd = selectedCar;
            fragmentContainer = findViewById(R.id.fragmentContainer);
            fragmentContainer.setVisibility(View.GONE);
            getData();
        }

        costTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegularServiceListing.this, BookingMain.class);
                mySingelton.paymentSelection = 1;
                String ammount = costTextView.getText().toString();
                ammount = ammount.substring(0, ammount.length() - 2);
                mySingelton.AmmountToPay = Double.parseDouble(ammount);
                mySingelton.regularServiceSelection = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    mySingelton.regularServiceSelection.add(data.get(i));
                }
                startActivity(myIntent);
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

        bookTextView = (TextView) findViewById(R.id.bookLabel);
        bookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegularServiceListing.this, BookingMain.class);
                mySingelton.paymentSelection = 1;
                String ammount = costTextView.getText().toString();
                ammount = ammount.substring(0, ammount.length() - 2);
                mySingelton.AmmountToPay = Double.parseDouble(ammount);
                mySingelton.regularServiceSelection = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    mySingelton.regularServiceSelection.add(data.get(i));
                }
                startActivity(myIntent);
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });
    }

    private void setCarList(){
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.carSLider);
//        myTabLayout.addTab(myTabLayout.newTab().setText("Send"));
//        myTabLayout.addTab(myTabLayout.newTab().setText("Send & Post"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentPopUp.refreshHeader();
    }

    private void getData(){
        finalCost = 0.0;
        JsonObjectRequest getExpressServcesList = new JsonObjectRequest(Request.Method.GET, DataSingelton.getExpressService,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");

                            if (!status.equalsIgnoreCase("Error")){
                                parseServerResponse(response);
                            }else{
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
        getExpressServcesList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getExpressServcesList);
    }

    private void parseServerResponse(JSONObject myObj){
        try{
            total = 0.0;
            data = new ArrayList<>();
            JSONArray jsonArr = myObj.getJSONArray("arr");
            for (int i=0;i<jsonArr.length();i++){
                JSONObject indexObject = jsonArr.getJSONObject(i);
                RegServiceStr str = new RegServiceStr();
                str.TaskId = indexObject.get("TaskId").toString();
                str.NameOfTask = indexObject.get("NameOfTask").toString();
                str.InfoOfTask = indexObject.get("InfoOfTask").toString();
                str.CostOfTask = Double.parseDouble(indexObject.get("CostOfTask").toString());
                str.TaxPercentage = Double.parseDouble(indexObject.get("TaxPercentage").toString());
                total = total + str.CostOfTask;
                str.TaxType = indexObject.get("TaxType").toString();
                data.add(str);
            }
            BottomView.setAlpha(1.0f);
            costTextView.setText("Rs. " + total + " ");
            hideLoading();
            myAdapter.notifyDataSetChanged();
        }catch (Exception e){
            hideLoadingWithMessage(getString(R.string.dataInconsistent));
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu,menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(RegularServiceListing.this, LoginNew.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }


    @Override
    public void carSelectedAtIndex(int index) {
        getData();
        fragmentContainer.setVisibility(View.GONE);
        selectedCar = mySingelton.userCarList.get(index);
        mySingelton.CarSelecetd = selectedCar;
    }

    private void showMoreInfoForView(int position, View v) {
        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);
        RegServiceStr info = data.get(position);
        Intent subActivity = new Intent(RegularServiceListing.this,
                RegularServiceDetail.class);
        subActivity.
                putExtra(MoinUtils.getReference().Package + ".left", screenLocation[0]).
                putExtra(MoinUtils.getReference().Package + ".top", screenLocation[1]).
                putExtra(MoinUtils.getReference().Package + ".width", v.getWidth()).
                putExtra(MoinUtils.getReference().Package + ".height", v.getHeight());

        RegServiceStr myStr = data.get(position);
        mySingelton.regServiceTaskId = myStr.TaskId;
        mySingelton.regServicetitle = myStr.NameOfTask;
        mySingelton.regServicesubTitle = myStr.InfoOfTask;
        mySingelton.regServiceCost = myStr.CostOfTask;
        mySingelton.regServiceTaxType = myStr.TaxType;
        mySingelton.regServiceTaxPercentage = myStr.TaxPercentage;
        startActivity(subActivity);
        overridePendingTransition(0, 0);
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
        MoinUtils.getReference().showMessage(RegularServiceListing.this, msg);
    }

    private class ExpressServiceAdapter extends RecyclerView.Adapter {

        private LayoutInflater inflator;

        public ExpressServiceAdapter(Context context){
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
                    UserBookingsCell holder = new UserBookingsCell(view);
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
                    UserBookingsCell holder1 = (UserBookingsCell) holder;
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
                        Double cost = data.get(i).CostOfTask;
                        cost = cost + ((data.get(i).TaxPercentage / 100) * cost);
                        costWithoutDiscount += cost;
                    }
                    for (int i = 0; i < data.size(); i++) {
                        Double cost = data.get(i).CostOfTask;
                        cost = cost - ((25.0 / 100.0) * cost);
                        cost = cost + ((data.get(i).TaxPercentage / 100) * cost);
                        costWithDiscount += cost;
                    }

                    Double truncatedDouble = new BigDecimal(costWithoutDiscount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    Double truncatedDouble2 = new BigDecimal(costWithDiscount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    holder2.orignalCostTextView.setText(truncatedDouble + "/-");
                    holder2.discountTextView.setText("25%");
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

    public class UserBookingsCell extends RecyclerView.ViewHolder {
        TextView titletextView,subtitleTextView,locationTextView,totalTextView;
        View backView;

        public UserBookingsCell(View itemView) {
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
        }
    }

    public class SupportCell extends RecyclerView.ViewHolder {
        TextView orignalCostTextView, discountTextView, finalCostTextView;

        public SupportCell(View itemView) {
            super(itemView);
            orignalCostTextView = (TextView) itemView.findViewById(R.id.orignalCostTextView);
            discountTextView = (TextView) itemView.findViewById(R.id.discountTextView);
            finalCostTextView = (TextView) itemView.findViewById(R.id.finalCostTextView);

        }
    }


}
