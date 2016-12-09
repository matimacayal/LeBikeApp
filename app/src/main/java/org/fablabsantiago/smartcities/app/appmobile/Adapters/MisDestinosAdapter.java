package org.fablabsantiago.smartcities.app.appmobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;

import java.util.List;

public class MisDestinosAdapter extends ArrayAdapter<Destino>
{

    public MisDestinosAdapter(Context context2, List<Destino> destinos2)
    {
        super(context2, 0, destinos2);
    }

    @Override
    public View getView(int position2, View convertView2, ViewGroup parent2)
    {
        Destino destino2 = getItem(position2);

        convertView2 = LayoutInflater.from(getContext()).inflate(R.layout.listitem_misdestinos, parent2, false);

        TextView titulo = (TextView) convertView2.findViewById(R.id.item_misdestinos_titulo);
        titulo.setText(destino2.getName());
        TextView direccion = (TextView) convertView2.findViewById(R.id.item_misdestinos_direccion);
        direccion.setText(destino2.getDirection());

        return convertView2;
    }
}
