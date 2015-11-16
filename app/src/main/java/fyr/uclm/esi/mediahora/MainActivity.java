package fyr.uclm.esi.mediahora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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


import butterknife.Bind;
import butterknife.ButterKnife;

import fyr.uclm.esi.mediahora.naview.ContentFragment;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    int contador = 0;
    float valorAntiguo = 0, valorActual = 0;

    private static final String TAG = MainActivity.class.getSimpleName();

    // Layout components
    @Bind(R.id.lblPrueba)
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

       /* mStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.putExtra("numSteps", mNumSteps);
                startActivity(intent);
            }
        });*/

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
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.perfil:
                        Toast.makeText(getApplicationContext(), "Perfil Selected", Toast.LENGTH_SHORT).show();
                        ContentFragment fragment = new ContentFragment();
                        fragment.setLayout(R.layout.info_usuario);
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,fragment);
                        fragmentTransaction.commit();
                        /*Intent i=new Intent(MainActivity.this,InfoUsuario.class);
                        startActivity(i);*/
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.stats:
                        Toast.makeText(getApplicationContext(),"Srats Selected",Toast.LENGTH_SHORT).show();
                        ContentFragment fragment2 = new ContentFragment();
                        fragment2.setLayout(R.layout.estadisticas_inicial);
                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.frame,fragment2);
                        fragmentTransaction2.commit();
                        /*Intent is=new Intent(MainActivity.this,Stats.class);
                        startActivity(is);*/
                        return true;
                    case R.id.compartir:
                        Toast.makeText(getApplicationContext(),"Compartir Selected",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.ajustes:
                        Toast.makeText(getApplicationContext(),"Ajustes Selected",Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;


        if (values.length > 0) {
            mNumSteps = (int) values[0];
        }
        Log.d("PRUEBA", String.valueOf(values[0]));
        Log.d(TAG, "Me muevo" + mNumSteps);
        Log.d(TAG, "mNumSteps: " + mNumSteps);

        mStepsText.setText(String.valueOf(mNumSteps));

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //mDetectorTypeText.setText("steps detected by the step counter");
            Log.d("COUNTER", "he contado");
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            //  mDetectorTypeText.setText("steps detected by the step detector");
            Log.d("DETECTOR", "he contado");
        }
        Log.d("SENSOR", sensor.getName() + " " + sensor.getType());
        /*synchronized(this) {
            switch (sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                 //   for (int i = 0; i < 3; i++) {
                        //log("Acelerómetro " + i + sensorEvent.values[i]);
                        //eje x= values[0]
                    mNumSteps++;
                        if(contador==0){
                            valorAntiguo= sensorEvent.values[0];
                        }
                        if(contador==1){
                            valorActual= sensorEvent.values[0];
                            contador=0;
                           /* if (valorActual-valorAntiguo>4){
                                mNumSteps++;
                            }
                        }
                    contador++;
                   // }
            }
        }*/
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