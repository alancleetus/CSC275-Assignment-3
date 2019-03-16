package com.example.sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private String LOG_TAG= "LOG:";

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor accelSensor;
    private Sensor gyroSensor;
    private Sensor magSensor;

    private WifiManager wifiManager;
    private WifiInfo  wifiInfo;

    private String timestampString = "Timestamp";
    private String AccelXString = "Accel X";
    private String AccelYString = "Accel Y";
    private String AccelZString = "Accel Z";

    private String GyroXString = "Gyro X";
    private String GyroYString = "Gyro Y";
    private String GyroZString = "Gyro Z";

    private String MagXString = "Mag X";
    private String MagYString = "Mag Y";
    private String MagZString = "Mag Z";

    private String LightString = "Light intensity";

    private String RSSIString = "RSSI";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Checks if the user gave permission to write to storage, if not asks the user for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            int res=0;
            Log.e(LOG_TAG,"no permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    res);
        }
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        //update timestamp
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        EditText timeStamp = findViewById(R.id.timeStampText);
        timeStamp.setText(ts);

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            EditText accelX = findViewById(R.id.accelXText);
            EditText accelY = findViewById(R.id.accelYText);
            EditText accelZ = findViewById(R.id.accelZText);

            accelX.setText(x+"");
            accelY.setText(y+"");
            accelZ.setText(z+"");

            AccelXString +=","+x;
            AccelYString +=","+y;
            AccelZString +=","+z;

            /*leave empty cell for other sensors*/

            GyroXString +=",";
            GyroYString +=",";
            GyroZString +=",";

            MagXString +=",";
            MagYString +=",";
            MagZString +=",";

            LightString +=",";
        }
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            EditText gyroX = findViewById(R.id.gyroXText);
            EditText gyroY = findViewById(R.id.gyroYText);
            EditText gyroZ = findViewById(R.id.gyroZText);

            gyroX.setText(x+"");
            gyroY.setText(y+"");
            gyroZ.setText(z+"");

            GyroXString +=","+x;
            GyroYString +=","+y;
            GyroZString +=","+z;

            /*leave empty cell for other sensors*/

            AccelXString +=",";
            AccelYString +=",";
            AccelZString +=",";

            MagXString +=",";
            MagYString +=",";
            MagZString +=",";

            LightString +=",";
        }
        else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            EditText magX = findViewById(R.id.magXText);
            EditText magY = findViewById(R.id.magYText);
            EditText magZ = findViewById(R.id.magZText);

            magX.setText(x+"");
            magY.setText(y+"");
            magZ.setText(z+"");

            MagXString +=","+x;
            MagYString +=","+y;
            MagZString +=","+z;

            /*leave empty cell for other sensors*/

            GyroXString +=",";
            GyroYString +=",";
            GyroZString +=",";

            AccelXString +=",";
            AccelYString +=",";
            AccelZString +=",";

            LightString +=",";
        }
        else if (sensor.getType() == Sensor.TYPE_LIGHT)
        {
            float lux = event.values[0];
            EditText light = findViewById(R.id.lightText);
            light.setText(lux+"");

            LightString +=","+lux;

            /*leave empty cell for other sensors*/

            AccelXString +=",";
            AccelYString +=",";
            AccelZString +=",";

            GyroXString +=",";
            GyroYString +=",";
            GyroZString +=",";

            MagXString +=",";
            MagYString +=",";
            MagZString +=",";
        }

        timestampString +=","+ts;

        //wifi
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        //update rssi value
        int rssivalue = wifiInfo.getRssi();
        EditText rssitext = findViewById(R.id.rssiText);
        rssitext.setText(rssivalue+"");

        RSSIString+=","+rssivalue;

        saveToCSV();

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void saveToCSV()
    {
        String fileName = "sensor_data_output.csv";
        String output = timestampString + "\n"
                + AccelXString + "\n"
                + AccelYString + "\n"
                + AccelZString + "\n"
                + GyroXString + "\n"
                + GyroYString + "\n"
                + GyroZString + "\n"
                + MagXString + "\n"
                + MagYString + "\n"
                + MagZString + "\n"
                + LightString + "\n"
                + RSSIString + "\n";

        File root = Environment.getExternalStorageDirectory();
        File csvFile = new File(root, fileName);
        try {
            FileWriter writer = new FileWriter(csvFile);
            writer.write(output);
            writer.flush();
            writer.close();

            //Toast.makeText(this, "File Saved at:"+csvFile, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG,"Saved at:"+csvFile+"\n"+"output: "+output);

        } catch (Exception e) {

            e.printStackTrace();
            Log.e(LOG_TAG,"exception "+e);
            Toast.makeText(this, "Not Saved" , Toast.LENGTH_LONG).show();

        }

    }
}
