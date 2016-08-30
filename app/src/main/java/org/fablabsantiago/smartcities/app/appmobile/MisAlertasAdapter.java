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
import java.util.List;

public class MisAlertasAdapter extends ArrayAdapter<Alerta>
{
    public MisAlertasAdapter(Context context2, List<Alerta> alertas2)
    {
        super(context2, R.layout.item_misalertas, alertas2);
    }

    @Override
    public View getView(int position2, View convertView2, ViewGroup parent2)
    {
        Alerta alerta = getItem(position2);

        Log.i("MisAlertasAdapter","destinationaweiubfkyu");
        convertView2 = LayoutInflater.from(getContext()).inflate(R.layout.item_misalertas, parent2, false);

        //TODO: resizear las imagenes de tipo de alerta. Además ver bien estos ya que los hice
        // a la rápida
        //TODO: mejorar esta manera de asignar el tipo de alerta y la imagen respectiva.
        String tipoAlerta = ((alerta.getPosNeg()) ? "pos":"neg") + alerta.getTipoAlerta();
        int resId;
        switch (tipoAlerta) {
            case "poscicl": resId = R.drawable.ic_positive_ciclovia; break;
            case "posvias": resId = R.drawable.ic_positive_vias; break;
            case "posvege": resId = R.drawable.ic_positive_vegetacion; break;
            case "posmant": resId = R.drawable.ic_positive_mantencion; break;
            case "posauto": resId = R.drawable.ic_positive_autos; break;
            case "pospeat": resId = R.drawable.ic_positive_peatones; break;
            case "posotro": resId = R.drawable.ic_positive_warning; break;
            case "negcicl": resId = R.drawable.ic_negative_ciclovia; break;
            case "negvias": resId = R.drawable.ic_negative_vias; break;
            case "negvege": resId = R.drawable.ic_negative_vegetacion; break;
            case "negmant": resId = R.drawable.ic_negative_mantencion; break;
            case "negauto": resId = R.drawable.ic_negative_autos; break;
            case "negpeat": resId = R.drawable.ic_negative_peatones; break;
            case "negotro": resId = R.drawable.ic_negative_warning; break;
            default: resId = R.drawable.ic_positive_vegetacion; break;
        }
        ImageView tipoAlertaIV = (ImageView) convertView2.findViewById(R.id.tipoalerta_itemmisalertas);
        tipoAlertaIV.setImageResource(resId);

        TextView titulo = (TextView) convertView2.findViewById(R.id.titulo_itemmisalertas);
        titulo.setText("''" + alerta.getTitulo() + "''");
        TextView fecha = (TextView) convertView2.findViewById(R.id.fecha_itemmisalertas);
        fecha.setText("''" + "hace 3 horas" + "''");

        return convertView2;
    }
}
