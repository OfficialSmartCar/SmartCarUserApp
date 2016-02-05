package com.moin.smartcar.Support;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.OwnServices.OwnServiceStr;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FAQHome extends AppCompatActivity {

    private ArrayList<FaqStr> data = new ArrayList<>();

    @Bind(R.id.faqParentRecycler)RecyclerView myRecyclerView;
    private FAQHomeAdapter myAdapter;

    @Bind(R.id.loadignView) View loadingView;
    @Bind(R.id.loadingIndicator) AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqhome);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("FAQ's");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new FAQHomeAdapter(this);
        myRecyclerView.setAdapter(myAdapter);

        showLoadingView();
    }

    private void getData(){

        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

        JsonObjectRequest getServcesList = new JsonObjectRequest(Request.Method.GET, mySingelton.getSupportInformation,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");

                            if (!status.equalsIgnoreCase("Error")){
                                parseServerResponse(response);
                            }else{
                                hideLoadingViewWithMessage(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingViewWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingViewWithMessage("There was some problem please try again");
                    }
                }

        );
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getServcesList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getServcesList);

    }

    private void parseServerResponse(JSONObject object){
        try{
            data = new ArrayList<>();
            JSONArray arr = object.getJSONArray("arr");
            for (int i=0;i<arr.length();i++){
                JSONObject myObj = arr.getJSONObject(i);
                FaqStr curr = new FaqStr();
                curr.mainTitle = myObj.get("mainTitle").toString();
                curr.subTitle = new ArrayList<>();
                curr.internalStructire = new ArrayList<>();
                JSONArray arr1 = myObj.getJSONArray("subTitle");
                for (int j=0;j<arr1.length();j++){
                    curr.subTitle.add(arr1.get(j).toString());
                }
                JSONArray arr2 = myObj.getJSONArray("internalStructire");
                for (int j=0;j<arr2.length();j++){
                    curr.internalStructire.add(arr2.get(j).toString());
                }
                data.add(curr);
            }
            myAdapter.notifyDataSetChanged();
            hideLoadingView();
        }catch (Exception e){
            hideLoadingViewWithMessage("Error In Server Communication");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private class FAQHomeAdapter extends RecyclerView.Adapter<FaqHomeHolder>{

        private LayoutInflater inflator;

        public FAQHomeAdapter(Context context){
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public FaqHomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.cell_faq_home, parent, false);
            FaqHomeHolder holder = new FaqHomeHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(FaqHomeHolder holder, int position) {
            holder.titletextView.setText(data.get(position).mainTitle);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class FaqHomeHolder extends RecyclerView.ViewHolder {
        TextView titletextView;

        public FaqHomeHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.faqTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateWithIndex(getAdapterPosition());
                }
            });
        }
    }

    private void navigateWithIndex(int index){
        Intent myIntent = new Intent(FAQHome.this,FAQPartTwo.class);
        myIntent.putExtra("FAQSelection",data.get(index));
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    private void showLoadingView(){
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView(){
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void hideLoadingViewWithMessage(String msg){
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        MoinUtils.getReference().showMessage(FAQHome.this, msg);
    }

    private void showMessage(String msg){
        MoinUtils.getReference().showMessage(FAQHome.this,msg);

    }

}
