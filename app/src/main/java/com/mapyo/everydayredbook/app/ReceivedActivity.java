package com.mapyo.everydayredbook.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class ReceivedActivity extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "called ReceivedActivity", Toast.LENGTH_SHORT).show();
    }
}
