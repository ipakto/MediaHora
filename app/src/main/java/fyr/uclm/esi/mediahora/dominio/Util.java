package fyr.uclm.esi.mediahora.dominio;

 import android.app.Activity;
 import android.content.SharedPreferences;
 import android.preference.PreferenceManager;

 import java.util.Calendar;

public  class Util {


    //Caloria por kilo: http://www.caloriasquemadas.com/a/cuantas_calorias_son_un_kilo.html
    public static double calcularTiempoMeta(Activity a){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(a);
        int caloriasKilo, caloriasQuemar;
        double tiempo, tiempoObjetivo;//min
        int pesoActual, pesoDeseado,difPesos;
        pesoActual=prefs.getInt("pPeso",70);
        pesoDeseado=prefs.getInt("pObjetivoPeso",60);
        difPesos=pesoActual-pesoDeseado;
        if(tieneSobrepeso(a)){
            caloriasKilo=8750-2800;
        }else{
            caloriasKilo=7700-2800;
        }
        caloriasQuemar=caloriasKilo*difPesos;
        tiempo=((caloriasQuemar*1000)/(2.2*pesoActual*0.026))/60;
        tiempoObjetivo=tiempo/30;

        prefs.edit().putInt("objetivoCalorias",caloriasQuemar).commit();
        prefs.edit().putFloat("objetivoSesiones",(float)tiempoObjetivo).commit();
        return tiempoObjetivo;
    }

    public static double calcularIMC(Activity a){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(a);
        double altura=prefs.getInt("pAltura",167)/100;
        int peso=prefs.getInt("pPeso", 70);
        double imc=peso/(altura*altura);
        return imc;
    }

    public static boolean tieneSobrepeso(Activity a){
        boolean sobrepeso=false;
        if(calcularIMC(a)>24.9){
            sobrepeso=true;
        }
        return sobrepeso;
    }

    public static String calcularTiempo(long milis){
        int h,m,s;
        String hFinal,mFinal,sFinal;
        s=(int) milis/1000;
        h=s/3600;
        /*m=h-(s/3600)*60;
        s=m-(((h-(s/3600))*60)*60);*/
        m=(s%3600)/60;//(s-(h*3600))/60;
        s-=(h*3600+m*60);
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
}