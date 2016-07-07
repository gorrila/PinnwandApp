package sb.pinnwandapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView lv = (ListView) findViewById(R.id.listView_messages);
        //get all messages from db
        final MessagesDB db = new MessagesDB(getApplicationContext());

        final ArrayList<String[]> messages = new ArrayList<>();
        Cursor cursor = db.getAllMessages();
        if (cursor.getCount() == 0){
            Toast toast = Toast.makeText(getApplicationContext(), "Leere DB", Toast.LENGTH_SHORT);
            toast.show();
        }else {
            try {
                while (cursor.moveToNext()) {
                    messages.add(new String[]{cursor.getString(0), cursor.getString(2)});
                }
            } finally {
                cursor.close();
            }
        }

        final MessageAdapter adapter = new MessageAdapter(getApplicationContext(), messages);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, messages.get(position)[1]);
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
            }
        });

        Button btn_id = (Button) findViewById(R.id.button_id);
        btn_id.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                messages.clear();
                Cursor cursor = db.getAllMessages("serverId ASC");
                if (cursor.getCount() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Leere DB", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    try {
                        while (cursor.moveToNext()) {
                            messages.add(new String[]{cursor.getString(0), cursor.getString(2)});
                        }
                    } finally {
                        cursor.close();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        Button btn_time = (Button) findViewById(R.id.button_time);
        btn_time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                messages.clear();
                Cursor cursor = db.getAllMessages("Timestamp ASC");
                if (cursor.getCount() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Leere DB", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    try {
                        while (cursor.moveToNext()) {
                            messages.add(new String[]{cursor.getString(0), cursor.getString(2)});
                        }
                    } finally {
                        cursor.close();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        Button btn_text = (Button) findViewById(R.id.button_text);
        btn_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                messages.clear();
                Cursor cursor = db.getAllMessages("serverId ASC");
                if (cursor.getCount() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Leere DB", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    try {
                        while (cursor.moveToNext()) {
                            messages.add(new String[]{cursor.getString(0), cursor.getString(2)});
                        }
                    } finally {
                        cursor.close();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
