package sb.pinnwandapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sinanbocker on 23.06.16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MessagesDB";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase database;

    private static final String DATABASE_CREATE = "create table Messages(" +
            "id integer primary key, " +
            "serverId text not null, " +
            "message text not null, " +
            "Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS Messages");
        onCreate(database);
    }
}
