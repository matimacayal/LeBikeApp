package org.fablabsantiago.smartcities.app.appmobile;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Ruta {
    /* Fields
     *
     */
    private int    id;
    private int    destid;
    private String name;         // será también el nombre del archivo gtx con los puntos
    private int    numPos;       // # de alertas negativas
    private int    numNeg;       // # de alertas positivas
    private String hora;
    private String fecha;
    private int    duration;     // en segundos
    private int    distancia;    // en metros

    /* Constructors
     *
     */
    public Ruta(int _id, int _destid, String _name, int _numpos, int _numneg,
                String _hora, String _fecha, int _duration, int _distancia) {
        id        = _id;
        destid    = _destid;
        name      = _name;
        numPos    = _numpos;
        numNeg    = _numneg;
        hora      = _hora;
        fecha     = _fecha;
        duration  = _duration;
        distancia = _distancia;
    }

    public Ruta(Cursor cursor) {
        id        = cursor.getInt(0);
        destid    = cursor.getInt(1);
        name      = cursor.getString(2);
        numPos    = cursor.getInt(3);
        numNeg    = cursor.getInt(4);
        hora      = cursor.getString(5);
        fecha     = cursor.getString(6);
        duration  = cursor.getInt(7);
        distancia = cursor.getInt(8);
    }


    /* Method
     *
     */
    public int getId() {
        return id;
    }
    public int getDestId() {
        return destid;
    }
    public String getName() {
        return name;
    }
    public int getNumPos() {
        return numPos;
    }
    public int getNumNeg() {
        return numNeg;
    }
    public String getHora() {
        return hora;
    }
    public String getFecha() {
        return fecha;
    }
    public int getDuration() {
        return duration;
    }
    public int getDistancia() {
        return distancia;
    }

    public List<LatLng> getTrack(Context context, int routeId) {
        List<LatLng> list = new ArrayList<LatLng>();
        try {
            String routeName = Integer.toString(routeId) + ".gpx";
            InputStream inStream = context.getAssets().open(routeName);
            // TODO: Idioma
            // Asset: Dicese del sustantivo en íngles que en el español se refiere a un
            //       valor, activo, acciones, recurso, ventaja, herramienta.
            list = parseRoute(inStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
    protected List<LatLng> parseRoute(InputStream inputStream) {
        List<LatLng> routePoints = new ArrayList<LatLng>();
        try {
            Log.i("Destination","parseRoute: parse: Begining xml parsing test");

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
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // TODO: gpx parse can be optimized by documents structure
                // Se puede optimizar el parse del gpx pensando en como esta estructurado el gpx.
                // Por ejemplo una vez que ya estamos en trkpt se asume que el proximo será asi.
                if(eventType == XmlPullParser.START_TAG) {
                    tag = gpxParser.getName();
                    //Log.i("OnRouteMapActivity","onMapReady: parse: Start tag " + tag);
                    if (tag.equals("trkpt")) {
                        String lat = gpxParser.getAttributeValue(null,"lat");
                        String lon = gpxParser.getAttributeValue(null,"lon");
                        //Log.i("OnRouteMapActivity","onMapReady: parse: " + lat + ", " + lon);
                        routePoints.add(new LatLng(Float.valueOf(lat), Float.valueOf(lon)));
                    }
                }
                eventType = gpxParser.next();
            }
            Log.i("Destination","parseRoute: parse: End document");
        }
        catch (XmlPullParserException e) {
            Log.i("Destination","parseRoute: error en xml pull parser");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.i("Destination","parseRoute: error getting route1.xml");
            e.printStackTrace();
        }

        return routePoints;
    }
}
