package com.moin.smartcar.MyBookings;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.R;
import com.moin.smartcar.RegService.RegServicePopUp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class UserBookings extends AppCompatActivity implements RegServicePopUp.regularserviceInterface {

    private Toolbar toolbar;
    private ArrayList<String> data = new ArrayList<>();
    private RecyclerView myRecyclerView;
    private UserBookingsAdapter myAdapter;

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private View fragmentContainer;

    private RegServicePopUp fragmentPopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bookings);

        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Bookings");

        navUserBookings navFragment = (navUserBookings)getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), toolbar, "My Bookings");

        data = getData();
        myRecyclerView = (RecyclerView)findViewById(R.id.userBookingsRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new UserBookingsAdapter(this);
        myRecyclerView.setAdapter(myAdapter);


        fragmentContainer = findViewById(R.id.fragmentContainer);
        fragmentContainer.setVisibility(View.INVISIBLE);
//        fragmentContainer.setAlpha(0.0f);

        fragmentPopUp = (RegServicePopUp)getSupportFragmentManager().findFragmentById(R.id.fragment1);
        fragmentPopUp.setMyFragmentManager(getSupportFragmentManager());
        fragmentPopUp.setMyContext(this);
        fragmentPopUp.setMy_regularserviceInterface(this);
//        fragmentPopUp.

//        DialogFragment newFragment = new DatePickerFragment(date1FromPicker,1);
//        newFragment.show(my_FragmentManager, "datePicker");
    }

    private static ArrayList<String> getData(){
        ArrayList<String> myList = new ArrayList<>();
        myList.add("1");
        myList.add("1");
        myList.add("1");
        myList.add("1");
        myList.add("1");
        return  myList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(UserBookings.this, LoginNew.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void closeTheFragment() {

        fragmentContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initiateBooking(String dateTime) {
        Toast.makeText(this,"InitiateBooking",Toast.LENGTH_SHORT).show();
    }

    private class UserBookingsAdapter extends RecyclerView.Adapter<UserBookingsCell>{

        private LayoutInflater inflator;

        public UserBookingsAdapter(Context context){
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
//            titletextView = (TextView) itemView.findViewById(R.id.nav_view_row_textView);
//            titletextView = (TextView) itemView.findViewById(R.id.nav_view_row_textView);
//            titletextView = (TextView) itemView.findViewById(R.id.nav_view_row_textView);
//            titletextView = (TextView) itemView.findViewById(R.id.nav_view_row_textView);

            parentView = itemView.findViewById(R.id.userbookingsCellParent);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    AlphaAnimation anim = new AlphaAnimation(0.0f,1.0f);
//                    anim.setDuration(500);
//                    fragmentContainer.startAnimation(anim);

                    fragmentContainer.setVisibility(View.VISIBLE);
                    fragmentPopUp.ScrollToTop();
                }
            });
        }
    }




}
