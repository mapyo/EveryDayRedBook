package com.mapyo.everydayredbook.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class RedDataActivity extends Activity {

    RedDataRow redDataRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_data);

        setRedDataRow();
        setLayout();

    }

    // 画面に変数をセット
    protected void setLayout() {
        TextView category = (TextView)findViewById(R.id.row_category);
        TextView taxon = (TextView)findViewById(R.id.row_taxon);
        TextView japaneseName = (TextView)findViewById(R.id.row_japanese_name);
        TextView scientificName = (TextView)findViewById(R.id.row_scientific_name);

        category.setText(redDataRow.getCategory());
        taxon.setText(redDataRow.getTaxon());
        japaneseName.setText(redDataRow.getJapaneseName());
        scientificName.setText(redDataRow.getScientificName());
    }

    private void setRedDataRow() {
        Intent intent = getIntent();
        String category = intent.getStringExtra("CATEGORY");
        String taxon = intent.getStringExtra("TAXON");
        String japaneseName = intent.getStringExtra("JAPANESE_NAME");
        String scientificName = intent.getStringExtra("SCIENTIFIC_NAME");

        redDataRow = new RedDataRow(
                category, taxon, japaneseName, scientificName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.red_data, menu);
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
}
