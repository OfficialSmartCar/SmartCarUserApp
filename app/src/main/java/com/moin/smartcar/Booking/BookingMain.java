package com.moin.smartcar.Booking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class BookingMain extends AppCompatActivity {

    @Bind(R.id.sourceTitleTextView) TextView sourceTitleTextView;
    @Bind(R.id.payableAmmountTextView) TextView payableAmmountTextView;
    @Bind(R.id.horizontalScroll) HorizontalScrollView scrollviewParent;
    @Bind(R.id.CarSelectionAutoComplete) AutoCompleteTextView carSelectionAutoComplete;
    @Bind(R.id.phoneNumberEditText) EditText phoneNumberEditText;
    @Bind(R.id.alternatephoneNumberEditText) EditText alternatephoneNumberEditText;
    @Bind(R.id.LocationEditText) EditText LocationEditText;
    @Bind(R.id.dateTextView) TextView dateTextView;
    @Bind(R.id.changeDateTextView) TextView changeDateTextView;
    @Bind(R.id.time_recycler) RecyclerView myRecycler;
    @Bind(R.id.loadingIndicator) AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.loadignView) View loadingView;
    @Bind(R.id.carNameTextView) TextView carNameTextView;
    @Bind(R.id.proceedToPaymentButton)Button proceedToPaymentButton;

    ArrayList<String> userCarNames = new ArrayList<>();
    private TimeAdapter myAdapter;
    private ArrayAdapter<String> carNameAdapter;
    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
    private ArrayList<TimeStr> data = new ArrayList<>();

    private ArrayList<String> topServicesList = new ArrayList<>();
    private ArrayList<String> ServiceIdentifiersSelected = new ArrayList<>();

    private String selectedServiceString = "";

    public final int RESULT_FAILED = 90;
    public final int RESULT_BACK = 8;

    private String keyForPayU = "0ut3csFe";
    private String merchantIdForPayU = "5419473";
    private String productNameForPayU = "";
    private String saltKeyFromPayU = "KkZobfRBws";
    private String failureUrlPayU = "http://184.95.55.236:8080/SmartCar/paymentfail";
    private String successUrlPayU = "http://184.95.55.236:8080/SmartCar/paymentsuccess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_main);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hideLoadingView();


        payableAmmountTextView.setText("Payable Amount : " + mySingelton.AmmountToPay);

        checkSource();

        addTopViews();
        prefillAllData();

        changeDateTextView.setAlpha(0f);

        getData();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        myRecycler.setLayoutManager(staggeredGridLayoutManager);
        myAdapter = new TimeAdapter(BookingMain.this);
        myRecycler.setAdapter(myAdapter);

        carNameTextView.setText("for " + mySingelton.CarSelecetd.carName);

        setFonts();

    }

    private void setFonts(){
        payableAmmountTextView.setTypeface(mySingelton.myCustomTypeface);
        sourceTitleTextView.setTypeface(mySingelton.myCustomTypeface);
        carNameTextView.setTypeface(mySingelton.myCustomTypeface);
        dateTextView.setTypeface(mySingelton.myCustomTypeface);
        changeDateTextView.setTypeface(mySingelton.myCustomTypeface);
        carSelectionAutoComplete.setTypeface(mySingelton.myCustomTypeface);
        phoneNumberEditText.setTypeface(mySingelton.myCustomTypeface);
        alternatephoneNumberEditText.setTypeface(mySingelton.myCustomTypeface);
        proceedToPaymentButton.setTypeface(mySingelton.myCustomTypeface);
        LocationEditText.setTypeface(mySingelton.myCustomTypeface);

    }

    private void checkSource() {
        ServiceIdentifiersSelected = new ArrayList<>();
        switch (mySingelton.paymentSelection) {
            case 1:
                productNameForPayU = "Regular Service";
                sourceTitleTextView.setText("Initiate "+"Regular Service Booking");
                topServicesList = new ArrayList<>();
                for (int i = 0; i < mySingelton.regularServiceSelection.size(); i++) {
                    topServicesList.add(mySingelton.regularServiceSelection.get(i).NameOfTask);
                    if (i == 0) {
                        ServiceIdentifiersSelected.add(mySingelton.regularServiceSelection.get(i).TaskId);
                    } else {
                        ServiceIdentifiersSelected.add("," + mySingelton.regularServiceSelection.get(i).TaskId);
                    }
                }
                break;
            case 2:
                productNameForPayU = "Custom Service";
                sourceTitleTextView.setText("Initiate "+"Custom Service Booking");
                topServicesList = new ArrayList<>();
                for (int i = 0; i < mySingelton.customServiceSelection.size(); i++) {
                    topServicesList.add(mySingelton.customServiceSelection.get(i).title);
                    if (i == 0) {
                        ServiceIdentifiersSelected.add(mySingelton.customServiceSelection.get(i).taskId);
                    } else {
                        ServiceIdentifiersSelected.add("," + mySingelton.customServiceSelection.get(i).taskId);
                    }
                }
                break;
            case 3:
                productNameForPayU = "Annual Maintenance Service";
                sourceTitleTextView.setText("Initiate "+"Annual Maintenance Contract Booking");
                topServicesList = new ArrayList<>();
                for (int i = 0; i < mySingelton.amcServiceSelection.size(); i++) {
                    topServicesList.add(mySingelton.amcServiceSelection.get(i).NameOfTask);
                    if (i == 0) {
                        ServiceIdentifiersSelected.add(mySingelton.amcServiceSelection.get(i).TaskId);
                    } else {
                        ServiceIdentifiersSelected.add("," + mySingelton.amcServiceSelection.get(i).TaskId);
                    }
                }
                break;
        }
    }

    private void getData() {
        data = new ArrayList<>();
        TimeStr str = new TimeStr();
        str.time = "9:00 AM";
        str.active = true;
        data.add(str);
        String[] titles = getResources().getStringArray(R.array.timeSlots);
        for (int i = 0; i < titles.length; i++) {
            TimeStr str1 = new TimeStr();
            str1.time = titles[i];
            str1.active = false;
            data.add(str1);
        }
    }

    @OnClick(R.id.dateSelectionView)
    void selectDates(View view) {
        DialogFragment newFragment = new DatePickerFragment(dateTextView, changeDateTextView, BookingMain.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void doNothing(View view) {
        return;
    }

    private void prefillAllData() {

        userCarNames = new ArrayList<>();
        for (int i = 0; i < mySingelton.userCarList.size(); i++) {
            userCarNames.add(mySingelton.userCarList.get(i).carName);
        }
        carNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userCarNames);
        carSelectionAutoComplete.setAdapter(carNameAdapter);
        carSelectionAutoComplete.setThreshold(1);

        try {
            carSelectionAutoComplete.setText(mySingelton.CarSelecetd.carName);
        } catch (Exception e) {
//            carSelectionAutoComplete.setText("No Car Selected");
        }

        phoneNumberEditText.setText(mySingelton.mobileNumber);
        LocationEditText.setText(mySingelton.address);

    }

    private void addTopViews() {
        LinearLayout topLinearLayout = new LinearLayout(this);
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < topServicesList.size(); i++) {
            final TextView textView = new TextView(this);
            textView.setTag(i);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            if (i == (topServicesList.size() - 1)) {
                selectedServiceString += topServicesList.get(i);
                textView.setText(topServicesList.get(i));
            } else {
                selectedServiceString += topServicesList.get(i) + ", ";
                textView.setText(topServicesList.get(i) + ", ");
            }
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);
            textView.setPadding(10, 10, 10, 10);
            textView.setTypeface(null, Typeface.BOLD);
            topLinearLayout.addView(textView);
        }
        scrollviewParent.addView(topLinearLayout);
    }

    private void timeSlotSelected(int index) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).active = false;
        }
        data.get(index).active = true;
        myAdapter.notifyDataSetChanged();
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(BookingMain.this, msg);
    }

    private int checkAllValidations() {
        if (dateTextView.getText().toString().equalsIgnoreCase("Select Date")) {
            DialogFragment newFragment = new DatePickerFragment(dateTextView, changeDateTextView, BookingMain.this);
            newFragment.show(getSupportFragmentManager(), "datePicker");
            showMessage("Please select a desired date");
            return 0;
        }
        if (carSelectionAutoComplete.getText().toString().length() == 0) {
            carSelectionAutoComplete.requestFocus();
            carSelectionAutoComplete.append("");
            showMessage("Please select a car");
            return 0;
        }
        if (LocationEditText.getText().toString().length() == 0) {
            LocationEditText.requestFocus();
            LocationEditText.append("");
            showMessage("Please enter your address");
            return 0;
        }

        int found = 0;
        for (int i = 0; i < mySingelton.userCarList.size(); i++) {
            if (carSelectionAutoComplete.getText().toString().equalsIgnoreCase(mySingelton.userCarList.get(i).carName)) {
                found = 1;
            }
        }

        if (found == 0) {
            showMessage("Please select a car");
            carSelectionAutoComplete.requestFocus();
            carSelectionAutoComplete.append("");
            return 0;
        }

        return 1;
    }



    @OnClick(R.id.proceedToPaymentButton) void proceed(View view) {
        if (checkAllValidations() == 0) {
            return;
        }

        checkIfCanBook();

//        makePayment();

//        JSONObject params = new JSONObject();
//        try {
//
//            String timeSelected = "";
//
//            for (int i = 0; i < data.size(); i++) {
//                if (data.get(i).active) {
//                    timeSelected = data.get(i).time;
//                }
//            }
//
//            String serviceIdentifiersStrign = "";
//
//            for (int i = 0; i < ServiceIdentifiersSelected.size(); i++) {
//                if (i == 0) {
//                    serviceIdentifiersStrign = ServiceIdentifiersSelected.get(i);
//                } else {
//                    serviceIdentifiersStrign += ServiceIdentifiersSelected.get(i);
//                }
//            }
//
//            String truncated = sourceTitleTextView.getText().toString().replaceAll("Initiate ","");
//            truncated = truncated.replaceAll(" Booking","");
//
//            params.put("type", truncated);
//            params.put("date", dateTextView.getText().toString());
//            params.put("time", timeSelected);
//            params.put("userName", mySingelton.userName);
//            params.put("userId", mySingelton.userId);
//            params.put("userPhoneNumber", phoneNumberEditText.getText().toString());
//            params.put("userAlternateNumber", alternatephoneNumberEditText.getText().toString());
//            params.put("userAddress", LocationEditText.getText().toString());
//            params.put("userCarId", mySingelton.CarSelecetd.carId);
//            params.put("servicesSelected", selectedServiceString);
//            params.put("servicesId", serviceIdentifiersStrign);
//            params.put("cost", mySingelton.AmmountToPay + "");
//            params.put("userCarName", mySingelton.CarSelecetd.carName);
//            params.put("userCarBrand", mySingelton.CarSelecetd.carBrand);
//            params.put("userCarModel", mySingelton.CarSelecetd.carModel);
//            params.put("userCarFuelType", mySingelton.CarSelecetd.carVariant);
//            params.put("userNotificationId", mySingelton.UserNotificationToken);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        JsonObjectRequest bookingRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.booking, params,
//
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String status = response.getString("Status");
//                            String message = response.getString("ErrorMessage");
//                            if (!status.equalsIgnoreCase("Error")) {
//                                hideLoadingView();
//                                startActivity(new Intent(BookingMain.this, BookingSuccess.class));
//                                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
//                            } else {
//                                hideLoadingWithMessage(message);
//                            }
//                            hideLoadingView();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            hideLoadingWithMessage("There was some problem please try again");
//                        }
//                    }
//                },
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        hideLoadingWithMessage("You Are Offline");
//                    }
//                }
//        );
//        showLoadingView();
//        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        bookingRequest.setRetryPolicy(policy);
//        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(bookingRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingWithMessage(String msg) {
        hideLoadingView();
        MoinUtils.getReference().showMessage(BookingMain.this, msg);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView passedTextView;
        private TextView backUpTextView;
        private Context myContext;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(TextView someTextView, TextView backUpTextView, Context context) {
            this.passedTextView = someTextView;
            this.backUpTextView = backUpTextView;
            this.myContext = context;
        }

        public DatePickerFragment() {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

//            String settingDate = day+"/"+(month+1)+"/20"+year;
            String dateMoin = day + "/" + (month + 1) + "/" + year;
            String initialSplit = dateMoin;
            String[] arr = initialSplit.split(":");
            String temp123 = arr[0].toString();
            String[] arr2 = temp123.split("/");
            int dateRecieved = Integer.parseInt(arr2[0]);
            int monthRecieved = Integer.parseInt(arr2[1]);
            int yearRecieved = Integer.parseInt(arr2[2]);

            String TodaysDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String[] arr12 = TodaysDate.split("-");
            int todaysDate = Integer.parseInt(arr12[0]);
            int todaysMonth = Integer.parseInt(arr12[1]);
            int todaysYear = Integer.parseInt(arr12[2]);

            passedTextView.setText("Select Date");
            backUpTextView.setAlpha(0.0f);
            if (yearRecieved < todaysYear) {
                MoinUtils.getReference().showMessage(myContext, "Please Select Date Ahead Of Today's Date");
            } else {
                if (monthRecieved < todaysMonth) {
                    MoinUtils.getReference().showMessage(myContext, "Please Select Date Ahead Of Today's Date");
                } else {
                    if (monthRecieved == todaysMonth) {
                        if (dateRecieved <= todaysDate) {
                            MoinUtils.getReference().showMessage(myContext, "Please Select Date Ahead Of Today's Date");
                        } else {
                            backUpTextView.setAlpha(1.0f);
                            passedTextView.setText(dateMoin);
                        }
                    } else {
                        backUpTextView.setAlpha(1.0f);
                        passedTextView.setText(dateMoin);
                    }
                }
            }
        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeCells> {

        private LayoutInflater inflator;

        public TimeAdapter(Context context) {
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public TimeCells onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.time_slot_cell, parent, false);
            TimeCells holder = new TimeCells(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(TimeCells holder, int position) {
            TimeStr myStr = data.get(position);
            if (myStr.active) {
                holder.parentView.setBackgroundResource(R.drawable.time_selection_selected);
                holder.timeTextView.setTextColor(Color.WHITE);
                holder.timeTextView.setTypeface(null, Typeface.BOLD);
            } else {
                holder.parentView.setBackgroundColor(Color.TRANSPARENT);
                holder.timeTextView.setTextColor(Color.BLACK);
                holder.timeTextView.setTypeface(null, Typeface.NORMAL);
            }
            holder.timeTextView.setText(myStr.time);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class TimeCells extends RecyclerView.ViewHolder {
        TextView timeTextView;
        View parentView;

        public TimeCells(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            parentView = itemView.findViewById(R.id.time_backGround);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeSlotSelected(getAdapterPosition());
                }
            });
            timeTextView.setTypeface(mySingelton.myCustomTypeface);
        }
    }

    private class TimeStr {
        public String time;
        public Boolean active;
    }

    public void makePayment() {
//
//        private String keyForPayU = "0ut3csFe";
//        private String merchantIdForPayU = "5419473";
//        private String productNameForPayU = "";
//        private String saltKeyFromPayU = "KkZobfRBws";
//        private String failureUrlPayU = "http://184.95.55.236:8080/SmartCar/paymentfail";
//        private String successUrlPayU = "http://184.95.55.236:8080/SmartCar/paymentsuccess";

        String m_amountToPay = payableAmmountTextView.getText().toString().replace("Payable Amount : ","");

        Random r = new Random();
        int i1 = r.nextInt(9000 - 10) + 10;
        String TnxId = i1 + "" + System.currentTimeMillis();

        Double amount = Double.parseDouble(m_amountToPay);
        if(isDouble(amount.toString())){
            amount = Double.parseDouble(amount.toString());
        }else{
            Toast.makeText(getApplicationContext(), "Enter correct amount", Toast.LENGTH_LONG).show();
            return ;
        }
        if (amount <= 0.0) {
            Toast.makeText(getApplicationContext(), "Enter Some amount", Toast.LENGTH_LONG).show();
        } else if (amount > 1000000.00) {
            Toast.makeText(getApplicationContext(), "Amount exceeding the limit : 1000000.00 ", Toast.LENGTH_LONG).show();
        } else {

            PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder(  );

            builder.setKey(keyForPayU); //Put your live KEY here
            builder.setSalt(saltKeyFromPayU); //Put your live SALT here
            builder.setMerchantId(merchantIdForPayU); //Put your live MerchantId here


            builder.setAmount(amount);
//            builder.setAmount(1);
            builder.setTnxId(TnxId);
            builder.setPhone(mySingelton.mobileNumber);
            builder.setProductName(productNameForPayU);
            builder.setFirstName(mySingelton.userName);
            builder.setEmail(mySingelton.userEmailId);
            builder.setsUrl("https://mobiletest.payumoney.com/mobileapp/payumoney/success.php");
//            builder.setsUrl(successUrlPayU);
            builder.setfUrl("https://mobiletest.payumoney.com/mobileapp/payumoney/failure.php");
//            builder.setfUrl(failureUrlPayU);
            builder.setUdf1("");
            builder.setUdf2("");
            builder.setUdf3("");
            builder.setUdf4("");
            builder.setUdf5("");


            PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

            PayUmoneySdkInitilizer.startPaymentActivityForResult(this,paymentParam);

        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
//                Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                showDialogMessage("Successful ","Payment Success Transaction Id : " + paymentId);
                callBookingAPIForMoinServer(paymentId);
            }
            else if (resultCode == RESULT_CANCELED) {
//                Log.i(TAG, "failure");
                showDialogMessage("Payment Cancelled","Dont Worry We Provide Awesome Services For The Charges We Take");
            }else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");

                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {

                    } else {
                        showDialogMessage("Failure","We Failed To Complete Payment");
                    }
                }
                //Write your code if there's no result
            }

            else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
//                Log.i(TAG, "User returned without login");
                showDialogMessage("Incomplete","You Need To Login To Proceed With Payments");
            }
        }

    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    public static String hashCal(String str) {
//        byte[] hashseq = str.getBytes();
//        StringBuilder hexString = new StringBuilder();
//        try {
//            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
//            algorithm.reset();
//            algorithm.update(hashseq);
//            byte messageDigest[] = algorithm.digest();
//            for (byte aMessageDigest : messageDigest) {
//                String hex = Integer.toHexString(0xFF & aMessageDigest);
//                if (hex.length() == 1) {
//                    hexString.append("0");
//                }
//                hexString.append(hex);
//            }
//        } catch (NoSuchAlgorithmException ignored) {
//        }
//        return hexString.toString();
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//
//        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {
//
//            if (resultCode == RESULT_OK) {
//                Log.i("moin", "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
//                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
////                showDialogMessage( "Payment Success Id : " + paymentId);
//                callBookingAPIForMoinServer();
//
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                Log.i("failed", "failure");
//                showDialogMessage("Payment Cancelled","Dont Worry We Provide Awesome Services For The Charges We Take");
//            }else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
//                Log.i("app_activity", "failure");
//
//                if (data != null) {
//                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {
//
//                    } else {
//                        showDialogMessage("Failure","We Failed To Complete Payment");
//                    }
//                }
//                //Write your code if there's no result
//            }
//
//            else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
////                Log.i(TAG, "User returned without login");
//                showDialogMessage("Incomplete","You Need To Login To Proceed With Payments");
//            }
//        }
//
//    }

    private void showDialogMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    private void callBookingAPIForMoinServer(String PaymentId){
        showLoadingView();
        JSONObject params = new JSONObject();
        try {

            String timeSelected = "";

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).active) {
                    timeSelected = data.get(i).time;
                }
            }

            String serviceIdentifiersStrign = "";

            for (int i = 0; i < ServiceIdentifiersSelected.size(); i++) {
                if (i == 0) {
                    serviceIdentifiersStrign = ServiceIdentifiersSelected.get(i);
                } else {
                    serviceIdentifiersStrign += ServiceIdentifiersSelected.get(i);
                }
            }

            String truncated = sourceTitleTextView.getText().toString().replaceAll("Initiate ","");
            truncated = truncated.replaceAll(" Booking","");

            params.put("PaymentId",PaymentId);
            params.put("type", truncated);
            params.put("date", dateTextView.getText().toString());
            params.put("time", timeSelected);
            params.put("userName", mySingelton.userName);
            params.put("userId", mySingelton.userId);
            params.put("userPhoneNumber", phoneNumberEditText.getText().toString());
            params.put("userAlternateNumber", alternatephoneNumberEditText.getText().toString());
            params.put("userAddress", LocationEditText.getText().toString());
            params.put("userCarId", mySingelton.CarSelecetd.carId);
            params.put("servicesSelected", selectedServiceString);
            params.put("servicesId", serviceIdentifiersStrign);
            params.put("cost", mySingelton.AmmountToPay + "");
            params.put("userCarName", mySingelton.CarSelecetd.carName);
            params.put("userCarBrand", mySingelton.CarSelecetd.carBrand);
            params.put("userCarModel", mySingelton.CarSelecetd.carModel);
            params.put("userCarFuelType", mySingelton.CarSelecetd.carVariant);
            params.put("userNotificationId", mySingelton.UserNotificationToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest bookingRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.booking, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoadingView();
                                startActivity(new Intent(BookingMain.this, BookingSuccess.class));
                                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingWithMessage("You Are Offline");
                    }
                }
        );
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        bookingRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(bookingRequest);
    }

    private void checkIfCanBook(){
        JSONObject params = new JSONObject();
        try {

            String timeSelected = "";

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).active) {
                    timeSelected = data.get(i).time;
                }
            }

            String serviceIdentifiersStrign = "";

            for (int i = 0; i < ServiceIdentifiersSelected.size(); i++) {
                if (i == 0) {
                    serviceIdentifiersStrign = ServiceIdentifiersSelected.get(i);
                } else {
                    serviceIdentifiersStrign += ServiceIdentifiersSelected.get(i);
                }
            }

            String truncated = sourceTitleTextView.getText().toString().replaceAll("Initiate ","");
            truncated = truncated.replaceAll(" Booking","");

            params.put("type", truncated);
            params.put("date", dateTextView.getText().toString());
            params.put("time", timeSelected);
            params.put("userName", mySingelton.userName);
            params.put("userId", mySingelton.userId);
            params.put("userPhoneNumber", phoneNumberEditText.getText().toString());
            params.put("userAlternateNumber", alternatephoneNumberEditText.getText().toString());
            params.put("userAddress", LocationEditText.getText().toString());
            params.put("userCarId", mySingelton.CarSelecetd.carId);
            params.put("servicesSelected", selectedServiceString);
            params.put("servicesId", serviceIdentifiersStrign);
            params.put("cost", mySingelton.AmmountToPay + "");
            params.put("userCarName", mySingelton.CarSelecetd.carName);
            params.put("userCarBrand", mySingelton.CarSelecetd.carBrand);
            params.put("userCarModel", mySingelton.CarSelecetd.carModel);
            params.put("userCarFuelType", mySingelton.CarSelecetd.carVariant);
            params.put("userNotificationId", mySingelton.UserNotificationToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest checkIfCanBookRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.checkIfCanBook, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoadingView();
                                makePayment();
//                                callBookingAPIForMoinServer("Moin1");
//                                startActivity(new Intent(BookingMain.this, BookingSuccess.class));
//                                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingWithMessage("You Are Offline");
                    }
                }
        );
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        checkIfCanBookRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(checkIfCanBookRequest);
    }
}
