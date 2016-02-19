package com.moin.smartcar.NavDrawer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import java.util.Collections;
import java.util.List;

/**
 * Created by macpro on 12/15/15.
 */

public class NavigationDrawerRecyclerViewAdapter extends RecyclerView.Adapter {

    List<String> data = Collections.emptyList();
    private LayoutInflater inflator;
    private MyClickListener var_ClickListener;
    private Context myContext;
    private String title;

    public NavigationDrawerRecyclerViewAdapter(Context context, List<String> data) {
        inflator = LayoutInflater.from(context);
        myContext = context;
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMyClickListener(MyClickListener clickListener){
        this.var_ClickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0) {
            return 0;
        }
        if (position == 0) {
            return 0;
        }
        return 1;
    }

//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = inflator.inflate(R.layout.nav_drawer_row, parent, false);
//        MyViewHolder holder = new MyViewHolder(view);
//        return holder;
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0:
                View view1 = inflator.inflate(R.layout.welcome_message, parent, false);
                MyViewHolder2 holder1 = new MyViewHolder2(view1);
                return holder1;
            case 1:
                View view = inflator.inflate(R.layout.nav_drawer_row, parent, false);
                MyViewHolder holder = new MyViewHolder(view);
                return holder;
        }
        return null;
    }

//    @Override
//    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        String myData = data.get(position);
//        holder.textView.setText(myData);
//        if (title.equalsIgnoreCase(myData)){
//            holder.parentView.setBackgroundResource(R.color.navBarSelectedBackground);
//            holder.selection.setAlpha(1.0f);
//        }
//        else{
//            holder.parentView.setBackgroundColor(Color.TRANSPARENT);
//            holder.selection.setAlpha(0.0f);
//        }
//        switch (position){
//            case 0 : holder.navIcon.setImageResource(R.drawable.mybooking1);break;
//            case 1 : holder.navIcon.setImageResource(R.drawable.regular1);break;
//            case 2 : holder.navIcon.setImageResource(R.drawable.customservice1);break;
//            case 3 : holder.navIcon.setImageResource(R.drawable.dentpaint1);break;
//            case 4 : holder.navIcon.setImageResource(R.drawable.breakdown1);break;
//            case 5 : holder.navIcon.setImageResource(R.drawable.amc1);break;
//            case 6 : holder.navIcon.setImageResource(R.drawable.notification1);break;
//            case 7 : holder.navIcon.setImageResource(R.drawable.profile1);break;
//            case 8 : holder.navIcon.setImageResource(R.drawable.support1);break; //
//            case 9 : holder.navIcon.setImageResource(R.drawable.terms1);break;
//            case 10 : holder.navIcon.setImageResource(R.drawable.share1);break;
//            case 11 : holder.navIcon.setImageResource(R.drawable.rate1);break;
//            case 12 : holder.navIcon.setImageResource(R.drawable.logout1);break;
//        }
//    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case 0:
                MyViewHolder2 holder1 = (MyViewHolder2) holder;
                holder1.textView.setText(DataSingelton.getMy_SingeltonData_Reference().userName);
                break;

            case 1:
                MyViewHolder holder2 = (MyViewHolder) holder;

                int myPosition = position - 1;

                String myData = data.get(myPosition);
                holder2.textView.setText(myData);
                if (title.equalsIgnoreCase(myData)) {
                    holder2.parentView.setBackgroundResource(R.color.navBarSelectedBackground);
                    holder2.selection.setAlpha(1.0f);
                } else {
                    holder2.parentView.setBackgroundColor(Color.TRANSPARENT);
                    holder2.selection.setAlpha(0.0f);
                }
                switch (myPosition) {
                    case 0:
                        holder2.navIcon.setImageResource(R.drawable.mybooking1);
                        break;
                    case 1:
                        holder2.navIcon.setImageResource(R.drawable.regular1);
                        break;
                    case 2:
                        holder2.navIcon.setImageResource(R.drawable.customservice1);
                        break;
                    case 3:
                        holder2.navIcon.setImageResource(R.drawable.dentpaint1);
                        break;
                    case 4:
                        holder2.navIcon.setImageResource(R.drawable.breakdown1);
                        break;
                    case 5:
                        holder2.navIcon.setImageResource(R.drawable.amc1);
                        break;
                    case 6:
                        holder2.navIcon.setImageResource(R.drawable.notification1);
                        break;
                    case 7:
                        holder2.navIcon.setImageResource(R.drawable.profile1);
                        break;
                    case 8:
                        holder2.navIcon.setImageResource(R.drawable.support1);
                        break; //
                    case 9:
                        holder2.navIcon.setImageResource(R.drawable.terms1);
                        break;
                    case 10:
                        holder2.navIcon.setImageResource(R.drawable.share1);
                        break;
                    case 11:
                        holder2.navIcon.setImageResource(R.drawable.rate1);
                        break;
                    case 12:
                        holder2.navIcon.setImageResource(R.drawable.logout1);
                        break;
                }
        }

    }

    @Override
    public int getItemCount() {
        return (data.size() + 1);
    }

    public interface MyClickListener {
        void ItemClicked(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View parentView,selection;
        ImageView navIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.nav_view_row_textView);
            parentView = itemView.findViewById(R.id.cellBackground);
            selection = itemView.findViewById(R.id.selectorView);
            navIcon = (ImageView) itemView.findViewById(R.id.navIcon);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition() - 1;
                    var_ClickListener.ItemClicked(v, position);
                }
            });
        }
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView textView;


        public MyViewHolder2(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView123);
        }
    }
}