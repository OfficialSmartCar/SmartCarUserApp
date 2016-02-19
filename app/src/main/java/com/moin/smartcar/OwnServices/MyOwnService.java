package com.moin.smartcar.OwnServices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.moin.smartcar.Utility.MoinUtils;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MyOwnService extends AppCompatActivity implements CarSelection.carSelectedInterface {

    int textCheck = 0;
    private TextView totalCostTextView;
    private int mSelected = 0;
    private View BottomConatiner;
    private CircleRefreshLayout mRefreshLayout;
    private RecyclerView myRecyclerView;
    private ArrayList<OwnServiceStr> data = new ArrayList<>();
    private MyOwnServiceAdapter myAdapter;
    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    private View fragmentContainer;
    private CarSelection fragmentPopUp;

    private CarInfoStr selectedCar;
    private Double costWithTax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_own_service);

        mySingelton.selectionOfScreen = 2;

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.customservice));

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, getResources().getString(R.string.customservice));

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView)findViewById(R.id.loadingIndicator);


        BottomConatiner = findViewById(R.id.bottomContainer);
        BottomConatiner.setAlpha(0.0f);
        totalCostTextView = (TextView)findViewById(R.id.costTextView);

        mRefreshLayout = (CircleRefreshLayout)findViewById(R.id.refresh_layout);
        myRecyclerView = (RecyclerView)findViewById(R.id.myOwnServiceRecyclerView);
        myRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
//        myRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyOwnServiceAdapter(this);
        myRecyclerView.setAdapter(myAdapter);

        mRefreshLayout.setOnRefreshListener(
                new CircleRefreshLayout.OnCircleRefreshListener() {
                    @Override
                    public void refreshing() {
                        getData();
                        // do something when refresh starts
                    }

                    @Override
                    public void completeRefresh() {

                        // do something when refresh complete
                    }
                });


        
//        showLoadingView();

        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentContainer.setVisibility(View.VISIBLE);

        fragmentPopUp = (CarSelection)getSupportFragmentManager().findFragmentById(R.id.fragment1);
        fragmentPopUp.setMyFragmentManager(getSupportFragmentManager());
//        fragmentPopUp.setMyContext(MyOwnService.this);
        fragmentPopUp.setMy_carSelectedInterface(MyOwnService.this);
