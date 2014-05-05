package com.mapyo.everydayredbook.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "redbook";
    private static String DB_NAME_ASSET = "redbook.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase mDatabase;
    private final Context mContext;
    private final File mDatabasePath;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        mDatabasePath = mContext.getDatabasePath(DB_NAME);
    }

    /**
     *  asset に格納したデータベースをコピーするための、空のDBを作成する
     */

    public void createEmptyDatabase() throws IOException {
        boolean dbExist = checkDatabaseExists();

        if (dbExist) {
            // 既にデータベースは作成されている
        } else {
            // このメソッドを呼ぶことで、空のデータベースがアプリのデフォルトシステムパスに作られる
            getReadableDatabase();

            try {
                // assetに格納したデータベースをコピーする
                copyDataBaseFromAsset();

                String dbPath = mDatabasePath.getAbsolutePath();
                SQLiteDatabase checkDb = null;
                try {
                    checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
                } catch (SQLiteException e) {
                }

                if (checkDb != null) {
                    checkDb.setVersion(DB_VERSION);
                    checkDb.close();
                }
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * 再コピーを防止する為に、既にデータベースがあるかどうか判定する
     *
     * @return 存在している場合 {@code true}
     */
    private boolean checkDatabaseExists() {
        String dbPath = mDatabasePath.getAbsolutePath();
        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // データベースはまだ存在していない
        }

        if (checkDb == null) {
            // データベースはまだ存在していない
            return false;
        }

        int oldVersion = checkDb.getVersion();
        int newVersion = DB_VERSION;

        if (oldVersion == newVersion) {
            // データベースは存在していて最新
            checkDb.close();
            return true;
        }

        // データベースが存在していて最新ではないので削除
        File f = new File(dbPath);
        f.delete();
        return false;
    }

    /**
     * asset に格納したデータベースをデフォルトのデータベースパスに作成してから、
     * データベースにコピーする
     */
    private void copyDataBaseFromAsset() throws IOException {
        // asset 内のデータベースファイルにアクセス
        InputStream mInput = mContext.getAssets().open(DB_NAME_ASSET);

        // デフォルトのデータベースパスに作成した空のDB
        OutputStream mOutput = new FileOutputStream(mDatabasePath);

        // コピー
        byte[] buffer = new byte[1024];
        int size;
        while ((size = mInput.read(buffer)) > 0) {
            mOutput.write(buffer, 0, size);
        }

        // Close the stream
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }


    public SQLiteDatabase openDataBase() throws SQLiteException {
        return getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    @Override
    public synchronized void close() {
        if(mDatabase != null) mDatabase.close();

        super.close();
    }
}
