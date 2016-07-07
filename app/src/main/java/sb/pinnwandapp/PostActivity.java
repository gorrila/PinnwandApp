package sb.pinnwandapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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
                    Toast toast = Toast.makeText(getApplicationContext(), "Keine zulässige Nachricht.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public class PostMessageTask extends AsyncTask<JSONObject, Void, Integer> {

        String link = "http://app-imtm.iaw.ruhr-uni-bochum.de:3000/posts";


        @Override
        protected Integer doInBackground(JSONObject... jsonObjects) {
            StringBuilder sb;
            BufferedReader reader = null;
            Integer status = -1;
            try {
                URL url;
                URLConnection urlConnection;
                DataOutputStream printout;
                DataInputStream input;
                url = new URL(link);
                urlConnection = url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Host", "android.schoolportal.gr");
                urlConnection.connect();
                printout = new DataOutputStream(urlConnection.getOutputStream());
                String str = jsonObjects[0].toString();
                byte[] data = str.getBytes("UTF-8");
                printout.write(data);
                printout.flush();
                printout.close();
                InputStream printin = new DataInputStream(urlConnection.getInputStream());
                status = printin.read();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(Integer status) {
            if(status!=123){
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid post. Response: " + status, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}
