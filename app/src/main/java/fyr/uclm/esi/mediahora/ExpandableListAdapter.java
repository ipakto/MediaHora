package fyr.uclm.esi.mediahora;

/**
 * Created by Rosana on 04/01/2016.
 */
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String,  List<String>> coleccionPreguntas;
    private List<String> preguntas;

    public ExpandableListAdapter(Activity context,  List<String> preguntas,
                                 Map<String,  List<String>> coleccionPreguntas) {
        this.context = context;
        this.coleccionPreguntas = coleccionPreguntas;
        this.preguntas = preguntas;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return coleccionPreguntas.get(preguntas.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String pregunta = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.pregunta);


        item.setText(pregunta);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return coleccionPreguntas.get(preguntas.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return preguntas.get(groupPosition);
    }

    public int getGroupCount() {
        return preguntas.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String tituloPregunta = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.pregunta);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(tituloPregunta);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}