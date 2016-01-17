package fyr.uclm.esi.mediahora;


import fyr.uclm.esi.mediahora.ExpandableListAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
/**
 * Created by Rosana on 04/01/2016.
 */
public class FAQ extends Activity{

    List<String> groupList;
    List<String> childList;
    String child;
    Map<String, String> colePre;
    Map<String, List<String>> coleccionPreguntas;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preguntas_frecuentes);

        createGroupList();

        createCollection();

        expListView = (ExpandableListView) findViewById(R.id.listaPreguntas);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this, groupList, coleccionPreguntas);
        expListView.setAdapter(expListAdapter);

        //setGroupIndicatorToRight();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);

                return true;
            }
        });
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("¿Cómo se calcula mi objetivo?");
        groupList.add("¿Qué pasa si quiero cambiar mi objetivo después de un tiempo?");
        groupList.add("¿Obtendré resultados inmediatos/notables une vez completado el plazo?");
        groupList.add("¿Cómo puedo compartirlo con mis amigos?");
        groupList.add("¿Qué tengo que hacer para la aplicación empiece a contar mis pasos?");
        groupList.add("¿Cómo puedo cambiar cualquiera de mis datos?");
    }

    private void createCollection() {
        // preparing laptops collection(child)
        String[] p1={"El objetivo se calcula en base al peso, la velocidad y los pasos realizados."};
        String[] p2 = {"Únicamente debe acceder a ajustes (a través del menú izquierdo) y cambiar la opción última (la de abajo del todo). El valor que introduzca se corresponderá con el peso que desa conseguir, es decir, su nuevo objetivo." };
        String[] p3 = {"El resultado al que queremos llegar no depende únicamente del ejercicio sino que también lo hace de la dieta que llevemos. /nRecuerda: es muy importante el ejercicio diario y una dieta equilibrada." };
        String[] p4 = {"Para compartir tus logros con tus amigos por las Redes Sociales, pulsa en la opción Compartir en el menú izquierdo."};
        String[] p5 = { "No hay que hacer nada más adicional. La aplicación empieza a contar tus pasos cuando la inicias por primera vez. /nMUY IMPORTANTE: configurar bien todos los datos personales que se solicitan para que el cálculo de objetivos, calorías, tiempos, etc. sea óptimo." };
        String[] p6 = { "Cualquiera de los datos (foto, altura, peso actual o deseado, etc.) se pueden modificar desde ajustes (a través del menú izquierdo)." };

        coleccionPreguntas = new LinkedHashMap<String, List<String>>();
        colePre=new LinkedHashMap<String, String>();
        for (String pregunta : groupList) {
            if (pregunta.equals("¿Cómo se calcula mi objetivo?")) {
                loadChild(p1);
            } else if (pregunta.equals("¿Qué pasa si quiero cambiar mi objetivo después de un tiempo?"))
                loadChild(p2);
            else if (pregunta.equals("¿Obtendré resultados inmediatos/notables une vez completado el plazo?"))
                loadChild(p3);
            else if (pregunta.equals("¿Cómo puedo compartirlo con mis amigos?"))
                loadChild(p4);
            else if (pregunta.equals("¿Qué tengo que hacer para la aplicación empiece a contar mis pasos?"))
                loadChild(p5);
            else if (pregunta.equals("¿Cómo puedo cambiar cualquiera de mis datos?"))
                loadChild(p6);
          //  else
               // loadChild(lenovoModels);

            coleccionPreguntas.put(pregunta, childList);
        }
    }
    private void loadChild2(String pregunta) {

            childList.add(pregunta);
    }
    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale =getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


}
