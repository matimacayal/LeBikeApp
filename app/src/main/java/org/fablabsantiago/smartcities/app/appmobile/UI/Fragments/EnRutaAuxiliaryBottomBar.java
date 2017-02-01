package org.fablabsantiago.smartcities.app.appmobile.UI.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Ruta;
import org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService;
import org.fablabsantiago.smartcities.app.appmobile.Utils.ServiceUtils;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EnRutaAuxiliaryBottomBar extends Fragment
{
    Context activityContext;

    private LinearLayout bottomBarView;

    SharedPreferences leBikePrefs;
    private boolean bTrackingRoute;

    private BottomBarListener bottomBarListener;

    private List<Ruta> listaRutas;
    private int numRutas;
    private int rutaActual;

    public interface BottomBarListener {
        void startTrack(boolean trackingState);
        void highlightRoute(Ruta ruta);
    }

    public void setBottomBarListener(BottomBarListener bottomBarListener) {
        this.bottomBarListener = bottomBarListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaRutas = new ArrayList<Ruta>();
        numRutas = 0;
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
        bTrackingRoute = leBikePrefs.getBoolean("BOOL_TRACKING_ROOT", false)
                            && ServiceUtils.isServiceRunning(getActivity(), TrackingService.class);
        refreshTrackUI(bTrackingRoute);

        ImageButton startTrackButton = (ImageButton) bottomBarView.findViewById(R.id.trackRouteButton);
        startTrackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                bottomBarListener.startTrack(bTrackingRoute);
            }
        });

        TextView rutaText = (TextView) bottomBarView.findViewById(R.id.rutaSeleccionada);
        rutaText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                LinearLayout infoRuta = (LinearLayout) bottomBarView.findViewById(R.id.routeInfoBottomBar);
                if (infoRuta.getVisibility() == View.GONE) {
                    infoRuta.setVisibility(View.VISIBLE);
                } else {
                    infoRuta.setVisibility(View.GONE);

                }
            }
        });

        ImageButton nextButton = (ImageButton) bottomBarView.findViewById(R.id.nextRouteButton);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                routeSelection(+1);
                refreshRouteUI();
            }
        });

        ImageButton previousButton = (ImageButton) bottomBarView.findViewById(R.id.previousRouteButton);
        previousButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                routeSelection(-1);
                refreshRouteUI();
            }
        });
    }

    public void startTrackResponse(boolean success, Destino destino) {
        bTrackingRoute = success;

        SharedPreferences.Editor editor = leBikePrefs.edit();
        editor.putBoolean("BOOL_TRACKING_ROOT", bTrackingRoute);
        if (bTrackingRoute) {
            editor.putInt("ID_TRACKING_ROOT", destino.getId());
        } else {
            editor.remove("ID_TRACKING_ROOT");
        }
        editor.commit();

        refreshTrackUI(bTrackingRoute);
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
        listaRutas = rutas;
        numRutas = listaRutas.size();
        //TODO: Manejar la visualización de las opciones de rutas
        // y la comunicación de vuelta hacia la actividad para la visualización en el mapa

        if (numRutas < 1)
            return;

        rutaActual = 1;
        refreshRouteUI();
    }

    /*---------- Tracking ----------*/
    public void refreshTrackUI(boolean trackingRoute) {
        ImageButton trackRouteButton = (ImageButton) bottomBarView.findViewById(R.id.trackRouteButton);
        //int imageButtonRsc = (trackingRoute) ? R.drawable.arrow_right_bold_circle_orange : R.drawable.arrow_right_bold_circle;
        int imageButtonRsc = (trackingRoute) ? R.drawable.pause_circle_orange : R.drawable.play_circle_green;
        trackRouteButton.setImageResource(imageButtonRsc);
        /*
        trackRouteButton.setTag(imageButtonRsc);

        final Handler h = new Handler();
        final int delay = 700; //milliseconds
        Runnable runnable = new Runnable(){
            public void run(){
                ImageButton trackRouteButton = (ImageButton) bottomBarView.findViewById(R.id.trackRouteButton);
                int imageButtonRsc = (((Integer) trackRouteButton.getTag()) == R.drawable.pause_circle_gray) ?
                        R.drawable.pause_circle_orange : R.drawable.pause_circle_gray;
                trackRouteButton.setImageResource(imageButtonRsc);
                trackRouteButton.setTag(imageButtonRsc);
                h.postDelayed(this, delay);
            }
        };
        if (trackingRoute) {
            h.postDelayed(runnable, delay);
        } else {
            h.removeCallbacks(runnable);
        }*/
    }

    public void refreshRouteUI() { //refreshRouteUI(rutaActual, listaRutas);
        Ruta ruta = listaRutas.get(rutaActual-1);

        TextView cuenta = (TextView) bottomBarView.findViewById(R.id.routeCountTextView);
        cuenta.setText(i2str(rutaActual) + "/" + i2str(numRutas));

        TextView fecha = (TextView) bottomBarView.findViewById(R.id.routeDateTextView);
        TextView hora = (TextView) bottomBarView.findViewById(R.id.routeHourTextView);
        fecha.setText(ruta.getFecha());
        hora.setText(ruta.getHora());

        int numPos = ruta.getNumPos();
        int numNeg = ruta.getNumNeg();
        int numVotes = numPos + numNeg;
        int perPos = 0;
        int perNeg = 0;
        if (numVotes > 0) {
            perPos = 100*numPos/(numPos+numNeg);
            perNeg = 100*numNeg/(numPos+numNeg);
        }

        TextView poss = (TextView) bottomBarView.findViewById(R.id.posProgressBarText);
        TextView negs = (TextView) bottomBarView.findViewById(R.id.negProgressBarText);
        poss.setText(i2str(perPos) + "%");
        negs.setText(i2str(perNeg) + "%");

        ProgressBar posBar = (ProgressBar) bottomBarView.findViewById(R.id.posProgressBar2);
        ProgressBar negBar = (ProgressBar) bottomBarView.findViewById(R.id.negProgressBar2);
        posBar.setProgress(perPos);
        negBar.setProgress(perNeg);

        bottomBarListener.highlightRoute(ruta);
    }


    /*---------- Utils ----------*/
    private String i2str(int i) {
        return Integer.toString(i);
    }

    private void routeSelection(int i) {
        rutaActual += i;

        if (rutaActual > numRutas)
            rutaActual = numRutas;

        if (rutaActual < 1)
            rutaActual = 1;
    }
}
