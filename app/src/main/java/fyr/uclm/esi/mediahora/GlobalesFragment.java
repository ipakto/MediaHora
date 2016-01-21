package fyr.uclm.esi.mediahora;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;

/**
 * Created by Rosana on 17/01/2016.
 */
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

        View v = inflater.inflate(R.layout.layout_global2, container, false);
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
            DecimalFormat df = new DecimalFormat("0.0");
            calGastadas.setText(df.format(kcal) + " kcal");
            calTotales.setText(caloriasT + " kcal");
            int sesionesComp = tiempo / (1000);
            sesCompletadas.setText(sesionesComp + " sesiones");
            sesTotales.setText((int) objetivo + " sesiones");
            sesiones.setText(Math.round(sesionesComp / objetivo * 100) + "%");

            sliceCurrent = new PieModel("", sesionesComp, Color.parseColor("#4d79ff"));
            pg.addPieSlice(sliceCurrent);
            sliceGoal = new PieModel("", objetivo, Color.parseColor("#0033cc"));
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
