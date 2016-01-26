package fyr.uclm.esi.mediahora.presentacion;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import fyr.uclm.esi.mediahora.dominio.MyReceiver;
import fyr.uclm.esi.mediahora.R;

/******************************************************************************************
 * *******************************MULTIMEDIA***********************************************
 * ******************ESCUELA SUPERIOR DE INFORMÁTICA(UCLM)*********************************
 * ************************PRÁCTICA REALIZADA POR:*****************************************
 *       *                                                                                *
 *		 * 				- Francisco Ruiz Romero											  *
 *		 * 				- Rosana Rodríguez-Bobada Aranda								  *
 * 																						  *
 ******************************************************************************************/

public class MainActivity extends AppCompatActivity {

    //Defining Variables NavBar
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("pCambia",true).commit();
        cargarPreferencias();
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
                    ActualFragment fg = new ActualFragment();
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
                        startActivity(new Intent(MainActivity.this, Estadisticas.class));
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

        ActualFragment fg=new ActualFragment();
        fg.setActivity(this);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fg);
        fragmentTransaction.commit();
    }

    private void cargarAlarma(){
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int[] horas={00,12,16,20,23};
        for (int i=0;i<horas.length;i++) {
            calendar.set(Calendar.HOUR_OF_DAY, horas[i]);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.AM_PM, Calendar.PM);

            Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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