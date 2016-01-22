package fyr.uclm.esi.mediahora;

/**
 * Created by Paco on 17/01/2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;


public class MyAlarmService extends Service
{
    String [] mensajesMotivadores={"La sonrisa es la mejor receta para caminar feliz por la vida. Sonríe mucho y camina más.","Como dijo Cervantes: \"El que lee mucho y anda mucho, ve mucho y sabe mucho\".","Haz camino como dijo Machado."};
    String [] mensajesAviso={"No hay ningún camino que se acabe, como no se opongan a la pereza y ociosidad, dijo Cervantes. ¿A qué esperas para cumplir tu objetivo diario?","Las uñas de los pies te crecerán igual las mires o no. ¡Muévete!","Suelen decir que todo esfuerzo tiene su recompensa, vence la pereza y sal a cumplir tu objetivo diario, lo agradecerás.","Da igual a quién escojas de aliado: al perro, la lluvia, nieve, el sol o al vecino. ¡Muévete, son sólo treinta minutos!"};
    //   String [] mensajesFelicitacion={"¡Así se hace! Has completado tu objetivo diario, comida sana, desncansa y mañana a por más.", "Ya estás más cerca de tu objetivo, ¡sigue así!", "Por hoy ya has acabado, ¿o no? Si te atreves, ve a por más, si no te estará esperando mañana." };
    //  String [] mensajesFinal={ "¡Enhorabuena! Congratulations! Félicitations! Herzlichen Glückwunsch! Da igual en el idioma que te lo digamos, la satisfacción ya es tuya."};
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Log.i("DEBUG", "HOLA");
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),MainActivity.class);

        Calendar calendar= Calendar.getInstance();
        Log.i("Debug",String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        //Con esto de Calendar.get hour of day haremos un switch para comprobar las distintas horas.
        int tope=30*60*60*1000;
        int posMensaje=0;
        String mensaje="";
        switch (calendar.get(calendar.HOUR_OF_DAY)){
            case 12:
                if(prefs.getInt("tiempo", 0)<tope/2){
                    posMensaje=(int)Math.random()*(mensajesMotivadores.length+1);
                    Log.i("d",mensajesMotivadores[posMensaje]);
                    mensaje=mensajesMotivadores[posMensaje];
                }
                break;
            case 16:
                if(prefs.getInt("tiempo", 0)<tope*0.7){
                    posMensaje=(int)Math.random()*(mensajesMotivadores.length+1);
                    Log.i("d",mensajesMotivadores[posMensaje]);
                    mensaje=mensajesMotivadores[posMensaje];
                }
                break;
            case 20:
                if(prefs.getInt("tiempo", 0)<tope/2){
                    posMensaje=(int)Math.random()*(mensajesAviso.length+1);
                    Log.i("d",mensajesAviso[posMensaje]);
                    mensaje=mensajesAviso[posMensaje];
                }
                break;
            case 23:
                if(prefs.getInt("tiempo", 0)<tope*0.7){
                    posMensaje=(int)Math.random()*(mensajesAviso.length+1);
                    Log.i("d",mensajesAviso[posMensaje]);
                    mensaje=mensajesAviso[posMensaje];
                }
                break;
            case 00:
                //MainActivity.insertarEnBD();
                break;

        }

        Notification notification = new Notification(R.drawable.icon, mensaje, System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "Alarma", mensaje, pendingNotificationIntent);

        mManager.notify(0, notification);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}