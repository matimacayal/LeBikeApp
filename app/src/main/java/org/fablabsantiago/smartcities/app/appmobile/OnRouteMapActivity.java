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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.Frame;
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
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class OnRouteMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private String destinationName;
    private String destinationDisplayName;
    private Destination destino;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private GoogleMap mMap;
    private LatLng destination;
    private LatLng fablabSCL;
    private ArrayList<Marker> mapMarkers;
    private ArrayList<Polyline> mapPolylines;
    private CameraPosition mapCameraPosition;

    SharedPreferences leBikePrefs;
    boolean bTrackingRoute;

    private BluetoothAdapter mBluetoothAdapter;
    boolean bluetoothIsEnabled;
    boolean isBeanConnected;
    final List<Bean> beans = new ArrayList<>();
    private Bean connectedBean;
    final Context context = this;
    private ArrayAdapter<String> adapter;
    private ListView beansListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("OnRouteMapActivity","onCreate - in");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onroutemap);

        Intent intent = getIntent();
        String destinationDisplayName = intent.getStringExtra("DESTINO");
        setTitle(destinationDisplayName);

        // leBikePrefs has:
        //     - trackingRoute: true , si se esta grabando una ruta
        //                      falso, si no
        leBikePrefs = getSharedPreferences("leBikePreferences",MODE_PRIVATE);
        bTrackingRoute = leBikePrefs.getBoolean("BOOL_TRACKING_ROOT",false);
        refreshUIOnRouteStarted(bTrackingRoute);

        // Initialize markers and polylines array for storing.
        mapMarkers = new ArrayList<Marker>();
        mapPolylines = new ArrayList<Polyline>();

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        destino = FakeDataBase.createDestinationObject(destinationDisplayName);
        Log.i("OnRouteMapActivity","onCreate - end");

    }

    public void refreshUIOnRouteStarted(boolean trackingRoute)
    {
        ImageButton trackRouteButton = (ImageButton) findViewById(R.id.trackRouteButton);
        int buttonText = Color.parseColor((trackingRoute)? "#CCec903a":"#00000000");
        trackRouteButton.setBackgroundColor(buttonText);

        //Button bienButton = (Button) findViewById(R.id.bienButton);
        //bienButton.setText(String.format(destino.getPositiveHospotNumber()));
        //Button malButton = (Button) findViewById(R.id.malButton);
        //bienButton.setText(Integer.toString(destino.getNegativeHospotNumber()));
    }

    @Override
    protected void onStart()
    {
        Log.i("OnRouteMapActivity","onStart - in");
        super.onStart();
        mGoogleApiClient.connect();


        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(this, "BLE is supported", Toast.LENGTH_SHORT).show();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "error: bluetooth not supported", Toast.LENGTH_SHORT).show();

            // mBluetoothAdapter.isEnabled() mejor hacerlo cada vez que se aprete porque el loco
            // puede activarlo en medio del uso de la app
        }
    }

    @Override
    protected void onStop()
    {
        Log.i("OnRouteMapActivity","onStop - in");
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        beansListView = (ListView) findViewById(R.id.beansListView);
        ArrayList<String> beanList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1,beanList);
        beansListView.setAdapter(adapter);
        beansListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String  itemValue    = (String) beansListView.getItemAtPosition(position);
                Log.i("MainActivity","onItemClick, pos:" + Integer.toString(position) +", item:" + itemValue);

                cancelScann(false);

                // Assume we have a reference to the 'beans' ArrayList from above.
                final Bean beanElement = beans.get(0);

                beanElementClicked(beanElement);
            }
        });
    }

    protected void cancelScann(boolean textFlag)
    {
        BeanManager.getInstance().cancelDiscovery();
        TextView info = (TextView) findViewById(R.id.infoTextView);
        String infoText = (textFlag)? "Scanning Canceled.":"Connecting...";
        info.setText(infoText);
        FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
        newmail.setVisibility(View.GONE);
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.i("OnRouteMapActivity", "onConnected - in");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*                                                        */
    /*    onMapReady, here go all map configurations          */
    /*                                                        */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Initialize custom ArrayList's like this it's strictly necessary, learned empiricaly,
        // dont do it and it dont work.

        Log.i("MainActivity:onMapReady", "in");
        //Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        isLocationAvailable();

        //Verify if we're reloading the map, in order not to redefine and reconfigure old markers
        if (mapPolylines.size() != 0)
        {
            if (mapCameraPosition != null)
            {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mapCameraPosition));
            }
            else
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(correctBounds(destino.latLng, fablabSCL),100));
            }
            return;
        }



        //Setup initial focus view of the map and route markers
        //     Importante notar que en esta etapa del trabajo no estan incluidas ni se implementarán
        // funciones de detectar la ruta hacia un lugar específico (GMDirectionsApi) si no que se
        // asumiran tres destinos predefinidos y con ellos tres rutas predefinidas a cada uno. Pero
        // para esto debe haber también un punto de partida predefinido que por ahora será el fablab
        // (para la feria se puede definir la estación mapopocho y crear nuevas rutas predefinidas).
        //     Por eso puede que to-do esto no se justifique enteramente en esta etapa pero si es un
        // proceso que debe realizarce de todas maneras y así se hace, aunque en el fondo no estemos
        // usando nuestra localización.
        mCurrentLocation = new Location("");
        mCurrentLocation.setLatitude(-33.449796);
        mCurrentLocation.setLongitude(-70.6277000);

        fablabSCL = new LatLng(-33.449796, -70.6277000);

        if(mCurrentLocation == null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.458704, -70.643623), 10));
            return;
        }

        //Configure Map Options
        mMap.setMyLocationEnabled(true); //commented because of the explained bellow
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
        {
            @Override
            public boolean onMyLocationButtonClick()
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(correctBounds(destino.latLng, fablabSCL),100));
                return true;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setInfoWindowAdapter(new HotspotInfoAdapter(this, getLayoutInflater()));

        //Load route from storage
        // TODO: to robust the code against failiures in xml parsing.

        List<LatLng> route1 = null;
        List<LatLng> route2 = null;
        try
        {
            //route1 = loadRoute("fablab_" + destinationName + "_2.gpx");
            //route2 = loadRoute("fablab_" + destinationName + "_1.gpx");
            route1 = destino.getRoute(this,1);
            route2 = destino.getRoute(this,2);
        }
        catch(Exception e)
        {
            Log.i("OnRouteMapActivity","onMapReady: in destination names. Verify hashmap values match the gpx file names");
        }

        // GUI elemens for the map
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(correctBounds(destino.latLng, fablabSCL),100));
        Marker origenMarker = mMap.addMarker(new MarkerOptions()
                .position(fablabSCL)
                .title("Fablab Santiago")
                .snippet("origen_______")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_inicio)));
        Marker destinoMarker = mMap.addMarker(new MarkerOptions()
                .position(destino.latLng)
                .title(destino.name)
                .snippet("destino______")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stop)));
        mapMarkers.add(origenMarker);
        mapMarkers.add(destinoMarker);
        if(route1 != null && route2 != null)
        {
            Polyline ruta1Polyline = mMap.addPolyline(new PolylineOptions().width((float) 15.0).color(Color.parseColor("#80ec903a")).addAll(route1));
            Polyline ruta2Polyline = mMap.addPolyline(new PolylineOptions().width((float) 5.0).color(Color.GRAY).addAll(route2));
            mapPolylines.add(ruta1Polyline);
            mapPolylines.add(ruta2Polyline);
        }
        else
        {
            Toast.makeText(this,"Error loading route. Please contact: Mati",Toast.LENGTH_SHORT).show();
        }

        for(int i=0; i<destino.getPositiveHospotNumber(); i++)
        {
            Marker posMarker = mMap.addMarker(new MarkerOptions()
                    .position(destino.posHotspots.get(i))
                    .title(destino.posHotspotsName.get(i))
                    .snippet(destino.posHotspotsSnip.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_positive)));
            Log.i("OnRouteMapActivity","loading markers: snip: " + destino.posHotspotsSnip.get(i));
            mapMarkers.add(posMarker);
        }
        for(int i=0; i<destino.getNegativeHospotNumber(); i++)
        {
            Marker negMarker = mMap.addMarker(new MarkerOptions()
                    .position(destino.negHotspots.get(i))
                    .title(destino.negHotspotsName.get(i))
                    .snippet(destino.negHotspotsSnip.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_negative)));
            Log.i("OnRouteMapActivity","loading markers: snip: " + destino.negHotspotsSnip.get(i));
            mapMarkers.add(negMarker);
        }

        getAndFilterMarkers();
        refreshRouteCirculasProgressStats();

        //Setup de las acciones en el mapa. Clicks sobre marcadores y polylines además del mismo mapa.
        // Tener ojo y pensar bien despues en los flujos de variables y estados, así como el flujo
        // de vida de las actividades y sus procesos correspondientes.
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {
                Log.i("OnRouteMapActivity","onPolylineClick - in");
                Integer elementIndex = mapPolylines.indexOf(polyline);

                polyline.setColor(Color.parseColor("#80ec903a"));
                polyline.setWidth((float) 15.0);
                for (int i = 0; i < mapPolylines.size(); i++)
                {
                    if (i != elementIndex)
                    {
                        Polyline poly = mapPolylines.get(i);
                        poly.setColor(Color.GRAY);
                        poly.setWidth((float) 5.0);
                    }
                }
            }
        });
    }

    protected boolean isLocationAvailable()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("OnRouteMapAcivity","isLocationAvaiable: no location permissions");
            return false;
        }
        //Check if location enabled       and notify
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Toast.makeText(this,"Only network location is available. It may not be precise.",Toast.LENGTH_SHORT).show();
                return true;
            }

            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation == null)
            {
                Toast.makeText(this,"Couldn't retrieve location. Try again later.",Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        else
        {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mCurrentLocation == null)
            {
                Toast.makeText(this,"Enable location settings, please :)",Toast.LENGTH_SHORT).show();
                // TODO: desplegar menú para activar localización.
                return false;
            }
            else
            {
                Toast.makeText(this,"Location is being retrieved neither from gps nor from network.",Toast.LENGTH_SHORT).show();
                Log.i("OnRouteMapActivity","onMapReady: Location is being retrieved neither from gps nor from network.");
                return true;
            }
        }
    }

    protected LatLngBounds correctBounds(LatLng marker1, LatLng marker2)
    {
        //Works for south east hemisphere (With the ecuator and first meridian as reference).
        double m1Lat = marker1.latitude;
        double m1Lon = marker1.longitude;
        double m2Lat = marker2.latitude;
        double m2Lon = marker2.longitude;
        double swLat = m2Lat;
        double swLon = m2Lon;
        double neLat = m1Lat;
        double neLon = m1Lon;

        if (m1Lat - m2Lat < 0)
        {
            swLat = m1Lat;
            neLat = m2Lat;
        }
        if (m1Lon - m2Lon < 0)
        {
            swLon = m1Lon;
            neLon = m2Lon;
        }

        return new LatLngBounds(new LatLng(swLat,swLon), new LatLng(neLat,neLon));
    }

    protected List<LatLng> loadRoute(String routeName)
    {
        List<LatLng> list = new ArrayList<LatLng>();
        try
        {
            InputStream inStream = this.getAssets().open(routeName);
            // TODO: Idioma
            // Asset: Dicese del sustantivo en íngles que en el español se refiere a un
            //       valor, activo, acciones, recurso, ventaja, herramienta.
            list = parseRoute(inStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    public void fetchRoute()
    {
        Log.i("OnRouteMapActivity","fetchGpx - in");

        final List<LatLng> list = new ArrayList<LatLng>();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection conn = null;
                try
                {
                    URL url = new URL("https://raw.githubusercontent.com/stereo92/leBikee/master/RecursosExternos/route1.2");
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    Log.i("OnRouteMapActivity","fetchGpx: http input stream, connect");

                    InputStream stream = conn.getInputStream();

                    List<LatLng> list = new ArrayList<LatLng>();
                    list = parseRoute(stream);
                    Log.i("OnRouteMapActivity","fetchGpx: route retrieved");
                    //mMap.addPolyline(new PolylineOptions().width((float) 5.0).color(Color.LTGRAY).addAll(list));
                    mMap.addPolyline(new PolylineOptions().add(new LatLng(-33.449796, -70.6277000), new LatLng(-33.432336,-70.653274)));
                    Log.i("OnRouteMapActivity","fetchGpx: polyline drawn");

                    stream.close();
                }
                catch (IOException e)
                {
                    Log.i("OnRouteMapActivity","fetchRoute: Error requesting gpx to server.");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    protected List<LatLng> parseRoute(InputStream inputStream)
    {
        List<LatLng> routePoints = new ArrayList<LatLng>();
        try
        {
            Log.i("OnRouteMapActivity","onMapReady: parse: Begining xml parsing test");

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser gpxParser = xmlFactoryObject.newPullParser();

            gpxParser.setInput(inputStream, null);

            /*Log.i("OnRouteMapActivity","onMapReady: parse: load complete:" + Integer.toString(inStream.read()) + " " +
                    Integer.toString(inStream.read()) + " " +
                    Integer.toString(inStream.read()) + " " +
                    Integer.toString(inStream.read()) + " " +
                    Integer.toString(inStream.read()) + "(ASCII values)");*/

            int eventType = gpxParser.getEventType();
            String tag;
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // TODO: gpx parse can be optimized by documents structure
                // Se puede optimizar el parse del gpx pensando en como esta estructurado el gpx.
                // Por ejemplo una vez que ya estamos en trkpt se asume que el proximo será asi.
                if(eventType == XmlPullParser.START_TAG)
                {
                    tag = gpxParser.getName();
                    //Log.i("OnRouteMapActivity","onMapReady: parse: Start tag " + tag);
                    if (tag.equals("trkpt"))
                    {
                        String lat = gpxParser.getAttributeValue(null,"lat");
                        String lon = gpxParser.getAttributeValue(null,"lon");
                        //Log.i("OnRouteMapActivity","onMapReady: parse: " + lat + ", " + lon);
                        routePoints.add(new LatLng(Float.valueOf(lat), Float.valueOf(lon)));
                    }
                }
                eventType = gpxParser.next();
            }
            Log.i("OnRouteMapActivity","onMapReady: parse: End document");
        }
        catch (XmlPullParserException e)
        {
            Log.i("OnRouteMapActivity","onMapReady: error en xml pull parser");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.i("OnRouteMapActivity","onMapReady: error getting route1.xml");
            e.printStackTrace();
        }

        return routePoints;
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i("OnRouteMapActivity","onConnectionSuspended - in");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i("OnRouteMapActivity","onConnectionFailed - in");
    }

    public void showRecordedRoute()
    {
        SharedPreferences serviceRetrievedLocations = getSharedPreferences("SERVICE_RETRIEVED_LOCATIONS_SHARED_PREFERENCES",MODE_PRIVATE);
        Set<String> rutaGrabada = serviceRetrievedLocations.getStringSet("RUTA_SERVICE", null);
        for (String loc : rutaGrabada)
        {
            Log.i("OnRouteMapActivity","bienOnClick: ruta grabada: " + loc);
        }

        Toast.makeText(this,rutaGrabada.toString(),Toast.LENGTH_SHORT).show();
    }

    private void refreshRouteCirculasProgressStats()
    {
        int pos3 = destino.getPositiveHospotNumber();
        int neg3 = destino.getNegativeHospotNumber();

        int point;
        int posRuta = 0;
        int negRuta = 0;

        TextView ruta1 = (TextView) findViewById(R.id.ruta1);
        String ruta = (ruta1.getTextSize()/2 > 17)?"ruta1":"ruta2";

        for(int i = 0; i<pos3; i++)
        {
            if(destino.posHotspotsSnip.get(i).substring(3,8).equals(ruta))
            {
                posRuta++;
            }
        }
        for(int i = 0; i<neg3; i++)
        {
            if(destino.negHotspotsSnip.get(i).substring(3,8).equals(ruta))
            {
                negRuta++;
            }
        }

        int total = posRuta + negRuta;
        point = Math.round(10 + 2 * posRuta - negRuta);
        posRuta = Math.round(100 * posRuta/total);
        negRuta = Math.round(100 * negRuta/total);

        ProgressBar posProgressBar = (ProgressBar) findViewById(R.id.posProgressBar);
        ProgressBar negProgressBar = (ProgressBar) findViewById(R.id.negProgressBar);
        TextView posPercent = (TextView) findViewById(R.id.posProgressBarPercent);
        TextView negPercent = (TextView) findViewById(R.id.negProgressBarPercent);
        TextView points = (TextView) findViewById(R.id.routePointsTextView);

        posProgressBar.setProgress(posRuta);
        negProgressBar.setProgress(negRuta);
        posPercent.setText(Integer.toString(posRuta) + "%");
        negPercent.setText(Integer.toString(negRuta) + "%");
        points.setText(Integer.toString(point) + " pts");
    }

    /*                           /
     *     ON_CLICK's            /
     *                          */

    /* OnClick - Comenzar recorrido */
    public void readLocationInBackground(View view)
    {
        // TODO: confirm correct service load from the service.
        // Actual implementation is blind to whats happening in the service.
        Intent locationService = new Intent(this, OnRouteLocationService.class);
        if (!bTrackingRoute)
        {
            if (isLocationAvailable())
            {
                Log.i("OnRouteMapActivity","onClick - comenzando servicio");
                startService(locationService);
                bTrackingRoute = true;
                // TODO: Check if location is available through the background service.
                // If location its shutted down while recording, push a notification warning.
            }
            else
            {
                Log.i("OnRouteMapActivity","onClick: ubicación no disponible");
            }
        }
        else
        {
            Log.i("OnRouteMapActivity","onClick - cerrando servicio");
            stopService(locationService);
            bTrackingRoute = false;
            showRecordedRoute();
        }
        SharedPreferences.Editor editor = leBikePrefs.edit();
        editor.putBoolean("BOOL_TRACKING_ROOT",bTrackingRoute);
        editor.commit();

        // TODO: El estado del botón bien debe ser actualizado con memoria.
        // Guardar su estado y establecer su correcto estado al inicar de nuevo la aplicación.
        //ProgressBar bien = (ProgressBar) findViewById(R.id.goodProgressBar);
        //bien.setEnabled(!bTrackingRoute);
        refreshUIOnRouteStarted(bTrackingRoute);
    }

    public void routeSelected(View view)
    {
        // VIEW
        //
        String id = getResources().getResourceName(view.getId());
        Log.i("OnRouteMapACtivity", "id:" + id);
        id = id.substring(id.length()-5,id.length());

        TextView ruta = (TextView) view;
        TextView ruta_;
        Integer elementIndex;

        if (id.equals("ruta1"))
        {
            ruta_ = (TextView) findViewById(R.id.ruta2);
            elementIndex = 0;
        }
        else
        {
            ruta_ = (TextView) findViewById(R.id.ruta1);
            elementIndex = 1;
        }

        ruta.setTextSize((float) 20.0);
        ruta_.setTextSize((float) 14.0);

        Log.i("OnRouteMapActivity","elementIndex:" + Integer.toString(elementIndex));

        mapPolylines.get(elementIndex).setColor(Color.parseColor("#80ec903a"));
        mapPolylines.get(elementIndex).setWidth((float) 15.0);
        for (int i = 0; i < mapPolylines.size(); i++)
        {
            if (i != elementIndex)
            {
                mapPolylines.get(i).setColor(Color.GRAY);
                mapPolylines.get(i).setWidth((float) 5.0);
            }
        }
        getAndFilterMarkers();
        refreshRouteCirculasProgressStats();
    }

    public void onPosPercentageSelected(View view)
    {
        if(view.getAlpha() == 1)
        {
            view.setAlpha((float) 0.3);
        }
        else
        {
            view.setAlpha(1);
        }
        getAndFilterMarkers();
    }

    public void onNegPercentageSelected(View view)
    {
        if(view.getAlpha() == 1)
        {
            view.setAlpha((float) 0.3);
        }
        else
        {
            view.setAlpha(1);
        }
        getAndFilterMarkers();
    }

    public void bringAlertasDrawer(View view)
    {
        ScrollView alertasDrawer = (ScrollView) findViewById(R.id.alertasDrawerMula);
        View nonAlertasDrawer = (View) findViewById(R.id.non_alertas_drawer_space);
        Button bringAlertasDrawerButton = (Button) findViewById(R.id.bring_alertas_drawer_button);
        alertasDrawer.setVisibility(View.VISIBLE);
        nonAlertasDrawer.setVisibility(View.VISIBLE);
        bringAlertasDrawerButton.setVisibility(View.GONE);
    }

    public void hideAlertasDrawer(View view)
    {
        ScrollView alertasDrawer = (ScrollView) findViewById(R.id.alertasDrawerMula);
        View nonAlertasDrawer = findViewById(R.id.non_alertas_drawer_space);
        Button bringAlertasDrawerButton = (Button) findViewById(R.id.bring_alertas_drawer_button);
        alertasDrawer.setVisibility(View.GONE);
        nonAlertasDrawer.setVisibility(View.GONE);
        bringAlertasDrawerButton.setVisibility(View.VISIBLE);

        getAndFilterMarkers();
    }

    public void clickedTrofeo(View view)
    {
        FrameLayout trofeo = (FrameLayout) findViewById(R.id.trofeo_framelayout);
        trofeo.setElevation(30);
        final Timer timer = new Timer();
        final TimerTask timerTask = new TimerTask()
        {
            public void run()
            {
                timer.cancel();
                FrameLayout trofeo = (FrameLayout) findViewById(R.id.trofeo_framelayout);
                trofeo.setElevation(2);
            }
        };
        timer.schedule(timerTask, 2000);


    }

    public void exitDeviceConnect(View view)
    {
        FrameLayout onRouteMapActivity = (FrameLayout) findViewById(R.id.on_route_map_activity_layout);
        LinearLayout beanConnectActivity = (LinearLayout) findViewById(R.id.bean_connect_layout);
        exitBeanConnectSubactivity(onRouteMapActivity,beanConnectActivity);
    }

    // customOnClick - beanListView element
    protected void beanElementClicked(final Bean bean)
    {
        BeanListener beanListener = new BeanListener()
        {
            @Override
            public void onConnected()
            {
                connectedBean = bean;

                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText(bean.getDevice().getName() + " Connected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.VISIBLE);

                Log.i("MainActivity","connected to '" + bean.getDevice().getName());
                bean.readTemperature(new Callback<Integer>()
                {
                    @Override
                    public void onResult(Integer temp)
                    {
                        Toast.makeText(context, bean.getDevice().getName() + " temp = " + Integer.toString(temp) + "°C", Toast.LENGTH_SHORT).show();
                        Log.i("MainActivity","onConnected," + bean.getDevice().getName() + "temp = " + Integer.toString(temp) + "°C");
                    }
                });

            }

            @Override
            public void onConnectionFailed()
            {
                Log.i("MainActivity","BeanListener.onConnectionFailed");
            }

            @Override
            public void onDisconnected()
            {
                Log.i("MainActivity","BeanListener.onDisconnected");
                connectedBean = null;
                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText("Bean Disconnected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.GONE);
            }

            @Override
            public void onSerialMessageReceived(byte[] data)
            {
                Log.i("MainActivity","BeanListener.onSerialMessageReceived");
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
                }
                else
                {
                    //Pensar que hacer con lo CR LF - \r\n
                }
            }

            @Override
            public void onScratchValueChanged(ScratchBank bank, byte[] value)
            {
                Log.i("MainActivity","BeanListener.onScratchValueChanged");
            }

            @Override
            public void onError(BeanError error)
            {
                Log.i("MainActivity","BeanListener.onError");
            }

            @Override
            public void onReadRemoteRssi(int rssi)
            {
                Log.i("MainActivity","BeanListener.onReadRemoteRssi");
            }
        };

        // Assuming you are in an Activity, use 'this' for the context
        bean.connect(context, beanListener);
    }

    // onCLick - info pressed
    public void infoPressed(View view)
    {
        if (mBluetoothAdapter.isEnabled())
        {
            adapter.clear();
            adapter.notifyDataSetChanged();
            BeanDiscoveryListener listener = new BeanDiscoveryListener()
            {
                @Override
                public void onBeanDiscovered(Bean bean, int rssi)
                {
                    adapter.add("name: " + bean.getDevice().getName() + "    adrr: " + bean.getDevice().getAddress());
                    //adapter.add("rssi:" + Integer.toString(rssi) + " name:" + bean.getDevice().getName() + " (" + bean.getDevice().getAddress() + ")");
                    adapter.notifyDataSetChanged();
                    beans.add(bean);
                }

                @Override
                public void onDiscoveryComplete()
                {
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
        }
        else //Bluetooth disabled
        {
            disabledBluetoothAction();
        }
    }

    public void disabledBluetoothAction()
    {
        Toast.makeText(this,"Enable bluetooth communication, please :)",Toast.LENGTH_SHORT).show();
        //show notification for accesing bluetooth settings
    }

    public void newMail(View view) //cancelButton
    {
        if(connectedBean != null)
        {
            if(connectedBean.isConnected())
            {
                connectedBean.disconnect();
                connectedBean = null;
                TextView info = (TextView) findViewById(R.id.infoTextView);
                info.setText("Bean Disconnected.");
                FloatingActionButton newmail = (FloatingActionButton) findViewById(R.id.newmail);
                newmail.setVisibility(View.GONE);
            }
            else
            {
                Log.e("MainActivity","newMail, ERROR: connectedBean != null && connectedBean disconnected, no valid states");
            }
        }
        else
        {
            cancelScann(true);
        }
    }

     /*                                        /
     *       DYNAMIC MARKER BEHEAVOR           /
     *                                        */

    public void getAndFilterMarkers()
    {
        FrameLayout neg_percentage = (FrameLayout) findViewById(R.id.neg_percentage);
        FrameLayout pos_percentage = (FrameLayout) findViewById(R.id.pos_percentage);
        TextView ruta1 = (TextView) findViewById(R.id.ruta1);
        TextView ruta2 = (TextView) findViewById(R.id.ruta2);
        CheckBox chkboxCiclo = (CheckBox) findViewById(R.id.checkbox_ciclovia);
        CheckBox chkboxVias  = (CheckBox) findViewById(R.id.checkbox_vias);
        CheckBox chkboxVeget = (CheckBox) findViewById(R.id.checkbox_vegetacion);
        CheckBox chkboxMante = (CheckBox) findViewById(R.id.checkbox_mantencion);
        CheckBox chkboxAutos = (CheckBox) findViewById(R.id.checkbox_autos);
        CheckBox chkboxPeato = (CheckBox) findViewById(R.id.checkbox_peatones);
        CheckBox chkboxOtros = (CheckBox) findViewById(R.id.checkbox_otros);

        Log.i("OnRouteMapActivity","getMarkerFilterStates: ruta1 size:" + ruta1.getTextSize());
        Log.i("OnRouteMapActivity","getMarkerFilterStates: ruta2 size:" + ruta2.getTextSize());

        filterMarkers(
                pos_percentage.getAlpha() == (float)1,
                neg_percentage.getAlpha() == (float)1,
                ruta1.getTextSize()/2 > 17,
                ruta2.getTextSize()/2 > 17,
                chkboxCiclo.isChecked(),
                chkboxVias.isChecked(),
                chkboxVeget.isChecked(),
                chkboxMante.isChecked(),
                chkboxAutos.isChecked(),
                chkboxPeato.isChecked(),
                chkboxOtros.isChecked());
    }

    public void filterMarkers(boolean fPos, boolean fNeg,
                              boolean fRuta1, boolean fRuta2,
                              boolean fCiclo, boolean fVias, boolean fVegeta, boolean fManten,
                              boolean fAuto, boolean fPeatones, boolean fOtros)
    {
        Marker marker;
        String snip;
        String posneg;
        String type;
        String ruta;
        boolean changed = false;
        for (int i=2; i<mapMarkers.size(); i++)
        {
            changed = false;
            marker = mapMarkers.get(i);
            snip = marker.getSnippet();
            Log.i("OnRouteMapActivity","filterMarkers: snippet: " + snip);
            // snipet = "xxx(->posneg)xxxxx(->rutaX)xxxxx(->type)"
            posneg = snip.substring(0,3);
            ruta = snip.substring(3,8);
            type = snip.substring(8);
            Log.i("OnRouteMapActivity","filterMarkers: posneg: " + posneg);
            Log.i("OnRouteMapActivity","filterMarkers: ruta: " + ruta);
            Log.i("OnRouteMapActivity","filterMarkers: type: " + type);
            //
            //RUTA
            //
            if (ruta.equals("ruta1")) {
                if (fRuta1) {
                    //poner
                    marker.setAlpha(1);
                }
                else {
                    //sacar
                    marker.setAlpha(0);
                    changed = true;
                }
            }
            else if(ruta.equals("ruta2")) {
                if (fRuta2) {
                    //poner
                    marker.setAlpha(1);
                }
                else {
                    //sacar
                    marker.setAlpha(0);
                    changed = true;
                }
            }

            //
            //POSNEG
            //
            if (!changed)
            {
                if (posneg.equals("pos"))
                {
                    if (fPos)
                    {
                        //poner
                        marker.setAlpha(1);
                    } else
                    {
                        //sacar
                        marker.setAlpha(0);
                        changed = true;
                    }
                } else if (posneg.equals("neg"))
                {
                    if (fNeg)
                    {
                        //poner
                        marker.setAlpha(1);
                    } else
                    {
                        //sacar
                        marker.setAlpha(0);
                        changed = true;
                    }
                }
            }

            //
            //TYPE
            //
            if(!changed)
            {
                if (type.equals("Ciclo"))
                {
                    if (fCiclo)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                } else if (type.equals("Vias_"))
                {
                    if (fVias)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                } else if (type.equals("Veget"))
                {
                    if (fVegeta)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                } else if (type.equals("Mante"))
                {
                    if (fManten)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                } else if (type.equals("Autos"))
                {
                    if (fAuto)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                } else if (type.equals("Peato"))
                {
                    if (fPeatones)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                } else if (type.equals("Otros"))
                {
                    if (fOtros)
                    {
                        marker.setAlpha(1);
                    } else
                    {
                        marker.setAlpha(0);
                    }
                }
            }
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

    public void exitBeanConnectSubactivity(View v1, View v2)
    {
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        //Cancel bean search
        if (connectedBean != null)
        {
            if (!connectedBean.isConnected())
            {
                cancelScann(true);
            }
        }
        else
        {
            cancelScann(true);
        }
    }
}
