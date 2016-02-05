package com.moin.smartcar.Support;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moin.smartcar.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FAQPartTwo extends AppCompatActivity {

    private FaqStr myStr;
    private ArrayList<String> data = new ArrayList<>();

    @Bind(R.id.faqRecycler)RecyclerView myRecyclerView;
    private FAQInternalAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqpart_two);
        ButterKnife.bind(this);

        Intent recieveingIntent = getIntent();
        myStr = recieveingIntent.getParcelableExtra("FAQSelection");

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(myStr.mainTitle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new FAQInternalAdapter(this);
        myRecyclerView.setAdapter(myAdapter);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        data = new ArrayList<>();
        for (int i=0;i<myStr.subTitle.size();i++){
            data.add(myStr.subTitle.get(i));
        }
    }

    private class FAQInternalAdapter extends RecyclerView.Adapter<FaqHolder>{

        private LayoutInflater inflator;

        public FAQInternalAdapter(Context context){
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public FaqHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.cell_faq_home, parent, false);
            FaqHolder holder = new FaqHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(FaqHolder holder, int position) {
            holder.titletextView.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class FaqHolder extends RecyclerView.ViewHolder {
        TextView titletextView;

        public FaqHolder(View itemView) {
            super(itemView);
            titletextView = (TextView) itemView.findViewById(R.id.faqTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateWithIndex(getAdapterPosition());
                }
            });
        }
    }

    private void navigateWithIndex(int index){
        Intent myIntent = new Intent(FAQPartTwo.this,faqFinal.class);
        myIntent.putExtra("faqQuestion",data.get(index));
        myIntent.putExtra("faqAnswer",myStr.internalStructire.get(index));
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }


}
