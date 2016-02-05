package com.moin.smartcar.NavDrawer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moin.smartcar.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by macpro on 12/15/15.
 */

public class NavigationDrawerRecyclerViewAdapter extends RecyclerView.Adapter<NavigationDrawerRecyclerViewAdapter.MyViewHolder> {

    List<String> data = Collections.emptyList();
    private LayoutInflater inflator;
    private MyClickListener var_ClickListener;
    private Context myContext;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public NavigationDrawerRecyclerViewAdapter(Context context, List<String> data) {
        inflator = LayoutInflater.from(context);
        myContext = context;
        this.data = data;
    }



    public void setMyClickListener(MyClickListener clickListener){
        this.var_ClickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String myData = data.get(position);
        holder.textView.setText(myData);
        if (title.equalsIgnoreCase(myData)){
            holder.parentView.setBackgroundResource(R.color.navBarSelectedBackground);
            holder.selection.setAlpha(1.0f);
        }
        else{
            holder.parentView.setBackgroundColor(Color.TRANSPARENT);
            holder.selection.setAlpha(0.0f);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View parentView,selection;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.nav_view_row_textView);
            parentView = itemView.findViewById(R.id.cellBackground);
            selection = itemView.findViewById(R.id.selectorView);
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    var_ClickListener.ItemClicked(v,getAdapterPosition());
                }
            });
        }
    }

    public interface MyClickListener{
        public void ItemClicked(View view,int position);
    }
}