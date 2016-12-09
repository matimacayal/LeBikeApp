package org.fablabsantiago.smartcities.app.appmobile.UI.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Ruta;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EnRutaAuxiliaryBottomBar extends Fragment
{
    Context activityContext;

    private LinearLayout bottomBarView;
    private List<Ruta> rutasADestino;

    SharedPreferences leBikePrefs;
    private boolean bTrackingRoute;

    private BottomBarListener bottomBarListener;

    public interface BottomBarListener {
        void startTrack(boolean trackingState);
    }

    public void setBottomBarListener(BottomBarListener bottomBarListener) {
        this.bottomBarListener = bottomBarListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rutasADestino = new ArrayList<Ruta>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottombar_auxiliar_enruta, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstaceState) {
        super.onViewCreated(view, savedInstaceState);

        bottomBarView = (LinearLayout) view;

        /*---------- Tracking ----------*/
        leBikePrefs = getActivity().getSharedPreferences("leBikePreferences", MODE_PRIVATE);
        bTrackingRoute = leBikePrefs.getBoolean("BOOL_TRACKING_ROOT", false);
        refreshUIOnRouteStarted(bTrackingRoute);

        ImageButton startTrackButton = (ImageButton) bottomBarView.findViewById(R.id.trackRouteButton);
        startTrackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                bottomBarListener.startTrack(bTrackingRoute);
            }
        });
    }

    public void startTrackResponse(boolean success) {
        bTrackingRoute = success;

        SharedPreferences.Editor editor = leBikePrefs.edit();
        editor.putBoolean("BOOL_TRACKING_ROOT", bTrackingRoute);
        editor.commit();

        // TODO: El estado del botón bien debe ser actualizado con memoria.
        // Guardar su estado y establecer su correcto estado al inicar de nuevo la aplicación.
        //ProgressBar bien = (ProgressBar) findViewById(R.id.goodProgressBar);
        //bien.setEnabled(!bTrackingRoute);
        refreshUIOnRouteStarted(bTrackingRoute);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityContext = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onActivityLoaded(Destino destino) {
        bottomBarView.setVisibility(View.VISIBLE);
        if (destino.getId() > 0) {
            ImageButton startStoptrackButton = (ImageButton) bottomBarView.findViewById(R.id.trackRouteButton);
            startStoptrackButton.setEnabled(true);
        }

    }

    public void destinoRutas(List<Ruta> rutas) {
        rutasADestino = rutas;
        //TODO: Manejar la visualización de las opciones de rutas
        // y la comunicación de vuelta hacia la actividad para la visualización en el mapa
    }

    /*---------- Tracking ----------*/
    public void refreshUIOnRouteStarted(boolean trackingRoute) {
        ImageButton trackRouteButton = (ImageButton) bottomBarView.findViewById(R.id.trackRouteButton);
        int imageButtonRsc = (trackingRoute) ? R.drawable.ic_directions_on_24dp : R.drawable.rutas;
        trackRouteButton.setImageResource(imageButtonRsc);

        //int buttonText = Color.parseColor((trackingRoute) ? "#CCec903a" : "#00000000");
        //trackRouteButton.setBackgroundColor(buttonText);

        //Button bienButton = (Button) findViewById(R.id.bienButton);
        //bienButton.setText(String.format(destino.getPositiveHospotNumber()));
        //Button malButton = (Button) findViewById(R.id.malButton);
        //bienButton.setText(Integer.toString(destino.getNegativeHospotNumber()));
    }

}
