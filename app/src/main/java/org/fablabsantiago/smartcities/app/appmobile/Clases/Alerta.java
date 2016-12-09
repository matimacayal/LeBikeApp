package org.fablabsantiago.smartcities.app.appmobile.Clases;


import android.database.Cursor;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class Alerta {

    /* Fields
     *
     */
    private int     id;
    private Boolean posneg;        // positivo o negativo (1 | 0)
    private double   lat;          // posición
    private double   lng;          // ""
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
        lat = cursor.getDouble(2);
        lng = cursor.getDouble(3);
        tipoAlerta = cursor.getString(4);
        hora = cursor.getString(5);
        fecha = cursor.getString(6);
        titulo = cursor.getString(7);
        description = cursor.getString(8);
        idRuta = cursor.getInt(9);
        version = cursor.getInt(10);
        estado = cursor.getString(11);

    }
    public Alerta(int _id, boolean pn, double _lat, double _lng, String _tA, String _hora, String _fecha,
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
        lat = bundle.getDouble(LAT_KEY);
        lng = bundle.getDouble(LON_KEY);
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
    public double getLat() {
        return lat;
    }
    public double getLng() {
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

    public String isComplete() {
        boolean com;
        com = (id > 0) &
                (posneg != null) &
                (lat > 0) &
                (lng > 0) &
                (tipoAlerta != null) &
                (hora != null) &
                (fecha != null) &
                (titulo != null) &
                (description != null) &
                (version > 0);
        return ((com) ? "completa" : "pendiente");
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt(ID_KEY, id);
        bundle.putBoolean(POSNEG_KEY, posneg);
        bundle.putDouble(LAT_KEY, lat);
        bundle.putDouble(LON_KEY, lng);
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
