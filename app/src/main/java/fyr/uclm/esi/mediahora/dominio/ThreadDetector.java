package fyr.uclm.esi.mediahora.dominio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import fyr.uclm.esi.mediahora.R;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;
import fyr.uclm.esi.mediahora.presentacion.MainActivity;

/**
 * Created by Paco on 21/01/2016.
 */
public class ThreadDetector extends Thread {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Listener listener;

    Context context;
    MainActivity activity;
    View view;

    private int mNumSteps = 0;
    private long tiempos[] = new long[3];

    private int metaDiaria = 1800000;
    private boolean notificado = false;
    @Bind(R.id.steps)
    TextView mStepsText;
    @Bind(R.id.txtDist)
    TextView distRecorrida;
    @Bind(R.id.txtCalorias)
    TextView caloriasConsumidas;
    @Bind(R.id.txtTiempo)
    TextView tiempoMedio;
    @Bind(R.id.txtVelocidad)
    TextView velocidadMedia;


    //Circulo pasos
    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graphPasos)
    PieChart pg;

    public ThreadDetector(Context context, MainActivity activity, View v) {
        this.context = context;
        tiempos[0] = System.currentTimeMillis();
        this.activity = activity;
        this.view = v;
    }

    @Override
    public void run() {
        // Start detecting
        listener = new Listener();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(listener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        ButterKnife.bind(view);
        distRecorrida = (TextView) view.findViewById(R.id.txtDist);
        caloriasConsumidas = (TextView) view.findViewById(R.id.txtCalorias);
        tiempoMedio = (TextView) view.findViewById(R.id.txtTiempo);
        velocidadMedia = (TextView) view.findViewById(R.id.txtVelocidad);
        mStepsText = (TextView) view.findViewById(R.id.steps);
        pg = (PieChart) view.findViewById(R.id.graphPasos);
        //iniciarGrafico();
        cargarValores();
    }

    public void setSensibilidad(float sensibilidad){
        listener.setSensibilidad(sensibilidad);
    }


    private class Listener implements SensorEventListener {

        private float mLimit;
        private float mLastValues[] = new float[3 * 2];
        private float mScale[] = new float[2];
        private float mYOffset;

        private float mLastDirections[] = new float[3 * 2];
        private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
        private float mLastDiff[] = new float[3 * 2];
        private int mLastMatch = -1;



        public Listener() {
            int h = 480; // TODO: remove this constant
            mYOffset = h * 0.5f;
            mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
            mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(activity);
            mLimit =Float.parseFloat(prefs.getString("pSensibilidad","10"));
        }

        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            synchronized (this) {
                if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                } else {
                    int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                    if (j == 1) {
                        float vSum = 0;
                        for (int i = 0; i < 3; i++) {
                            final float v = mYOffset + event.values[i] * mScale[j];
                            vSum += v;
                        }

                        int k = 0;
                        float v = vSum / 3;

                        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                        if (direction == -mLastDirections[k]) {
                            // Direction changed
                            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                            mLastExtremes[extType][k] = mLastValues[k];
                            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                            if (diff > mLimit) {

                                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                                boolean isNotContra = (mLastMatch != 1 - extType);

                                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                    Log.i("PASO", "step");
                                    mNumSteps++;
                                    comprobarTiempo();
                                    realizarCalculos(mNumSteps);
                                    mStepsText.setText(String.valueOf(mNumSteps));
                                    actualizarGrafico();
                                    mLastMatch = extType;
                                } else {
                                    mLastMatch = -1;
                                }
                            }
                            mLastDiff[k] = diff;
                        }
                        mLastDirections[k] = direction;
                        mLastValues[k] = v;
                    }
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        public void setSensibilidad(float sensibilidad){
            mLimit=sensibilidad;
        }
    }
    public void insertarEnBD(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(activity);
        ConectorBD conectorBD = new ConectorBD(activity);
        conectorBD.abrir();
        float kcal=prefs.getFloat("calorias",0);
        float velocidad=prefs.getFloat("velocidad",0);
        int tiempo=prefs.getInt("tiempo",0);
        int distancia=prefs.getInt("distancia",0);
        int pasos=prefs.getInt("pasos", 0);
        conectorBD.insertarValor(System.currentTimeMillis(),pasos,kcal,tiempo,velocidad,distancia);
        conectorBD.cerrar();
        notificado=false;

        prefs.edit().putFloat("calorias", 0).commit();
        prefs.edit().putInt("distancia", 0).commit();
        prefs.edit().putFloat("velocidad", 0).commit();
        prefs.edit().putInt("tiempo", 0).commit();
        prefs.edit().putInt("pasos", 0).commit();
        tiempos[2]=0;
        cargarValores();
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
        distRecorrida.setText(distancia + "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadMedia.setText(df.format(velocidad) + "km/h");
        caloriasConsumidas.setText(df.format(kcal) + "kcal");
        tiempoMedio.setText(fyr.uclm.esi.mediahora.dominio.Util.calcularTiempo(tiempo));
        mStepsText.setText(String.valueOf(pasos));
    }
    public void cargarSlices(PieModel goal,PieModel current){
        sliceCurrent=current;
        sliceGoal=goal;

    }
    public void comprobarTiempo(){
        tiempos[1]=System.currentTimeMillis();
        long res=tiempos[1]-tiempos[0];
        if(res<5000){
            tiempos[2]+=res;
        }
        tiempos[0]=tiempos[1];

    }

    public void realizarCalculos(int steps){
        int lZancada=0,distancia,peso;
        double velocidad , calorias, nivel;
        double tiempo=tiempos[2]/1000; //Tiempo en segundos
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        lZancada=prefs.getInt("pDistanciaP", 65); // en cm
        distancia=(int)(steps*lZancada*0.01); //pasada a metros
        peso=prefs.getInt("pPeso",70);//en kg
        velocidad=(distancia/tiempo)*3.6;
        if (velocidad <= 2.9) nivel=2.5;
        else if(velocidad>3.0 && velocidad <=4.7) nivel=3;
        else if(velocidad> 4.8 && velocidad <= 6.0) nivel=4;
        else nivel = 4.85;
        calorias=peso*nivel*tiempo/3600;
        distRecorrida.setText(distancia+ "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadMedia.setText(df.format(velocidad) + "km/h");
        caloriasConsumidas.setText(df.format(calorias)+"kcal");
        tiempoMedio.setText(fyr.uclm.esi.mediahora.dominio.Util.calcularTiempo(tiempos[2]));
        mStepsText.setText(String.valueOf(steps));
        prefs.edit().putFloat("calorias", (float) calorias).commit();
        prefs.edit().putInt("distancia", distancia).commit();
        prefs.edit().putFloat("velocidad", (float)velocidad).commit();
        prefs.edit().putInt("tiempo", (int)tiempos[2]).commit();
        prefs.edit().putInt("pasos", steps).commit();

    }


    private void actualizarGrafico(){
        sliceCurrent.setValue(tiempos[2]);
        if(metaDiaria-tiempos[2]>0){
            sliceGoal.setValue(metaDiaria-tiempos[2]);
        }else{
            sliceGoal.setValue(0);
            if(!notificado) {
                notificar();
                notificado=true;
            }
        }
        pg.update();
    }

    private void notificar() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        String [] mensajesFelicitacion={"¡Así se hace! Has completado tu objetivo diario, comida sana, desncansa y mañana a por más.", "Ya estás más cerca de tu objetivo, ¡sigue así!", "Por hoy ya has acabado, ¿o no? Si te atreves, ve a por más, si no te estará esperando mañana." };
        int posMensaje=(int)Math.random()*(mensajesFelicitacion.length+1);

        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("¡¡ENHORABUENA!!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("¡¡Meta completada!!")
                .setContentText(mensajesFelicitacion[posMensaje]);
        builder.setVibrate(new long[]{1000, 500, 1000});
        builder.setLights(Color.CYAN, 1, 0);
        builder.setAutoCancel(true);


        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Hoy llevo caminados "+ mNumSteps+" pasos. Comprueba los tuyos con #MediaHora ");
        PendingIntent pi=PendingIntent.getActivity(context,0,Intent.createChooser(i, "Compartir con"),PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        builder.addAction(android.R.drawable.ic_menu_share, "Compartir", pi);
        int notif_ref = 1;

        notifManager.notify(notif_ref, builder.build());
    }
}
