package com.polygalov.taskreminder;

import android.content.Intent;

/**
 * Created by Константин on 28.01.2018.
 */

public class ReminderService extends WakeReminderIntentService {

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Long rowId = intent.getExtras().getLong(RemindersDbAdapter.KEY_ROWID);
    }
}
