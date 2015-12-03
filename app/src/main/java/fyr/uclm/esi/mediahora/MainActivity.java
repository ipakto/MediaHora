package fyr.uclm.esi.mediahora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import butterknife.Bind;
import butterknife.ButterKnife;

import fyr.uclm.esi.mediahora.naview.ContentFragment;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private int contador = 0;
    private int meta=250;
    private static final String TAG = MainActivity.class.getSimpleName();

    // Layout components
    @Bind(R.id.steps)
    TextView mStepsText;
    // @Bind(R.id.detectorTypeText) TextView mDetectorTypeText;
    //@Bind(R.id.statsButton) Button mStatsButton;

    // Variables
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private int mNumSteps;

    //Defining Variables NavBar
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    //Cambio de modulo
    ContentFragment fragment;

    //Circulo pasos
    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph)
    PieChart pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        iniciarGrafico();

        //NAVIGATION BAR

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                /*if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);*/

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Inicio Selected", Toast.LENGTH_SHORT).show();
                        cambiarFragment(R.layout.prueba);
                        /*Intent i=new Intent(MainActivity.this,InfoUsuario.class);
                        startActivity(i);*/
                        return true;

                    case R.id.perfil:
                        Toast.makeText(getApplicationContext(), "Perfil Selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, InfoUsuario.class));
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.stats:
                        Toast.makeText(getApplicationContext(),"Stats Selected",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, InfoUsuario.class));
                        cambiarFragment(R.layout.prueba);
                        return true;
                    case R.id.compartir:
                        Toast.makeText(getApplicationContext(),"Compartir Selected",Toast.LENGTH_SHORT).show();
                        Intent is=new Intent(MainActivity.this,Prueba.class);
                        startActivity(is);

                        return true;
                    case R.id.ajustes:
                        Toast.makeText(getApplicationContext(),"Ajustes Selected",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Opciones.class));
                        return true;
                    case R.id.acerca:
                        Toast.makeText(getApplicationContext(),"Acerca Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.faq:
                        Toast.makeText(getApplicationContext(),"FAQ Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        ContentFragment fragment = new ContentFragment();
        fragment.setLayout(R.layout.prueba);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    private void iniciarGrafico() {
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // Pasos restantes para alcanzar la meta
        sliceGoal = new PieModel("", meta, Color.parseColor("#CC0000"));
        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
    }

    private void actualizarGrafico(){
        sliceCurrent.setValue(mNumSteps);
        if(meta-mNumSteps>0){
            sliceGoal.setValue(meta-mNumSteps);
        }else{
            sliceGoal.setValue(0);
        }
        pg.update();
    }

    private void cambiarFragment(int id){
       ContentFragment fragment = new ContentFragment();
       fragment.setLayout(id);
       android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
       fragmentTransaction.replace(R.id.frame,fragment);
       fragmentTransaction.commit();
   }

    @Bind (R.id.average) TextView a;
    @Bind (R.id.total) TextView t;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                contador++;
                break;
            case Sensor.TYPE_STEP_COUNTER:
                mNumSteps = (int) sensorEvent.values[0];
                break;
        }
        a.setText(String.valueOf(contador));
        t.setText(String.valueOf(mNumSteps));

        mStepsText.setText(String.valueOf(mNumSteps));

        actualizarGrafico();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_about);
                break;

        }
    return true;
    }
}