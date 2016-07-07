package sb.pinnwandapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by sinanbocker on 23.06.16.
 */
public class MessagesDB {
    private DBHelper dbHelper;

    private SQLiteDatabase database;

    public final static String EMP_TABLE="Messages";
    public final static String message_NAME="message";
    public final static String id_NAME="serverId";
    /**
     *
     * @param context
     */
    public MessagesDB(Context context){
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }


    public long insertMessage(String msg, String id){
        ContentValues values = new ContentValues();
        values.put("serverId", id);
        values.put("message", msg);
        return database.insert("Messages", null, values);
    }

    public Cursor getAllMessages(){
        return getAllMessages(null);
    }

    public Cursor getAllMessages(String orderBy) {
        String[] cols = new String[] {"serverId", "Timestamp", "message"};
        Cursor mCursor = database.query(true, "Messages", cols, null , null, null, null, orderBy, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor; // iterate to get each value.
    }

    public String getLatestMessage() {
        String[] cols = new String[] {"message"};
        Cursor mCursor = database.query(true, "Messages", cols, null , null, null, null, "Timestamp DESC", "1");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        if(mCursor.getCount() == 0){
            return "";
        }
        return mCursor.getString(0);
    }

    public boolean isIdInTable(String id){
        String sql = "select count(*) from " + EMP_TABLE + " where "
                + id_NAME + " = " + DatabaseUtils.sqlEscapeString(id);
        SQLiteStatement statement = database.compileStatement(sql);

        try {
            return statement.simpleQueryForLong() > 0;
        } finally {
            statement.close();
        }
    }
}
