package org.fablabsantiago.smartcities.app.appmobile.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.R;

import java.util.List;

public class MisAlertasAdapter extends ArrayAdapter<Alerta>
{
    public MisAlertasAdapter(Context context2, List<Alerta> alertas2)
    {
        super(context2, R.layout.listitem_misalertas, alertas2);
    }

    @Override
    public View getView(int position2, View convertView2, ViewGroup parent2)
    {
        Alerta alerta = getItem(position2);

        Log.i("MisAlertasAdapter","destinationaweiubfkyu");
        convertView2 = LayoutInflater.from(getContext()).inflate(R.layout.listitem_misalertas, parent2, false);

        //TODO: 1. resizear las imagenes de tipo de alerta. Además ver bien estos ya que los hice
        // a la rápida
        //      2. mejorar esta manera de asignar el tipo de alerta y la imagen respectiva.
        // Cuando una alerta no es de ningún tipo, es del tipo '""'
        String tipoAlerta = (alerta.getTipoAlerta().equals("")) ? "void":alerta.getTipoAlerta();
        String votoAlerta = (alerta.getPosNeg()) ? "pos":"neg";
        String tipoTotalAlerta = votoAlerta + tipoAlerta;
        int resId;
        switch (tipoTotalAlerta) {
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
            case "posvoid": resId = R.drawable.ic_positive; break;
            case "negvoid": resId = R.drawable.ic_negative; break;
            default: resId = R.drawable.ic_add_white_24dp; break;
        }

        String tituloText = alerta.getTitulo();
        if (tituloText.equals("")) {
            tituloText = "Alerta " + (votoAlerta.equals("pos")?"positiva":"negativa") + ".\nPresionar para editar.";
        }

        ImageView tipoAlertaIV = (ImageView) convertView2.findViewById(R.id.tipoalerta_itemmisalertas);
        TextView titulo = (TextView) convertView2.findViewById(R.id.titulo_itemmisalertas);
        TextView fecha = (TextView) convertView2.findViewById(R.id.fecha_itemmisalertas);

        tipoAlertaIV.setImageResource(resId);
        titulo.setText(tituloText);
        fecha.setText("hace 3 horas");

        return convertView2;
    }
}
