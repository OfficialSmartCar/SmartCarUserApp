package com.moin.smartcar.User;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileCarFragment extends Fragment {

    public carSectionInterface my_carSectionInterface;

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    private Context myContext;
    private FragmentManager mFragmentManager;

    private RecyclerView myRecyclerView;
    private ProfileCarListAdapter myAdapter;

    private ArrayList<CarInfoStr> data = new ArrayList<>();


    public ProfileCarFragment() {
        // Required empty public constructor
    }

    public void setMy_carSectionInterface(carSectionInterface my_carSectionInterface) {
        this.my_carSectionInterface = my_carSectionInterface;
    }

    public void setMyContext(Context myContext) {
        this.myContext = myContext;
    }

    public ProfileCarFragment getInstance() {
        return new ProfileCarFragment();
    }

    public void setmFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    private void getData() {
        data = new ArrayList<>();
        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        for (int i = 0; i < mySingelton.userCarList.size(); i++) {
            data.add(mySingelton.userCarList.get(i));
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_car, container, false);

        myRecyclerView = (RecyclerView) view.findViewById(R.id.profileCarRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        myAdapter = new ProfileCarListAdapter(myContext);
        myRecyclerView.setAdapter(myAdapter);
        return view;
    }

    public void reloadtable() {
        myAdapter.notifyDataSetChanged();
    }

    private void showDetailsOf(int index) {
        my_carSectionInterface.carSelected(data.get(index), index);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public interface carSectionInterface {
        void carSelected(CarInfoStr someStr, int index);
    }

    private class ProfileCarListAdapter extends RecyclerView.Adapter<ProfileCarCell> {

        private LayoutInflater inflator;

        public ProfileCarListAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public ProfileCarCell onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.profile_car_cell, parent, false);
            ProfileCarCell holder = new ProfileCarCell(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ProfileCarCell holder, int position) {
            CarInfoStr myStr = data.get(position);
            holder.carName.setText("Car Name : " + myStr.carName);
            holder.carBrand.setText("Brand : " + myStr.carBrand);
            holder.carModel.setText("Model : " + myStr.carModel);
            holder.carYearOfReg.setText("Year : " + myStr.yearOfMaufacture);
            holder.RegNumber.setText("Reg No. : " + myStr.carRegNo);
            holder.carVariant.setText("Fuel Type :" + myStr.carVariant);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class ProfileCarCell extends RecyclerView.ViewHolder {
        TextView carName, carBrand, carModel, carYearOfReg, RegNumber, carVariant;
        ImageView deleteImage;
        View mainView;
        View rootLayout;

        public ProfileCarCell(View itemView) {
            super(itemView);
            carName = (TextView) itemView.findViewById(R.id.carNameTextView);
            carBrand = (TextView) itemView.findViewById(R.id.carBrandNameTextView);
            carModel = (TextView) itemView.findViewById(R.id.carModelTextView);
            carYearOfReg = (TextView) itemView.findViewById(R.id.carYearOfManufactureTextView);
            RegNumber = (TextView) itemView.findViewById(R.id.carNumberTextView);
            carVariant = (TextView) itemView.findViewById(R.id.carVariantTextView);
            deleteImage = (ImageView) itemView.findViewById(R.id.deleteImage);
            mainView = itemView.findViewById(R.id.card_view);

            carName.setTypeface(mySingelton.myCustomTypeface);
            carBrand.setTypeface(mySingelton.myCustomTypeface);
            carModel.setTypeface(mySingelton.myCustomTypeface);
            carYearOfReg.setTypeface(mySingelton.myCustomTypeface);
            RegNumber.setTypeface(mySingelton.myCustomTypeface);
            carVariant.setTypeface(mySingelton.myCustomTypeface);


            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailsOf(getAdapterPosition());

                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.size() == 1) {
                        new AlertDialog.Builder(myContext)
                                .setTitle("Invalid Selection")
                                .setMessage("We need at least one car information to serve your queries")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        new AlertDialog.Builder(myContext)
                                .setTitle("Remove Car Confirmation")
                                .setMessage("Are you sure you want to delete this car ?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mySingelton.deletedCars.add(data.get(getAdapterPosition()));
                                        data.remove(getAdapterPosition());
                                        myAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
        }
    }
}
