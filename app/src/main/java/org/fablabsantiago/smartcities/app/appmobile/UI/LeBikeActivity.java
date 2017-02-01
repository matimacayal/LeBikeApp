package org.fablabsantiago.smartcities.app.appmobile.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fablabsantiago.smartcities.app.appmobile.Adapters.DondeVasAdapter;
import org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService;
import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Utils.ServiceUtils;


public class LeBikeActivity extends AppCompatActivity
{
    public static final String DESTINO_LIBRE = "Libre";

    private String TAG = LeBikeActivity.class.getSimpleName();
    private Context context = this;

    DatabaseHandler baseDatos;

    SharedPreferences leBikePrefs;

    HashMap<String, String> hmap = new HashMap<String, String>();
    List<String> listaNombresDestinos = new ArrayList<String>();
    List<Destino> listaDestinos = new ArrayList<Destino>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate in");
        super.onCreate(savedInstanceState);

        leBikePrefs = getSharedPreferences("leBikePreferences", MODE_PRIVATE);
        String user = leBikePrefs.getString(LoginActivity.USER_NAME, null);
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_lebike);

        //Revisamos si es necesario cargar base de datos.
        if (listaDestinos.isEmpty()) {
            Log.i(TAG,"isEmpty 1");
            baseDatos = new DatabaseHandler(this);
            listaDestinos = baseDatos.getDestinations();

            if (listaDestinos.isEmpty()) {
                Log.i(TAG,"isEmpty 2");
                // Creamos 'destino libre'
                baseDatos.newDestiny(new Destino(DESTINO_LIBRE, null, 100001, 0.0, 0.0));
                // Fake data for testing
                baseDatos.newDestiny(new Destino("Fablab", "Seminario 642, Providencia", 100002, -33.44967, -70.627735));
                baseDatos.newDestiny(new Destino("Universidad", "Beaucheff 850, Santiago Centro", 100003, -33.457773, -70.663823));
                baseDatos.newDestiny(new Destino("Casa Papá", "Casas del Alba, El Alba 2, Colina", 100004, -33.296311, -70.679086));
                listaDestinos = baseDatos.getDestinations();
            }
        }
        Log.i(TAG, "id ultima alerta BD: " + baseDatos.getLastAlertaId());
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"onStart - in");
        super.onStart();

        ListView destinosListView = (ListView) findViewById(R.id.destinationsList);
        if (listaDestinos.isEmpty()) {
            TextView agregaDestinoEditText = (TextView) findViewById(R.id.agregaDestinoEditText);

            destinosListView.setVisibility(View.GONE);
            agregaDestinoEditText.setVisibility(View.VISIBLE);

            return;
        }

        boolean bTrackingRoute = leBikePrefs.getBoolean("BOOL_TRACKING_ROOT", false)
                                    && ServiceUtils.isServiceRunning(this, TrackingService.class);
        int idTrackingRoute = -1;
        if (bTrackingRoute)
            idTrackingRoute = leBikePrefs.getInt("ID_TRACKING_ROOT", -1);

        DondeVasAdapter adapter = new DondeVasAdapter(this, listaDestinos, idTrackingRoute);
        destinosListView.setAdapter(adapter);

        destinosListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProgressBar spinner = (ProgressBar) view.findViewById(R.id.dondeVasListItemProgressBar);
                spinner.setVisibility(View.VISIBLE);

                Destino item = (Destino) ((ListView) findViewById(R.id.destinationsList)).getItemAtPosition(position);

                boolean bTrackingRoute = leBikePrefs.getBoolean("BOOL_TRACKING_ROOT", false)
                                            && ServiceUtils.isServiceRunning(context, TrackingService.class);
                int idTrackingRoute = leBikePrefs.getInt("ID_TRACKING_ROOT", -1);

                if (bTrackingRoute) {
                    if (item.getId() != idTrackingRoute) {
                        Toast.makeText(context, "Tracking activo a otro destino.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent enRutaMapIntent = new Intent(context, EnRutaActivity.class);
                Log.i(TAG,"pressed '" + item.getName() + "', id: " + Integer.toString(item.getId()));
                enRutaMapIntent.putExtra("DESTINO_ID", item.getId());
                startActivity(enRutaMapIntent);
            }
        });

    }

    /* Otro+ - OnClick */
    public void addDestination(View view) {
        String infoText = "Later you will be able to add new destinations and we will recomend the" +
                        " best route to get there. ";
        // TODO: add new personalized destinations
        // We will use google map apis:
        //     - Google Places API for Android
        //     - Google Maps Road API (web)
        //     - Google Maps Directions API (web)
        // For now we will just have 3 destinations with its routes predefined. We will be able to
        // add event in the route and the notifications have to be enabled.
        Toast.makeText(this,infoText,Toast.LENGTH_LONG).show();

        Intent misDestinosIntent = new Intent(this, MisDestinosActivity.class);
        misDestinosIntent.putExtra("REQUESTING_NEW_DESTINATION",true);
        startActivity(misDestinosIntent);
    }

    public void misDestinos(View view) {
        Log.i(TAG,"onMisDestinosOnClick entered");
        Intent misDestinosIntent = new Intent(this, MisDestinosActivity.class);
        startActivity(misDestinosIntent);
    }

    public void misAlertas(View view) {
        Log.i(TAG,"onMisAlertas entered");
        Intent misAlertasIntent = new Intent(this, MisAlertasActivity.class);
        misAlertasIntent.setAction("DEFAULT");
        startActivity(misAlertasIntent);
    }

    public void toMapActivity(View view) {
        Log.i(TAG,"toMapActivity - in");
        Intent enRutaMapIntent = new Intent(context, EnRutaActivity.class);
        startActivity(enRutaMapIntent);
    }

    public void miPerfil(View view) {
        Log.i(TAG, "onMiPerfil entered");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void misAmigos(View view) {
        Toast.makeText(this, "Función no disponible", Toast.LENGTH_SHORT).show();
    }
}
