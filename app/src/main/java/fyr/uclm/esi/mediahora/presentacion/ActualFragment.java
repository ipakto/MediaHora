package fyr.uclm.esi.mediahora.presentacion;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.PieChart;

import org.eazegraph.lib.models.PieModel;

import fyr.uclm.esi.mediahora.R;
import fyr.uclm.esi.mediahora.dominio.ThreadDetector;

/**
 * Created by Paco on 16/11/2015.
 */
public class ActualFragment extends Fragment {

    private PieModel sliceGoal, sliceCurrent;
    PieChart pg;
    MainActivity activity;
    static ThreadDetector hilo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.actual,container,false);
        pg=(PieChart) v.findViewById(R.id.graphPasos);
        iniciarGrafico();
        hilo= new ThreadDetector(activity.getBaseContext(),activity,v);
        hilo.cargarSlices(sliceGoal,sliceCurrent);
        hilo.start();
        return v;
    }

    public static void cambiarSensibilidad(String sensibilidad){
        if(hilo!=null){
            hilo.setSensibilidad(Float.valueOf(sensibilidad));
        }
    }

    public static void insertarEnBD(){
        if(hilo!=null){
            hilo.insertarEnBD();
            Log.d("DEBUG", "HE INSERTADO");
        }
    }

    private void iniciarGrafico() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(activity);
        int current=prefs.getInt("tiempo",0);
        sliceCurrent = new PieModel("", current, Color.parseColor("#4d79ff"));
        pg.addPieSlice(sliceCurrent);
        if(1800000-current>0) sliceGoal = new PieModel("", 1800000-current, Color.parseColor("#0033cc"));
        else sliceGoal = new PieModel("", 0, Color.parseColor("#0033cc"));

        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
    }

    public void setActivity(MainActivity a){
        this.activity=a;
    }


}
