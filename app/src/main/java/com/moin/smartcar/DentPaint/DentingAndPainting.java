package com.moin.smartcar.DentPaint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Booking.BookingSuccess;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.MyApplication;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DentingAndPainting extends AppCompatActivity {

    @Bind(R.id.checkBox1)
    ImageView checkBox1;
    @Bind(R.id.checkBox2)
    ImageView checkBox2;
    @Bind(R.id.checkBox3)
    ImageView checkBox3;
    @Bind(R.id.checkBox4)
    ImageView checkBox4;
    @Bind(R.id.check1)View checkSelector1;
    @Bind(R.id.check2)View checkSelector2;
    @Bind(R.id.check3)View checkSelector3;
    @Bind(R.id.check4)View checkSelector4;
    @Bind(R.id.attachImages)View attachImages;
    @Bind(R.id.numberOfImages)TextView numberOfImages;
    @Bind(R.id.loadingView)
    View loadingView;
    @Bind(R.id.loadingIndicator)
    AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.locationOfDamage)
    EditText locationOfDamage;
    @Bind(R.id.moreInfo)
    EditText moreInfoEditText;
    private Boolean val1, val2, val3, val4;
    private ProgressDialog progressDialog;

    private String dentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denting_and_painting);

        ButterKnife.bind(this);

        dentId = "";

        hideLoadingView();
        DataSingelton.getMy_SingeltonData_Reference().imagesSelected = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
                mySingelton.successWebView = new WebView(MyApplication.getAppContext());
                mySingelton.successWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                mySingelton.successWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                if (Build.VERSION.SDK_INT >= 11) {
                    mySingelton.successWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                mySingelton.successWebView.getSettings().setJavaScriptEnabled(true);
                mySingelton.successWebView.loadUrl("file:///android_asset/index.html");
            }
        }).run();

        DataSingelton.getMy_SingeltonData_Reference().imagesSelected = new ArrayList<>();

        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.dentandpaint));

        navUserBookings navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), myToolbar, getResources().getString(R.string.dentandpaint));

        val1 = val2 = val3  = val4 = false;


        checkSelector1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val1 = !val1;
                setValueForCheckBox1();
            }
        });

        checkSelector2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val2 = !val2;
                setValueForCheckBox2();
            }
        });

        checkSelector3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val3 = !val3;
                setValueForCheckBox3();
            }
        });

        checkSelector4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val4 = !val4;
                setValueForCheckBox4();
            }
        });

        attachImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DentingAndPainting.this,DentPaintHome.class));
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

    }

    private void setValueForCheckBox1() {
        if (val1) {
            checkBox1.setImageResource(R.drawable.check1);
        } else {
            checkBox1.setImageResource(R.drawable.check0);
        }
    }

    private void setValueForCheckBox2() {
        if (val2) {
            checkBox2.setImageResource(R.drawable.check1);
        } else {
            checkBox2.setImageResource(R.drawable.check0);
        }
    }

    private void setValueForCheckBox3() {
        if (val3) {
            checkBox3.setImageResource(R.drawable.check1);
        } else {
            checkBox3.setImageResource(R.drawable.check0);
        }
    }

    private void setValueForCheckBox4() {
        if (val4) {
            checkBox4.setImageResource(R.drawable.check1);
        } else {
            checkBox4.setImageResource(R.drawable.check0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() > 0){
            numberOfImages.setText(DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() + " Images Attached");
        }else{
            numberOfImages.setText("");

        }
    }

    private String getOptionsSelected() {
        String returnString = "";
        if (val1 == true) {
            returnString = "Crack";
        }
        if (val2 == true) {
            if (returnString.length() == 0) {
                returnString = "Dent";
            } else {
                returnString = returnString + ", Dent";
            }
        }
        if (val3 == true) {
            if (returnString.length() == 0) {
                returnString = "Scratch";
            } else {
                returnString = returnString + ", Scratch";
            }
        }
        if (val4 == true) {
            if (returnString.length() == 0) {
                returnString = "Paint Chip";
            } else {
                returnString = returnString + ", Paint Chip";
            }
        }

        return returnString;
    }

    public void startSendingData(View view) {

        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        String options = getOptionsSelected();
        int check = checkAllValidations();
        if (check == 0) {
            return;
        }


        JSONObject params = new JSONObject();
        try {
            params.put("options", options);
            params.put("locationOfDamage", locationOfDamage.getText().toString());
            params.put("additionalInfo", moreInfoEditText.getText().toString());
            params.put("imageCount", mySingelton.imagesSelected.size() + "");
            params.put("userId", mySingelton.userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest dentAndPaintRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.reportDentPaint, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                dentId = response.getString("dentPaintId");
                                checkIfImagesAreAttached();
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
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
        dentAndPaintRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(dentAndPaintRequest);

    }

    private void checkIfImagesAreAttached() {
        if (DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() > 0) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading File ");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgress(0);
            progressDialog.show();
            uploadImage1();
        } else {
            hideLoadingView();
            Intent myIntent = new Intent(DentingAndPainting.this, BookingSuccess.class);
            myIntent.putExtra("sourceIsDentPaint", "YES");
            startActivity(myIntent);
            overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
        }
    }


    private int checkAllValidations() {
        if (val1 == false && val2 == false && val3 == false && val4 == false) {
            showMessage("Please select at least one option");
            return 0;
        }
        if (locationOfDamage.getText().toString().length() == 0) {
            showMessage("Please enter damage location");
            return 0;
        }
        return 1;
    }

    private void hideLoadingWithMessage(String msg) {
        hideLoadingView();
        MoinUtils.getReference().showMessage(DentingAndPainting.this, msg);
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(DentingAndPainting.this, msg);
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void uploadCompleted() {
        hideLoadingView();
        Intent myIntent = new Intent(DentingAndPainting.this, BookingSuccess.class);
        myIntent.putExtra("sourceIsDentPaint", "YES");
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

    private void uploadImage1() {
        new UpLoadImagesAssync1().execute();
    }

    private void uploadImage2() {
        new UpLoadImagesAssync2().execute();
    }

    private void uploadImage3() {
        new UpLoadImagesAssync3().execute();
    }

    private void uploadImage4() {
        new UpLoadImagesAssync4().execute();

    }

    private class UpLoadImagesAssync1 extends AsyncTask<Void, Integer, Void> {

        File file = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading Image " + 1);
            savebitmap("temp1");
            progressDialog.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {

//            if (file!=null){

            String url = DataSingelton.getMy_SingeltonData_Reference().uploadImageUrl + dentId + "_" + DataSingelton.getMy_SingeltonData_Reference().userId;
//                File file = new File(getRealPathFromURI(fileUri.));
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);
                FileInputStream fileIn = openFileInput("myImage.jpg");
                InputStreamEntity reqEntity = new InputStreamEntity(
                        fileIn, -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Done", "Done");
            } catch (Exception e) {
                Log.d("Moin", "Some Error");
                progressDialog.setMessage("Error Occured");
            }

//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() > 1) {
                uploadImage2();
            } else {
                progressDialog.hide();
                uploadCompleted();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        private File savebitmap(String filename) {
            String extStorageDirectory = Environment.getDataDirectory().toString();
            OutputStream outStream = null;

            file = new File("SmartCar/" + filename + ".jpg");
            if (file.exists()) {
                file.delete();
                file = new File("SmartCar/" + filename + ".jpg");
                Log.e("file exist", "" + file + ",Bitmap= " + filename);
            }
            try {
                FileOutputStream fileout = openFileOutput("myImage.jpg", MODE_PRIVATE);

                Bitmap bitmap = DataSingelton.getMy_SingeltonData_Reference().imagesSelected.get(0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileout);
                fileout.flush();
                fileout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("file", "" + file);
            return file;

        }

    }

    private class UpLoadImagesAssync2 extends AsyncTask<Void, Integer, Void> {

        File file = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading Image " + 2);
            savebitmap("temp1");
            progressDialog.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {

//            if (file!=null){

            String url = DataSingelton.getMy_SingeltonData_Reference().uploadImageUrl + dentId + "_" + DataSingelton.getMy_SingeltonData_Reference().userId;
//                File file = new File(getRealPathFromURI(fileUri.));
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);
                FileInputStream fileIn = openFileInput("myImage.jpg");
                InputStreamEntity reqEntity = new InputStreamEntity(
                        fileIn, -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Done", "Done");
            } catch (Exception e) {
                Log.d("Moin", "Some Error");
                progressDialog.setMessage("Error Occured");
            }

//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() > 2) {
                uploadImage3();
            } else {
                progressDialog.hide();
                uploadCompleted();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        private File savebitmap(String filename) {
            String extStorageDirectory = Environment.getDataDirectory().toString();
            OutputStream outStream = null;

            file = new File("SmartCar/" + filename + ".jpg");
            if (file.exists()) {
                file.delete();
                file = new File("SmartCar/" + filename + ".jpg");
                Log.e("file exist", "" + file + ",Bitmap= " + filename);
            }
            try {
                FileOutputStream fileout = openFileOutput("myImage.jpg", MODE_PRIVATE);
                Bitmap bitmap = DataSingelton.getMy_SingeltonData_Reference().imagesSelected.get(1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileout);
                fileout.flush();
                fileout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("file", "" + file);
            return file;

        }

    }

    private class UpLoadImagesAssync3 extends AsyncTask<Void, Integer, Void> {

        File file = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading Image " + 3);
            savebitmap("temp2");
            progressDialog.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {

//            if (file!=null){

            String url = DataSingelton.getMy_SingeltonData_Reference().uploadImageUrl + dentId + "_" + DataSingelton.getMy_SingeltonData_Reference().userId;
//                File file = new File(getRealPathFromURI(fileUri.));
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);
                FileInputStream fileIn = openFileInput("myImage.jpg");
                InputStreamEntity reqEntity = new InputStreamEntity(
                        fileIn, -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Done", "Done");
            } catch (Exception e) {
                Log.d("Moin", "Some Error");
                progressDialog.setMessage("Error Occured");
            }

//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() > 3) {
                uploadImage4();
            } else {
                progressDialog.hide();
                uploadCompleted();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        private File savebitmap(String filename) {
            String extStorageDirectory = Environment.getDataDirectory().toString();
            OutputStream outStream = null;

            file = new File("SmartCar/" + filename + ".jpg");
            if (file.exists()) {
                file.delete();
                file = new File("SmartCar/" + filename + ".jpg");
                Log.e("file exist", "" + file + ",Bitmap= " + filename);
            }
            try {
                FileOutputStream fileout = openFileOutput("myImage.jpg", MODE_PRIVATE);
                Bitmap bitmap = DataSingelton.getMy_SingeltonData_Reference().imagesSelected.get(2);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileout);
                fileout.flush();
                fileout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("file", "" + file);
            return file;

        }

    }

    private class UpLoadImagesAssync4 extends AsyncTask<Void, Integer, Void> {

        File file = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Uploading Image " + 4);
            savebitmap("temp3");
            progressDialog.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {

//            if (file!=null){

            String url = DataSingelton.getMy_SingeltonData_Reference().uploadImageUrl + dentId + "_" + DataSingelton.getMy_SingeltonData_Reference().userId;
//                File file = new File(getRealPathFromURI(fileUri.));
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);
                FileInputStream fileIn = openFileInput("myImage.jpg");
                InputStreamEntity reqEntity = new InputStreamEntity(
                        fileIn, -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Done", "Done");
            } catch (Exception e) {
                Log.d("Moin", "Some Error");
                progressDialog.setMessage("Error Occured");
            }

//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.hide();
            uploadCompleted();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        private File savebitmap(String filename) {
            String extStorageDirectory = Environment.getDataDirectory().toString();
            OutputStream outStream = null;

            file = new File("SmartCar/" + filename + ".jpg");
            if (file.exists()) {
                file.delete();
                file = new File("SmartCar/" + filename + ".jpg");
                Log.e("file exist", "" + file + ",Bitmap= " + filename);
            }
            try {
                FileOutputStream fileout = openFileOutput("myImage.jpg", MODE_PRIVATE);
                Bitmap bitmap = DataSingelton.getMy_SingeltonData_Reference().imagesSelected.get(3);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileout);
                fileout.flush();
                fileout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("file", "" + file);
            return file;

        }

    }
}
