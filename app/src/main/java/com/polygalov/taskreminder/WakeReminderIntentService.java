package com.polygalov.taskreminder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Константин on 28.01.2018.
 */

public abstract class WakeReminderIntentService extends IntentService {

    abstract void doReminderWork(Intent intent);

    public static final String LOCK_NAME_STATIC = "com.polygalov.taskreminder.Static";

    private static PowerManager.WakeLock lockStatic = null;

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mrg = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mrg.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    public WakeReminderIntentService(String name) {
        super(name);
    }

    @Override
    final protected void onHandleIntent (Intent intent) {
        try {
            doReminderWork(intent);
        } finally {
            getLock(this).release();
        }
    }
}
