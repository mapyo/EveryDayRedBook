package com.mapyo.everydayredbook.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


public class ReceivedActivity extends BroadcastReceiver {
    private Context mContext;
    private Intent mIntent;

    public void onReceive(Context context, Intent intent)
    {
        mContext = context;
        mIntent = intent;
        Toast.makeText(context, "called ReceivedActivity", Toast.LENGTH_SHORT).show();
        sendNotification();
    }

    private void sendNotification() {

        // ここ、第２引数になんで０なのかがよくわかってない。
        PendingIntent contentIntent = PendingIntent.getActivity( mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // LargIcon の Bitmap を作成
        // todo 画像変えたいと思うので、いずれここを修正する
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);

        // NotificatonBilderを作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder( mContext );
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
        // タップするとキャンセル（消える）
        builder.setAutoCancel(true);

        // NotificationManagerを取得
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);

        // Notificationを作成して通知
        //manager.notify(NOTIFICATION_CLICK, builder.build());
        manager.notify(0, builder.build());
    }
}
