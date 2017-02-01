package org.fablabsantiago.smartcities.app.appmobile.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.UI.LeBikeActivity;

import java.util.List;

public class DondeVasAdapter extends ArrayAdapter<Destino> {
    int idTrackedDestino;

    public DondeVasAdapter(Context context, List<Destino> destinos, int idDestinoTrackeado) {
        super(context, 0, destinos);
        Log.i("DondeVasAdapter","num destinos: " + Integer.toString(destinos.size()));

        idTrackedDestino = idDestinoTrackeado;
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

        LinearLayout background = (LinearLayout) convertView.findViewById(R.id.relativeLayoutParent);
        if (destino.getName().equals(LeBikeActivity.DESTINO_LIBRE)) {
            background.setBackgroundResource(R.drawable.shape_item_dondevas_verde);
            dondeVoy.setTextColor(Color.parseColor("#ffffff"));

        }
        if (destino.getId() == idTrackedDestino) {
            background.setBackgroundResource(R.drawable.shape_item_dondevas_naranjo);
        }

        return convertView;
    }
}
