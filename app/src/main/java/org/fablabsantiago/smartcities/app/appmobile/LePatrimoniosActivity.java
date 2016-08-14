package org.fablabsantiago.smartcities.app.appmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class LePatrimoniosActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("LePatrimoniosActivity","onCreate in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lepatrimonios);

        //Configuramos la lista de patrimonios simple
        /* //Rara esta wea, si activo esta lista simple, el mapa de OnRouteMapActivity se cae ://.
        final ListView patrimoniosListView = (ListView) findViewById(R.id.patrimoniosListView);
        String[] patrimonios = new String[] {"Mi Casa",
                                             "Estación Mapocho",
                                             "Palacio La Moneda",
                                             "Iglesia San Fransisco",
                                             "Casa Rosada"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,android.R.id.text1,patrimonios);
        patrimoniosListView.setAdapter(adapter);
        patrimoniosListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int itemPosition     = position;
                String  itemValue    = (String) patrimoniosListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });
        */

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        final GridView patrimoniosGridView = (GridView) findViewById(R.id.patrimoniosGridView);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1,android.R.id.text1,patrimonios);
        PatrimonioAdapter adapter = new PatrimonioAdapter(this);
        patrimoniosGridView.setAdapter(adapter);
        patrimoniosGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String url = "http://www.example.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    public void activatePatrimoniosAlarm(View view)
    {
        Toast.makeText(this, "Se tiene que configurar la geofensa :)", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patrimonios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification)
        {
            //Do what happens when the user ask's for information
            TextView informationTextView = (TextView) findViewById(R.id.alarma_textview);

            if (informationTextView.getVisibility() == View.VISIBLE)
            {
                informationTextView.setVisibility(View.GONE);
                return true;
            }

            informationTextView.setVisibility(View.VISIBLE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
