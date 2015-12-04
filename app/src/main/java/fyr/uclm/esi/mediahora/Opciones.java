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
import android.widget.ImageView;
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
        Preference foto=findPreference("pFoto");
        foto.setOnPreferenceClickListener(this);

    }
    @Override
    public boolean onPreferenceClick(final Preference preference){
        switch (preference.getTitleRes()) {
            case R.string.fotoPerfil:
                final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Elige una opcion :D");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int seleccion) {
                        if(options[seleccion] == "Tomar foto"){
                            openCamera();
                        }else if (options[seleccion] == "Elegir de galeria") {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                        }else if(options[seleccion] == "Cancelar"){
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
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
                            //findPreference("fotoCambia").getEditor().putBoolean("fotoCambia",true).commit();
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
                    findPreference("pFoto").getEditor().putString("pFoto",path).commit();
                    //findPreference("fotoCambia").getEditor().putBoolean("fotoCambia", true).commit();
                }
                break;
        }

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
