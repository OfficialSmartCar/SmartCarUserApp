package com.moin.smartcar.ReportBreakdown;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.MyApplication;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import java.util.ArrayList;

public class BreakdownCategory extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<>();
    private RecyclerView myRecyclerView;
    private BreakDownAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_category);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.reportbreakdown));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navUserBookings navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), myToolbar, getResources().getString(R.string.reportbreakdown));


        getData();

        myRecyclerView = (RecyclerView)findViewById(R.id.breakdownRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new BreakDownAdapter(this);
        myRecyclerView.setAdapter(myAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
                mySingelton.successWebView = new WebView(MyApplication.getAppContext());
                mySingelton.successWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                mySingelton.successWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                if (Build.VERSION.SDK_INT >= 11) {
                    mySingelton.successWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                mySingelton.successWebView.getSettings().setJavaScriptEnabled(true);
                mySingelton.successWebView.loadUrl("file:///android_asset/index.html");
            }
        }).run();
    }

    private void getData(){
        data.add("Tyre Puncture");
        data.add("OverHeating");
        data.add("Car Not Starting");
        data.add("Out Of Fuel");
        data.add("Towing");
        data.add("Spare Wheel Change");
        data.add("Flat Battery");
        data.add("Other issue");
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
            startActivity(new Intent(BreakdownCategory.this, LoginNew.class));
        }else{
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateWithIndex(int index) {
        Intent myIntent = new Intent(BreakdownCategory.this, BreakdownHome.class);
        DataSingelton.getMy_SingeltonData_Reference().breakdownReason = data.get(index);
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    private class BreakDownAdapter extends RecyclerView.Adapter<BreakDownCell>{

        private LayoutInflater inflator;

        public BreakDownAdapter(Context context){
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public BreakDownCell onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.cell_faq_home, parent, false);
            BreakDownCell holder = new BreakDownCell(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BreakDownCell holder, int position) {
            holder.titletextView.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class BreakDownCell extends RecyclerView.ViewHolder {
        TextView titletextView;

        public BreakDownCell(View itemView) {
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
}
