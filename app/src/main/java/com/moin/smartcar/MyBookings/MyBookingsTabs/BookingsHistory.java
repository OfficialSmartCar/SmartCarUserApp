package com.moin.smartcar.MyBookings.MyBookingsTabs;


import android.content.Context;
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
public class BookingsHistory extends Fragment {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    FragmentManager my_FragmentManager;
    Context myContext;
    private RecyclerView myRecyclerView;
    private ArrayList<UpCommingStr> data = new ArrayList<>();
    private CircleRefreshLayout mRefreshLayout;
    private UpCommingBookingsAdapter myAdapter;
    private TextView messageTextView;
    private HistoryInterface my_HistoryInterface;

    public BookingsHistory() {
        // Required empty public constructor
    }

    public static BookingsHistory getInstance() {
        return (new BookingsHistory());
    }

    public void setMy_HistoryInterface(HistoryInterface my_HistoryInterface) {
        this.my_HistoryInterface = my_HistoryInterface;
    }

    public void updateData(ArrayList<UpCommingStr> serverData) {

        try {
            data = new ArrayList<>();
            for (int i = 0; i < serverData.size(); i++) {
                data.add(serverData.get(i));
            }
            myAdapter.notifyDataSetChanged();
            if (data.size() == 0) {
                messageTextView.setAlpha(1.0f);
            } else {
                messageTextView.setAlpha(0.0f);
            }
            completeRefresh();
        } catch (Exception e) {

        }
    }

    public void setfragmentManager(FragmentManager some_manager) {
        this.my_FragmentManager = some_manager;
    }

    public void setMyContext(Context myContext) {
        this.myContext = myContext;
    }

    @Override
    public void onResume() {
        super.onResume();
        myAdapter.notifyDataSetChanged();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookings_history, container, false);

        myRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRefreshLayout = (CircleRefreshLayout) view.findViewById(R.id.refresh_layout);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        myAdapter = new UpCommingBookingsAdapter(myContext);
        myRecyclerView.setAdapter(myAdapter);

        messageTextView = (TextView) view.findViewById(R.id.noInHistoryTextView);

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


        messageTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);

        return view;
    }

    private void getData() {
        my_HistoryInterface.requestDataHistoryScreen();
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

    public interface HistoryInterface {
        void requestDataHistoryScreen();

        void selectedHistory(UpCommingStr someStr);
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

            holder.carnameTextView.setText(data.get(position).carName);

            if (data.get(position).title.equalsIgnoreCase("Annual Maintenance Contract")) {
                holder.titletextView.setText(data.get(position).title + " " + data.get(position).serviceIndex);
            }

            if (data.get(position).isCancelled == 1) {
                holder.cancelledTextView.setAlpha(1.0f);
            } else {
                holder.cancelledTextView.setAlpha(0.0f);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class UpCommingBookingsCell extends RecyclerView.ViewHolder {
        TextView titletextView, dateTextView, timeTextView, costTextView, cancelledTextView, carnameTextView;

        public UpCommingBookingsCell(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.myBookingTitle);
            dateTextView = (TextView) itemView.findViewById(R.id.myBookingDate);
            timeTextView = (TextView) itemView.findViewById(R.id.myBookingTime);
            costTextView = (TextView) itemView.findViewById(R.id.myBookingCost);
            cancelledTextView = (TextView) itemView.findViewById(R.id.cancelledTextView);
            carnameTextView = (TextView) itemView.findViewById(R.id.carNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataSingelton.getMy_SingeltonData_Reference().UpCommingOrOther = false;
                    my_HistoryInterface.selectedHistory(data.get(getAdapterPosition()));
                }
            });

            titletextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            dateTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            timeTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            costTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            carnameTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
            cancelledTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);

        }
    }

}
