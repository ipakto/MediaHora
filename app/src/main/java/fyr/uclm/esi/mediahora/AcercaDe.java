package fyr.uclm.esi.mediahora;

import android.app.Activity;
import android.os.Bundle;

import fyr.uclm.esi.mediahora.dominio.Util;

/**
 * Created by Paco on 03/01/2016.
 */
public class AcercaDe extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        Util.cambiarColorStatusBar(this);
    }
}
