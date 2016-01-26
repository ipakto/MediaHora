package fyr.uclm.esi.mediahora.presentacion;

import android.app.Activity;
import android.os.Bundle;

import fyr.uclm.esi.mediahora.R;
import fyr.uclm.esi.mediahora.dominio.Util;

/******************************************************************************************
 * *******************************MULTIMEDIA***********************************************
 * ******************ESCUELA SUPERIOR DE INFORMÁTICA(UCLM)*********************************
 * ************************PRÁCTICA REALIZADA POR:*****************************************
 *       *                                                                                *
 *		 * 				- Francisco Ruiz Romero											  *
 *		 * 				- Rosana Rodríguez-Bobada Aranda								  *
 * 																						  *
 ******************************************************************************************/
public class AcercaDe extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acerca_de);
        Util.cambiarColorStatusBar(this);
    }
}
