package com.moin.smartcar.MyBookings.MyBookingsTabs;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moin.smartcar.MyBookings.DataStr.UpCommingStr;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpCommingBookings extends Fragment {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    FragmentManager my_FragmentManager;
    Context myContext;
    TextView messageTextView;
    private RecyclerView myRecyclerView;
    private ArrayList<UpCommingStr> data = new ArrayList<>();
    private CircleRefreshLayout mRefreshLayout;
    private UpCommingBookingsAdapter myAdapter;
    private UpCommingInterface my_UpCommingInterface;

    public UpCommingBookings() {
        // Required empty public constructor
    }

    public static UpCommingBookings getInstance() {
        return (new UpCommingBookings());
    }

    public void setMy_UpCommingInterface(UpCommingInterface my_UpCommingInterface) {
        this.my_UpCommingInterface = my_UpCommingInterface;
    }

    public void setfragmentManager(FragmentManager some_manager) {
        this.my_FragmentManager = some_manager;
    }

    public void setMyContext(Context myContext) {
        this.myContext = myContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_up_comming_bookings, container, false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRefreshLayout = (CircleRefreshLayout) view.findViewById(R.id.refresh_layout);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        myAdapter = new UpCommingBookingsAdapter(myContext);
        myRecyclerView.setAdapter(myAdapter);

        messageTextView = (TextView) view.findViewById(R.id.noInUpCommingTextView);

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

        setFonts();

        return view;
    }

    private void setFonts(){
        messageTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
    }


    public void completeRefresh() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.finishRefreshing();
            }
        };
        worker.schedule(task, 1000, TimeUnit.MILLISECONDS);
    }

    public void updateData(ArrayList<UpCommingStr> serverData) {
        data = new ArrayList<>();
        for (int i = 0; i < serverData.size(); i++) {
            data.add(serverData.get(i));
        }

        if (data.size() == 0) {
            messageTextView.setAlpha(1.0f);
        } else {
            messageTextView.setAlpha(0.0f);
        }

        myAdapter.notifyDataSetChanged();
        completeRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data.size() == 0) {
            messageTextView.setAlpha(1.0f);
        } else {
            messageTextView.setAlpha(0.0f);
        }
        try{
            myAdapter.notifyDataSetChanged();
        }catch (Exception e){

        }
    }

    private void getData() {
        my_UpCommingInterface.requestDataUpCommingScreen();
    }

    public interface UpCommingInterface {
        void requestDataUpCommingScreen();

        void selectedUpCommingBooking(UpCommingStr someStr);
    }

    private class UpCommingBookingsAdapter extends RecyclerView.Adapter<UpCommingBookingsCell> {

        private LayoutInflater inflator;

        public UpCommingBookingsAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public UpCommingBookingsCell onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.upcomming_cell, parent, false);
            UpCommingBookingsCell holder = new UpCommingBookingsCell(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(UpCommingBookingsCell holder, int position) {
            holder.titletextView.setText(data.get(position).title);
            holder.dateTextView.setText(data.get(position).date);
            holder.timeTextView.setText(data.get(position).time);
            holder.costTextView.setText("Invoice Value : " + data.get(position).cost + "/-");

            if (data.get(position).title.equalsIgnoreCase("Annual Maintenance Contract")) {
                holder.titletextView.setText(data.get(position).title + " " + data.get(position).serviceIndex);
            }

            holder.carnameTextView.setText(data.get(position).carName);

            holder.cell_background.setBackgroundColor(Color.WHITE);
            if (data.get(position).isActive != 1) {
                holder.cell_background.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class UpCommingBookingsCell extends RecyclerView.ViewHolder {
        TextView titletextView, dateTextView, timeTextView, costTextView, carnameTextView;
        View cell_background;

        public UpCommingBookingsCell(View itemView) {
            super(itemView);
            cell_background = itemView.findViewById(R.id.cell_background);
            titletextView = (TextView) itemView.findViewById(R.id.myBookingTitle);
            dateTextView = (TextView) itemView.findViewById(R.id.myBookingDate);
            timeTextView = (TextView) itemView.findViewById(R.id.myBookingTime);
            costTextView = (TextView) itemView.findViewById(R.id.myBookingCost);
            carnameTextView = (TextView) itemView.findViewById(R.id.carNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(getAdapterPosition()).isActive == 1) {
                        DataSingelton.getMy_SingeltonData_Reference().UpCommingOrOther = true;
                        my_UpCommingInterface.selectedUpCommingBooking(data.get(getAdapterPosition()));
                    }
                }
            });

            titletextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            dateTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            timeTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            costTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            carnameTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);

        }
    }
}
