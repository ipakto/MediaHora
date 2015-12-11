package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rosana on 15/11/2015.
 */
public class InfoUsuario extends Activity
{
    // Layout components
    @Bind(R.id.txtNombre) TextView txtNombre;
    @Bind(R.id.txtCorreo) TextView txtCorreo;
    @Bind(R.id.tAltura) TextView tAltura;
    @Bind(R.id.tZancada) TextView tZancada;
    @Bind(R.id.tPeso) TextView tPeso;
    @Bind(R.id.lblAltura) TextView lblAltura;
    @Bind(R.id.lblPeso) TextView lblPeso;
    @Bind(R.id.lblZancada) TextView lblZancada;
    @Bind(R.id.fotoPerfil) CircleImageView fotoPerf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_usuario);
        ButterKnife.bind(this);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        txtNombre.setText(prefs.getString("pNombre", getStr(R.string.desc_nombre)));
        txtCorreo.setText(prefs.getString("pCorreo", getStr(R.string.desc_correo)));
        tAltura.setText(String.valueOf(prefs.getInt("pAltura", 0))+" cm.");
        tZancada.setText(String.valueOf(prefs.getInt("pDistanciaP", 0))+" cm.");
        tPeso.setText(String.valueOf(prefs.getInt("pPeso", 0)) + " kg.");
        String foto=prefs.getString("pFoto","");
        if(!foto.equals("")) {
            Drawable d = Drawable.createFromPath(foto);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = (width <= height) ? height / 500 : width / 500;
            Bitmap b2 = Bitmap.createScaledBitmap(bitmap, Math.round(width / scale), Math.round(height / scale), false);
            Drawable d2 = new BitmapDrawable(getBaseContext().getResources(), b2);
            fotoPerf.setImageDrawable(d2);
        }
    }
    private String getStr(int id){
        return getResources().getString(id);
    }
}
