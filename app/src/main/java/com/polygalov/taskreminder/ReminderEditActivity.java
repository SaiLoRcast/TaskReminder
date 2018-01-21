package com.polygalov.taskreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderEditActivity extends AppCompatActivity {

    private static final int DATE_PICKER_DIALOG = 0;
    private static final int TIME_PICKER_DIALOG = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    private Button mDateButton;
    private Button mTimeButton;

    private EditText mTitleText;
    private EditText mBodyText;
    private Button mConfirmButton;

    private Calendar mCalendar;
    private RemindersDbAdapter mDbHelper;

    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new RemindersDbAdapter(this);
        setContentView(R.layout.activity_reminder_edit);

        mCalendar = Calendar.getInstance();

        mDateButton = findViewById(R.id.reminder_date);
        mTimeButton = findViewById(R.id.reminder_time);

        mConfirmButton = findViewById(R.id.confirm);
        mTitleText = findViewById(R.id.title_text);
        mBodyText = findViewById(R.id.body_text);

//        if (getIntent() != null) {
//            Bundle extras = getIntent().getExtras();
//            int rowId = extras != null ? extras.getInt("RowId") : -1;
//        }

        mRowId = savedInstanceState != null ? savedInstanceState.getLong(RemindersDbAdapter.KEY_ROWID) : null;

        registerButtonListenersAndSetDefaultText();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_DIALOG:
                return showDatePicker();
            case TIME_PICKER_DIALOG:
                return showTimePicker();
        }
        return super.onCreateDialog(id);
    }

    private TimePickerDialog showTimePicker() {
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                updateTimeButtonText();
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE), true);
        return timePicker;
    }

    private DatePickerDialog showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(ReminderEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        }, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private void registerButtonListenersAndSetDefaultText() {

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveState();
                setResult(RESULT_OK);
                Toast.makeText(ReminderEditActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TIME_PICKER_DIALOG);
            }
        });

        updateDateButtonText();
        updateTimeButtonText();
    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(RemindersDbAdapter.KEY_ROWID) : null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open();
        setRowIdFromIntent();
        populateFields();
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor reminder = mDbHelper.fetchReminder(mRowId);
            startManagingCursor(reminder);
            mTitleText.setText(reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_TITLE)));
            mBodyText.setText(reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_BODY)));
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date = null;
            try {
                String dateString = reminder.getString(reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_DATE_TIME));
                date = dateTimeFormat.parse(dateString);
                mCalendar.setTime(date);
            } catch (ParseException e) {
                Log.e("ReminderEditActivity", e.getMessage(), e);
            }
        }

        updateDateButtonText();
        updateTimeButtonText();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RemindersDbAdapter.KEY_ROWID, mRowId);
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

//        long id = mDbHelper.createReminder(title, body, reminderDateTime);

        if (mRowId == null) {
            long id = mDbHelper.createReminder(title, body, reminderDateTime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
        }
    }

    private void updateTimeButtonText() {
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        String timeForButton = timeFormat.format(mCalendar.getTime());
        mTimeButton.setText(timeForButton);
    }

    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }
}
