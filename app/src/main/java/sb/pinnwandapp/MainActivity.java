package sb.pinnwandapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    final static float THRESHOLD = 1000000000.0f;
    SensorManager sensorManager;
    Sensor sensorAccelerometer;
    Sensor lightSensor;
    JSONObject current_message;
    float SHAKE_THRESHOLD_GRAVITY = 2.f;
    float last_time = 0.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_list = (Button) findViewById(R.id.btn_showList);
        btn_list.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        Button btn_post = (Button) findViewById(R.id.btn_postMsg);
        btn_post.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "No light sensor on this phone", Toast.LENGTH_SHORT);
            toast.show();
        }

        sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        MessagesDB db = new MessagesDB(getApplicationContext());
        TextView tv_latest = (TextView) findViewById(R.id.textView_lastMessage);
        tv_latest.setText(db.getLatestMessage());
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY && ((System.nanoTime() - last_time)>THRESHOLD)) {
                //get new message from server
                ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    new GetMessageTask().execute();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Kein Internet", Toast.LENGTH_SHORT);
                    toast.show();
                }
                last_time = System.nanoTime();
            }
        }else if (mySensor.getType() == Sensor.TYPE_LIGHT) {
            float light = sensorEvent.values[0];

            if(light <= 40f){
                Toast toast = Toast.makeText(getApplicationContext(), "Light = 0", Toast.LENGTH_SHORT);
                toast.show();
                //Save new message
                MessagesDB db = new MessagesDB(getApplicationContext());
                try {
                    if(!db.isIdInTable(current_message.get("_id").toString())){
                        db.insertMessage(current_message.get("msg").toString(), current_message.get("_id").toString());
                        toast = Toast.makeText(getApplicationContext(), "Nachricht gespeichert.", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{
                        toast = Toast.makeText(getApplicationContext(), "Nachricht schon in DB.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public class GetMessageTask extends AsyncTask<Void, Void, JSONObject> {

        String link = "http://app-imtm.iaw.ruhr-uni-bochum.de:3000/posts/random";
        JSONObject json;
        @Override
        protected JSONObject doInBackground(Void... voids) {
            StringBuilder sb;
            try {
                URL url = new URL(link);
                HttpURLConnection urlConnection = urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream is = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();

                json = new JSONObject(sb.toString());
                //post.setId(reader.getInt("id"));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

        protected void onPostExecute(JSONObject json){
            current_message = json;
            TextView tv_message = (TextView) findViewById(R.id.textView_lastMessage);
            TextView tv_save = (TextView) findViewById(R.id.textView_save);

            try {
                tv_message.setText(json.getString("msg"));
                tv_save.setText("Halte den Lichtsensor zu um die Nachricht zu speichern!");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), "Malformed JSON", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }
}
