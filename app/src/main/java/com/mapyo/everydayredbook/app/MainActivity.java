package com.mapyo.everydayredbook.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements OnClickListener{
    static List<RedDataRow> dataList = new ArrayList<RedDataRow>();

    ListView listView;
    Button addButton;

    static RedDataRowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        setAdapters();
    }

    protected void findViews() {
       listView = (ListView)findViewById(R.id.list_view);
       addButton = (Button)findViewById(R.id.button);
    }

    protected void setListeners() {
       addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                addItem();
                break;
        }
    }

    protected void setAdapters(){
        adapter = new RedDataRowAdapter();
        listView.setAdapter(adapter);
    }

    protected void addItem() {
        dataList.add(
                new RedDataRow(
                        "絶滅（EX）", "哺乳類",
                        "オキナワオオコウモリ", "Pteropus loochoensis"
                        ));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class RedDataRowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView (
            int position,
            View convertView,
            ViewGroup parent
        ) {

            TextView category;
            TextView taxon;
            TextView japaneseName;
            TextView scientificName;

            View v = convertView;

            if(v == null) {
                LayoutInflater inflater =
                        (LayoutInflater)
                          getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row, null);
            }
            RedDataRow row = (RedDataRow)getItem(position);

            if(row != null) {
                category = (TextView) v.findViewById(R.id.row_category);
                taxon = (TextView) v.findViewById(R.id.row_taxon);
                japaneseName = (TextView) v.findViewById(R.id.row_japanese_name);
                scientificName = (TextView) v.findViewById(R.id.row_scientific_name);

                category.setText(row.getCategory());
                taxon.setText(row.getTaxon());
                japaneseName.setText(row.getJapaneseName());
                scientificName.setText(row.getScientificName());
            }

            return v;
        }

    }
}
