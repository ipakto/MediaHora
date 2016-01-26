package fyr.uclm.esi.mediahora.presentacion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fyr.uclm.esi.mediahora.R;
import fyr.uclm.esi.mediahora.dominio.Util;
import fyr.uclm.esi.mediahora.presentacion.ActualFragment;

/**
 * Created by Paco on 28/11/2015.
 */
/*
 * IMAGENES: https://github.com/Roberasd/Pictures-Android
 */

public class Opciones extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener{
    //Trato de imagenes
    private String APP_DIRECTORY = "mediaHora/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private String TEMPORAL_PICTURE_NAME = "perfil.jpg";

    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    private ImageView imageView;
    private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    String nombreF;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.opciones);
        findPreference("pNombre").setOnPreferenceChangeListener(this);
        findPreference("pCorreo").setOnPreferenceChangeListener(this);
        findPreference("pFondoPerf").setOnPreferenceChangeListener(this);
        findPreference("pFoto").setOnPreferenceClickListener(this);
        findPreference("pEdad").setOnPreferenceClickListener(this);
        findPreference("pSexo").setOnPreferenceChangeListener(this);
        findPreference("pAltura").setOnPreferenceClickListener(this);
        findPreference("pPeso").setOnPreferenceClickListener(this);
        findPreference("pDistanciaP").setOnPreferenceClickListener(this);
        findPreference("pObjetivoPeso").setOnPreferenceClickListener(this);
        findPreference("pSensibilidad").setOnPreferenceChangeListener(this);
        completarCampos();

        Util.cambiarColorStatusBar(this);


    }
    private void completarCampos(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        findPreference("pNombre").setSummary(prefs.getString("pNombre", getStr(R.string.desc_nombre)));
        findPreference("pCorreo").setSummary(prefs.getString("pCorreo", getStr(R.string.desc_correo)));
        findPreference("pFondoPerf").setSummary(prefs.getString("pFondoPref", getStr(R.string.img_fondo)));
        findPreference("pObjetivoPeso").setSummary(prefs.getInt("pObjetivoPeso", 5) + " kg.");
        getPrefValue("pEdad", prefs, " años");
        findPreference("pSexo").setSummary(prefs.getString("pSexo", getStr(R.string.desc_calorias)));
        findPreference("pSensibilidad").setSummary(getNombre(prefs.getString("pSensibilidad", getStr(R.string.desc_sens))));
        getPrefValue("pAltura", prefs, " cm.");
        getPrefValue("pPeso", prefs, " kg.");
        getPrefValue("pDistanciaP", prefs, " cm.");
        getPrefObjetivo("pObjetivoPeso", prefs);
    }

    private String getStr(int id){
        return getResources().getString(id);
    }
    private void getPrefValue(String nombreP, SharedPreferences prefs, String unidad){
        int value=prefs.getInt(nombreP, -1);
        if(value==-1){
            findPreference(nombreP).setSummary(getStr(R.string.desc_calorias));
        }else{
            findPreference(nombreP).setSummary(String.valueOf(value)+unidad);
        }
    }
    private void getPrefObjetivo(String nombreP, SharedPreferences prefs){
        int value=prefs.getInt(nombreP, -1);
        if(value==-1){
            findPreference(nombreP).setSummary(getStr(R.string.pref_objetivo));
        }else{
            findPreference(nombreP).setSummary(String.valueOf(value) + "kg.");
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        switch (preference.getTitleRes()) {
            case R.string.pref_sexo:
                prefs.edit().putString("pSexo",String.valueOf(newValue)).commit();
                preference.setSummary(String.valueOf(newValue));
                break;
            case R.string.pref_sens:
                prefs.edit().putString("pSensibilidad",String.valueOf(newValue)).commit();
                preference.setSummary(getNombre(String.valueOf(newValue)));
                ActualFragment.cambiarSensibilidad(String.valueOf(newValue));
                break;
            case R.string.pref_nombre:
                prefs.edit().putString("pNombre", String.valueOf(newValue)).commit();
                preference.setSummary(String.valueOf(newValue));
                cambiaPreferencias();
                break;
            case R.string.pref_correo:
                prefs.edit().putString("pCorreo", String.valueOf(newValue)).commit();
                preference.setSummary(String.valueOf(newValue));
                cambiaPreferencias();
                break;
            case R.string.img_fondo:
                prefs.edit().putString("pFondoPref", String.valueOf(newValue)).commit();
                preference.setSummary(String.valueOf(newValue));
                cambiaPreferencias();
                break;
        }
        return true;
    }
    private String getNombre(String value){
        String nombre="";
        switch(value){
            case "1.9753":
                nombre="Extra alta";
                break;
            case "2.9630":
                nombre="Muy alta";
                break;
            case "4.4444":
                nombre="Alta";
                break;
            case "6.6667":
                nombre= "Superior";
                break;
            case "10":
                nombre= "Media";
                break;
            case "5":
                nombre="Inferior";
                break;
            case "22.5":
                nombre= "Baja";
                break;
            case "33.75":
                nombre= "Muy baja";
                break;
            case "50.625":
                nombre="Extra baja";
                break;
            default:
                nombre=value;
                break;
        }
        return nombre;
    }
    @Override
    public boolean onPreferenceClick(final Preference preference){
        final AlertDialog.Builder builder;
        final NumberPicker np;
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        switch (preference.getTitleRes()) {
            case R.string.fotoPerfil:
                final CharSequence[] options = {getStr(R.string.tomarFoto), getStr(R.string.elegirGaleria), getStr(R.string.cancelar)};
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.sel_foto);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if(options[seleccion].equals(getStr(R.string.tomarFoto))){
                            openCamera();
                        }else if (options[seleccion].equals(getStr(R.string.elegirGaleria))) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, getStr(R.string.sel_foto)), SELECT_PICTURE);
                        }else if(options[seleccion].equals(getStr(R.string.cancelar))){
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                break;
            case R.string.pref_edad:
                builder = new AlertDialog.Builder(this);
                np = new NumberPicker(this);
                np.setMinValue(1);
                np.setMaxValue(120);
                np.setValue(prefs.getInt("pEdad",20));
                builder.setView(np);
                builder.setTitle(R.string.sel_edad);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        findPreference("pEdad").getEditor().putInt("pEdad", np.getValue()).commit();
                        preference.setSummary(String.valueOf(np.getValue())+" años");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

            case R.string.pref_altura:
                builder = new AlertDialog.Builder(this);
                np = new NumberPicker(this);
                np.setMinValue(80);
                np.setMaxValue(230);
                np.setValue(prefs.getInt("pAltura",170));
                builder.setView(np);

                builder.setTitle(R.string.sel_altura);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        findPreference("pAltura").getEditor().putInt("pAltura", np.getValue()).commit();
                        preference.setSummary(String.valueOf(np.getValue())+" cm.");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.string.pref_peso:
                builder = new AlertDialog.Builder(this);
                np = new NumberPicker(this);
                np.setMinValue(10);
                np.setMaxValue(120);
                np.setValue(prefs.getInt("pPeso",70));
                builder.setView(np);
                builder.setTitle(R.string.sel_peso);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        findPreference("pPeso").getEditor().putInt("pPeso", np.getValue()).commit();
                        preference.setSummary(String.valueOf(np.getValue())+" kg.");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.string.pref_dist:
                builder = new AlertDialog.Builder(this);
                np = new NumberPicker(this);
                np.setMinValue(0);
                np.setMaxValue(100);
                np.setValue(prefs.getInt("pDistanciaP",60));
                builder.setView(np);
                builder.setTitle(R.string.sel_dist);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        findPreference("pDistanciaP").getEditor().putInt("pDistanciaP", np.getValue()).commit();
                        preference.setSummary(String.valueOf(np.getValue())+" cm.");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.string.pref_obj_peso:
                builder = new AlertDialog.Builder(this);
                np = new NumberPicker(this);
                np.setMinValue(10);
                np.setMaxValue(prefs.getInt("pPeso",70));
                np.setValue(prefs.getInt("pObjetivoPeso",prefs.getInt("pPeso",70)));
                builder.setView(np);
                builder.setTitle(R.string.sel_obj_peso);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        findPreference("pObjetivoPeso").getEditor().putInt("pObjetivoPeso", np.getValue()).commit();
                        preference.setSummary(String.valueOf(np.getValue()) + " kg.");
                        dialog.dismiss();
                        calcularObjetivo();

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

            default:
              /*  cambiaPreferencias();*/
                break;
        }

        return false;
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        file.mkdirs();

        Date d = new Date();
        nombreF = timeStampFormat.format(d)+".jpg";

        String path = Environment.getExternalStorageDirectory() + File.separator
                + MEDIA_DIRECTORY + File.separator + nombreF;

        File newFile = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);
    }
    private String dir="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PHOTO_CODE:
                if(resultCode == RESULT_OK){
                    dir =  Environment.getExternalStorageDirectory() + File.separator
                            + MEDIA_DIRECTORY + File.separator + nombreF;
                    //decodeBitmap(dir);
                    //Guardar la imagen en la sd (https://github.com/androidMDW/guia3completo)
                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        private MediaScannerConnection msc = null; {
                            msc = new MediaScannerConnection(getApplicationContext(), this); msc.connect();
                        }
                        public void onMediaScannerConnected() {
                            msc.scanFile(dir, null);
                            findPreference("pFoto").getEditor().putString("pFoto",dir).commit();
                            cambiaPreferencias();
                            Toast.makeText(getApplicationContext(), R.string.toastFoto, Toast.LENGTH_LONG).show();
                        }
                        public void onScanCompleted(String path, Uri uri) {
                            msc.disconnect();
                        }
                    };
                }
                break;

            case SELECT_PICTURE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String path=getRealPathFromURI(uri);
                    findPreference("pFoto").getEditor().putString("pFoto", path).commit();
                    cambiaPreferencias();
                    Toast.makeText(getApplicationContext(), R.string.toastFoto, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
    private void calcularObjetivo(){
        double tiempo=Util.calcularTiempoMeta(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DecimalFormat df= new DecimalFormat("0.0");
        builder.setTitle(getStr(R.string.sel_obj_tiempo) + " " + df.format(tiempo) + " sesiones de 30mins.?");
        builder.setPositiveButton(R.string.acepto, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void cambiaPreferencias() {
        SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e=p.edit();
        e.putBoolean("pCambia", true).commit();
    }

    //Metodo proporcionado en:
    // http://stackoverflow.com/questions/2789276/android-get-real-path-by-uri-getpath
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}
