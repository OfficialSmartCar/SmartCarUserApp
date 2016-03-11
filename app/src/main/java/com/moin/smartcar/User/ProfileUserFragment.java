package com.moin.smartcar.User;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUserFragment extends Fragment {


    private Context myContext;
    private FragmentManager mFragmentManager;


    private EditText userName, emailId, mobileTextView, addressTextView;

    public ProfileUserFragment() {
        // Required empty public constructor
    }

    public void setMyContext(Context myContext) {
        this.myContext = myContext;
    }

    public void setmFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    public ProfileUserFragment getInstance() {
        return new ProfileUserFragment();
    }

    private void setFonts(){
        userName.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        emailId.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        mobileTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        addressTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);

        userName = (EditText) view.findViewById(R.id.userNameEditText);
        emailId = (EditText) view.findViewById(R.id.userEmailEditText);
        mobileTextView = (EditText) view.findViewById(R.id.userMobileNumberEditText);
        addressTextView = (EditText) view.findViewById(R.id.userAddressEditText);

        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        userName.setText(mySingelton.userName);
        emailId.setText(mySingelton.userEmailId);
        mobileTextView.setText(mySingelton.mobileNumber);
        addressTextView.setText(mySingelton.address);

        mySingelton.backUpName = mySingelton.userName;
        mySingelton.backUpAdress = mySingelton.address;
        mySingelton.backUpMobileNUmber = mySingelton.mobileNumber;

        mobileTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 10) {
                    addressTextView.requestFocus();
                    addressTextView.append("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setFonts();

        return view;
    }

    public void reloadData(){
        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        userName.setText(mySingelton.backUpName);
        emailId.setText(mySingelton.userEmailId);
        mobileTextView.setText(mySingelton.backUpMobileNUmber);
        addressTextView.setText(mySingelton.backUpAdress);

        mySingelton.userName = mySingelton.backUpName;
        mySingelton.address = mySingelton.backUpAdress;
        mySingelton.mobileNumber = mySingelton.backUpMobileNUmber;
    }

    private void changeColorOfToTransparent(View view) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundColor((Color.TRANSPARENT));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private void changeColorOfToRed(View view) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(getResources().getDrawable(R.color.gradientBackColor));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(getResources().getDrawable(R.color.gradientBackColor));
            }
        }
    }


    public int getData() {

        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

        int check = validateAllDetaild();

        if (check <  0) {
            return check;
        }

        mySingelton.userName = userName.getText().toString();
        mySingelton.userEmailId = emailId.getText().toString();
        mySingelton.mobileNumber = mobileTextView.getText().toString();
        mySingelton.address = addressTextView.getText().toString();
        return 1;

    }

    private int validateAllDetaild() {
        if (userName.getText().toString().length() == 0) {
            return -1;
        }
        if (mobileTextView.getText().toString().length() == 0) {
            return -2;
        }
        if (addressTextView.getText().toString().length() == 0) {
            return -3;
        }
        try {
            Double mobilenumber = Double.parseDouble(mobileTextView.getText().toString());
        } catch (Exception e) {
            return -4;
        }

        if (mobileTextView.getText().toString().length() < 10 || mobileTextView.getText().toString().length() > 10){
            return -5;
        }
        return 1;
    }
}
