package fyr.uclm.esi.mediahora.presentacion;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

import fyr.uclm.esi.mediahora.R;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;

/******************************************************************************************
 * *******************************MULTIMEDIA***********************************************
 * ******************ESCUELA SUPERIOR DE INFORMÁTICA(UCLM)*********************************
 * ************************PRÁCTICA REALIZADA POR:*****************************************
 *       *                                                                                *
 *		 * 				- Francisco Ruiz Romero											  *
 *		 * 				- Rosana Rodríguez-Bobada Aranda								  *
 * 																						  *
 ******************************************************************************************/
public class GlobalesFragment extends Fragment {

    private PieModel sliceGoal, sliceCurrent;
    PieChart pg;
    TextView sesCompletadas;
    TextView sesiones;
    TextView sesTotales;
    TextView calGastadas;
    TextView calTotales;
    Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.global, container, false);
        pg=(PieChart) v.findViewById(R.id.graph2);
        sesCompletadas=(TextView) v.findViewById(R.id.lblSesionesCompletadas);
        sesiones =(TextView) v.findViewById(R.id.sesiones);
        sesTotales=(TextView) v.findViewById(R.id.lblSesionesTotales);
        calGastadas=(TextView) v.findViewById(R.id.lblCalGastadas);
        calTotales=(TextView) v.findViewById(R.id.lblCalTotales);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        float objetivo = prefs.getFloat("objetivoSesiones", 0);
        float caloriasT = prefs.getInt("objetivoCalorias", 0);
        if (objetivo == 0) {
            Toast.makeText(activity.getApplicationContext(), "Para poder consultar esta ventana, seleccione un objetivo en ajustes", Toast.LENGTH_LONG).show();
        } else {

            ConectorBD bd = new ConectorBD(activity);
            bd.abrir();
            Cursor c = bd.obtenerTodosValores();
            int tiempo = 0;
            double kcal = 0;
            if (c.moveToFirst()) {
                do {
                    kcal += c.getDouble(2);
                    tiempo += c.getInt(3);
                } while (c.moveToNext());
            }

            bd.cerrar();
            kcal += prefs.getFloat("calorias", 0);
            tiempo += prefs.getInt("tiempo", 0);

            calTotales.setText(caloriasT + " kcal");
            sesTotales.setText((int) objetivo + " sesiones");
            int sesionesComp = tiempo / (1000*60*30);
            int porcentaje=Math.round(sesionesComp / objetivo * 100);
            DecimalFormat df = new DecimalFormat("0.0");
            if(porcentaje>=100){
                calGastadas.setText(caloriasT + " kcal");
                sesCompletadas.setText((int) objetivo + " sesiones");
                sesiones.setText("100%");
                sliceCurrent = new PieModel("", sesionesComp, Color.parseColor("#4d79ff"));
                sliceGoal = new PieModel("", 0, Color.parseColor("#0033cc"));

            }else{
                calGastadas.setText(df.format(kcal) + " kcal");
                sesCompletadas.setText(sesionesComp + " sesiones");
                sesiones.setText(Math.round(sesionesComp / objetivo * 100) + "%");
                sliceCurrent = new PieModel("", sesionesComp, Color.parseColor("#4d79ff"));
                sliceGoal = new PieModel("", objetivo-sesionesComp, Color.parseColor("#0033cc"));
            }

            pg.addPieSlice(sliceCurrent);
            pg.addPieSlice(sliceGoal);
            pg.setDrawValueInPie(false);
            pg.setUsePieRotation(true);
            pg.startAnimation();


        }


        // slice for the steps taken today

        return v;
        // slice for the "missing" steps until reaching the goal

    }

    public void setActivity(Activity a){
        this.activity=a;
    }

}
