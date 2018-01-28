package com.polygalov.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Константин on 28.01.2018.
 */

public class OnAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long rowId = intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
        WakeReminderIntentService.acquireStaticLock(context);

        Intent i = new Intent(context, ReminderService.class);
        i.putExtra(RemindersDbAdapter.KEY_ROWID, rowId);
        context.startService(i);
    }
}
