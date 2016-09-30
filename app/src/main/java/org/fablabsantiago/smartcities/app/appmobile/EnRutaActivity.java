package org.fablabsantiago.smartcities.app.appmobile;

import android.*;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        GoogleApiClient.OnConnectionFailedListener
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
        Log.i("EnRutaACtivity","id destino: " + Integer.toString(destinationId));

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
        defaultLatLng = new LatLng(-33.449796, -70.6277000);

        addAlertaMarker = null;
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
    }

    @Override
    protected void onStop() {
        Log.i("EnRutaActivity", "onStop - in");
        super.onStop();

        /*---------- Mapa ----------*/
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        Log.i("EnRutaActivity","onResume - in");
        super.onResume();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("EnRutaActivity", "onConnected - in");

        /*---------- Base Datos ----------*/
        if (alertas.isEmpty()) {
            Log.i("EnRutaActivity","populating mapAlertas from BD");
            alertas = baseDatos.getAlertas();
        }
        if (destino != null) {
            if (rutasADestino.isEmpty()) {
                rutasADestino = baseDatos.getRoutesByDestId(destinationId);
            }
        }

        /*---------- Mapa ----------*/
        isLocationAvailableAndStoreIt();
        if (destino != null) {
            destinoLatLng = new LatLng((double) destino.getLatitude(), (double) destino.getLongitude());
        }
        mCurrentLocation = new Location("");
        mCurrentLocation.setLatitude(-33.449455);
        mCurrentLocation.setLongitude(-70.627729);

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

        /*--------------- Enfoque ---------------*/
        /*---------------------------------------*/
        if (mCurrentLocation != null) {
            if(destinoLatLng != null) {
                // enfocar área de interés
                Log.i("EnRutaActivity", "onMapReady - enfoque área de interés");
                mCurrentLocation.setLatitude(-33.37129);
                mCurrentLocation.setLongitude(-70.634097);

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

                LatLngBounds area = latLng2CorrectBounds(destinoLatLng, currentLocation);
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
            Log.i("EnRutaActivity", "ruta: " + ruta.getName());
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("EnRutaActivity", "Location permissions (fine|coarse) not granted");
            return false;
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
                Log.i("OnRouteMapActivity","onMapReady: Location is being retrieved neither from gps nor from network.");
                return true;
            }
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
}
