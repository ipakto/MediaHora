package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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
public class Globales extends Activity {

    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph2)  PieChart pg;
    @Bind(R.id.lblPorcentajeComplet)    TextView porcCompletado;
    @Bind(R.id.lblSesionesCompletadas) TextView sesCompletadas;
    @Bind(R.id.sesiones) TextView sesiones;
    @Bind(R.id.lblSesionesTotales) TextView sesTotales;
    @Bind(R.id.lblCalGastadas) TextView calGastadas;
    @Bind(R.id.lblCalTotales) TextView calTotales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_global);
        ButterKnife.bind(this);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        float objetivo=prefs.getFloat("objetivoSesiones", 0);
        float caloriasT=prefs.getInt("objetivoCalorias", 0);
        if(objetivo==0){
            Toast.makeText(getApplicationContext(), "Para poder consultar esta ventana, seleccione un objetivo en ajustes", Toast.LENGTH_LONG).show();
        }else{

            ConectorBD bd=new ConectorBD(this);
            bd.abrir();
            Cursor c=bd.obtenerTodosValores();
            int  tiempo=0;
            double kcal=0;
            if (c.moveToFirst()) {
                do {
                    kcal+=c.getDouble(2);
                    tiempo+=c.getInt(3);
                } while(c.moveToNext());
            }

            bd.cerrar();
            kcal += prefs.getFloat("calorias",0);
            tiempo+=prefs.getInt("tiempo", 0);
            DecimalFormat df = new DecimalFormat("0.0");
            calGastadas.setText(df.format(kcal)+" kcal");
            calTotales.setText(caloriasT+" kcal");
            int sesionesComp=tiempo/(1000);
            sesCompletadas.setText(sesionesComp+" sesiones");
            sesTotales.setText((int)objetivo+" sesiones");
            sesiones.setText(String.valueOf(sesionesComp));
            porcCompletado.setText(Math.round(sesionesComp/objetivo*100)+"% completado");

            sliceCurrent = new PieModel("", sesionesComp, Color.parseColor("#4d79ff"));
            pg.addPieSlice(sliceCurrent);

            sliceGoal = new PieModel("", objetivo, Color.parseColor("#0033cc"));
            pg.addPieSlice(sliceGoal);
            pg.setDrawValueInPie(false);
            pg.setUsePieRotation(true);
            pg.startAnimation();


        }


        // slice for the steps taken today


        // slice for the "missing" steps until reaching the goal



    }

}
