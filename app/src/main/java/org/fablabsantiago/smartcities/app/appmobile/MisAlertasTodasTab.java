package org.fablabsantiago.smartcities.app.appmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MisAlertasTodasTab extends ListFragment
{
    DatabaseHandler baseDatos;
    List<Alerta> listaAlertas = new ArrayList<Alerta>();

    private MisAlertasInterfaces.MisAlertasTabListener alertasTodasTabListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseDatos = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_misalertas_todas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listaAlertas = baseDatos.getAlertas();
    }

    @Override
    public void onStart() {
        super.onStart();
        MisAlertasAdapter alertasAdapter = new MisAlertasAdapter(getActivity(), listaAlertas);
        setListAdapter(alertasAdapter);

        final ListView list = getListView();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                alertasTodasTabListener.onAlertasListClick((Alerta) list.getItemAtPosition(position));
                //Toast.makeText(getActivity(), "bluxe", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setAlertasTodasTabListener(MisAlertasInterfaces.MisAlertasTabListener alertasTodasTabListener) {
        this.alertasTodasTabListener = alertasTodasTabListener;
    }
}
