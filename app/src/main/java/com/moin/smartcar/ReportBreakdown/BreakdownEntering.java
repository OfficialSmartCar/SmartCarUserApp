package com.moin.smartcar.ReportBreakdown;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.moin.smartcar.Custom.LocationAddress;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.LinkedHashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BreakdownEntering extends AppCompatActivity {

    @Bind(R.id.checkBox)
    AnimCheckBox checkBox;
    private DataSingelton mySingelton  =DataSingelton.getMy_SingeltonData_Reference();
    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;
    private EditText contactNumberEditText,alternateContactNumberEditText,addressEditText,commentsEditText;
    private Button sendBreakdownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_entering);

        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Enter Location");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkBox.setChecked(true, false);

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();

        contactNumberEditText = (EditText)findViewById(R.id.phoneNumberEditText);
        alternateContactNumberEditText = (EditText)findViewById(R.id.alternatephoneNumberEditText);
        addressEditText = (EditText)findViewById(R.id.LocationEditText);
        commentsEditText = (EditText)findViewById(R.id.commentsEditText);

        contactNumberEditText.setText(mySingelton.mobileNumber);
        contactNumberEditText.setEnabled(false);

        sendBreakdownButton = (Button)findViewById(R.id.sendBreakdownButton);

        if (mySingelton.myLat != 0.0){
            showLoadingView();
            getAddress();
        }

        sendBreakdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndUpdate();
            }
        });
    }

    @OnClick(R.id.termsOfUseTextFeild)
    void showTermsOfUse(View view) {
        String url = "ww.google.com";
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void validateAndUpdate(){

        if (mySingelton.myLat == 0.0){
            if (addressEditText.getText().toString().length() == 0){
                MoinUtils.getReference().showMessage(BreakdownEntering.this, "Please Enter Address");
            }
        }

        if (commentsEditText.getText().toString().length() == 0){
            MoinUtils.getReference().showMessage(BreakdownEntering.this, "Please Enter Some Comments");
            commentsEditText.requestFocus();
            commentsEditText.append("");
            return;
        }

        Toast.makeText(BreakdownEntering.this,"Start Sending Data",Toast.LENGTH_LONG).show();



    }

    private void getAddress(){
        double latitude = mySingelton.myLat;
        double longitude = mySingelton.myLong;
        LocationAddress locationAddress = new LocationAddress();
        LocationAddress.getAddressFromLocation(latitude, longitude,
                getApplicationContext(), new GeocoderHandler());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            try {
                String add = locationAddress.substring(locationAddress.lastIndexOf("Address:"));
                String[] temp123_moin = add.split("\n");
                char c = temp123_moin[1].charAt(0);
                Boolean isDigit = (c >= '0' && c <= '9');
                String finalStringSending = "";
                if (isDigit) {
                    for (int i = 1; i < temp123_moin.length; i++) {
                        if (i == 1) {
                            String first1 = temp123_moin[i].replaceAll("[0-9]", "");
                            finalStringSending = first1;
                        } else {
                            if (!temp123_moin[i].toString().equalsIgnoreCase("null")) {
                                finalStringSending = finalStringSending + ", " + temp123_moin[i];
                            }
                        }
                    }
                } else {
                    for (int i = 1; i < temp123_moin.length; i++) {
                        if (i == 1) {
                            finalStringSending = temp123_moin[i];
                        } else {
                            finalStringSending = finalStringSending + ", " + temp123_moin[i];
                        }
                    }
                }

                String[] duplicate = finalStringSending.split(", ");
                String moinAddress = "";

                Set<String> hs = new LinkedHashSet<>();
                for (int i = 0; i < duplicate.length; i++) {
                    hs.add(duplicate[i]);
                }
                int checkmoin = 0;
                String[] setMoin = null;
                setMoin = hs.toArray(new String[hs.size()]);
                for (String s : setMoin) {
                    if (!s.equalsIgnoreCase("null")) {
                        if (checkmoin == 0) {
                            moinAddress = s;
                        } else {
                            if (moinAddress.length() == 0) {
                                moinAddress = s;
                            } else {
                                if (moinAddress.indexOf(s) == -1) {
                                    moinAddress = moinAddress + ", " + s;
                                }
                            }
                        }
                        checkmoin = 1;
                    }
                }


                mySingelton.address = moinAddress;


                addressEditText.setText(moinAddress);
                hideLoadingView();
            } catch (Exception e) {
                Toast.makeText(BreakdownEntering.this, "No Address Found", Toast.LENGTH_SHORT).show();
                addressEditText.setText("Location Not Found");
                hideLoadingView();
            }
        }
    }
}
