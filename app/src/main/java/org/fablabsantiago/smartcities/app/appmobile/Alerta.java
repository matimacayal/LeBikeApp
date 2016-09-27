package org.fablabsantiago.smartcities.app.appmobile;


import android.database.Cursor;
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


}
