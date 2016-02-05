package com.moin.smartcar.RequestBooking;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class InvoicePage extends AppCompatActivity {

    private RecyclerView myRecyclerView;
    private InvoiceAdapter myAdapter;

    private ArrayList<InvoiceStr> data = new ArrayList<InvoiceStr>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_page);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Invoice");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getData();
        myRecyclerView = (RecyclerView)findViewById(R.id.recycler);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new InvoiceAdapter(this);
        myRecyclerView.setAdapter(myAdapter);

        double price = 0;
        for (int i=0;i<data.size();i++){
            price += data.get(i).total;
        }

        ((TextView)findViewById(R.id.finalCostLabel)).setText("6,333/-");

        TextView dateTime = (TextView)findViewById(R.id.dateSelected);
        String selection = getIntent().getStringExtra("dateTimeSelected");
        dateTime.setText("Appointment Time : " + selection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    private ArrayList<InvoiceStr> getData(){
        ArrayList<InvoiceStr> list = new ArrayList<>();

        InvoiceStr str1 = new InvoiceStr();
        str1.category = "Front Break Pads/ Liners Scleaning (Both Side)";
        str1.rate = "350";
        str1.quantity = 1;
        str1.taxType = "Ser Tax";
        str1.taxRate = 14;
        str1.taxAmmount = Float.parseFloat(str1.rate) * str1.taxRate *0.01;
        str1.total = Float.parseFloat(str1.rate) + str1.taxAmmount;
        list.add(str1);

        InvoiceStr str2 = new InvoiceStr();
        str2.category = "Rear Break shoe/ Pad cleaning (Both Side)";
        str2.rate = "350";
        str2.quantity = 1;
        str2.taxType = "Ser Tax";
        str2.taxRate = 14;
        str2.taxAmmount = Float.parseFloat(str2.rate) * str2.taxRate *0.01;
        str2.total = Float.parseFloat(str2.rate) + str2.taxAmmount;
        list.add(str2);

        InvoiceStr str3 = new InvoiceStr();
        str3.category = "Labour";
        str3.rate = "950";
        str3.quantity = 1;
        str3.taxType = "Ser Tax";
        str3.taxRate = 14;
        str3.taxAmmount = Float.parseFloat(str3.rate) * str3.taxRate *0.01;
        str3.total = Float.parseFloat(str3.rate) + str3.taxAmmount;
        list.add(str3);

        InvoiceStr str4 = new InvoiceStr();
        str4.category = "Filter Assy-Engine Oil";
        str4.rate = "130";
        str4.quantity = 1;
        str4.taxType = "VAT";
        str4.taxRate = 12.5;
        str4.taxAmmount = Float.parseFloat(str4.rate) * str4.taxRate *0.01;
        str4.total = Float.parseFloat(str4.rate) + str4.taxAmmount;
        list.add(str4);

        InvoiceStr str5 = new InvoiceStr();
        str5.category = "Engine Oil";
        str5.rate = "350";
        str5.quantity = 5;
        str5.taxType = "VAT";
        str5.taxRate = 12.5;
        str5.taxAmmount =  1575 * str5.taxRate *0.01;
        str5.total = Float.parseFloat(str5.rate) + str5.taxAmmount;
        list.add(str5);

        InvoiceStr str6 = new InvoiceStr();
        str6.category = "Break Fluid GC 100AA (250 ML)";
        str6.rate = "175";
        str6.quantity = 1;
        str6.taxType = "VAT";
        str6.taxRate = 12.5;
        str6.taxAmmount = Float.parseFloat(str6.rate) * str6.taxRate *0.01;
        str6.total = Float.parseFloat(str6.rate) + str6.taxAmmount;
        list.add(str6);

        InvoiceStr str7 = new InvoiceStr();
        str7.category = "Water Pump Cleaning";
        str7.rate = "300";
        str7.quantity = 1;
        str7.taxType = "Ser Tax";
        str7.taxRate = 14;
        str7.taxAmmount = Float.parseFloat(str7.rate) * str7.taxRate *0.01;
        str7.total = Float.parseFloat(str7.rate) + str7.taxAmmount;
        list.add(str7);

        InvoiceStr str8 = new InvoiceStr();
        str8.category = "Vehicle Cleaning & Washing";
        str8.rate = "400";
        str8.quantity = 1;
        str8.taxType = "Ser Tax";
        str8.taxRate = 14;
        str8.taxAmmount = Float.parseFloat(str8.rate) * str8.taxRate *0.01;
        str8.total = Float.parseFloat(str8.rate) + str8.taxAmmount;
        list.add(str8);

        InvoiceStr str9 = new InvoiceStr();
        str9.category = "Engine Tune up";
        str9.rate = "400";
        str9.quantity = 1;
        str9.taxType = "Ser Tax";
        str9.taxRate = 14;
        str9.taxAmmount = Float.parseFloat(str9.rate) * str9.taxRate *0.01;
        str9.total = Float.parseFloat(str9.rate) + str9.taxAmmount;
        list.add(str9);

        InvoiceStr str10 = new InvoiceStr();
        str10.category = "Air Filter Cleaning";
        str10.rate = "400";
        str10.quantity = 1;
        str10.taxType = "Ser Tax";
        str10.taxRate = 14;
        str10.taxAmmount = Float.parseFloat(str10.rate) * str10.taxRate *0.01;
        str10.total = Float.parseFloat(str10.rate) + str10.taxAmmount;
        list.add(str10);

        return list;
    }

    private class InvoiceAdapter extends RecyclerView.Adapter<InvoiceCell>{

        private LayoutInflater inflator;

        public InvoiceAdapter(Context context){
            this.inflator = LayoutInflater.from(context);
        }

        @Override
        public InvoiceCell onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.invoice_cell, parent, false);
            InvoiceCell holder = new InvoiceCell(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(InvoiceCell holder, int position) {
            InvoiceStr myStr = data.get(position);
            holder.quantityTextView.setText(myStr.quantity+"");
            holder.categoryTextView.setText(myStr.category);
            holder.rateTextView.setText(myStr.rate + "/-");
            holder.taxTypeTextView.setText(myStr.taxType);
            holder.taxValueTextView.setText(myStr.taxRate+"%");
            holder.subTotalTextView.setText(round(myStr.total,2)+"/-");

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public class InvoiceCell extends RecyclerView.ViewHolder {
        TextView quantityTextView,categoryTextView,rateTextView,taxTypeTextView,taxValueTextView,subTotalTextView;

        public InvoiceCell(View itemView) {
            super(itemView);
            quantityTextView = (TextView) itemView.findViewById(R.id.cellQuantity);
            categoryTextView = (TextView) itemView.findViewById(R.id.cellCategory);
            rateTextView = (TextView) itemView.findViewById(R.id.cellIndividualPrice);
            taxTypeTextView = (TextView) itemView.findViewById(R.id.cellTaxType);
            taxValueTextView = (TextView) itemView.findViewById(R.id.cellTaxValue);
            subTotalTextView = (TextView) itemView.findViewById(R.id.cellSubTotal);


        }
    }

}
