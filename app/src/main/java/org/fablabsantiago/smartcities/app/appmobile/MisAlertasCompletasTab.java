package org.fablabsantiago.smartcities.app.appmobile;

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
import java.util.Arrays;
import java.util.List;

public class MisAlertasCompletasTab extends ListFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_misalertas_completas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        MisAlertasAdapter alertasAdapter = new MisAlertasAdapter(getActivity(), new ArrayList<String>(Arrays.asList(
                "Conchetumare un dinosaurio",
                "Que wea el hoyo en la calle",
                "Hermano manso meteorito",
                "Para la mano oe",
                "Da lo mismo lo que ponga acá",
                "Ver bien esto"
        )));
        setListAdapter(alertasAdapter);
        ListView list = getListView();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getActivity(), "aewge", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(getActivity(), "aewge", Toast.LENGTH_SHORT).show();
    }
    */
}
