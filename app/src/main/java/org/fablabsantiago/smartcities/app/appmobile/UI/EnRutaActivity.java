package org.fablabsantiago.smartcities.app.appmobile.UI;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanManager;

import org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService;
import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;
import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.EnRutaAuxiliaryBottomBar;
import org.fablabsantiago.smartcities.app.appmobile.Utils.GPX;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Ruta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnRutaActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        EnRutaAuxiliaryBottomBar.BottomBarListener
{
    protected String TAG = EnRutaActivity.class.getSimpleName();

    private Context context;
    DatabaseHandler baseDatos;
    String intentAction;
    Destino destino;
    int destinationId;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LatLng destinoLatLng;
    private LatLng defaultLatLng;

    private GoogleMap mMap;
    private List<Ruta> rutasADestino;
    private List<Alerta> alertas;
    private List<Marker> mapAlertas;
    private Map<Marker, Alerta> angelaMerkel;
    private CameraPosition mapCameraPosition;

    private Marker addAlertaMarker;
    private Snackbar addAlertaSnackbar;

    private BluetoothAdapter mBluetoothAdapter;
    boolean bluetoothIsEnabled;
    boolean isBeanConnected;
    final List<Bean> beans = new ArrayList<>();
    private Bean connectedBean;
    private ArrayAdapter<String> adapter;
    private ListView beansListView;

    private BroadcastReceiver trackingReceiver;

    EnRutaAuxiliaryBottomBar auxiliarBottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("EnRutaActivity", "onCreate - in");
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_enruta);

        // Leyendo contenido del intent:
        //     - destino (en caso de querer dirigirse a un destino)
        //     - action (en caso de que la actividad se habrá con una intención especifica)
        //           '-> opciones: "NEW_ALERTA"
        Intent intent = getIntent();
        destinationId = intent.getIntExtra("DESTINO_ID", 0);
        intentAction = intent.getAction();
        if (intentAction == null) {intentAction = "";}
        Log.i("EnRutaACtivity", "id destino: " + Integer.toString(destinationId));

        /*---------- Base Datos ----------*/
        baseDatos = new DatabaseHandler(this);

        /*---------- Mapa ----------*/
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApiIfAvailable(LocationServices.API)
                    .build();
        }
        rutasADestino = new ArrayList<Ruta>();
        alertas = new ArrayList<Alerta>();
        mapAlertas = new ArrayList<Marker>();
        angelaMerkel = new HashMap<Marker, Alerta>();
        mapCameraPosition = null;

        mCurrentLocation = null;
        destinoLatLng = null;
        defaultLatLng = new LatLng(-33.432531, -70.653368);

        addAlertaMarker = null;

        /*---------- Tracking ----------*/
        auxiliarBottomFragment = (EnRutaAuxiliaryBottomBar) getSupportFragmentManager().findFragmentById(R.id.bottomEnRutaFragment);
        auxiliarBottomFragment.setBottomBarListener(this);

        /*---------- Comunicación TrackingService ----------*/
        initializeServiceCommunication();
    }

    protected void initializeServiceCommunication() {
        trackingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Broadcast received.");
                String action = intent.getAction();
                switch(action) {
                    case TrackingService.BEAN_CONNECTED:
                        beanConnected(intent);
                        break;
                    case TrackingService.BEAN_DISCONNECTED:
                        beanDisconnected();
                        break;
                    case TrackingService.NEW_ALERTA:
                        beanNewAlerta(intent);
                        break;
                    case TrackingService.TRACKING_STARTED:
                        onTrackingStarted();
                        break;
                    case TrackingService.NEW_ROUTE_POINT:
                        onNewRoutePoint(intent);
                        break;
                    case TrackingService.TRACKING_ENDED:
                        onTrackingEnded();
                        break;
                    default:
                        Log.i(TAG, "TrackingReceiver - Invalid action");
                        break;
                }

            }
        };
    }

    protected void beanConnected(Intent intent) {
        connectedBean = intent.getParcelableExtra(TrackingService.BEAN);

        TextView info = (TextView) findViewById(R.id.infoTextView);
        info.setText(connectedBean.getDevice().getName() + " Connected.");
        FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
        newmail.setVisibility(View.VISIBLE);
    }

    protected void beanDisconnected() {
        connectedBean = null;
        TextView info = (TextView) findViewById(R.id.infoTextView);
        info.setText("Bean Disconnected.");
        FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
        newmail.setVisibility(View.GONE);
    }

    protected void beanNewAlerta(Intent intent) {
        Log.i(TAG, "beanNewAlerta - in");
        Alerta alerta = intent.getParcelableExtra(TrackingService.ALERTA);

        TextView info = (TextView) findViewById(R.id.infoTextView);
        Snackbar.make(info, "Vote: " + Boolean.toString(alerta.getPosNeg()), Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        /*                          */
        /* Point alerta and save it */
        /* ------------------------ */
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        //Location pointLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        BitmapDescriptor posNegIcon = (alerta.getPosNeg()) ?
                BitmapDescriptorFactory.fromResource(R.drawable.marker_positive)
                : BitmapDescriptorFactory.fromResource(R.drawable.marker_negative);

        // TODO: (7) agregar marker a mapAlertas.
        Marker point = mMap.addMarker(new MarkerOptions()
                .position(alerta.getLatLng())
                .icon(posNegIcon));
    }

    protected void onTrackingStarted() {
        boolean trackingState = true;
        startTrackResponseToAuxFragment(trackingState);
    }

    protected void onNewRoutePoint(Intent intent) {
        Location location = intent.getParcelableExtra(TrackingService.LOCATION);
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(loc2LL(location))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.big_dot)));
        }
    }

    protected void onTrackingEnded() {
        //Save ruta table to a gpx file
        List<Location> routePoints = baseDatos.getRoutePoints();
        if (routePoints.size() > 2) {
            File file = new File(context.getFilesDir(), Integer.toString(baseDatos.getLastRutasId()) + ".gpx");
            GPX.writePath(file, Integer.toString(baseDatos.getLastRutasId()), routePoints);
            Log.i("EnRutaActivity", "saved file: " + file.toString());
        } else {
            baseDatos.deleteRoute(baseDatos.getLastRutasId());
        }

        baseDatos.eraseTrackPoints();

        boolean trackingState = false;
        startTrackResponseToAuxFragment(trackingState);
    }

    @Override
    protected void onStart() {
        Log.i("EnRutaActivity", "onStart - in");
        super.onStart();

        /*---------- Base Datos ----------*/
        //TODO: (1) Implementar rutina para saber si se esta grabando un track o no.
        // En caso de estar grabandose un track el destino debe estar guardado como estado.
        // De esta manera debe agregarse al vector de estados (SharedPreferences) estos valores.
        destino = baseDatos.getDestinationById(destinationId);
        if (destino != null) {
            setTitle("En Ruta: " + destino.getName());
            auxiliarBottomFragment.onActivityLoaded(destino);
        } else {
            setTitle("Mapa");
            //Destino no existe.
            //TODO: ver que hacer cuando el destino no existe.
            //  - cerrar la actividad y informar error y pedir que se contacten
            //  - desplegar un mapa default genérico
            //  - pedir que ingrese un nuevo destino o presione un destino válido
            Log.i("EnRutaActivity", "Invalid destinationId, no destino.");
        }

        /*---------- Mapa ----------*/
        mGoogleApiClient.connect();

        /*---------- Bluetooth ----------*/
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "BLE is supported", Toast.LENGTH_SHORT).show();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "error: bluetooth not supported", Toast.LENGTH_SHORT).show();

            // mBluetoothAdapter.isEnabled() mejor hacerlo cada vez que se aprete porque el loco
            // puede activarlo en medio del uso de la app
        }

        /*---------- Service-Activity Communication ----------*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(TrackingService.BEAN_CONNECTED);
        filter.addAction(TrackingService.BEAN_DISCONNECTED);
        filter.addAction(TrackingService.NEW_ALERTA);
        filter.addAction(TrackingService.TRACKING_STARTED);
        filter.addAction(TrackingService.NEW_ROUTE_POINT);
        filter.addAction(TrackingService.TRACKING_ENDED);
        LocalBroadcastManager.getInstance(this).registerReceiver(trackingReceiver, filter);
    }

    // BottomBar interface
    @Override
    public void startTrack(boolean trackingState) {
        // TODO: (3) confirm correct service load from the service.
        // Actual implementation is blind to whats happening in the service.
        Intent trackingRouteService = new Intent(this, TrackingService.class);
        if (!trackingState) {
            if (isLocationAvailableAndStoreIt()) {
                trackingRouteService.putExtra("destino_id", destinationId);
                trackingRouteService.setAction(TrackingService.START_TRACK);
                Log.i("EnRutaActivity", "startTrack - comenzando servicio");
                startService(trackingRouteService);
                // Then, when the service initializces correctly, it calls back the activity by a broadcast

                // TODO: (4) Check if location is available through the background service.
                // If location its shutted down while recording, push a notification warning.
            } else {
                Log.i("EnRutaActivity", "onClick: ubicación no disponible");
            }
        } else {
            Log.i("EnRutaActivity", "onClick - cerrando servicio");
            stopService(trackingRouteService);
        }
    }

    protected void startTrackResponseToAuxFragment(boolean trackngState) {
        auxiliarBottomFragment.startTrackResponse(trackngState);
    }

    @Override
    protected void onStop() {
        Log.i("EnRutaActivity", "onStop - in");
        super.onStop();

        /*---------- Mapa ----------*/
        mGoogleApiClient.disconnect();

        /*---------- Service-Activity Communication ----------*/
        LocalBroadcastManager.getInstance(this).unregisterReceiver(trackingReceiver);
    }


    @Override
    protected void onResume() {
        Log.i("EnRutaActivity", "onResume - in");
        super.onResume();

        /*---------- Bluetooth ----------*/
        beansListView = (ListView) findViewById(R.id.beansListView);
        ArrayList<String> beanList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, beanList);
        beansListView.setAdapter(adapter);
        beansListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) beansListView.getItemAtPosition(position);
                Log.i("MainActivity", "onItemClick, pos:" + Integer.toString(position) + ", item:" + itemValue);

                cancelScann(false);

                //TODO: cambiar '0' por 'position'
                // Assume we have a reference to the 'beans' ArrayList from above.
                final Bean beanElement = beans.get(0);

                beanElementClicked(beanElement);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("EnRutaActivity", "onConnected - in");

        /*---------- Base Datos ----------*/
        if (alertas.isEmpty()) {
            Log.i("EnRutaActivity", "populating mapAlertas from BD");
            alertas = baseDatos.getAlertas();
        }
        if (destino != null) {
            if (rutasADestino.isEmpty()) {
                rutasADestino = baseDatos.getRoutesByDestId(destinationId);
                Log.i("EnRutaActivity", "loaded rutas a destino, cant: " + Integer.toString(rutasADestino.size()));
            }
            auxiliarBottomFragment.destinoRutas(rutasADestino);
        }

        /*---------- Mapa ----------*/
        isLocationAvailableAndStoreIt();
        if (destino != null) {
            destinoLatLng = new LatLng(destino.getLatitude(), destino.getLongitude());
        }
        //mCurrentLocation = new Location("");
        //mCurrentLocation.setLatitude(-33.449455);
        //mCurrentLocation.setLongitude(-70.627729);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("EnRutaActivity", "onConnectionSuspended - in");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("EnRutaActivity", "onConnectionFailed - in");
    }

    /* ------------------------------------------------------ */
    /*    onMapReady, here go all map configurations          */
    /*                                                        */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("EnRutaActivity", "onMapReady - in");

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //
        }

        mMap.setMyLocationEnabled(true);
        //isLocationAvailableAndStoreIt();
        //mMap.addMarker(new MarkerOptions().position(loc2LL(mCurrentLocation)));

        /*--------------- Enfoque ---------------*/
        /*---------------------------------------*/
        if (mCurrentLocation != null) {
            if(destinoLatLng != null) {
                // enfocar área de interés
                Log.i("EnRutaActivity", "onMapReady - enfoque área de interés");
                //mCurrentLocation.setLatitude(-33.37129);
                //mCurrentLocation.setLongitude(-70.634097);

                LatLng currentLocation = new LatLng(-33.437026, -70.634376);

                // Aeropuerto   -33.397981, -70.792608
                // Costanera    -33.417842, -70.605256
                // Cementerio   -33.416481, -70.643458
                // E. Nacional  -33.464745, -70.609142
                // Fablab SCL   -33.449455, -70.627729
                // Beauchef     -33.457892, -70.663839
                // MIM          -33.519556, -70.612131
                // Antumapu     -33.572439, -70.630439
                // Irrarazabal  -33.453808, -70.628589
                // Tilianos     -33.450417, -70.628432
                // P. Baquedano -33.437026, -70.634376

                LatLngBounds area = latLng2CorrectBounds(destinoLatLng, loc2LL(mCurrentLocation));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(area, 200));
            } else {
                // enfocar solo posición actual
                Log.i("EnRutaActivity", "onMapReady - enfoque posición actual");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc2LL(mCurrentLocation), 14));
            }
        } else {
            if(destinoLatLng != null) {
                //enfocar solo destino
                Log.i("EnRutaActivity", "onMapReady - enfoque solo destino");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinoLatLng, 10));
            } else {
                //enfocar locación por defecto
                Log.i("EnRutaActivity", "onMapReady - enfoque por defecto");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 10));
            }
        }

        /*--------------- Verify if points already drawn ---------------*/
        /*--------------------------------------------------------------*/
        //Verify if we're reloading the map, in order not to redefine and reconfigure old markers
        if (mapAlertas.size() != 0) {
            Log.i("EnRutaActivity","");
            Log.i("EnRutaActivity", "onMapReady - reloading activity, scaping method");
            return;
        }

        /*---------------- Elementos Gráficos Extra ----------------*/
        /*----------------------------------------------------------*/
        if (destino != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(destinoLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_stop)));
        }

        if (intentAction.equals("NEW_ALERTA_ACTION")) {
            LinearLayout container = (LinearLayout) findViewById(R.id.enRutaLinearLayoutContainer);
            Snackbar.make(container, "Long press to place new alerta", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }

        /*--------------- Posicionamiento de Alertas ---------------*/
        /*----------------------------------------------------------*/
        Log.i("EnRutaActivity","dibujando " + alertas.size() + " alertas");
        for (Alerta alerta : alertas) {
            Log.i("EnRutaActivity", "alerta: " + alerta.getTitulo() + ", id:" + alerta.getId());
            BitmapDescriptor posNegIcon = (alerta.getPosNeg()) ?
                    BitmapDescriptorFactory.fromResource(R.drawable.marker_positive)
                    : BitmapDescriptorFactory.fromResource(R.drawable.marker_negative);

            Marker point = mMap.addMarker( new MarkerOptions()
                    .position(alerta.getLatLng())
                    .icon(posNegIcon));

            // Evaluar bien como hacer esto, ya que necesito tener un relación entre
            // el marker y su respectiva alerta.
            mapAlertas.add(point);
            angelaMerkel.put(point, alerta);
        }
        Log.i("EnRutaActivity", "last alerta id: " + baseDatos.getLastAlertaId());

        /*--------------- Dibujo de rutas a destino ----------------*/
        /*----------------------------------------------------------*/
        for (Ruta ruta : rutasADestino) {
            Log.i("EnRutaActivity", "ruta: " + ruta.getName() + "id: " + Integer.toString(ruta.getId()));
            mMap.addPolyline(new PolylineOptions()
                    .width((float) 10.0)
                    .color(Color.parseColor("#80ec903a"))
                    .addAll(ruta.getTrack(this, ruta.getId()))
            );
        }

        Log.i("EnRutaACtivity","las ruta id: " + baseDatos.getLastRutasId());

        /*---------------- Agregar nuevas alertas ------------------*/
        /*----------------------------------------------------------*/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (addAlertaMarker != null) {
                    addAlertaMarker.remove();
                }
                addAlertaMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_new_alerta_grey)));
                LinearLayout container = (LinearLayout) findViewById(R.id.enRutaLinearLayoutContainer);
                addAlertaSnackbar = Snackbar.make(container, "Presionar para agregar alerta", Snackbar.LENGTH_INDEFINITE);
                addAlertaSnackbar.show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i("EnRutaActivity", "onMarkerClick - in");
                if (marker.equals(addAlertaMarker)) {
                    Log.i("EnRutaActivity", "onMarkerClick - on addAlertaMarker");
                    Intent addAlertaIntent = new Intent(context, MisAlertasActivity.class);
                    addAlertaIntent.setAction("REQUESTING_NEW_ALERTA");
                    addAlertaIntent.putExtra("NEW_ALERTA_LATITUDE", marker.getPosition().latitude);
                    addAlertaIntent.putExtra("NEW_ALERTA_LONGITUDE", marker.getPosition().longitude);
                    startActivity(addAlertaIntent);
                }
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng) {
                if (addAlertaMarker != null) {
                    addAlertaMarker.remove();
                }
            }
        });
    }

    protected boolean isLocationAvailableAndStoreIt()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: (5) Handle this permission request properly
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("EnRutaActivity", "Location permissions (fine|coarse) not granted");
            //return false;

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 234);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        //Check if location enabled       and notify
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this,"Only network location is available. It may not be precise.",Toast.LENGTH_SHORT).show();
                return true;
            }

            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation == null) {
                Toast.makeText(this,"Couldn't retrieve location. Try again later.",Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mCurrentLocation == null) {
                Toast.makeText(this,"Enable location settings, please :)",Toast.LENGTH_SHORT).show();
                // TODO: (6) desplegar menú para activar localización.
                return false;
            } else {
                Toast.makeText(this,"Location is being retrieved neither from gps nor from network.",Toast.LENGTH_SHORT).show();
                Log.i("EnRutaActivity","onMapReady: Location is being retrieved neither from gps nor from network.");
                return true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 234: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected LatLngBounds latLng2CorrectBounds(LatLng marker1, LatLng marker2) {
        //Works for south east hemisphere (With the eculator and first meridian as reference).
        double m1Lat = marker1.latitude;
        double m1Lon = marker1.longitude;
        double m2Lat = marker2.latitude;
        double m2Lon = marker2.longitude;
        double swLat = m2Lat;
        double neLon = m1Lon;
        double swLon = m2Lon;
        double neLat = m1Lat;

        Log.i("EnRutaActivity", "correcting bounds: m1 (" + String.valueOf(m1Lat) + ", " + String.valueOf(m1Lon) + "), " +
                                                   "m2 (" + Double.toString(m2Lat) + ", " + Double.toString(m2Lon) + ")");

        if (m1Lat < m2Lat) {
            swLat = m1Lat;
            neLat = m2Lat;
        }
        if (m1Lon < m2Lon) {
            swLon = m1Lon;
            neLon = m2Lon;
        }

        Log.i("EnRutaActivity", "correcting bounds: m1 (" + Double.toString(swLat) + ", " + Double.toString(swLon) + "), " +
                                                   "m2 (" + Double.toString(neLat) + ", " + Double.toString(neLon) + ")");

        return new LatLngBounds(new LatLng(swLat,swLon), new LatLng(neLat,neLon));
    }

    protected LatLng loc2LL(Location location) {
        return  new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void bringAlertasDrawer(View view) {
        ScrollView alertasDrawer = (ScrollView) findViewById(R.id.alertasDrawerMula);
        View nonAlertasDrawer = (View) findViewById(R.id.non_alertas_drawer_space);
        Button bringAlertasDrawerButton = (Button) findViewById(R.id.bring_alertas_drawer_button);
        alertasDrawer.setVisibility(View.VISIBLE);
        nonAlertasDrawer.setVisibility(View.VISIBLE);
        bringAlertasDrawerButton.setVisibility(View.GONE);
    }

    public void hideAlertasDrawer(View view) {
        ScrollView alertasDrawer = (ScrollView) findViewById(R.id.alertasDrawerMula);
        View nonAlertasDrawer = findViewById(R.id.non_alertas_drawer_space);
        Button bringAlertasDrawerButton = (Button) findViewById(R.id.bring_alertas_drawer_button);
        alertasDrawer.setVisibility(View.GONE);
        nonAlertasDrawer.setVisibility(View.GONE);
        bringAlertasDrawerButton.setVisibility(View.VISIBLE);
    }


    /*---------- Bluetooth ----------*/
    protected void cancelScann(boolean textFlag) {
        BeanManager.getInstance().cancelDiscovery();
        TextView info = (TextView) findViewById(R.id.infoTextView);
        String infoText = (textFlag) ? "Scanning Canceled." : "Connecting...";
        info.setText(infoText);
        FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
        newmail.setVisibility(View.GONE);
    }

    /*---------- Bluetooth ----------*/
    protected void beanElementClicked(final Bean bean) {
        Intent bleIntent = new Intent(this, TrackingService.class);
        bleIntent.setAction(TrackingService.CONNECT_BLE);
        bleIntent.putExtra(TrackingService.BEAN, bean);
        startService(bleIntent);
    }

    /*---------- Bluetooth ----------*/
    public void beanOptionsClick(View view) {
        // Si bluetooth disabled, acá se cae porque mBluetoothAdapter da null
        if (mBluetoothAdapter.isEnabled()) {
            Log.i("EnRutaActivity","Bean options button clicked");
            beans.clear();
            adapter.clear();
            adapter.notifyDataSetChanged();
            BeanDiscoveryListener listener = new BeanDiscoveryListener()
            {
                @Override
                public void onBeanDiscovered(Bean bean, int rssi) {
                    Log.i("EnRutaActivity","onBeanDiscovered - in");
                    beans.add(bean);
                    adapter.add("name: " + bean.getDevice().getName() + "    adrr: " + bean.getDevice().getAddress());
                    //adapter.add("rssi:" + Integer.toString(rssi) + " name:" + bean.getDevice().getName() + " (" + bean.getDevice().getAddress() + ")");
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onDiscoveryComplete() {
                    Toast.makeText(context, "Bean listener finished", Toast.LENGTH_SHORT).show();
                    Log.i("EnRutaActivity", "onDiscoveryComplete");
                    TextView info = (TextView) findViewById(R.id.infoTextView);
                    info.setText("Scanning complete.");
                    FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                    newmail.setVisibility(View.GONE);
                }
            };

            BeanManager.getInstance().startDiscovery(listener);

            TextView info = (TextView) findViewById(R.id.infoTextView);
            info.setText("Scanning...");
            FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
            newmail.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Press new mail to cancel ->", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "Bean listener started", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else { //Bluetooth disabled
            disabledBluetoothAction();
        }
    }

    /*---------- Bluetooth ----------*/
    public void disabledBluetoothAction() {
        Toast.makeText(this, "Enable bluetooth communication, please :)", Toast.LENGTH_SHORT).show();
        //TODO: (8) show notification for accesing bluetooth settings
    }

    /*---------- Bluetooth ----------*/
    public void newMail(View view) //cancelButtonc
    {
        if (connectedBean != null) {
            if (connectedBean.isConnected()) {
                connectedBean.disconnect();
                connectedBean = null;
                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText("Bean Disconnected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.GONE);
            } else {
                Log.e("MainActivity", "newMail, ERROR: connectedBean != null && connectedBean disconnected, no valid states");
            }
        } else {
            cancelScann(true);
        }
    }

    /*---------- Bluetooth ----------*/
    public void exitDeviceConnect(View view) {
        FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
        LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);
        exitBeanConnectSubactivity(onRouteMapActivity,beanConnectActivity);
    }

    @Override
    public void onBackPressed(){
        Log.i("EnRutaActivity", "OnBackPressed - in");

        FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
        LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);

        if(beanConnectActivity.getVisibility() == View.VISIBLE) {
            //Exited xd
            exitBeanConnectSubactivity(onRouteMapActivity, beanConnectActivity);
        } else {
            //Out
            super.onBackPressed();
        }
    }

    /*                                /
     *        MENUU_ITEMSS            /
     *                               */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onroutemap, menu);
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
        if (id == R.id.action_bean_device) {
            FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
            LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);

            if(beanConnectActivity.getVisibility() == View.VISIBLE) {
                //Exited xd
                exitBeanConnectSubactivity(onRouteMapActivity, beanConnectActivity);
            } else {
                //Entered
                onRouteMapActivity.setVisibility(View.INVISIBLE);
                beanConnectActivity.setVisibility(View.VISIBLE);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void exitBeanConnectSubactivity(View v1, View v2) {
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        //Cancel bean search
        if (connectedBean != null) {
            if (!connectedBean.isConnected()) {
                cancelScann(true);
            }
        } else {
            cancelScann(true);
        }
    }
}
