package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import fyr.uclm.esi.mediahora.dominio.Util;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;

/**
 * Created by Rosana on 15/11/2015.
 */
public class Stats extends Activity
{
    @Bind(R.id.txtCalTotales) TextView caloriasR;
    @Bind(R.id.txtPasosTotales) TextView pasosR;
    @Bind(R.id.txtDistTotal) TextView distanciaR;
    @Bind(R.id.txtTiempoTotal) TextView tiempoR;
    @Bind(R.id.txtVelTotal) TextView velocidadR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estadisticas);
        ButterKnife.bind(this);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        ConectorBD bd=new ConectorBD(this);
        bd.abrir();
        Cursor c=bd.obtenerTodosValores();
        int pasos=0, tiempo=0,distancia=0;
        double kcal=0,velocidad=0;
        if (c.moveToFirst()) {
            do {
                pasos+=c.getInt(1);
                kcal+=c.getDouble(2);
                tiempo+=c.getInt(3);
                //velocidad+=c.getDouble(4);
                distancia+=c.getInt(5);
            } while(c.moveToNext());
        }
        pasos+=prefs.getInt("pasos",0);
        kcal+=prefs.getFloat("calorias",0);
        tiempo+=prefs.getInt("tiempo",0);
        distancia+=prefs.getInt("distancia",0);
        velocidad=(distancia*1000/tiempo)*3.6;
        pasosR.setText(String.valueOf(pasos));
        distanciaR.setText(distancia+ "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadR.setText(df.format(velocidad) + "km/h");
        caloriasR.setText(df.format(kcal)+"kcal");
        tiempoR.setText(fyr.uclm.esi.mediahora.dominio.Util.calcularTiempo(tiempo));
        bd.cerrar();
        Util.cambiarColorStatusBar(this);
    }
}
