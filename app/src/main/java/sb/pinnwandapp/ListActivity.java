package sb.pinnwandapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ListView lv = (ListView) findViewById(R.id.listView_messages);
        final MessagesDB db = new MessagesDB(getApplicationContext());

        final ArrayList<String[]> messages = new ArrayList<>();
        Cursor cursor = db.getAllMessages();
        setListItems(lv, cursor, messages);

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
                setListItems(lv, cursor, messages);
                adapter.notifyDataSetChanged();
            }
        });
        Button btn_time = (Button) findViewById(R.id.button_time);
        btn_time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                messages.clear();
                Cursor cursor = db.getAllMessages("Timestamp ASC");
                setListItems(lv, cursor, messages);
                adapter.notifyDataSetChanged();
            }
        });
        Button btn_text = (Button) findViewById(R.id.button_text);
        btn_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                messages.clear();
                Cursor cursor = db.getAllMessages("LOWER(message) ASC");
                setListItems(lv, cursor, messages);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setListItems(ListView lv, Cursor cursor, ArrayList<String[]> messages){
        if (cursor.getCount() == 0){
            Toast toast = Toast.makeText(getApplicationContext(), "Leere DB", Toast.LENGTH_SHORT);
            toast.show();
        }else {
            try {
                if(cursor.getCount() != 0) {
                    messages.add(new String[]{cursor.getString(1), cursor.getString(2)});
                    while (cursor.moveToNext()) {
                        messages.add(new String[]{cursor.getString(1), cursor.getString(2)});
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }
}
