package fyr.uclm.esi.mediahora;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import butterknife.Bind;
import butterknife.ButterKnife;

import de.hdodenhof.circleimageview.CircleImageView;
import fyr.uclm.esi.mediahora.naview.ContentFragment;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;


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
    private ConectorBD conectorBD;

    //Defining Variables NavBar
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    //Cambio de modulo
    ContentFragment fragment;

    //Circulo pasos
    private PieModel sliceGoal, sliceCurrent;
    @Bind(R.id.graph)PieChart pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        conectorBD = new ConectorBD(this);
        conectorBD.abrir();
        conectorBD.insertarValor(Util.getToday(), 27);
        conectorBD.cerrar();
        Toast.makeText(getBaseContext(), "Se añadió una nueva entrada a la BD!", Toast.LENGTH_SHORT).show();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        iniciarGrafico();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("pCambia",true).commit();
        cargarPreferencias();
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
                        startActivity(new Intent(MainActivity.this, Stats.class));
                        cambiarFragment(R.layout.estadisticas);
                        return true;
                    case R.id.compartir:
                        Toast.makeText(getApplicationContext(),"Compartir Selected",Toast.LENGTH_SHORT).show();

                        return true;
                    case R.id.ajustes:
                        Toast.makeText(getApplicationContext(),"Ha seleccionado Ajustes",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Opciones.class));
                        //getFragmentManager().beginTransaction().replace(android.R.id.content,new Opciones()).addToBackStack(null).commit();
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
                realizarCalculos(mNumSteps);
                break;
        }
        a.setText(String.valueOf(contador));
        t.setText(String.valueOf(mNumSteps));

        mStepsText.setText(String.valueOf(mNumSteps));

        actualizarGrafico();


    }
    @Bind(R.id.txtDist) TextView distRecorrida;
    @Bind(R.id.txtCalorias) TextView caloriasConsumidas;
    @Bind (R.id.txtTiempo) TextView tiempoMedio;
    @Bind (R.id.txtVelocidad) TextView velocidadMedia;
    //Valor de las calorias: http://es.calcuworld.com/deporte-y-ejercicio/calculadora-de-calorias-quemadas/
    public void realizarCalculos(int steps){
        int distancia=0,lZancada=0, minutos=0;
        double velocidad , calorias, nivel;
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        lZancada=prefs.getInt("pDistanciaP", 0); // en cm
        distancia=steps*(lZancada/100); //pasada a metros
        velocidad=(distancia/1000)/(minutos/60); //pasada a kilometros/hora
        if (velocidad <= 2.9){
            nivel=0.010; //no viene la he puesto aproximada
            //calorias=(67.5*minutos)/30;
        }else if(velocidad>3.0 && velocidad <=4.7){
            nivel=0.026;
          //  calorias=(150*minutos)/30;
        }else if(velocidad> 4.8 && velocidad <= 6.0){
            nivel=0.035;
           // calorias=(180*minutos)/30;
        }else{
            nivel=0.0485;
           // calorias=(220*minutos)/30;
        }
        calorias=(2.2*prefs.getInt("pPeso",0))*minutos*nivel;
        calorias=Math.round(calorias*10)/10;
        distRecorrida.setText(distancia+ "m.");
        velocidadMedia.setText(velocidad+ "km./hora");
        caloriasConsumidas.setText(calorias+"cal");
        tiempoMedio.setText(calcularTiempo(minutos));

    }
    public String calcularTiempo(int minutos){
        int h,m,s;
        String hFinal,mFinal,sFinal;
        s=minutos*60;
        h=s/3600;
        m=h-(s/3600)*60;
        s=m-(h-(s/3600)*60)*60;
        if (h < 10) {
            hFinal="0"+h;
        }else{
            hFinal=""+h;
        }
        if (m < 10) {
            mFinal="0"+m;
        }else{
           mFinal=""+m;
        }
        if (s < 10) {
            sFinal="0"+s;
        }else{
            sFinal=""+s;
        }
        return hFinal+":"+mFinal+":"+sFinal;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        cargarPreferencias();
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void cargarPreferencias(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        //En caso de que haya cambiado alguna preferencia, actualizamos el cambio
        if(prefs.getBoolean("pCambia",true)){
            String nombre,mail,fondo,foto;
            Drawable f = null;
            nombre=prefs.getString("pNombre","Su nombre");
            mail=prefs.getString("pCorreo","Su correo");
            fondo=prefs.getString("pFondoPerf","Fondo1");
            foto=prefs.getString("pFoto","");
            TextView n=(TextView) findViewById(R.id.username);
            TextView e=(TextView) findViewById(R.id.email);
            RelativeLayout r=(RelativeLayout) findViewById(R.id.layoutNav);
            CircleImageView c=(CircleImageView)findViewById(R.id.profile_image);

            n.setText(nombre);
            e.setText(mail);
            //Fondo Perfil
            switch (fondo) {
                case "Fondo1":
                    f = this.getResources().getDrawable(R.drawable.fondo1);
                    break;
                case "Fondo2":
                    f = this.getResources().getDrawable(R.drawable.fondo2);
                    break;
                case "Fondo3":
                    f = this.getResources().getDrawable(R.drawable.fondo3);
                    break;
            }
            r.setBackground(f);
            //Foto Perfil
            if(!foto.equals("")) {
                Drawable d = Drawable.createFromPath(foto);
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scale = (width <= height) ? height / 500 : width / 500;
                Bitmap b2 = Bitmap.createScaledBitmap(bitmap, Math.round(width / scale), Math.round(height / scale), false);
                Drawable d2 = new BitmapDrawable(getBaseContext().getResources(), b2);
                c.setImageDrawable(d2);
            }
            prefs.edit().putBoolean("pCambia",false).commit();
        }
    }

}