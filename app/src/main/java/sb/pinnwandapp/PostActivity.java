package sb.pinnwandapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostActivity extends AppCompatActivity {

    private JSONObject message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Button btn_postMsg = (Button) findViewById(R.id.button_sendMessage);
        btn_postMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                EditText et = (EditText) findViewById(R.id.editText);
                try {
                    message = new JSONObject();
                    message.put("msg", et.getText().toString());
                    new PostMessageTask().execute(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_invalid_message), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public class PostMessageTask extends AsyncTask<JSONObject, Void, JSONObject> {

        String link = "http://app-imtm.iaw.ruhr-uni-bochum.de:3000/posts";


        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            JSONObject result = new JSONObject();
            try {
                URL url = new URL(link);
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                os.write(jsonObjects[0].toString().getBytes("UTF-8"));
                os.close();

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String response = in.toString();
                result = new JSONObject(response);


                in.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (result.toString() == "{}") {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_post_successfull), Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}
