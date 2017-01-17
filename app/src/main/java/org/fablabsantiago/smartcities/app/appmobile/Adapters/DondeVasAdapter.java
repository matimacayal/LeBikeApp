package org.fablabsantiago.smartcities.app.appmobile.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;

import java.util.List;

public class DondeVasAdapter extends ArrayAdapter<Destino> {

    public DondeVasAdapter(Context context, List<Destino> destinos) {
        super(context, 0, destinos);
        Log.i("DondeVasAdapter","num destinos: " + Integer.toString(destinos.size()));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Destino destino = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_dondevas, parent, false);
        }

        TextView dondeVoy = (TextView) convertView.findViewById(R.id.dondevas_item_text);
        dondeVoy.setText(destino.getName());
        dondeVoy.setTypeface(null, Typeface.BOLD);

        return convertView;
    }
}
