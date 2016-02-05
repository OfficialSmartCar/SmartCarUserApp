package com.moin.smartcar.RegService;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.CarSelector.CarSelection;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.OwnServices.OwnServiceStr;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

public class RegularServiceListing extends AppCompatActivity implements CarSelection.carSelectedInterface {


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

    DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
    CarInfoStr selectedCar = new CarInfoStr();
    private Double total = 0.0;

    private View bottomContainer;

//    private RecyclerAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_service_listing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getResources().getString(R.string.expressService));

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, getResources().getString(R.string.expressService));

        costTextView = (TextView)findViewById(R.id.costTextView);
        bottomContainer = findViewById(R.id.bottomContainer);

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
        bottomContainer.setAlpha(0.0f);
        bottomContainer.setEnabled(false);

        showLoadingView();

        if (mySingelton.userCarList.size() == 1){
            selectedCar = mySingelton.userCarList.get(0);
            fragmentContainer = findViewById(R.id.fragmentContainer);
            fragmentContainer.setVisibility(View.VISIBLE);
        }

    }

    private void setCarList(){
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.carSLider);
//        myTabLayout.addTab(myTabLayout.newTab().setText("Send"));
//        myTabLayout.addTab(myTabLayout.newTab().setText("Send & Post"));
    }

    private void getData(){
        JsonObjectRequest getExpressServcesList = new JsonObjectRequest(Request.Method.GET, mySingelton.getExpressService,

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
            costTextView.setText("Rs. " + total+ " ");
            hideLoadingView();
            myAdapter.notifyDataSetChanged();
            mRefreshLayout.finishRefreshing();
        }catch (Exception e){
            hideLoadingWithMessage(getString(R.string.dataInconsistent));
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
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }


    @Override
    public void carSelectedAtIndex(int index) {
        bottomContainer.setAlpha(1.0f);
        bottomContainer.setEnabled(true);
        getData();
        fragmentContainer.setVisibility(View.GONE);
        selectedCar = mySingelton.userCarList.get(index);
        Toast.makeText(RegularServiceListing.this,selectedCar.carName + "",Toast.LENGTH_LONG).show();
    }

    private class ExpressServiceAdapter extends RecyclerView.Adapter<UserBookingsCell>{

        private LayoutInflater inflator;

        public ExpressServiceAdapter(Context context){
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public UserBookingsCell onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.reciept_layout, parent, false);
            UserBookingsCell holder = new UserBookingsCell(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(UserBookingsCell holder, int position) {
            holder.titletextView.setText(data.get(position).NameOfTask);
            holder.subtitleTextView.setText(data.get(position).InfoOfTask);
            String combination = data.get(position).TaxPercentage + "%  "+data.get(position).TaxType;
            holder.locationTextView.setText(combination);
            holder.totalTextView.setText(data.get(position).CostOfTask+"");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


    public class UserBookingsCell extends RecyclerView.ViewHolder {
        TextView titletextView,subtitleTextView,locationTextView,totalTextView;
        View parentView;

        public UserBookingsCell(View itemView) {
            super(itemView);
            titletextView = (TextView)itemView.findViewById(R.id.cellTitle);
            subtitleTextView = (TextView)itemView.findViewById(R.id.cellSubtitle);
            locationTextView = (TextView)itemView.findViewById(R.id.cellTax);
            totalTextView = (TextView)itemView.findViewById(R.id.cellTotal);

            parentView = itemView.findViewById(R.id.userbookingsCellParent);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RegularServiceListing.this, "Initiate Booking", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        MoinUtils.getReference().showMessage(RegularServiceListing.this,msg);
    }


}
