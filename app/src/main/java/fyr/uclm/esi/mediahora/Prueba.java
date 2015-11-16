package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import org.eazegraph.lib.charts.PieChart;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Paco on 16/11/2015.
 */
public class Prueba extends Activity
{

    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph) PieChart pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba);
        ButterKnife.bind(this);



        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", 999, Color.parseColor("#CC0000"));
        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();

        sliceCurrent.setValue(300);
        //pg.addPieSlice(sliceGoal);
        //sliceGoal.setValue(100);pg.update();
    }
}
