package org.fablabsantiago.smartcities.app.appmobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class HotspotInfoAdapter implements GoogleMap.InfoWindowAdapter
{
    LayoutInflater inflater = null;
    Context context;
    private TextView textViewTitle;

    public HotspotInfoAdapter(Context cont, LayoutInflater inflater) {
        this.inflater = inflater;
        this.context = cont;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Integer source = R.layout.infowindow_map_hotspot_positive;
        View v = new View(context);
        if (marker != null)
        {
            String snip = marker.getSnippet();
            Log.i("HotspotInfoAdapter","snippet:" + snip);
            // snipet = "xxx(->posneg)xxxxx(->rutaX)xxxxx(->type)"
            if(!snip.equals("origen_______") && !snip.equals("destino______"))
            {
                String posneg = snip.substring(0,3);
                String ruta = snip.substring(3,8);
                String type = snip.substring(8);
                Log.i("HotspotInfoAdapter","posneg: " + posneg);
                Log.i("HotspotInfoAdapter","ruta  : " + ruta);
                Log.i("HotspotInfoAdapter","type  : " + type);
                if (posneg.equals("neg"))
                {
                    //TODO: Cambiar elemento en funcion del tipo y comentarios.
                    v = inflater.inflate(R.layout.infowindow_map_hotspot_negative, null);
                }
                else if (posneg.equals("pos"))
                {
                    v = inflater.inflate(R.layout.infowindow_map_hotspot_positive, null);
                }
                TextView info = (TextView) v.findViewById(R.id.infoWindowInfoTextView);
                info.setText(marker.getTitle());
            }
        }

        return (v);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return (null);
    }
}
