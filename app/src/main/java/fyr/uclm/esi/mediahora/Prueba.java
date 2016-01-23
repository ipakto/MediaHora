package fyr.uclm.esi.mediahora;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;

import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

import fyr.uclm.esi.mediahora.dominio.*;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;

/**
 * Created by Paco on 16/11/2015.
 */
public class Prueba extends Fragment
{

    private PieModel sliceGoal, sliceCurrent;
    PieChart pg;

    private int mNumSteps=0;
    private long tiempos[]=new long [3];
    private ConectorBD conectorBD;
    MainActivity activity;
    private int metaDiaria=1800000;


    TextView distRecorrida;
    TextView caloriasConsumidas;
    TextView tiempoMedio;
    TextView velocidadMedia;
    TextView mStepsText;
    Button b;
    static PruebaThread hilo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.prueba,container,false);
        recuperarElementos(v);
        iniciarGrafico();
        cargarValores();
        //MiThread hilo = new MiThread(activity.getBaseContext(),activity,v);
        hilo= new PruebaThread(activity.getBaseContext(),activity,v);
        hilo.start();
        /*MiThread hilo = new MiThread(getContext(),activity);
        hilo.start();*/


        return v;
    }
    public static void cambiarSensibilidad(String sensibilidad){
        if(hilo!=null){
            hilo.setSensibilidad(Float.valueOf(sensibilidad));
        }
    }
    private void recuperarElementos(View v){
        pg=(PieChart) v.findViewById(R.id.graphPasos);
        distRecorrida=(TextView) v.findViewById(R.id.txtDist);
        caloriasConsumidas=(TextView) v.findViewById(R.id.txtCalorias);
        tiempoMedio=(TextView) v.findViewById(R.id.txtTiempo);
        velocidadMedia=(TextView) v.findViewById(R.id.txtVelocidad);
        mStepsText=(TextView) v.findViewById(R.id.steps);
        b=(Button) v.findViewById(R.id.btnPaso);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorSimulado();
            }
        });
        tiempos[0]=System.currentTimeMillis();
    }
    private void iniciarGrafico() {
        sliceCurrent = new PieModel("", 0, Color.parseColor("#4d79ff"));
        pg.addPieSlice(sliceCurrent);

        // Pasos restantes para alcanzar la meta
        // sliceGoal = new PieModel("", meta, Color.parseColor("#CC0000"));
        sliceGoal = new PieModel("", 1800000, Color.parseColor("#0033cc"));
        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
    }
    public void sensorSimulado(){
        mNumSteps += 1;
        comprobarTiempo();
        realizarCalculos(mNumSteps);
        mStepsText.setText(String.valueOf(mNumSteps));
        actualizarGrafico();
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
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(activity);
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
    public void cargarValores(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(activity);
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
        tiempoMedio.setText(fyr.uclm.esi.mediahora.dominio.Util.calcularTiempo(tiempo));
        mStepsText.setText(String.valueOf(pasos));
        actualizarGrafico();
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
            activity.notificar();
        }
        pg.update();
    }
    public void setActivity(MainActivity a){
        this.activity=a;
    }

}
