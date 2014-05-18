package com.mapyo.everydayredbook.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimeZone;

public class DailyScheduler {

    private Context mContext;

    public DailyScheduler(Context c) {
        mContext = c;
    }

    /**
     * duration_time（ミリ秒）後 launch_serviceを実行する
     * service_idはどのサービスかを区別する為のID（同じなら上書き）
     * 一回起動するとそのタイミングで毎日１回動き続ける
     */
    public <T> void set(Class<T> launch_service, long duration_time, int service_id) {
        Intent intent = new Intent(mContext, launch_service);

        PendingIntent action = PendingIntent.getService(mContext, service_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC, duration_time, AlarmManager.INTERVAL_DAY, action);
    }


    /**
     * 起動したい時刻(houre:minute)を指定する
     * 指定した時刻で毎日起動する
     */
    public <T> void setByTime(Class<T> launch_service, int hour, int minuite, int service_id) {
        // 日本(+9)以外のタイムゾーンを使う時はここを修正する
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");

        // 今日の目標時刻のカレンダーインスタンス作成
        Calendar calendarTarget = Calendar.getInstance();
        calendarTarget.setTimeZone(tz);
        calendarTarget.set(Calendar.HOUR_OF_DAY, hour);
        calendarTarget.set(Calendar.MINUTE, minuite);
        calendarTarget.set(Calendar.SECOND, 0);

        // 現在時刻のカレンダーインスタンス作成
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTimeZone(tz);

        // ミリ秒取得
        long targetMs = calendarTarget.getTimeInMillis();
        long nowMs = calendarNow.getTimeInMillis();

        // 今日ならそのまま指定
        if (targetMs >= nowMs) {
            set(launch_service, targetMs, service_id);

        } else {
            // 過ぎていたら明日の同時刻を指定
            calendarTarget.add(Calendar.DAY_OF_MONTH, 1);
            targetMs = calendarTarget.getTimeInMillis();
            set(launch_service, targetMs, service_id);
        }
    }

    /**
     * キャンセル用
     */
    public <T> void cancel(Class<T> launch_service, int service_id) {
        Intent intent = new Intent(mContext, launch_service);
        PendingIntent action = PendingIntent.getService(mContext, service_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
        alarm.cancel(action);
    }
}
