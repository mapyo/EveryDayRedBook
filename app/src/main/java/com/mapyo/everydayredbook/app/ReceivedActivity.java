package com.mapyo.everydayredbook.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceivedActivity extends BroadcastReceiver {
    private Context mContext;
    private Intent mIntent;

    public void onReceive(Context context, Intent intent)
    {
        Log.i("ReceivedActivity", "start");

        mContext = context;
        mIntent = intent;
        sendNotification();
    }

    private void sendNotification() {

        RedBookNotification notification =
                new RedBookNotification(mContext, mIntent);

        notification.sendNotification();
    }
}
