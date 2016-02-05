package com.moin.smartcar.DentPaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vignesh_iopex.confirmdialog.Confirm;
import com.github.vignesh_iopex.confirmdialog.Dialog;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.Network.MyApplication;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

public class DentPaintHome extends AppCompatActivity {

    private FeatureCoverFlow mCoverFlow,tempCoverFlow;
    private CoverFlowAdapter mAdapter;
    private ArrayList<Bitmap> data = new ArrayList<>(0);
    private int currentPosition = -1;

    private TextView deleteButton;
    private ImageView deleteUpArrow;
    private TextView hintTextView;

    private TextView CameraButton;
    private TextView GalleryButton;

    private Button ContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dent_paint_home);


        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.dentandpaint));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        CameraButton = (TextView)findViewById(R.id.CameraTextView);
        GalleryButton = (TextView)findViewById(R.id.GalleryTextView);

        deleteButton = (TextView)findViewById(R.id.deleteButton);
        deleteUpArrow = (ImageView)findViewById(R.id.deleteUpArrow);
        hintTextView = (TextView)findViewById(R.id.hintTextView);

        deleteButton.setAlpha(0.0f);
        deleteUpArrow.setAlpha(0.0f);

        mCoverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);
        tempCoverFlow = (FeatureCoverFlow)findViewById(R.id.coverflow);
        mAdapter = new CoverFlowAdapter(this);
        mCoverFlow.setAdapter(mAdapter);

        ContinueButton = (Button)findViewById(R.id.ContinueButton);
        ContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageBeforeUploading();
            }
        });

        mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DentPaintHome.this,
                        position + "",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mCoverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                currentPosition = position;
            }

            @Override
            public void onScrolling() {
            }
        });

        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent, 101);
//                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

        GalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 102);
//                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(currentPosition);
                reloadCarousel();
                reloadCarousel();
//                mAdapter.notifyDataSetChanged();
                if (data.size() == 0){
                    deleteButton.setAlpha(0.0f);
                    deleteUpArrow.setAlpha(0.0f);
                    hintTextView.setAlpha(1.0f);
                }else{
                    deleteButton.setAlpha(1.0f);
                    deleteUpArrow.setAlpha(1.0f);
                }
            }
        });

    }

    private void reloadCarousel(){
        mCoverFlow.releaseAllMemoryResources();
        mCoverFlow = null;
        mAdapter = null;
        mCoverFlow = tempCoverFlow;
        mAdapter = new CoverFlowAdapter(this);
        mCoverFlow.setAdapter(mAdapter);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

//            syncButton.setLayoutParams(addButton.getLayoutParams());
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                data.add(photo);
                this.data.add(photo);
//                mAdapter.notifyDataSetChanged();
                reloadCarousel();
                deleteButton.setAlpha(1.0f);
                deleteUpArrow.setAlpha(1.0f);
                hintTextView.setAlpha(0.0f);


            } catch (Exception e) {

                Toast.makeText(DentPaintHome.this, "Please Select A Valid Image", Toast.LENGTH_LONG).show();
            }

        }
        if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (bitmap != null) {
                    this.data.add(bitmap);
                    reloadCarousel();
//                    mAdapter.notifyDataSetChanged();
                    deleteButton.setAlpha(1.0f);
                    deleteUpArrow.setAlpha(1.0f);
                    hintTextView.setAlpha(0.0f);
                } else {
                    Toast.makeText(DentPaintHome.this, "Please Select A Valid Image", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Toast.makeText(DentPaintHome.this, "Please Select A Valid Image", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logoutmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(DentPaintHome.this, LoginNew.class));
        }else{
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class CoverFlowAdapter extends BaseAdapter {

        private Context mContext;

        public CoverFlowAdapter(Context context) {
            mContext = context;
        }


        @Override
        public int getCount() {
            if (data.size() == 0){
                return 1;
            }else{
                return data.size();
            }
        }

        @Override
        public Object getItem(int pos) {
            try{
                return data.get(pos);
            }catch (Exception e){
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.image_1);
                return largeIcon;
            }
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.carousel_design_cell, null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) rowView
                        .findViewById(R.id.image);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();

            try{
                holder.image.setAlpha(1.0f);
                holder.image.setImageBitmap(data.get(position));
            }catch (Exception e){
                holder.image.setAlpha(0.0f);
                holder.image.setImageResource(R.drawable.image_1);
            }
            return rowView;
        }


        class ViewHolder {
            public ImageView image;
        }
    }

    private void showMessageBeforeUploading(){
        if (data.size() == 0){
            MoinUtils.getReference().showMessage(DentPaintHome.this,"Please select at least One Photo");
        }else{
            DataSingelton.getMy_SingeltonData_Reference().imagesSelected = new ArrayList<>();
            DataSingelton.getMy_SingeltonData_Reference().imagesSelected = data;
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        data = new ArrayList<>();
        data = DataSingelton.getMy_SingeltonData_Reference().imagesSelected;
        reloadCarousel();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }


}
