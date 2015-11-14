package fyr.uclm.esi.mediahora;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    int contador=0;
    float valorAntiguo=0, valorActual=0;

    private static final String TAG = MainActivity.class.getSimpleName();

    // Layout components
    @Bind (R.id.lblPrueba) TextView mStepsText;
   // @Bind(R.id.detectorTypeText) TextView mDetectorTypeText;
    //@Bind(R.id.statsButton) Button mStatsButton;

    // Variables
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private int mNumSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

       /* mStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.putExtra("numSteps", mNumSteps);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;


        if (values.length > 0) {
            mNumSteps = (int) values[0];
        }
        Log.d("PRUEBA", String.valueOf(values[0]));
        Log.d(TAG, "Me muevo" + mNumSteps);
        Log.d(TAG, "mNumSteps: " + mNumSteps);

        mStepsText.setText(String.valueOf(mNumSteps));

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //mDetectorTypeText.setText("steps detected by the step counter");
            Log.d("COUNTER","he contado");
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
          //  mDetectorTypeText.setText("steps detected by the step detector");
            Log.d("DETECTOR", "he contado");
        }
        Log.d("SENSOR",sensor.getName()+" "+sensor.getType());
        /*synchronized(this) {
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                 //   for (int i = 0; i < 3; i++) {
                        //log("Acelerómetro " + i + sensorEvent.values[i]);
                        //eje x= values[0]
                    mNumSteps++;
                        if(contador==0){
                            valorAntiguo= sensorEvent.values[0];
                        }
                        if(contador==1){
                            valorActual= sensorEvent.values[0];
                            contador=0;
                           /* if (valorActual-valorAntiguo>4){
                                mNumSteps++;
                            }
                        }
                    contador++;
                   // }
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}