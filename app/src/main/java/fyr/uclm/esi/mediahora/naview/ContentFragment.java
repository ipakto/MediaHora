package fyr.uclm.esi.mediahora.naview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fyr.uclm.esi.mediahora.R;

/**
 * Created by Admin on 04-06-2015.
 */
public class ContentFragment extends Fragment {

    int layout=R.layout.activity_main;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layout,container,false);
        return v;
    }
    public void setLayout(int l){
        this.layout=l;
    }
}
