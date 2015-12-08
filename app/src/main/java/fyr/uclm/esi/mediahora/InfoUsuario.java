package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    @Bind(R.id.fotoPerfil) de.hdodenhof.circleimageview.CircleImageView fotoPerf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_usuario);
        ButterKnife.bind(this);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        txtNombre.setText(prefs.getString("pNombre", getStr(R.string.desc_nombre)));
        txtCorreo.setText(prefs.getString("pCorreo", getStr(R.string.desc_correo)));
        tAltura.setText(prefs.getInt("pAltura", 0)+" cm.");
        tZancada.setText(prefs.getInt("pDistanciaP", 0)+" cm.");
        tPeso.setText(prefs.getInt("pPeso", 0) + " kg.");
    }
    private String getStr(int id){
        return getResources().getString(id);
    }
}
