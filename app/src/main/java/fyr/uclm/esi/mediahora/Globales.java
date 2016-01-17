package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rosana on 17/01/2016.
 */
public class Globales extends Activity {

    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph2)
    PieChart pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_global);
        ButterKnife.bind(this);
        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#4d79ff"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", 999, Color.parseColor("#0033cc"));
        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();

        sliceCurrent.setValue(300);


    }

}
