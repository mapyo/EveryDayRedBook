package com.mapyo.everydayredbook.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements OnClickListener {

    static List<RedData> dataList = new ArrayList<RedData>();

    ListView listView;
    Button addButton;
    int num=1;

    static RedDataRowAdapter adapter;

    // db関連
    private DataBaseHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDatabase();

        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        setAdapters();
    }

    private void setDatabase() {
        mDbHelper = new DataBaseHelper(this);
        try {
            mDbHelper.createEmptyDatabase();
            db = mDbHelper.openDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLiteException sqle) {
            throw sqle;
        }
    }

    private static final String [] COLUMNS = {"_id", "category", "taxon", "japanese_name", "scientific_name"};

    private Cursor findData(int id) {
        Cursor cursor = db.query("red_data", COLUMNS, "_id=" + id, null, null, null, null);
        return cursor;
    }

    protected void findViews() {
       listView = (ListView)findViewById(R.id.list_view);
       addButton = (Button)findViewById(R.id.button);
    }

    protected void setListeners() {
       addButton.setOnClickListener(this);
        // ここはいずれ
        // listView.setOnItemClickListener(this) {
        // という形にもっていく
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,View view,
                                    int position, long id) {
                // 新しい画面へ遷移する
                // インテントのインスタンス作成
                Intent intent = new Intent(MainActivity.this, RedDataActivity.class);

                // タップしたviewのtextの取得
                TextView category = (TextView) view.findViewById(R.id.row_category);
                TextView taxon = (TextView) view.findViewById(R.id.row_taxon);
                TextView japaneseName = (TextView) view.findViewById(R.id.row_japanese_name);
                TextView scientificName = (TextView) view.findViewById(R.id.row_scientific_name);

                intent.putExtra("CATEGORY", category.getText());
                intent.putExtra("TAXON", taxon.getText());
                intent.putExtra("JAPANESE_NAME", japaneseName.getText());
                intent.putExtra("SCIENTIFIC_NAME", scientificName.getText());

                // 次画面のアクティビティ起動
                startActivity(intent);
            }
        });

    }

    // これは使わなくなるはずなので後で消す
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
        Cursor c = findData(num);
        num++;
        if(c.moveToFirst()) {
            String category = c.getString(c.getColumnIndex("category"));
            String taxon = c.getString(c.getColumnIndex("taxon"));
            String japanese_name = c.getString(c.getColumnIndex("japanese_name"));
            String scientific_name = c.getString(c.getColumnIndex("scientific_name"));

            dataList.add(
                    new RedData(
                            category, taxon,
                            japanese_name, scientific_name
                    ));
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "表示できるデータがありませんでした", Toast.LENGTH_SHORT).show();
        }
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
            RedData row = (RedData)getItem(position);

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
