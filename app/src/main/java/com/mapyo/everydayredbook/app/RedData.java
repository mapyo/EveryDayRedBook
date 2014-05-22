package com.mapyo.everydayredbook.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RedData {
    private Context mContext;
    // 初期設定用のDBたち
    private DataBaseHelper mRedDataBaseHelper = null;
    private SQLiteDatabase mRedDbReadable = null;

    // addedDbたち
    private AddedDataBaseHelper mAddedDataBaseHelper = null;
    private SQLiteDatabase mAddedDbWritable = null;
    private SQLiteDatabase mAddedDbReadable = null;


    private String category = null;
    private String taxon = null;
    private String japaneseName = null;
    private String scientificName = null;


    // red_dataのカラム一覧
    private static final String [] RED_DATA_COLUMNS = {"_id", "category", "taxon", "japanese_name", "scientific_name"};

    // 追加した情報をもつDB
    private SQLiteDatabase addedDb;

    public RedData(Context context) {
        Log.i("RedData", "start");
        mContext = context;
    }

    public void setRedData(
            String category,
            String taxon,
            String japaneseName,
            String scientific_name
    ) {
        // todo:いずれ必要そうだったら、各自のsetterを作る
        this.category = category;
        this.taxon = taxon;
        this.japaneseName = japaneseName;
        this.scientificName = scientific_name;
    }

    public void setAddedRedData() {
        // 追加済みのDBから追加済みのIDを抽出
        List addedIdList = findAddedIds();

        String whereSql = makeWhereSql(addedIdList);


        // todo:log
        Log.i("RedData", "test");


        // 追加済のIDを除いてランダムに１つselectする
        setRedDataBaseReadable();
        Cursor c = mRedDbReadable.query("red_data", RED_DATA_COLUMNS, whereSql, null, null, null, "RANDOM()", "1");

        if(c.moveToFirst()) {
            // 追加済リストに追加
            insertAddedData(c.getInt(c.getColumnIndex("_id")));

            setRedData(
                    c.getString(c.getColumnIndex("category")),
                    c.getString(c.getColumnIndex("taxon")),
                    c.getString(c.getColumnIndex("japanese_name")),
                    c.getString(c.getColumnIndex("scientific_name"))
            );
        }
    }

    private void insertAddedData(int addedId) {
        ContentValues values = new ContentValues();
        values.put("added_id", addedId);
        getAddedDataBaseWritable();
        mAddedDbWritable.insert("added_redbook", null, values);
    }

    private String makeWhereSql(List addedIdList) {
        String whereSql = "_id not in(";

        for(int i=0; i < addedIdList.size(); i++) {
            if(i==0) {
                whereSql = whereSql + "'" + addedIdList.get(i) + "'";
            } else {
                whereSql = whereSql + ", '" + addedIdList.get(i) + "'";
            }
        }

        // カンマ区切りで連結する
        whereSql = whereSql + ")";

        return whereSql;
    }

    private ArrayList<String> findAddedIds() {
        ArrayList<String> addedIdList = new ArrayList();

        String [] addedReddataColumns = {"added_id"};
        Cursor c = getAddedDataBaseReadable().query(
                "added_redbook",
                addedReddataColumns,
                null, null, null, null, "_id desc");

        c.moveToFirst();
        for (int i = 1; i <= c.getCount(); i++) {
            // added_idを取り出す
            addedIdList.add(c.getString(c.getColumnIndex("added_id")));

            c.moveToNext();
        }

        return addedIdList;
    }

    private DataBaseHelper getRedDataBaseHelper() {
        if(mRedDataBaseHelper == null) {
            mRedDataBaseHelper = new DataBaseHelper(mContext);
        }
        return mRedDataBaseHelper;
    }

    private SQLiteDatabase setRedDataBaseReadable() {
        if (mRedDbReadable == null) {
            Log.i("RedData", "if");
            mRedDbReadable = getRedDataBaseHelper().getReadableDatabase();
        }
        Log.i("RedData", "out");

        return mAddedDbReadable;
    }

    private SQLiteDatabase getAddedDataBaseWritable() {
        if (mAddedDbWritable == null) {
            mAddedDbWritable = getAddedDataBaseHelper(mContext).getWritableDatabase();
        }

        return mAddedDbWritable;
    }

    private SQLiteDatabase getAddedDataBaseReadable() {
        if (mAddedDbReadable == null) {
            mAddedDbReadable = getAddedDataBaseHelper(mContext).getReadableDatabase();
        }

        return mAddedDbReadable;
    }

    private AddedDataBaseHelper getAddedDataBaseHelper(Context context) {
        if(mAddedDataBaseHelper == null) {
            mAddedDataBaseHelper = new AddedDataBaseHelper(mContext);
        }
        return mAddedDataBaseHelper;
    }

    public String getCategory() {
        return this.category;
    }

    public String getTaxon() {
       return this.taxon;
    }

    public String getJapaneseName(){
        return this.japaneseName;
    }

    public String getScientificName() {
        return this.scientificName;
    }
}
