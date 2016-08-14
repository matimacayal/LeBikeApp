package org.fablabsantiago.smartcities.app.appmobile;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MisAlertasAdapter extends ArrayAdapter<String>
{
    public MisAlertasAdapter(Context context2, ArrayList<String> destinos2)
    {
        super(context2, R.layout.item_misalertas, destinos2);
    }

    @Override
    public View getView(int position2, View convertView2, ViewGroup parent2)
    {
        String destino2 = getItem(position2);

        Destination destination2 = FakeDataBase.createDestinationObject("Casa");

        ArrayList<String> alertas = new ArrayList<>(destination2.posHotspotsName);
        ArrayList<String> alertasdesc = new ArrayList<>(destination2.posHotspotsDesc);

        String oij = alertas.get(destination2.getPositiveHospotNumber() - position2 -1);
        String oijdesc = alertasdesc.get(destination2.getPositiveHospotNumber() - position2 -1);

        Log.i("MisAlertasAdapter","destinationaweiubfkyu");
        convertView2 = LayoutInflater.from(getContext()).inflate(R.layout.item_misalertas, parent2, false);

        TextView titulo = (TextView) convertView2.findViewById(R.id.desc_item_misalertas);
        titulo.setText("''" + oij + ", " + oijdesc + "''");
        //ImageView direccion = (ImageView) convertView2.findViewById(R.id.type_item_misalertas);
        //direccion.setImageResource();

        return convertView2;
    }
}
