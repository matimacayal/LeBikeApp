package org.fablabsantiago.smartcities.app.appmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MisDestinosActivity extends AppCompatActivity
{
    DatabaseHandler baseDatos;
    List<Destino> listaDestinos = new ArrayList<Destino>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_misdestinos);

        Intent rxIntent = getIntent();
        if (rxIntent.getBooleanExtra("REQUESTING_NEW_DESTINATION", false)) {
            editDestination();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (listaDestinos.isEmpty()) {
            baseDatos = new DatabaseHandler(this);
            listaDestinos = baseDatos.getDestinations();
        }

        final ListView destinosListView = (ListView) findViewById(R.id.misdestinos_listview);
        if(listaDestinos.isEmpty()) {
            TextView noDestinos = (TextView) findViewById(R.id.no_destinos_textview);
            noDestinos.setVisibility(View.VISIBLE);
            destinosListView.setVisibility(View.GONE);
            return;
        }

        MisDestinosAdapter adapter = new MisDestinosAdapter(this, listaDestinos);
        destinosListView.setAdapter(adapter);


        List<Ruta> listaRutas = new ArrayList<Ruta>();
        baseDatos = new DatabaseHandler(this);
        listaRutas = baseDatos.getRutas();
        Log.i("MisDestinosActivity", "num. rutas: " + Integer.toString(listaRutas.size()));
        if (listaRutas.isEmpty()) {
            baseDatos.newRuta(new Ruta(
                    3051, 1044, "fablab_casa",
                    0, 0,
                    "07:50", "13/09/2016",
                    2800, 9855));
            baseDatos.newRuta(new Ruta(
                    3052, 1044, "fablab_casa",
                    0, 0,
                    "18:12", "13/09/2016",
                    2710, 9234));
            baseDatos.newRuta(new Ruta(
                    3053, 1045, "fablab_beauchef850",
                    0, 0,
                    "09:30", "26/09/2016",
                    3502, 1460));
            baseDatos.newRuta(new Ruta(
                    3054, 1045, "fablab_beauchef850",
                    0, 0,
                    "19:58", "26/09/2016",
                    3440, 1567));
            baseDatos.newRuta(new Ruta(
                    3055, 1046, "fablab_estacionmapocho",
                    0, 0,
                    "15:02", "23/09/2016",
                    1807, 545));
            baseDatos.newRuta(new Ruta(
                    3056, 1046, "fablab_estacionmapocho",
                    0, 0,
                    "21:30", "23/09/2016",
                    1630, 530));
        }
    }

    public void nuevoDestinozi(View view) {
        String infoText = "Later you will be able to add new destinations and we will recomend the" +
                " best route to get there. ";
        Toast.makeText(this,infoText,Toast.LENGTH_LONG).show();

        editDestination();
    }

    public void editarMiDestino(View view) {
        Toast.makeText(this, "Editando ... .-.", Toast.LENGTH_SHORT).show();

        editDestination();
    }

    protected void editDestination() {
        LinearLayout subwindow = (LinearLayout) findViewById(R.id.edit_destination_subwindow);
        ListView destinosListx = (ListView) findViewById(R.id.misdestinos_listview);

        subwindow.setVisibility(View.VISIBLE);
        destinosListx.setClickable(false);
        destinosListx.setEnabled(false);
    }

    public void closeNewDestination(View view) {
        LinearLayout subwindow = (LinearLayout) findViewById(R.id.edit_destination_subwindow);
        ListView destinosListx = (ListView) findViewById(R.id.misdestinos_listview);

        subwindow.setVisibility(View.GONE);
        destinosListx.setClickable(true);
        destinosListx.setEnabled(true);
    }
}
