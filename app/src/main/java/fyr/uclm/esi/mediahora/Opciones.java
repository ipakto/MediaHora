package fyr.uclm.esi.mediahora;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import butterknife.Bind;

/**
 * Created by Paco on 28/11/2015.
 */
/*
 * IMAGENES: https://github.com/Roberasd/Pictures-Android
 */

public class Opciones extends PreferenceActivity implements Preference.OnPreferenceClickListener{
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
        findPreference("pNombre").setOnPreferenceClickListener(this);
        findPreference("pCorreo").setOnPreferenceClickListener(this);
        findPreference("pFondoPerf").setOnPreferenceClickListener(this);
        findPreference("pFoto").setOnPreferenceClickListener(this);
        findPreference("pEdad").setOnPreferenceClickListener(this);
        findPreference("pAltura").setOnPreferenceClickListener(this);
        findPreference("pPeso").setOnPreferenceClickListener(this);
        findPreference("pDistanciaP").setOnPreferenceClickListener(this);

    }
    private String getStr(int id){
        return getResources().getString(id);
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
            default:
                cambiaPreferencias();
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
                }
                break;
        }

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
