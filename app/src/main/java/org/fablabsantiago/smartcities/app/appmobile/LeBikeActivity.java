package org.fablabsantiago.smartcities.app.appmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LeBikeActivity extends AppCompatActivity
{
    protected final Context context = this;

    DatabaseHandler baseDatos;

    HashMap<String, String> hmap = new HashMap<String, String>();
    List<String> listaNombresDestinos = new ArrayList<String>();
    List<Destino> listaDestinos = new ArrayList<Destino>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LeBikeActivity","onCreate in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lebike);

        //Revisamos si es necesario cargar base de datos.
        if (listaDestinos.isEmpty()) {
            Log.i("LeBikeActivity","isEmpty 1");
            baseDatos = new DatabaseHandler(this);
            listaDestinos = baseDatos.getDestinations();

            if (listaDestinos.isEmpty()) {
                Log.i("LeBikeActivity","isEmpty 2");
                baseDatos.newDestiny(new Destino("Casa", "Augusto Leguía Norte 280, Las Condes", 1044, (float)-33.412900, (float)-70.597636));
                baseDatos.newDestiny(new Destino("Beauchef 850", "Vosco do Gomo 4840, Los Condos", 1045, (float) -33.457892, (float )-70.663839));
                baseDatos.newDestiny(new Destino("Estacion Mapocho", "Vesce de Geme 4840, Les Cendes", 1046, (float)-33.432577, (float)-70.653212));
                listaDestinos = baseDatos.getDestinations();
            }
        }
    }

    @Override
    protected void onStart() {
        Log.i("LeBikeActivity","onStart - in");
        super.onStart();

        ListView destinosListView = (ListView) findViewById(R.id.destinationsList);
        if (listaDestinos.isEmpty()) {
            TextView agregaDestinoEditText = (TextView) findViewById(R.id.agregaDestinoEditText);

            destinosListView.setVisibility(View.GONE);
            agregaDestinoEditText.setVisibility(View.VISIBLE);

            return;
        }

        DondeVasAdapter adapter = new DondeVasAdapter(this, listaDestinos);
        destinosListView.setAdapter(adapter);

        destinosListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Destino item = (Destino) ((ListView) findViewById(R.id.destinationsList)).getItemAtPosition(position);
                Intent enRutaMapIntent = new Intent(context, EnRutaActivity.class);
                Log.i("LeBikeActivity","pressed '" + item.getName() + "', id: " + Integer.toString(item.getId()));
                enRutaMapIntent.putExtra("DESTINO_ID", item.getId());
                startActivity(enRutaMapIntent);
                /*if (itemValue.equals("Casa") ||
                        itemValue.equals("Beauchef 850") ||
                        itemValue.equals("Estacion Mapocho")) {
                    Intent enRutaMapIntent = new Intent(context, EnRutaActivity.class);
                    enRutaMapIntent.putExtra("DESTINO", itemValue);
                    enRutaMapIntent.putExtra("DESTINO_ID", 1045); //destinoId default for beauchef850
                    startActivity(enRutaMapIntent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Destino de demostración. Presione alguno de los primeros 3.",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }

    /* Otro+ - OnClick */
    public void otroRoute(View view) {
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
        Log.i("LeBikeActivity","onMisDestinosOnClick entered");
        Intent misDestinosIntent = new Intent(this, MisDestinosActivity.class);
        startActivity(misDestinosIntent);
    }

    public void misAlertas(View view) {
        Log.i("LeBikeActivity","onMisAlertas entered");
        Intent misAlertasIntent = new Intent(this, MisAlertasActivity.class);
        misAlertasIntent.setAction("DEFAULT");
        startActivity(misAlertasIntent);
    }

    public void toMapActivity(View view) {
        Log.i("LeBikeActivity","toMapActivity - in");
        Intent enRutaMapIntent = new Intent(context, EnRutaActivity.class);
        startActivity(enRutaMapIntent);
    }
}
