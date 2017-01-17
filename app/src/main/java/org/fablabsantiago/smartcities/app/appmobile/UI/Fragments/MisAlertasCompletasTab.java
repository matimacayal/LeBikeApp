package org.fablabsantiago.smartcities.app.appmobile.UI.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;
import org.fablabsantiago.smartcities.app.appmobile.R;

import java.util.ArrayList;
import java.util.List;

import org.fablabsantiago.smartcities.app.appmobile.Interfaces.MisAlertasInterfaces;
import org.fablabsantiago.smartcities.app.appmobile.Adapters.MisAlertasAdapter;


public class MisAlertasCompletasTab extends ListFragment
{
    DatabaseHandler baseDatos;
    List<Alerta> listaAlertas = new ArrayList<Alerta>();

    private MisAlertasInterfaces.MisAlertasTabListener alertasCompletasTabListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseDatos = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmenttab_misalertas_completas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listaAlertas = baseDatos.getAlertasByEstado("completa");
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
                alertasCompletasTabListener.onAlertasListClick((Alerta) list.getItemAtPosition(position));
                //Toast.makeText(getActivity(), "aewge", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setAlertasCompletasTabListener(MisAlertasInterfaces.MisAlertasTabListener alertasCompletasTabListener) {
        this.alertasCompletasTabListener = alertasCompletasTabListener;
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(getActivity(), "aewge", Toast.LENGTH_SHORT).show();
    }
    */
}
