package com.example.happysensors;

import android.animation.ObjectAnimator;
import android.media.metrics.Event;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int A = 150;
    private TextView sensorList, tvg1, tvg2, tvg3, compassOrientation, tvm1, tvm2, tvm3, tvl1, tvl2, tvh1, tvh2, tvp1;
    private ImageView Compass;
    private SensorManager mSensorManager;
    private Sensor sensor, gyroscope, accelerometer,magnetometer, brightness, humidity, pressure, proximity;
    private LinearLayout GyroscopeLayout, MagneticLayout, LightLayout, HumidityLayout, ProximityLayout;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    boolean isLastAccelerometerArrayCopied = false;
    boolean isLastMagnetometerArrayCopied = false;

    long lastUpdatedTime = 0;
    float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(sensor.TYPE_ALL);
        sensorList=findViewById(R.id.sensorList);

        GyroscopeLayout = findViewById(R.id.GyroscopeLayout);
        GyroscopeLayout.setVisibility(View.GONE);
        gyroscope = mSensorManager.getDefaultSensor(sensor.TYPE_GYROSCOPE);
        tvg1=findViewById(R.id.tv1);
        tvg2=findViewById(R.id.tv2);
        tvg3=findViewById(R.id.tv3);

        MagneticLayout = findViewById(R.id.MagneticLayout);
        MagneticLayout.setVisibility(View.GONE);
        compassOrientation = findViewById(R.id.compassOrientation);
        Compass = findViewById(R.id.compass);
        accelerometer = mSensorManager.getDefaultSensor(sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(sensor.TYPE_MAGNETIC_FIELD);
        tvm1 = findViewById(R.id.magneticView1);
        tvm2 = findViewById(R.id.magneticView2);
        tvm3 = findViewById(R.id.magneticView3);

        LightLayout = findViewById(R.id.LightLayout);
        LightLayout.setVisibility(View.GONE);
        brightness = mSensorManager.getDefaultSensor(sensor.TYPE_LIGHT);
        tvl1 = findViewById(R.id.LightView);
        tvl2 = findViewById(R.id.LightInterpretation);

        HumidityLayout = findViewById(R.id.HumidityLayout);
        HumidityLayout.setVisibility(View.GONE);
        humidity = mSensorManager.getDefaultSensor(sensor.TYPE_RELATIVE_HUMIDITY);
        tvh1 = findViewById(R.id.HumidityView);
        tvh2 = findViewById(R.id.PressureView);

        pressure = mSensorManager.getDefaultSensor(sensor.TYPE_PRESSURE);

        ProximityLayout = findViewById(R.id.ProximityLayout);
        ProximityLayout.setVisibility(View.GONE);
        proximity = mSensorManager.getDefaultSensor(sensor.TYPE_PROXIMITY);
        tvp1 = findViewById(R.id.ProximityView);

        List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(int i=1;i<list.size();i++){
            String sensorType = list.get(i).getStringType();
            sensorList.append("\n"+ sensorType.substring(sensorType.lastIndexOf('.') + 1) + " Sensor");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(this,"Sensor List",Toast.LENGTH_SHORT).show();
                sensorList.setVisibility(View.VISIBLE);
                GyroscopeLayout.setVisibility(View.GONE);
                MagneticLayout.setVisibility(View.GONE);
                LightLayout.setVisibility(View.GONE);
                HumidityLayout.setVisibility(View.GONE);
                ProximityLayout.setVisibility(View.GONE);
                return true;
            case R.id.item2:
                Toast.makeText(this,"Gyroscope Selected",Toast.LENGTH_SHORT).show();
                sensorList.setVisibility(View.GONE);
                GyroscopeLayout.setVisibility(View.VISIBLE);
                MagneticLayout.setVisibility(View.GONE);
                LightLayout.setVisibility(View.GONE);
                HumidityLayout.setVisibility(View.GONE);
                ProximityLayout.setVisibility(View.GONE);
                return true;
            case R.id.item3:
                Toast.makeText(this,"Magnetic field Sensor Selected",Toast.LENGTH_SHORT).show();
                sensorList.setVisibility(View.GONE);
                GyroscopeLayout.setVisibility(View.GONE);
                MagneticLayout.setVisibility(View.VISIBLE);
                LightLayout.setVisibility(View.GONE);
                HumidityLayout.setVisibility(View.GONE);
                ProximityLayout.setVisibility(View.GONE);
                return true;
            case R.id.item4:
                Toast.makeText(this,"Light Sensor Selected",Toast.LENGTH_SHORT).show();
                sensorList.setVisibility(View.GONE);
                GyroscopeLayout.setVisibility(View.GONE);
                MagneticLayout.setVisibility(View.GONE);
                LightLayout.setVisibility(View.VISIBLE);
                HumidityLayout.setVisibility(View.GONE);
                ProximityLayout.setVisibility(View.GONE);
                return true;
            case R.id.item5:
                Toast.makeText(this,"Humidity/Pressure Sensor Selected",Toast.LENGTH_SHORT).show();
                sensorList.setVisibility(View.GONE);
                GyroscopeLayout.setVisibility(View.GONE);
                MagneticLayout.setVisibility(View.GONE);
                LightLayout.setVisibility(View.GONE);
                HumidityLayout.setVisibility(View.VISIBLE);
                ProximityLayout.setVisibility(View.GONE);
                return true;
            case R.id.item6:
                Toast.makeText(this,"Proximity Sensor Selected",Toast.LENGTH_SHORT).show();
                sensorList.setVisibility(View.GONE);
                GyroscopeLayout.setVisibility(View.GONE);
                MagneticLayout.setVisibility(View.GONE);
                LightLayout.setVisibility(View.GONE);
                HumidityLayout.setVisibility(View.GONE);
                ProximityLayout.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == sensor.TYPE_GYROSCOPE) {
            tvg1.setText("X = " + event.values[0]);
            tvg2.setText("Y = " + event.values[1]);
            tvg3.setText("Z = " + event.values[2]);
        }

        if (event.sensor.getType() == sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            isLastAccelerometerArrayCopied = true;
        } else if (event.sensor.getType() == sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            isLastMagnetometerArrayCopied = true;
        }

        if (isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis() - lastUpdatedTime > 50) {
            mSensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            mSensorManager.getOrientation(rotationMatrix, orientation);

            float radians = orientation[0];
            float degrees = (float) Math.toDegrees(radians);

            currentDegree = degrees;
            Compass.setRotation(-degrees);
            tvm1.setText("X =" + lastMagnetometer[0]);
            tvm2.setText("Y =" + lastMagnetometer[1]);
            tvm3.setText("Z =" + lastMagnetometer[2]);

            lastUpdatedTime = System.currentTimeMillis();

            int x = (int) degrees;
            compassOrientation.setText(x + " Degrees");
        }

        if (event.sensor.getType() == sensor.TYPE_LIGHT) {
            tvl1.setText("Sensor Value: " + (int) event.values[0]);
            if (event.values[0] < 3)
                tvl2.setText("Light: \nPITCH BLACK");
            else if (event.values[0] >= 3 && event.values[0] < 10)
                tvl2.setText("Light: \nDARK");
            else if (event.values[0] >= 10 && event.values[0] < 50)
                tvl2.setText("Light: \nDIM");
            else if (event.values[0] >= 50 && event.values[0] < 5000)
                tvl2.setText("Light: \nNORMAL");
            else if (event.values[0] >= 5000 && event.values[0] < 25000)
                tvl2.setText("Light: \nVERY BRIGHT");
            else
                tvl2.setText("Light: \nTHIS WILL BLIND YOU");
        }

        if (event.sensor.getType() == sensor.TYPE_RELATIVE_HUMIDITY) {
            tvh1.setText("Humidity: " + (int) event.values[0] + "%");

        }
        if (event.sensor.getType() == sensor.TYPE_PRESSURE) {
            tvh2.setText("Pressure: " + event.values[0] + " hPa");
        }

        if (event.sensor.getType() == sensor.TYPE_PROXIMITY) {
            tvp1.setText("Distance to Object:\n" + event.values[0] + " cm");
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //I LIKE BIG COCKS
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,gyroscope,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,accelerometer,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,magnetometer,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,brightness,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,humidity,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,pressure,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,proximity,mSensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this,gyroscope);
        mSensorManager.unregisterListener(this,accelerometer);
        mSensorManager.unregisterListener(this,magnetometer);
        mSensorManager.unregisterListener(this,brightness);
        mSensorManager.unregisterListener(this,humidity);
        mSensorManager.unregisterListener(this,pressure);
        mSensorManager.unregisterListener(this,proximity);
    }
}