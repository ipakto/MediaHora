package fyr.uclm.esi.mediahora;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import butterknife.Bind;
import butterknife.ButterKnife;

import fyr.uclm.esi.mediahora.dominio.*;
import fyr.uclm.esi.mediahora.dominio.Util;

/**
 * Created by Paco on 16/01/2016.
 */
class MiThread extends Thread {
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    Context context;
    MainActivity activity;

    private int mNumSteps=0;
    private long tiempos[]=new long [3];

    private int metaDiaria=30000;

    @Bind(R.id.steps)  TextView mStepsText;
    @Bind(R.id.txtDist) TextView distRecorrida;
    @Bind(R.id.txtCalorias) TextView caloriasConsumidas;
    @Bind (R.id.txtTiempo) TextView tiempoMedio;
    @Bind (R.id.txtVelocidad) TextView velocidadMedia;

    //Circulo pasos
    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph)PieChart pg;


    public  MiThread(Context context,MainActivity activity) {
        this.context = context;
        tiempos[0]=System.currentTimeMillis();
        this.activity=activity;
    }

    @Override
    public void run() {
        Log.d("RunTag", Thread.currentThread().getName()); // To display thread
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        MyListener msl = new MyListener();
        mSensorManager.registerListener(msl, mStepCounterSensor, Sensor.TYPE_STEP_COUNTER );
        mSensorManager.registerListener(msl, mStepDetectorSensor, SensorManager.SENSOR_DELAY_UI);

        ButterKnife.bind(activity);
        distRecorrida= (TextView) activity.findViewById(R.id.txtDist);
        caloriasConsumidas= (TextView) activity.findViewById(R.id.txtCalorias);
        tiempoMedio= (TextView) activity.findViewById(R.id.txtTiempo);
        velocidadMedia= (TextView) activity.findViewById(R.id.txtVelocidad);
        mStepsText= (TextView) activity.findViewById(R.id.steps);
        pg=(PieChart)activity.findViewById(R.id.graph);
        iniciarGrafico();
        cargarValores();


    }


    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
                notificar();
            }
        }
    };

    private class MyListener implements SensorEventListener {
        public void onAccuracyChanged (Sensor sensor, int accuracy) {}
        public void onSensorChanged(SensorEvent sensorEvent) {


        switch(sensorEvent.sensor.getType())

        {
            case Sensor.TYPE_STEP_DETECTOR:
                Log.d("DETECTOR", "Paso detectado");
                notificar();
                break;
            case Sensor.TYPE_STEP_COUNTER:
                Log.d("DETECTOR","Paso contado");
                notificar();
                //Este primer if borrar cuando no esté el simulador yh descomentar lo comentado
                //mNumSteps = (int) sensorEvent.values[0];
                if ((mNumSteps - (int) sensorEvent.values[0]) < 3) {
                    mNumSteps = (int) sensorEvent.values[0];
                }
                if (mNumSteps == 0) {
                    //insertarEnBD();
                }
                comprobarTiempo();
                realizarCalculos(mNumSteps);
                break;
        }

        mStepsText.setText(String.valueOf(mNumSteps));

        actualizarGrafico();
    }
}

    public void cargarValores(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        int pasos=prefs.getInt("pasos", 0);
        float kcal=prefs.getFloat("calorias", 0);
        int tiempo=prefs.getInt("tiempo", 0);
        int distancia=prefs.getInt("distancia",0);
        float velocidad=prefs.getFloat("velocidad", 0);
        tiempos[2]=tiempo;
        mNumSteps=pasos;
        distRecorrida.setText(distancia+ "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadMedia.setText(df.format(velocidad) + "km/h");
        caloriasConsumidas.setText(df.format(kcal) + "kcal");
        tiempoMedio.setText(Util.calcularTiempo(tiempo));
        //actualizarGrafico();
    }

    public void comprobarTiempo(){
        //tiempos[1]=Util.getToday();
        tiempos[1]=System.currentTimeMillis();
        long res=tiempos[1]-tiempos[0];
        if(res<5000){
            tiempos[2]+=res;
        }
        tiempos[0]=tiempos[1];

    }

    public void realizarCalculos(int steps){
        int lZancada=0,distancia;
        double velocidad , calorias, nivel;
        double tiempo=tiempos[2]/1000; //Tiempo en segundos
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        lZancada=prefs.getInt("pDistanciaP", 65); // en cm
        distancia=(int)(steps*lZancada*0.01); //pasada a metros
//        velocidad=(distancia/1000.0)/((tiempo/60)); //pasada a kilometros/hora
        velocidad=(distancia/tiempo)*3.6;
        //velocidad=1;
        if (velocidad <= 2.9){
            nivel=0.010; //no viene la he puesto aproximada
            //calorias=(67.5*minutos)/30;
        }else if(velocidad>3.0 && velocidad <=4.7){
            nivel=0.026;
            //  calorias=(150*minutos)/30   ;
        }else if(velocidad> 4.8 && velocidad <= 6.0){
            nivel=0.035;
            // calorias=(180*minutos)/30;
        }else{
            nivel=0.0485;
            // calorias=(220*minutos)/30;
        }
        //  minutos+=.1;
        calorias=((2.2*prefs.getInt("pPeso",70))*tiempo*nivel)/1000;
        distRecorrida.setText(distancia+ "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadMedia.setText(df.format(velocidad) + "km/h");
        caloriasConsumidas.setText(df.format(calorias)+"kcal");
        tiempoMedio.setText(fyr.uclm.esi.mediahora.dominio.Util.calcularTiempo(tiempos[2]));
        prefs.edit().putFloat("calorias", (float) calorias).commit();
        prefs.edit().putInt("distancia", distancia).commit();
        prefs.edit().putFloat("velocidad", (float)velocidad).commit();
        prefs.edit().putInt("tiempo", (int)tiempos[2]).commit();
        prefs.edit().putInt("pasos", steps).commit();

    }

    private void iniciarGrafico() {
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
    //    pg.addPieSlice(sliceCurrent);

        // Pasos restantes para alcanzar la meta
        // sliceGoal = new PieModel("", meta, Color.parseColor("#CC0000"));
        sliceGoal = new PieModel("", 1800000, Color.parseColor("#CC0000"));
      /*  pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();*/
    }

    private void actualizarGrafico(){
       /* sliceCurrent.setValue(mNumSteps);
        if(meta-mNumSteps>0){
            sliceGoal.setValue(meta-mNumSteps);
        }else{
            sliceGoal.setValue(0);
            notificar();
        }
        pg.update();*/
        sliceCurrent.setValue(tiempos[2]);
        if(metaDiaria-tiempos[2]>0){
            sliceGoal.setValue(metaDiaria-tiempos[2]);
        }else{
            sliceGoal.setValue(0);
            notificar();
        }
        pg.update();
    }

    private void notificar() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);

        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("¡¡ENHORABUENA!!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("¡¡Meta completada!!")
                .setContentText("Enhorabuena, has caminado pasos.");
        builder.setVibrate(new long[]{1000, 500, 1000});
        builder.setLights(Color.CYAN, 1, 0);
        builder.setAutoCancel(true);


        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Hoy llevo caminados  pasos. Comprueba los tuyos con #MediaHora ");
        PendingIntent pi=PendingIntent.getActivity(context,0,Intent.createChooser(i, "Compartir con"),PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        builder.addAction(android.R.drawable.ic_menu_share, "Compartir", pi);
        int notif_ref = 1;

        notifManager.notify(notif_ref, builder.build());
    }
}
