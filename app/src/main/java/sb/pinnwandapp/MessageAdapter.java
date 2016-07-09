package sb.pinnwandapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<String[]> {

    private final ArrayList<String[]> messages;

    public MessageAdapter(Context context, ArrayList<String[]> messages) {
        super(context, 0, messages);
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }
        // Lookup view for data population
        TextView tvMessage = (TextView) convertView.findViewById(R.id.message);
        TextView tvId = (TextView) convertView.findViewById(R.id.id);


        // Populate the data into the template view using the data object
        tvMessage.setText(messages.get(position)[1]);
        tvId.setText(messages.get(position)[0]);

        // Return the completed view to render on screen
        return convertView;
    }
}
