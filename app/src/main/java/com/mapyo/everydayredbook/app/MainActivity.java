package com.mapyo.everydayredbook.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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
import java.util.Calendar;
import java.util.List;



public class MainActivity extends Activity
        implements OnClickListener {

    static List<RedData> dataList = new ArrayList<RedData>();

    ListView listView;
    Button addButton;

    static RedDataRowAdapter adapter;

    // db関連
    // 初期設定用のDB
    private DataBaseHelper mDbHelper;
    private SQLiteDatabase db;

    // 追加した情報をもつDB
    private SQLiteDatabase addedDb;

    // red_dataのカラム一覧
    private static final String [] RED_DATA_COLUMNS = {"_id", "category", "taxon", "japanese_name", "scientific_name"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // DB関連の初期設定
        setDatabase();
        setAddedDatabase();

        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        setAdapters();

        // 既に追加済みのデータをセットする
        loadAddedRedbook();

        // 定期的に絶滅危惧種を追加してくれるサービスを追加する
        // 以下、役目を終えたので一旦コメントアウトする
        // たぶん、ボタンを押すと３秒後にnotification＆リスト追加のイメージ
        //setAlarmManager();

        // 毎日12:00にpush通知をセットする
        DailyScheduler scheduler = new DailyScheduler(getApplicationContext());
        scheduler.setByTime(ReceivedActivity.class, 12, 00, -1);
    }

    private void loadAddedRedbook() {
        // 追加済みのIDを取得
        // 最近追加したものから順番に入るように
        RedData findReddata = new RedData(this);
        ArrayList<String> addedIdList = findReddata.findAddedIds();

        // 追加済みのデータを10件分リストから取得
        // とりあえず、１件ずつ取得して配列に入れる感じにするかー

        // RedDataのarray List的なものを宣言
        ArrayList<RedData> addedRedDataList = new ArrayList<RedData>();

        for( String addedId : addedIdList) {
            int id = Integer.parseInt(addedId);
            RedData reddata = new RedData(this);
            reddata.setRedDataById(id);
            // リストに要素を追加
            addedRedDataList.add(reddata);
        }

        // viewに値をセットする
        dataList = addedRedDataList;
        adapter.notifyDataSetChanged();
    }

    // idに対応したred_dataを取得する
    private RedData getRedDataById(int id) {
        RedData redData=null;

        Cursor c = db.query("red_data", RED_DATA_COLUMNS, "_id=" + id, null, null, null, null, null);

        if(c.moveToFirst()) {
            redData = new RedData(this);
            redData.setRedData(
                    c.getString(c.getColumnIndex("category")),
                    c.getString(c.getColumnIndex("taxon")),
                    c.getString(c.getColumnIndex("japanese_name")),
                    c.getString(c.getColumnIndex("scientific_name"))
            );
        }

        return redData;
    }

    // 追加済みのDBを取得する
    private void setAddedDatabase() {
        AddedDataBaseHelper helper = new AddedDataBaseHelper(this);
        addedDb = helper.getWritableDatabase();
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
                sendNotification();
                break;
        }
    }

    protected void setAdapters(){
        adapter = new RedDataRowAdapter();
        listView.setAdapter(adapter);
    }

    // これが１日１回実行されるようになる
    protected void addItem() {
        // 追加のイメージ
        RedData addedRedData = new RedData(this);
        // 追加用のデータをセット
        addedRedData.setAddedRedData();
        Log.i("addItem", "test");
        if(addedRedData.getCategory() == null) {
            Toast.makeText(this, "追加できるデータがありませんでした", Toast.LENGTH_SHORT).show();
        }
        // データリストに追加
        dataList.add(0, addedRedData);
        adapter.notifyDataSetChanged();
    }

    private void insertAddedData(int addedId) {
        ContentValues values = new ContentValues();
        values.put("added_id", addedId);
        addedDb.insert("added_redbook", null, values);
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


    private void sendNotification() {
        // Intentの作成
        Intent intent = new Intent(MainActivity.this, MainActivity.class);

        // ここ、第２引数になんで０なのかがよくわかってない。
        PendingIntent contentIntent = PendingIntent.getActivity(
                MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // LargIcon の Bitmap を作成
        // todo 画像変えたいと思うので、いずれここを修正する
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        // NotificatonBilderを作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext() );
        builder.setContentIntent(contentIntent);
        // ステータスバーに表示されるテキスト
        builder.setTicker("新しい絶滅危惧種が追加されたよ！！");
        // アイコン
        builder.setSmallIcon(R.drawable.ic_launcher);
        // Notificationを開いた時に表示されるタイトル
        builder.setContentTitle("毎日の絶滅危惧種");
        // Notificationを開いた時に表示されるサブタイトル
        builder.setContentText("今日はどんな絶滅危惧種だろうね？");
        // Notificationを開いた時に表示されるアイコン
        builder.setLargeIcon(largeIcon);
        // 通知するタイミング
        builder.setWhen(System.currentTimeMillis());
        // 通知時の音・バイブ・ライト → 何も震わせないのでやらない
//        builder.setDefaults(Notification.DEFAULT_SOUND
//                | Notification.DEFAULT_VIBRATE
//                | Notification.DEFAULT_LIGHTS);
        // タップするとキャンセル（消える）
        builder.setAutoCancel(true);

        // NotificationManagerを取得
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        // Notificationを作成して通知
        //manager.notify(NOTIFICATION_CLICK, builder.build());
        manager.notify(0, builder.build());
    }

    private void setAlarmManager() {
        // ReceivedActivityを呼び出すインテントを作成
        Intent i = new Intent(getApplicationContext(), ReceivedActivity.class);
        // ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);

        // Calendar取得
        Calendar calendar = Calendar.getInstance();
        // 現在時刻を取得
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 現在寄り3秒後を設定
        calendar.add(Calendar.SECOND, 3);

        // AlramManager取得
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }
}
