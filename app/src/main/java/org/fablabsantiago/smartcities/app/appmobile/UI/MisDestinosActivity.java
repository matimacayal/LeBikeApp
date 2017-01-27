package org.fablabsantiago.smartcities.app.appmobile.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;
import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.DestinoEditDialog;
import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.DestinoEditDialog.DialogListener;
import org.fablabsantiago.smartcities.app.appmobile.Adapters.MisDestinosAdapter;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Ruta;

public class MisDestinosActivity extends AppCompatActivity implements DialogListener
{
    DatabaseHandler baseDatos;
    List<Destino> listaDestinos = new ArrayList<Destino>();

    FragmentManager fragmentManager;
    DestinoEditDialog dialog;

    //int onEditDestinoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_misdestinos);

        Intent rxIntent = getIntent();
        if (rxIntent.getBooleanExtra("REQUESTING_NEW_DESTINATION", false)) {
            nuevoDestino(null);
        }

        //onEditDestinoId = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (listaDestinos.isEmpty()) {
            baseDatos = new DatabaseHandler(this);
            listaDestinos = baseDatos.getDestinations();
            // Se elimina 'destino libre' para no mostrarlo.
            listaDestinos.remove(0);
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

        destinosListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Destino destinoClicked = (Destino) destinosListView.getItemAtPosition(position);

                Log.i("MisDestinosActivity", "clicked list item: lat:" + String.valueOf(destinoClicked.getLatitude()));

                //Esto también podría hacerce dando la vuelta hacia el fragment y de vuelta mediante el interface,
                //pero no se sabe si es demasiado, ya que de esta manera funciona equivalentemente.
                //onEditDestinoId = destinoClicked.getId();

                Bundle bundle = new Bundle();
                bundle.putString("name", destinoClicked.getName());
                bundle.putString("direction", destinoClicked.getDirection());
                bundle.putInt("id", destinoClicked.getId());
                bundle.putDouble("latitude", destinoClicked.getLatitude());
                bundle.putDouble("longitude", destinoClicked.getLongitude());

                editDestino(bundle);
            }
        });

        List<Ruta> listaRutas = new ArrayList<Ruta>();
        baseDatos = new DatabaseHandler(this);
        listaRutas = baseDatos.getRutas();
        Log.i("MisDestinosActivity", "num. rutas: " + Integer.toString(listaRutas.size()));
        if (listaRutas.isEmpty()) {
            baseDatos.newRuta(new Ruta(3051, 1044, "fablab_casa", 0, 0, "07:50", "13/09/2016", 2800, 9855));
            baseDatos.newRuta(new Ruta(3052, 1044, "fablab_casa", 0, 0, "18:12", "13/09/2016", 2710, 9234));
            baseDatos.newRuta(new Ruta(3053, 1045, "fablab_beauchef850", 0, 0, "09:30", "26/09/2016", 3502, 1460));
            baseDatos.newRuta(new Ruta(3054, 1045, "fablab_beauchef850", 0, 0, "19:58", "26/09/2016", 3440, 1567));
            baseDatos.newRuta(new Ruta(3055, 1046, "fablab_estacionmapocho", 0, 0, "15:02", "23/09/2016", 1807, 545));
            baseDatos.newRuta(new Ruta(
                    3056, 1046, "fablab_estacionmapocho",
                    0, 0,
                    "21:30", "23/09/2016",
                    1630, 530));
        }
    }

    public void nuevoDestino(View view) {
        //String infoText = "Later you will be able to add new destinations and we will recomend the" + " best route to get there. ";
        //Toast.makeText(this,infoText,Toast.LENGTH_LONG).show();

        editDestino(null);
    }

    protected void editDestino(Bundle bundle) {
        fragmentManager = getSupportFragmentManager();
        dialog = new DestinoEditDialog();
        if (bundle != null) {
            dialog.setArguments(bundle);
            Log.i("MisDestinosActivity", "info: nombre:" + bundle.getString("name") + ", id:" + bundle.getString("id"));
        }
        dialog.setDialogListener(this);
        dialog.show(fragmentManager, "edit_destino_fragment");
    }

    /*                                             */
    /*             DestinoEditDialog               */
    /* ------------------------------------------- */
    @Override
    public void onCloseClick() {
        Log.i("MisDestinosActivity","'close' pressed");
        dialog.dismiss();
    }

    @Override
    public void onEliminarClick(int ide) {
        Log.i("MisDestinosActivity","'eliminar' pressed");
        baseDatos.deleteDestino(ide);
        dialog.dismiss();
    }

    //TODO: asegurarse de operación de escritura, borrado o actualización fue exitosa mediante el boolean que devolverá la fx.

    @Override
    public void onGuardarClick(String nombre, String direccion, int ide, Double lat, Double lon) {
        Log.i("MisDestinosActivity","'guardar' pressed, nombre:" + nombre + ", direccion:" + direccion + ", lat:" + Double.toString(lat) + ", lon:" + Double.toString(lon));
        if (ide > 0) {
            Log.i("MisDestinosActivity", "updating old destino");
            baseDatos.updateDestino(nombre, direccion, ide, lat, lon);
        } else {
            int idd = baseDatos.getLastDestinationId() + 1;
            baseDatos.newDestiny(new Destino(nombre, direccion, idd, lat, lon));

            Log.i("MisDestinosActivity", "creating new destino id: " + idd);
        }
        dialog.dismiss();
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //TODO: capturar 'onDestroy del dialog fragment'
}
