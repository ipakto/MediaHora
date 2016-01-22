package fyr.uclm.esi.mediahora;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

import de.hdodenhof.circleimageview.CircleImageView;
import fyr.uclm.esi.mediahora.naview.ContentFragment;
import fyr.uclm.esi.mediahora.persistencia.ConectorBD;
import fyr.uclm.esi.mediahora.dominio.Util;


public class MainActivity extends AppCompatActivity {
    private int meta=50;
    private int metaDiaria=30000;
    private static final String TAG = MainActivity.class.getSimpleName();

    // Layout components
    @Bind(R.id.steps)
    TextView mStepsText;

    // Variables
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private int mNumSteps=0;
    private long tiempos[]=new long [3];
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
    @Bind(R.id.btnPaso) Button b;
    @Bind (R.id.button) Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        /*mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);*/

        //iniciarGrafico();
        tiempos[0]=System.currentTimeMillis();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("pCambia",true).commit();

        cargarPreferencias();
        //cargarValores();
        esPrimeraVez();


        cargarAlarma();
        //NAVIGATION BAR
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("ACTUAL"), true);
        tabs.addTab(tabs.newTab().setText("GLOBAL"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("ACTUAL")) {
                    Prueba fg = new Prueba();
                    fg.setActivity(MainActivity.this);
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fg);
                    fragmentTransaction.commit();
                } else {
                    GlobalesFragment fg = new GlobalesFragment();
                    fg.setActivity(MainActivity.this);
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fg);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorSimulado();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Globales.class));
            }
        });
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){

                    case R.id.perfil:
                        startActivity(new Intent(MainActivity.this, InfoUsuario.class));
                        return true;

                    case R.id.stats:
                        startActivity(new Intent(MainActivity.this, Stats.class));
                        return true;

                    case R.id.compartir:
                        compartir();
                        return true;

                    case R.id.ajustes:
                        startActivity(new Intent(MainActivity.this, Opciones.class));
                        return true;

                    case R.id.acerca:
                        startActivity(new Intent(MainActivity.this, AcercaDe.class));
                        return true;

                    case R.id.faq:
                        startActivity(new Intent(MainActivity.this, FAQ.class));
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

        Prueba fg=new Prueba();
        fg.setActivity(this);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fg);
        fragmentTransaction.commit();
       Log.d("O","a");
    }

    private void cargarAlarma(){
        Calendar calendar = Calendar.getInstance();

        /*calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.DAY_OF_MONTH, 17);*/
        int[] horas={12,16,20,23};
        for (int i=0;i<horas.length;i++) {
            calendar.set(Calendar.HOUR_OF_DAY, horas[i]);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.AM_PM, Calendar.PM);

            Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 10, pendingIntent);
        }
           /* AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
            Calendar calendar =  Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 17);
            calendar.set(Calendar.SECOND, 05);
            long when = calendar.getTimeInMillis();         // notification time
            Intent intent = new Intent(this, Alarma.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
            alarmManager.set(AlarmManager.RTC, when, pendingIntent);*/

         /*Intent myIntent = new Intent(MainActivity.this , Alarma.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE,52 );
        calendar.set(Calendar.SECOND, 00);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);  //set repeating every 24 hours*/

    }
    private void insertarEnBD(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        conectorBD = new ConectorBD(this);
        conectorBD.abrir();
        float kcal=prefs.getFloat("calorias",0);
        float velocidad=prefs.getFloat("velocidad",0);
        int tiempo=prefs.getInt("tiempo",0);
        int distancia=prefs.getInt("distancia",0);
        int pasos=prefs.getInt("pasos",0);
        //conectorBD.insertarValor(System.currentTimeMillis(), 27,prefs.getFloat("kcal",0),prefs.getInt("tiempo",0),prefs.getFloat("velocidad",0),prefs.getInt("distancia",0));
        conectorBD.insertarValor(System.currentTimeMillis(),pasos,kcal,tiempo,velocidad,distancia);
        conectorBD.cerrar();
        Toast.makeText(getBaseContext(), "Se añadió una nueva entrada a la BD!", Toast.LENGTH_SHORT).show();

        prefs.edit().putFloat("calorias", 0).commit();
        prefs.edit().putInt("distancia", 0).commit();
        prefs.edit().putFloat("velocidad", 0).commit();
        prefs.edit().putInt("tiempo", 0).commit();
        prefs.edit().putInt("pasos", 0).commit();
    }
    private void copiaBD () throws IOException {


            final String inFileName = "/data/data/fyr.uclm.esi.mediahora/databases/PasosRealizados";
            File dbFile = new File(inFileName);
            FileInputStream fis = null;

            fis = new FileInputStream(dbFile);

            String directorio = "/storage/emulated/0/mediaHora/media/BBDD";
            File d = new File(directorio);
            if (!d.exists()) {
                d.mkdir();
            }
            String outFileName = directorio+"/PasosRealizados" ;

            OutputStream output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();

        }


    private void iniciarGrafico() {
        sliceCurrent = new PieModel("", 0, Color.parseColor("#4d79ff"));
        pg.addPieSlice(sliceCurrent);

        // Pasos restantes para alcanzar la meta
        // sliceGoal = new PieModel("", meta, Color.parseColor("#CC0000"));
        sliceGoal = new PieModel("", 1800000, Color.parseColor("#0033cc"));
        pg.addPieSlice(sliceGoal);
        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
    }

    private void actualizarGrafico(){
        sliceCurrent.setValue(tiempos[2]);
        if(metaDiaria-tiempos[2]>0){
            sliceGoal.setValue(metaDiaria-tiempos[2]);
        }else{
            sliceGoal.setValue(0);
            notificar();
        }
        pg.update();
    }

    private void cambiarFragment(int id){
        ContentFragment fragment = new ContentFragment();
        fragment.setLayout(id);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
    @Bind(R.id.txtDist) TextView distRecorrida;
    @Bind(R.id.txtCalorias) TextView caloriasConsumidas;
    @Bind (R.id.txtTiempo) TextView tiempoMedio;
    @Bind (R.id.txtVelocidad) TextView velocidadMedia;

    /*@Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                contador++;
                Log.d("DETECTOR", "MAIN:Paso detectado");
                break;
            case Sensor.TYPE_STEP_COUNTER:
                //Este primer if borrar cuando no esté el simulador yh descomentar lo comentado
                //mNumSteps = (int) sensorEvent.values[0];
                Log.d("DETECTOR","MAIN:Paso detectado");
                if((mNumSteps-(int)sensorEvent.values[0])<3){
                    mNumSteps=(int) sensorEvent.values[0];
                }
                if(mNumSteps==0){
                    insertarEnBD();
                }
                comprobarTiempo();
                realizarCalculos(mNumSteps);
                break;
        }

        mStepsText.setText(String.valueOf(mNumSteps));

        actualizarGrafico();
    }*/


    public void comprobarTiempo(){
        //tiempos[1]=Util.getToday();
        tiempos[1]=System.currentTimeMillis();
        long res=tiempos[1]-tiempos[0];
        if(res<5000){
            tiempos[2]+=res;
        }
        tiempos[0]=tiempos[1];

    }
    public void sensorSimulado(){
        mNumSteps += 1;
        comprobarTiempo();
        realizarCalculos(mNumSteps);
        mStepsText.setText(String.valueOf(mNumSteps));
        actualizarGrafico();
    }
    public void cargarValores(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        int pasos=prefs.getInt("pasos", 0);
        float kcal=prefs.getFloat("calorias", 0);
        int tiempo=prefs.getInt("tiempo", 0);
        int distancia=prefs.getInt("distancia",0);
        float velocidad=prefs.getFloat("velocidad", 0);
        tiempos[2]=tiempo;
        mNumSteps=pasos;
        distRecorrida.setText(distancia+ "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadMedia.setText(df.format(velocidad) + "km/h");
        caloriasConsumidas.setText(df.format(kcal) + "kcal");
        tiempoMedio.setText(Util.calcularTiempo(tiempo));
        //actualizarGrafico();
    }
    //Valor de las calorias: http://es.calcuworld.com/deporte-y-ejercicio/calculadora-de-calorias-quemadas/
    //double minutos=1;
    public void realizarCalculos(int steps){
        int lZancada=0,distancia;
        double velocidad , calorias, nivel;
        double tiempo=tiempos[2]/1000; //Tiempo en segundos
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        lZancada=prefs.getInt("pDistanciaP", 65); // en cm
        distancia=(int)(steps*lZancada*0.01); //pasada a metros
//        velocidad=(distancia/1000.0)/((tiempo/60)); //pasada a kilometros/hora
        velocidad=(distancia/tiempo)*3.6;
        //velocidad=1;
        if (velocidad <= 2.9){
            nivel=0.010; //no viene la he puesto aproximada
            //calorias=(67.5*minutos)/30;
        }else if(velocidad>3.0 && velocidad <=4.7){
            nivel=0.026;
            //  calorias=(150*minutos)/30   ;
        }else if(velocidad> 4.8 && velocidad <= 6.0){
            nivel=0.035;
            // calorias=(180*minutos)/30;
        }else{
            nivel=0.0485;
            // calorias=(220*minutos)/30;
        }
        //  minutos+=.1;
        calorias=((2.2*prefs.getInt("pPeso",70))*tiempo*nivel)/1000;
        distRecorrida.setText(distancia+ "m");
        DecimalFormat df = new DecimalFormat("0.0");
        velocidadMedia.setText(df.format(velocidad) + "km/h");
        caloriasConsumidas.setText(df.format(calorias)+"kcal");
        tiempoMedio.setText(Util.calcularTiempo(tiempos[2]));
        prefs.edit().putFloat("calorias", (float) calorias).commit();
        prefs.edit().putInt("distancia", distancia).commit();
        prefs.edit().putFloat("velocidad", (float)velocidad).commit();
        prefs.edit().putInt("tiempo", (int)tiempos[2]).commit();
        prefs.edit().putInt("pasos", steps).commit();

    }

   /* @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        //mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        cargarPreferencias();
        cargarValores();
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


    public void notificacion(){
        /*Intent i=new Intent(Intent.ACTION_VIEW);
        PendingIntent pi=PendingIntent.getActivities(this,0,i,0);*/

        NotificationCompat.Builder b=new NotificationCompat.Builder(this);
        //b.setSmallIcon(R.mipmap.ic_launcher);
        b.setAutoCancel(true);
        b.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        b.setContentTitle("¡¡Meta completada!!");
        b.setContentText("Enhorabuena, has caminado " + mNumSteps + " pasos.");

        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1,b.build());
    }

    public void notificar() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);

        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("¡¡ENHORABUENA!!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("¡¡Meta completada!!")
                .setContentText("Enhorabuena, has caminado " + mNumSteps + " pasos.")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setVibrate(new long[]{1000, 500, 1000});
        builder.setLights(Color.CYAN, 1, 0);
        builder.setAutoCancel(true);


        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "Hoy llevo caminados " + mNumSteps + " pasos. Comprueba los tuyos con #MediaHora ");
        PendingIntent pi=PendingIntent.getActivity(this,0,Intent.createChooser(i, "Compartir con"),PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        builder.addAction(android.R.drawable.ic_menu_share, "Compartir", pi);
        int notif_ref = 1;

        notifManager.notify(notif_ref, builder.build());
    }
    private void compartir(){

        Intent intent = new Intent(Intent.ACTION_SEND);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        int pasos=prefs.getInt("pasos", 0);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hoy llevo caminados " + pasos + " pasos. Comprueba los tuyos con #MediaHora ");
        startActivity(Intent.createChooser(intent, "Compartir con"));
    }
    private void esPrimeraVez(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        boolean primeraVez=prefs.getBoolean("primeraVez",true);
        if(primeraVez){
            prefs.edit().putBoolean("primeraVez",false).commit();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DecimalFormat df= new DecimalFormat("0.0");
            builder.setTitle("¡Bienvenid@ a MediaHora!");
            builder.setMessage("Configura tu perfil para poder empezar a cumplir tu objetivo");
            builder.setPositiveButton(R.string.configurarAhora, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    cambiarPref();
                    startActivity(new Intent(MainActivity.this, Opciones.class));
                }
            });
            builder.setNegativeButton(R.string.masTarde, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }
    private void cambiarPref(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("primeraVez",false).commit();
    }
}