//        getData();

        if (mySingelton.userCarList.size() == 1){
            selectedCar = mySingelton.userCarList.get(0);
            mySingelton.CarSelecetd = selectedCar;
            fragmentContainer = findViewById(R.id.fragmentContainer);
            fragmentContainer.setVisibility(View.GONE);
            getData();
        }

        showLoadingView();

        BottomConatiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MyOwnService.this, BookingMain.class);
                mySingelton.paymentSelection = 2;
                mySingelton.AmmountToPay = costWithTax;
                mySingelton.customServiceSelection = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isActive) {
                        mySingelton.customServiceSelection.add(data.get(i));
                    }
                }
                startActivity(myIntent);
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

    }

    private void getData(){

//        hintTextView.setAlpha(1.0f);

        JsonObjectRequest getServcesList = new JsonObjectRequest(Request.Method.GET, DataSingelton.getCustomeServices,

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

        unselectAllSelections();

//        showLoadingView();
        calculateTotal();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getServcesList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getServcesList);
    }

    private void unselectAllSelections() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isActive = false;
        }
    }

    private void parseServerResponse(JSONObject myObj){
        try{
            data = new ArrayList<>();
            JSONArray jsonArr = myObj.getJSONArray("arr");
            for (int i=0;i<jsonArr.length();i++){
                JSONObject indexObject = jsonArr.getJSONObject(i);
                OwnServiceStr str = new OwnServiceStr();
                str.taskId = indexObject.get("TaskId").toString();
                str.title = indexObject.get("NameOfTask").toString();
                str.subTitle = indexObject.get("InfoOfTask").toString();
                str.cost = Double.parseDouble(indexObject.get("CostOfTask").toString());
                str.taxPercentage = Double.parseDouble(indexObject.get("TaxPercentage").toString());
                str.taxType = indexObject.get("TaxType").toString();
                str.isActive = false;
                data.add(str);
            }
            textCheck = 2;
            hideLoadingView();
            myAdapter.notifyDataSetChanged();
            mRefreshLayout.finishRefreshing();
        }catch (Exception e){
            hideLoadingWithMessage(getString(R.string.dataInconsistent));
        }
    }

    public void expand(final View v) {

        BottomConatiner.setAlpha(1.0f);

        myRecyclerView.setPadding(0, 0, 0, 100);

        v.setEnabled(true);
//        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        final int targetHeight = v.getMeasuredHeight();
//
//        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
//        v.getLayoutParams().height = 1;
//        v.setVisibility(View.VISIBLE);
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1
//                        ? LinearLayout.LayoutParams.WRAP_CONTENT
//                        : (int)(targetHeight * interpolatedTime);
//                v.requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
//        v.startAnimation(a);
    }

    public void collapse(final View v) {

        BottomConatiner.setAlpha(0.0f);

        myRecyclerView.setPadding(0, 0, 0, 0);

        v.setEnabled(false);
//        final int initialHeight = v.getMeasuredHeight();
//
//        Animation a = new Animation()
//        {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                if(interpolatedTime == 1){
//                    v.setVisibility(View.GONE);
//                }else{
//                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
//                    v.requestLayout();
//                }
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
//        v.startAnimation(a);
    }

    private void hideLoading(){
        try{
            hideLoadingView();
            mRefreshLayout.finishRefreshing();
        }catch (Exception e){
        }
    }

    private void hideLoadingView(){
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView(){

        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingWithMessage(String msg){
        hideLoading();
        MoinUtils.getReference().showMessage(MyOwnService.this,msg);
    }

    private void viewSelected (int position){

        data.get(position).isActive = !data.get(position).isActive;
        calculateTotal();
        myAdapter.notifyDataSetChanged();
    }

    private void calculateTotal(){
        costWithTax = 0.0;
        for (int i=0;i<data.size();i++){
            if (data.get(i).isActive){
                double mTotal = 0;
                mTotal = data.get(i).cost;
                mTotal = mTotal + (((data.get(i).taxPercentage) / 100) * mTotal);
                costWithTax += mTotal;
            }
        }

        Double truncatedDouble = new BigDecimal(costWithTax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        costWithTax = truncatedDouble;

        if (costWithTax == 0.0) {
            collapse(BottomConatiner);
            mSelected = 0;
        }else{
            if (mSelected == 0){
                mSelected = 1;
                expand(BottomConatiner);
            }
        }

        totalCostTextView.setText(" Rs. " + costWithTax + " ");
    }

    private void showMoreInfoForView(int position, View v){
        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);
        OwnServiceStr info = data.get(position);
        Intent subActivity = new Intent(MyOwnService.this,
                OwnServiceDetail.class);
        subActivity.
                putExtra(MoinUtils.getReference().Package + ".left", screenLocation[0]).
                putExtra(MoinUtils.getReference().Package + ".top", screenLocation[1]).
                putExtra(MoinUtils.getReference().Package + ".width", v.getWidth()).
                putExtra(MoinUtils.getReference().Package + ".height", v.getHeight());

        OwnServiceStr myStr = data.get(position);
        mySingelton.customServicetitle = myStr.title;
        mySingelton.customServicesubTitle = myStr.subTitle;
        mySingelton.customServiceTaxType = myStr.taxType;
        mySingelton.customServiceTaxPercentage = myStr.taxPercentage;
        mySingelton.customServiceCost = myStr.cost;

        startActivity(subActivity);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentPopUp.refreshHeader();
        mySingelton.customServicetitle = "";
        mySingelton.customServicesubTitle = "";
        mySingelton.customServiceTaxType = "";
        mySingelton.customServiceTaxPercentage = 0.0;
        mySingelton.customServiceCost = 0.0;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(MyOwnService.this, LoginNew.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void carSelectedAtIndex(int index) {
        getData();
        fragmentContainer.setVisibility(View.GONE);
        selectedCar = mySingelton.userCarList.get(index);
//        Toast.makeText(MyOwnService.this,selectedCar.carName + "",Toast.LENGTH_LONG).show();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logoutmenu,menu);
//        return true;
//    }

    private class MyOwnServiceAdapter extends RecyclerView.Adapter<MyOwnServiceHolder> {

        private LayoutInflater inflator;

        public MyOwnServiceAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public MyOwnServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.my_own_service_cell, parent, false);
            MyOwnServiceHolder holder = new MyOwnServiceHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyOwnServiceHolder holder, int position) {
//            holder.titletextView.setText(data.get(position));
            OwnServiceStr myStr = data.get(position);
            holder.titletextView.setText(myStr.title);
            holder.subTitleTextView.setText(myStr.subTitle);
            holder.costTextView.setText("Rs " + myStr.cost.toString());

            if (myStr.isActive) {
                holder.parentView.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                holder.checkImage1.setImageResource(R.drawable.check);
            } else {
                holder.parentView.setBackgroundColor(Color.WHITE);
                holder.checkImage1.setImageResource(R.drawable.uncheck);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class MyOwnServiceHolder extends RecyclerView.ViewHolder {
        TextView titletextView, subTitleTextView, costTextView;
        View parentView;
        View topView, BottomView;
        ImageView checkImage1;

        public MyOwnServiceHolder(View itemView) {
            super(itemView);
            checkImage1 = (ImageView) itemView.findViewById(R.id.checkImage1);
            titletextView = (TextView) itemView.findViewById(R.id.ownServiceTitle);
            subTitleTextView = (TextView) itemView.findViewById(R.id.contentOfService);
            costTextView = (TextView) itemView.findViewById(R.id.costTextVew);
            parentView = itemView.findViewById(R.id.parent);
            topView = itemView.findViewById(R.id.top1);
            BottomView = itemView.findViewById(R.id.lowerView);
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewSelected(getAdapterPosition());
                }
            });
            BottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreInfoForView(getAdapterPosition(), parentView);
                }
            });
        }
    }
}
