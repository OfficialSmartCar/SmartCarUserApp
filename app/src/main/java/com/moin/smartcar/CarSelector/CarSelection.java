package com.moin.smartcar.CarSelector;


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

import com.moin.smartcar.Network.MyApplication;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarSelection extends Fragment {

    private FragmentManager myFragmentManager;
    private Context myContext;
    private RecyclerView myRecyclerView;
    private ArrayList<CarInfoStr>data = new ArrayList<>();
    private CarSelectionAdapter myAdapter;
    private carSelectedInterface my_carSelectedInterface;
    private TextView bottomMessage, carSelectionHeading;

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    public CarSelection() {
        // Required empty public constructor
    }

    public static CarSelection getInstance() {
        return new CarSelection();
    }

    public void setMy_carSelectedInterface(carSelectedInterface my_carSelectedInterface) {
        this.my_carSelectedInterface = my_carSelectedInterface;
    }

//    public void setMyContext(Context myContext) {
//        this.myContext = myContext;
//    }

    public void setMyFragmentManager(FragmentManager myFragmentManager) {
        this.myFragmentManager = myFragmentManager;
    }

    private void getData(){
        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        for (int i=0;i<mySingelton.userCarList.size();i++){
            data.add(mySingelton.userCarList.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_selection, container, false);
        myContext = MyApplication.getAppContext();
        getData();
        myRecyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        myAdapter = new CarSelectionAdapter(myContext);
        myRecyclerView.setAdapter(myAdapter);

        carSelectionHeading = (TextView) view.findViewById(R.id.carSelectionHeading);
        bottomMessage = (TextView) view.findViewById(R.id.bottomMessage);

        setFonts();

        return view;
    }

    private void setFonts(){
        carSelectionHeading.setTypeface(mySingelton.myCustomTypeface);
        bottomMessage.setTypeface(mySingelton.myCustomTypeface);

    }

    public void refreshHeader() {
        if (mySingelton.selectionOfScreen == 1) {
            bottomMessage.setText(" 3 more steps to confirm Regular Service");
            carSelectionHeading.setText("Select Your Car for Regular Service");
        } else {
            if (mySingelton.selectionOfScreen == 2) {
                bottomMessage.setText(" 3 more steps to confirm Custom Service");
                carSelectionHeading.setText("Select Your Car for Custom Service");
            } else {
                bottomMessage.setText(" 3 more steps to confirm AMC");
                carSelectionHeading.setText("Select Your Car for AMC");
            }
        }
    }

    private void carSelected(int index) {
        DataSingelton.getMy_SingeltonData_Reference().CarSelecetd = data.get(index);
        my_carSelectedInterface.carSelectedAtIndex(index);
    }

    public interface carSelectedInterface {
        void carSelectedAtIndex(int index);
    }

    private class CarSelectionAdapter extends RecyclerView.Adapter<CarSelectionHolder>{

        private LayoutInflater inflator;

        public CarSelectionAdapter(Context context){
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public CarSelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.car_selection_cell, parent, false);
            CarSelectionHolder holder = new CarSelectionHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CarSelectionHolder holder, int position) {
            holder.titletextView.setText(data.get(position).carName);
            holder.brand.setText(data.get(position).carBrand);
            holder.model.setText(data.get(position).carModel);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class CarSelectionHolder extends RecyclerView.ViewHolder {
        TextView titletextView,brand,model;

        public CarSelectionHolder(View itemView) {
            super(itemView);
            titletextView = (TextView)itemView.findViewById(R.id.carSelectionCellMainTitle);
            brand = (TextView)itemView.findViewById(R.id.carBrandNameTextView);
            model = (TextView)itemView.findViewById(R.id.carModelNameTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carSelected(getAdapterPosition());
                }
            });
            titletextView.setTypeface(mySingelton.myCustomTypeface);
            brand.setTypeface(mySingelton.myCustomTypeface);
            model.setTypeface(mySingelton.myCustomTypeface);

        }
    }

}
