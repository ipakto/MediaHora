package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class Prueba extends Fragment
{

    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph) PieChart pg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.prueba,container,false);
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


return v;

        //pg.addPieSlice(sliceGoal);
        //sliceGoal.setValue(100);pg.update();
    }
}
