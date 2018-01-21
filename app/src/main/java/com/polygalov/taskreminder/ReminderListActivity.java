package com.polygalov.taskreminder;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ReminderListActivity extends ListActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private RemindersDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        mDbHelper = new RemindersDbAdapter(this);
        mDbHelper.open();

//        String[] items = new String[]{"item1", "item2", "item3", "item4"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.reminder_row, R.id.text1, items);
//        setListAdapter(adapter);
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
        Cursor remindersCursor = mDbHelper.fetchAllReminders();
        startManagingCursor(remindersCursor);

        //Создание массива полей заголовков
        String[] from = new String[]{RemindersDbAdapter.KEY_TITLE};

        //Создание массива полей, связанных с представлением
        int[] to = new int[]{R.id.text1};

        //Создание адаптера курсора
        SimpleCursorAdapter reminders = new SimpleCursorAdapter(this, R.layout.reminder_row, remindersCursor, from, to);
        setListAdapter(reminders);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteReminder(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_insert:
                createReminder();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void createReminder() {
        Intent intent = new Intent(this, ReminderEditActivity.class);
        startActivityForResult(intent, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillData();
    }

}