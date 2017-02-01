package org.fablabsantiago.smartcities.app.appmobile.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService;
import org.fablabsantiago.smartcities.app.appmobile.Services.UploadAlertasService;
import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.AlertaEditDialog;
import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;
import org.fablabsantiago.smartcities.app.appmobile.Interfaces.MisAlertasInterfaces;
import org.fablabsantiago.smartcities.app.appmobile.Adapters.MisAlertasPagerAdapter;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Utils.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

public class MisAlertasActivity extends AppCompatActivity implements
        MisAlertasInterfaces.MisAlertasTabListener,
        MisAlertasInterfaces.AlertaDialogListener
{
    private String TAG = MisAlertasActivity.class.getSimpleName();
    private Context context = this;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    DatabaseHandler baseDatos;
    List<Alerta> listaAlertas = new ArrayList<Alerta>();

    FragmentManager fragmentManager;
    AlertaEditDialog dialog;

    private BroadcastReceiver uploadingReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misalertas);

        // Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initializing BaseDatos
        baseDatos = new DatabaseHandler(this);
        //baseDatos.eraseAlertasTable();

        // Catch new Alerta action
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            switch(action) {
                case "REQUESTING_NEW_ALERTA":
                    openAlertasEditDialog(
                            null,
                            intent.getDoubleExtra("NEW_ALERTA_LATITUDE", 0),
                            intent.getDoubleExtra("NEW_ALERTA_LONGITUDE", 0),
                            intent.getIntExtra("NEW_ALERTA_IDRUTA", -1));
                    break;
                case "EDIT_ALERTA":
                    Alerta alerta = intent.getParcelableExtra("ALERTA_TO_EDIT");
                    if (alerta == null)
                        Log.i(TAG, "alerta nula");
                    Log.i(TAG, "alerta:" + alerta.getId() + ", vote:" + alerta.getPosNeg());
                    openAlertasEditDialog(alerta, 0, 0, -1);
                    break;
                default:
                    Log.i(TAG, "invalid action");
                    break;
            }
        }

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Completas"));
        tabLayout.addTab(tabLayout.newTab().setText("Pendientes"));
        tabLayout.addTab(tabLayout.newTab().setText("Todas"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.setScrollPosition(position, positionOffset, true);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*---------- Comunicación TrackingService ----------*/
        initializeServiceCommunication();
    }

    protected void initializeServiceCommunication() {
        // TODO: Mejorar este feedback de información.
        // Ahora avisa a actividad cuando comienza y cuando termina. Si actividad empieza en medio,
        // hay que esperar a que alguna alerta se suba para que se actualice la info.
        uploadingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Broadcast received.");
                Log.i(TAG, "package: " + this);
                String action = intent.getAction();
                switch(action) {
                    case UploadAlertasService.UPLOADING:
                        showUploadingInfo(true, intent);
                        break;
                    case UploadAlertasService.ALL_UPLOADED:
                        showUploadingInfo(false, intent);
                        break;
                    case UploadAlertasService.ALERTA_UPLOADED:
                        showUploadingInfoBufferSize(intent);
                        break;
                    default:
                        Log.i(TAG, "UploadingReceiver - Invalid action");
                        break;
                }

            }
        };
    }

    protected void showUploadingInfo(Boolean uploading, Intent intent) {
        Log.i(TAG, "showUploadingInfo - in");

        int visibility = (uploading)? View.VISIBLE : View.GONE;

        LinearLayout info = (LinearLayout) findViewById(R.id.uploadingAlertasInfoLL);
        info.setVisibility(visibility);

        LinearLayout infoNotUp = (LinearLayout) findViewById(R.id.notUploadedAlertasInfoLL);
        infoNotUp.setVisibility(View.GONE);

        if (uploading && (intent != null)) {
            showUploadingInfoBufferSize(intent);
        }
    }

    protected void showUploadingInfoBufferSize(Intent intent) {
        int num = intent.getIntExtra(UploadAlertasService.BUFFER_SIZE, -1);
        TextView text = (TextView) findViewById(R.id.uploadingAlertasInfoText);
        if (num > 0) {
            text.setText("Subiendo alertas (" + Integer.toString(num) + " restantes)");
        } else {
            text.setText("Terminando.");
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (listaAlertas.isEmpty()) {
            listaAlertas = baseDatos.getAlertas();

            if (listaAlertas.isEmpty()) {
                Log.i("MisAlertasActivity","populating alertas table");
                SharedPreferences preferences = getSharedPreferences("leBikePreferences", MODE_PRIVATE);
                String userId = preferences.getString(LoginActivity.USER_NAME, "");

                baseDatos.newAlerta(new Alerta(
                        200001, false, -33.450276, -70.627628,
                        "auto",
                        "22:22:21", "2017-01-20",
                        "Cruce de autos imprudentes",
                        "Casi salgo volando por un auto que se precipito con mi dedo chico",
                        300001, 0, "completa",
                        userId, false
                        ));
                /*
                baseDatos.newAlerta(new Alerta(
                        0, true, -33.444446, -70.628695,
                        "vias",
                        "22:22:22", "2017-01-20",
                        "Nueva ciclovia",
                        "esta super choriflai me encanta para venir cno mis amigos de la prepa",
                        3010, 0, "pendiente"));
                baseDatos.newAlerta(new Alerta(
                        0, false, -33.451013, -70.629386,
                        "peat",
                        "22:22:23", "2017-01-20",
                        "Peaton qlo",
                        "Que wea se cree el zarpao se te cruza y na así nomá",
                        3010, 0, "pendiente"));
                baseDatos.newAlerta(new Alerta(
                        0, false, -33.452174, -70.626191,
                        "auto",
                        "22:22:24", "2017-01-20",
                        "Muchas micros",
                        "Ando toldo nervioso por culpa de todas las micros",
                        3010, 0, "pendiente"));
                baseDatos.newAlerta(new Alerta(
                        0, true, -33.446737, -70.630335,
                        "mant",
                        "22:22:25", "2017-01-20",
                        "Buen taller de bicis",
                        "Justo se me pincho la rueda por acá cerca y pase a parcharla",
                        3010, 0, "completa"));
                baseDatos.newAlerta(new Alerta(
                        0, true, -33.446978, -70.628538,
                        "otro",
                        "22:22:26", "2017-01-20",
                        "Harta vida nocturna",
                        "Esta bueno, hay artos bares y pubs con estacionamiento para bicis",
                        3011, 0, "completa"));
                baseDatos.newAlerta(new Alerta(
                        0, true, -33.446057, -70.630664,
                        "vege",
                        "22:22:27", "2017-01-20",
                        "Rica calzada con arboles",
                        "Agradable circular por esta área",
                        3011, 0, "pendiente"));
                baseDatos.newAlerta(new Alerta(
                        0, false, -33.452373, -70.628868,
                        "peat",
                        "22:22:28", "2017-01-20",
                        "Cruce de muchos peatones",
                        "Es algo molesto poder cruzar a veces",
                        3011, 0, "completa"));
                baseDatos.newAlerta(new Alerta(
                        0, false, -33.448125, -70.630219,
                        "vege",
                        "22:22:29", "2017-01-20",
                        "Arbol molesto",
                        "Arbol se asoma hacía la calle y molesta el paso",
                        3011, 0, "pendiente"));
                */
            }
        }

        Log.i("MisAlertasActivity","número de alertas: " + listaAlertas.size());

        // TODO: Ahora cada fragment hace acceso a la base de datos y carga las alertas
        // correspondientes de manera independiente. El siguiente paso es hacer que la actividad
        // cargue la data de la BD y la entregue a los fragments que la desplegarán en listas.
        MisAlertasPagerAdapter adapter = new MisAlertasPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        /*---------- Service-Activity Communication ----------*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(UploadAlertasService.UPLOADING);
        filter.addAction(UploadAlertasService.ALL_UPLOADED);
        filter.addAction(UploadAlertasService.ALERTA_UPLOADED);
        LocalBroadcastManager.getInstance(this).registerReceiver(uploadingReceiver, filter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Check if something is not uploaded
        List<Alerta> notUpAlertas = baseDatos.getNotUploadedAlertas();
        if (notUpAlertas.size() > 0) {
            LinearLayout notUpInfo = (LinearLayout) findViewById(R.id.notUploadedAlertasInfoLL);
            notUpInfo.setVisibility(View.VISIBLE);

            Button uploadButton = (Button) findViewById(R.id.notUploadedAlertasInfoButton);
            uploadButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UploadAlertasService.class);
                    intent.setAction(UploadAlertasService.UPLOAD_NOT_UPLOADED);
                    startService(intent);
                }
            });
        }

        // Check if uploading
        Boolean uploading = ServiceUtils.isServiceRunning(this, UploadAlertasService.class);
        if (uploading) {
            showUploadingInfo(true, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*---------- Service-Activity Communication ----------*/
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadingReceiver);
    }

    /*                                /
     *        MENUU_ITEMSS            /
     *                               */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_misalertas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_alerta_menuitem) {
            Intent mapIntent = new Intent(this, EnRutaActivity.class);
            mapIntent.setAction("NEW_ALERTA_ACTION");
            startActivity(mapIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openAlertasEditDialog(Alerta alerta, double lat, double lon, int idRuta) {
        fragmentManager = getSupportFragmentManager();
        dialog = new AlertaEditDialog();
        Bundle newAlertaExtras = new Bundle();
        if (alerta != null) {
            Log.i(TAG, "alerta:" + alerta.getId() + ", vote:" + alerta.getPosNeg());
            newAlertaExtras = alerta.toBundle();
            newAlertaExtras.putString("NEW_ALERTA_ACTION", "EDIT_ALERTA");
        } else {
            int alertaId = 0; // Porque ahora al agregar una nueva alerta el id se genera solo.
            newAlertaExtras.putString("NEW_ALERTA_ACTION","NEW_ALERTA_FROM_MAP");
            newAlertaExtras.putInt("NEW_ALERTA_ID", alertaId);
            newAlertaExtras.putDouble("NEW_ALERTA_LAT", lat);
            newAlertaExtras.putDouble("NEW_ALERTA_LON", lon);
            newAlertaExtras.putInt("NEW_ALERTA_IDRUTA", idRuta);
        }

        dialog.setArguments(newAlertaExtras);
        dialog.setAlertasDialogListener(this);
        dialog.show(fragmentManager, "edit_alerta_fragment");
    }


    @Override
    public void onAlertasListClick(Alerta alerta) {
        openAlertasEditDialog(alerta, 0, 0, -1);
    }

    @Override
    public void onCloseClick() {
        dialog.dismiss();
    }

    @Override
    public void onMostrarMapa(Alerta alerta) {
        Intent mapIntent = new Intent(this, EnRutaActivity.class);
        mapIntent.setAction("SEE_ALERTA_ACTION");
        mapIntent.putExtra("ALERTA", alerta);
        startActivity(mapIntent);
        // TODO: terminar de implementar esto.
    }

    @Override
    public void onAgregarAlerta(Alerta alerta, String action) {
        Log.i(TAG, "onAgregarAlerta - in");
        switch(action) {
            case "UPDATE_ALERTA":
                Toast.makeText(this, "updating alerta " + alerta.getTitulo(), Toast.LENGTH_SHORT).show();
                baseDatos.updateAlerta(alerta);
                break;
            case "NEW_ALERTA":
                // No podemos subir esta alerta ya que puede ser una alerta nueva caso en el cual le faltaría
                // el id, generado por DatabaseHandler al agregarla a la BD. Entonces nos damos una vueltecilla.
                alerta.setId(baseDatos.getLastAlertaId() + 1);

                // Aquí puede haber un error pero tendría que haber demasiado timing para el problema. Puede
                // pasar que justo aquí, una vez que se obtuvo lastAlertaId, y antes de guardar esta alerta,
                // se agregue una alerta por otro medio (que tendría que ser bluetooth), luego esa alerta
                // tendría la misma id que esta de acá. Habrían dos alertas con el mismo id.
                // TODO: para evitar esto, habría que bloquear el agregar nuevas alertas mientras se esté
                // haciendo un tracking con bluetooth.

                Toast.makeText(this, "creating alerta " + alerta.getId(), Toast.LENGTH_SHORT).show();
                baseDatos.newAlerta(alerta);
                break;
            default:
                Log.i(TAG, "invalid action");
                break;
        }

        dialog.dismiss();

        Intent intent = new Intent(this, UploadAlertasService.class);
        intent.setAction(UploadAlertasService.UPLOAD_SINGLE_TRACK);
        intent.putExtra(UploadAlertasService.ALERTA, alerta);
        startService(intent);

        Intent intent1 = new Intent(this, MisAlertasActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent1);
    }

    @Override
    public void onEliminarAlerta(int alertaId) {
        if (alertaId > 0) {
            baseDatos.deleteAlerta(alertaId);
        }
        dialog.dismiss();
    }
}
