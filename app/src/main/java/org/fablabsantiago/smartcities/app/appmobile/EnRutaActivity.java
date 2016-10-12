package org.fablabsantiago.smartcities.app.appmobile;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Permission;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class EnRutaActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        EnRutaAuxiliaryBottomBar.BottomBarListener
{
    private Context context;
    DatabaseHandler baseDatos;
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

    private BluetoothAdapter mBluetoothAdapter;
    boolean bluetoothIsEnabled;
    boolean isBeanConnected;
    final List<Bean> beans = new ArrayList<>();
    private Bean connectedBean;
    private ArrayAdapter<String> adapter;
    private ListView beansListView;

    EnRutaAuxiliaryBottomBar auxiliarBottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("EnRutaActivity", "onCreate - in");
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_enruta);
        /*---------- Base Datos ----------*/
        baseDatos = new DatabaseHandler(this);

        Intent intent = getIntent();
        destinationId = intent.getIntExtra("DESTINO_ID", 0);
        Log.i("EnRutaACtivity", "id destino: " + Integer.toString(destinationId));

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

        addAlertaMarker = null;

        /*---------- Tracking ----------*/
        auxiliarBottomFragment = (EnRutaAuxiliaryBottomBar) getSupportFragmentManager().findFragmentById(R.id.bottomEnRutaFragment);;
        auxiliarBottomFragment.setBottomBarListener(this);
    }

    @Override
    protected void onStart() {
        Log.i("EnRutaActivity", "onStart - in");
        super.onStart();

        /*---------- Base Datos ----------*/
        //TODO: Implementar rutina para saber si se esta grabando un track o no.
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
    }

    @Override
    public void startTrack(boolean trackingState) {
        // TODO: confirm correct service load from the service.
        // Actual implementation is blind to whats happening in the service.
        Intent trackingRouteService = new Intent(this, EnRutaTrackingService.class);
        if (!trackingState) {
            if (isLocationAvailableAndStoreIt()) {
                trackingRouteService.putExtra("destino_id", destinationId);
                Log.i("EnRutaActivity", "startTrack - comenzando servicio");
                startService(trackingRouteService);
                trackingState = true;
                // TODO: Check if location is available through the background service.
                // If location its shutted down while recording, push a notification warning.
            } else {
                Log.i("EnRutaActivity", "onClick: ubicación no disponible");
            }
        } else {
            Log.i("EnRutaActivity", "onClick - cerrando servicio");
            stopService(trackingRouteService);
            trackingState = false;

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
        }

        auxiliarBottomFragment.startTrackResponse(trackingState);
    }

    @Override
    protected void onStop() {
        Log.i("EnRutaActivity", "onStop - in");
        super.onStop();

        /*---------- Mapa ----------*/
        mGoogleApiClient.disconnect();

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

                // Assume we have a reference to the 'beans' ArrayList from above.
                final Bean beanElement = beans.get(0);

                beanElementClicked(beanElement);
            }
        });
    }


    @Override
    protected void onResume() {
        Log.i("EnRutaActivity", "onResume - in");
        super.onResume();

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
            destinoLatLng = new LatLng((double) destino.getLatitude(), (double) destino.getLongitude());
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

        /*--------------- Posicionamiento de Alertas ---------------*/
        /*----------------------------------------------------------*/
        for (Alerta alerta : alertas) {
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
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_grey)));
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
                    addAlertaIntent.setAction("NEW_ALERTA_FROM_MAP");
                    addAlertaIntent.putExtra("NEW_ALERTA_LATITUDE", marker.getPosition().latitude);
                    addAlertaIntent.putExtra("NEW_ALERTA_LONGITUDE", marker.getPosition().longitude);
                    startActivity(addAlertaIntent);
                }
                return false;
            }
        });
    }

    protected boolean isLocationAvailableAndStoreIt()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle this permission request properly
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
                // TODO: desplegar menú para activar localización.
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
        //Works for south east hemisphere (With the ecuator and first meridian as reference).
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
        BeanListener beanListener = new BeanListener()
        {
            @Override
            public void onConnected() {
                connectedBean = bean;

                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText(bean.getDevice().getName() + " Connected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.VISIBLE);

                Log.i("MainActivity", "connected to '" + bean.getDevice().getName());
                bean.readTemperature(new Callback<Integer>()
                {
                    @Override
                    public void onResult(Integer temp) {
                        Toast.makeText(context, bean.getDevice().getName() + " temp = " + Integer.toString(temp) + "°C", Toast.LENGTH_SHORT).show();
                        Log.i("MainActivity", "onConnected," + bean.getDevice().getName() + "temp = " + Integer.toString(temp) + "°C");
                    }
                });

            }

            @Override
            public void onConnectionFailed() {
                Log.i("MainActivity", "BeanListener.onConnectionFailed");
            }

            @Override
            public void onDisconnected() {
                Log.i("MainActivity", "BeanListener.onDisconnected");
                connectedBean = null;
                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText("Bean Disconnected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.GONE);
            }

            @Override
            public void onSerialMessageReceived(byte[] data) {
                Log.i("MainActivity", "BeanListener.onSerialMessageReceived");
                Integer dataLength = data.length;
                if (dataLength != 1)
                {
                    // ¿Hay que revisar cuando el length puede ser 0?
                    Log.i("MainActivity", "BeanListener message length: " + Integer.toString(dataLength));
                    char[] dataChar = new char[dataLength];
                    //byte b;
                    for (int i = 0; i < dataLength; i++)
                    {
                        //Log.i("MainActivity","BeanListener msg: " + Byte.toString(b) + "(end)");
                        dataChar[i] = (char) data[i];
                    }
                    String msg = new String(dataChar);
                    Log.i("MainActivity", "BeanListener message content: " + msg);

                    TextView info = (TextView) findViewById(R.id.infoTextView);
                    Snackbar.make(info, "Serial msg: " + msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                    /*                          */
                    /* Point alerta and save it */
                    /* ------------------------ */
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return;
                    }
                    Location pointLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    BitmapDescriptor posNegIcon = (msg.contains("ii")) ?
                            BitmapDescriptorFactory.fromResource(R.drawable.marker_positive)
                            : BitmapDescriptorFactory.fromResource(R.drawable.marker_negative);

                    Marker point = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(pointLocation.getLatitude(), pointLocation.getLongitude()))
                            .icon(posNegIcon));

                } else
                {
                    //Pensar que hacer con lo CR LF - \r\n
                }
            }

            @Override
            public void onScratchValueChanged(ScratchBank bank, byte[] value) {
                Log.i("MainActivity", "BeanListener.onScratchValueChanged");
            }

            @Override
            public void onError(BeanError error) {
                Log.i("MainActivity", "BeanListener.onError");
            }

            @Override
            public void onReadRemoteRssi(int rssi) {
                Log.i("MainActivity", "BeanListener.onReadRemoteRssi");
            }
        };

        // Assuming you are in an Activity, use 'this' for the context
        bean.connect(context, beanListener);
    }

    /*---------- Bluetooth ----------*/
    public void beanOptionsClick(View view) {
        // Si bluetooth disabled, acá se cae porque mBluetoothAdapter da null
        if (mBluetoothAdapter.isEnabled())
        {
            adapter.clear();
            adapter.notifyDataSetChanged();
            BeanDiscoveryListener listener = new BeanDiscoveryListener()
            {
                @Override
                public void onBeanDiscovered(Bean bean, int rssi) {
                    adapter.add("name: " + bean.getDevice().getName() + "    adrr: " + bean.getDevice().getAddress());
                    //adapter.add("rssi:" + Integer.toString(rssi) + " name:" + bean.getDevice().getName() + " (" + bean.getDevice().getAddress() + ")");
                    adapter.notifyDataSetChanged();
                    beans.add(bean);
                }

                @Override
                public void onDiscoveryComplete() {
                    Toast.makeText(context, "Bean listener finished", Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", "onDiscoveryComplete");
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
        } else //Bluetooth disabled
        {
            disabledBluetoothAction();
        }
    }

    /*---------- Bluetooth ----------*/
    public void disabledBluetoothAction() {
        Toast.makeText(this, "Enable bluetooth communication, please :)", Toast.LENGTH_SHORT).show();
        //TODO: show notification for accesing bluetooth settings
    }

    /*---------- Bluetooth ----------*/
    public void newMail(View view) //cancelButton
    {
        if (connectedBean != null)
        {
            if (connectedBean.isConnected())
            {
                connectedBean.disconnect();
                connectedBean = null;
                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText("Bean Disconnected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.GONE);
            } else
            {
                Log.e("MainActivity", "newMail, ERROR: connectedBean != null && connectedBean disconnected, no valid states");
            }
        } else
        {
            cancelScann(true);
        }
    }

    /*---------- Bluetooth ----------*/
    public void exitDeviceConnect(View view)
    {
        FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
        LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);
        exitBeanConnectSubactivity(onRouteMapActivity,beanConnectActivity);
    }

    @Override
    public void onBackPressed(){
        Log.i("EnRutaActivity", "OnBackPressed - in");

        FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
        LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);

        if(beanConnectActivity.getVisibility() == View.VISIBLE)
        {
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
        if (id == R.id.action_bean_device)
        {
            FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
            LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);

            if(beanConnectActivity.getVisibility() == View.VISIBLE)
            {
                //Exited xd
                exitBeanConnectSubactivity(onRouteMapActivity, beanConnectActivity);
            }
            else
            {
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
