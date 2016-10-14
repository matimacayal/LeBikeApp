package org.fablabsantiago.smartcities.app.appmobile;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Alerta {

    /* Fields
     *
     */
    private int     id;
    private boolean posneg;        // positivo o negativo (1 | 0)
    private Float   lat;           // posición
    private Float   lng;           // ""
    private String  tipoAlerta;    // cicl(ovía), vias, vege(tación), mant(ención), auto(s), peat(ones) y otro(s)
    private String  hora;
    private String  fecha;
    private String  titulo;
    private String  description;
    private int     idRuta;
    private int     version;       // cada vez que se realiza un cambio se suma +1 al campo
    private String  estado;        // completa o pendiente

    /* Constructors
     *
     */
    public Alerta(Cursor cursor) {
        id = cursor.getInt(0);
        posneg = (cursor.getInt(1) > 0);
        lat = (float) cursor.getDouble(2);
        lng = (float) cursor.getDouble(3);
        tipoAlerta = cursor.getString(4);
        hora = cursor.getString(5);
        fecha = cursor.getString(6);
        titulo = cursor.getString(7);
        description = cursor.getString(8);
        idRuta = cursor.getInt(9);
        version = cursor.getInt(10);
        estado = cursor.getString(11);
    }
    public Alerta(int _id, boolean pn, float _lat, float _lng, String _tA, String _hora, String _fecha,
                  String _titulo, String _desc, int _idRuta, int _version, String _estado) {
        id = _id;
        posneg = pn;
        lat = _lat;
        lng = _lng;
        tipoAlerta = _tA;
        hora = _hora;
        fecha = _fecha;
        titulo = _titulo;
        description = _desc;
        idRuta = _idRuta;
        version = _version;
        estado = _estado;
    }
    public Alerta(Bundle bundle) {
        id = bundle.getInt(ID_KEY);
        posneg = bundle.getBoolean(POSNEG_KEY);
        lat = bundle.getFloat(LAT_KEY);
        lng = bundle.getFloat(LON_KEY);
        tipoAlerta = bundle.getString(TA_KEY);
        hora = bundle.getString(HORA_KEY);
        fecha = bundle.getString(FECHA_KEY);
        titulo = bundle.getString(TITULO_KEY);
        description = bundle.getString(DESC_KEY);
        idRuta = bundle.getInt(ID_RUTA_KEY);
        version = bundle.getInt(VER_KEY);
        estado = bundle.getString(EST_KEY);
    }

    /* Method
     *
     */
    public int getId() {
        return id;
    }
    public boolean getPosNeg() {
        return posneg;
    }
    public Float getLat() {
        return lat;
    }
    public Float getLng() {
        return lng;
    }
    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
    public String getTipoAlerta() {
        return tipoAlerta;
    }
    public String getHora() {
        return hora;
    }
    public String getFecha() {
        return fecha;
    }
    public String getTitulo() {
        return titulo;
    }
    public String getDescrption() {
        return description;
    }
    public int getIdRuta() {
        return idRuta;
    }
    public int getVersion() {
        return version;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado_) {
        estado = estado_;
    }

    public boolean isComplete() {
        boolean com;
        com = (id > 0) &
                ((posneg == true) | (posneg == false)) &
                (lat != null) &
                (lng != null) &
                (tipoAlerta != null) &
                (hora != null) &
                (fecha != null) &
                (titulo != null) &
                (description != null) &
                (version > 0);
        return com;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt(ID_KEY, id);
        bundle.putBoolean(POSNEG_KEY, posneg);
        bundle.putFloat(LAT_KEY, lat);
        bundle.putFloat(LON_KEY, lng);
        bundle.putString(TA_KEY, tipoAlerta);
        bundle.putString(HORA_KEY, hora);
        bundle.putString(FECHA_KEY, fecha);
        bundle.putString(TITULO_KEY, titulo);
        bundle.putString(DESC_KEY, description);
        bundle.putInt(ID_RUTA_KEY, idRuta);
        bundle.putInt(VER_KEY, version);
        bundle.putString(EST_KEY, estado);

        return bundle;
    }




    /* Constants
     *
     */
    String ID_KEY = "alerta_id";
    String POSNEG_KEY = "alerta_posneg";
    String LAT_KEY = "alerta_lat";
    String LON_KEY = "alerta_lon";
    String TA_KEY = "alerta_tipoAlerta";
    String HORA_KEY = "alerta_hora";
    String FECHA_KEY = "alerta_fecha";
    String TITULO_KEY = "alerta_titulo";
    String DESC_KEY = "alerta_desc";
    String ID_RUTA_KEY = "alerta_idRuta";
    String VER_KEY = "alerta_version";
    String EST_KEY = "alerta_estado";
}
