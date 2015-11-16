package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;

/**
 * Created by Rosana on 15/11/2015.
 */
public class InfoUsuario extends Activity
{
    // Layout components
    @Bind(R.id.lblAltura) TextView lblAltura;
    @Bind(R.id.lblCorreo)TextView lblCorreo;
    @Bind(R.id.lblPeso) TextView lblPeso;
    @Bind(R.id.lblZancada) TextView lblZancada;
    @Bind(R.id.txtAltura) TextView txtAltura;
    @Bind(R.id.txtCorreo) TextView txtCorreo;
    @Bind(R.id.txtPeso) TextView txtPeso;
    @Bind(R.id.txtZancada) TextView txtZancada;
    @Bind(R.id.txtNombre) TextView txtNombre;
    @Bind(R.id.imgUsuario)ImageView imgUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_usuario);
    }
}
