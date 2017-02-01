package org.fablabsantiago.smartcities.app.appmobile.Clases;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Alerta implements Parcelable
{
    /* Constants
     *
     */
    public static final String ID_KEY = "alerta_id";
    public static final String POSNEG_KEY = "alerta_posneg";
    public static final String LAT_KEY = "alerta_lat";
    public static final String LON_KEY = "alerta_lon";
    public static final String TA_KEY = "alerta_tipoAlerta";
    public static final String HORA_KEY = "alerta_hora";
    public static final String FECHA_KEY = "alerta_fecha";
    public static final String TITULO_KEY = "alerta_titulo";
    public static final String DESC_KEY = "alerta_desc";
    public static final String ID_RUTA_KEY = "alerta_idRuta";
    public static final String VER_KEY = "alerta_version";
    public static final String EST_KEY = "alerta_estado";
    public static final String USERID_KEY = "alerta_userid";
    public static final String UPLOADED_KEY = "alerta_uploaded";

    public static final String COMPLETA = "completa";
    public static final String PENDIENTE = "pendiente";


    /* Fields
     *
     */
    private Integer id;
    private Boolean posneg;        // positivo o negativo (1 | 0)
    private double  lat;          // posición
    private double  lng;          // ""
    private String  tipoAlerta;    // cicl(ovía), vias, vege(tación), mant(ención), auto(s), peat(ones) y otro(s)
    private String  hora;
    private String  fecha;
    private String  titulo;
    private String  description;
    private int     idRuta;
    private int     version;       // cada vez que se realiza un cambio se suma +1 al campo
    private String  estado;        // completa o pendiente
    private String  userId;
    private Boolean uploaded;

    /* Constructors
     *
     */
    public Alerta(Cursor cursor) {
        id          = cursor.getInt(0);
        posneg      = (cursor.getInt(1) > 0);
        lat         = cursor.getDouble(2);
        lng         = cursor.getDouble(3);
        tipoAlerta  = cursor.getString(4);
        hora        = cursor.getString(5);
        fecha       = cursor.getString(6);
        titulo      = cursor.getString(7);
        description = cursor.getString(8);
        idRuta      = cursor.getInt(9);
        version     = cursor.getInt(10);
        estado      = cursor.getString(11);
        userId      = cursor.getString(12);
        uploaded    = (cursor.getInt(13) > 0);
    }

    public Alerta(int _id, boolean _posneg, double _lat, double _lng, String _tA, String _hora, String _fecha,
                  String _titulo, String _desc, int _idRuta, int _version, String _estado, String _userId, Boolean _uploaded) {
        id          = _id;
        posneg      = _posneg;
        lat         = _lat;
        lng         = _lng;
        tipoAlerta  = _tA;
        hora        = _hora;
        fecha       = _fecha;
        titulo      = _titulo;
        description = _desc;
        idRuta      = _idRuta;
        version     = _version;
        estado      = _estado;
        userId      = _userId;
        uploaded    = _uploaded;
    }
    public Alerta(Bundle bundle) {
        id          = bundle.getInt(ID_KEY);
        posneg      = bundle.getBoolean(POSNEG_KEY);
        lat         = bundle.getDouble(LAT_KEY);
        lng         = bundle.getDouble(LON_KEY);
        tipoAlerta  = bundle.getString(TA_KEY);
        hora        = bundle.getString(HORA_KEY);
        fecha       = bundle.getString(FECHA_KEY);
        titulo      = bundle.getString(TITULO_KEY);
        description = bundle.getString(DESC_KEY);
        idRuta      = bundle.getInt(ID_RUTA_KEY);
        version     = bundle.getInt(VER_KEY);
        estado      = bundle.getString(EST_KEY);
        userId      = bundle.getString(USERID_KEY);
        uploaded    = bundle.getBoolean(UPLOADED_KEY);
    }

    protected Alerta(Parcel in) {
        id          = in.readInt();
        posneg      = in.readByte() != 0;
        lat         = in.readDouble();
        lng         = in.readDouble();
        tipoAlerta  = in.readString();
        hora        = in.readString();
        fecha       = in.readString();
        titulo      = in.readString();
        description = in.readString();
        idRuta      = in.readInt();
        version     = in.readInt();
        estado      = in.readString();
        userId      = in.readString();
        uploaded    = in.readByte() != 0;
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
    public String getUserId() {
        return userId;
    }
    public Boolean isUploaded() {
        return uploaded;
    }

    public void setId(int idd) {
        id = idd;
    }
    public void setEstado(String estado_) {
        estado = estado_;
    }
    public void setPosneg(Boolean vote) {
        posneg = vote;
    }
    public void setTitulo(String title) {
        titulo = title;
    }
    public void setDescription(String desc) {
        description = desc;
    }
    public void setTipoAlerta(String type) {
        tipoAlerta = type;
    }
    public void increaseVersion() {
        version += 1;
    }


    public String isComplete() {
        // Hay que estar preparado para que los campos no completos puedan ser o 'null' o vacios en
        // su respectiva clase.
        if ((tipoAlerta == null) || (titulo == null))
            return PENDIENTE;

        boolean com =
                (id != null) &
                (posneg != null) &
                (lat != 0) &
                (lng != 0) &
                (!tipoAlerta.equals("")) &
                (hora != null) &
                (fecha != null) &
                (!titulo.equals("")) &
                (userId != null);
                //(version > 0);
        return ((com) ? COMPLETA : PENDIENTE);
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
        bundle.putString(USERID_KEY, userId);
        bundle.putBoolean(UPLOADED_KEY, uploaded);

        return bundle;
    }



    /********** Parceable Methods **********/
    // Alerta parceable constructor its in the constructors section
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte((byte) (posneg ? 1 : 0));
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(tipoAlerta);
        dest.writeString(hora);
        dest.writeString(fecha);
        dest.writeString(titulo);
        dest.writeString(description);
        dest.writeInt(idRuta);
        dest.writeInt(version);
        dest.writeString(estado);
        dest.writeString(userId);
        dest.writeByte((byte) (uploaded ? 1 : 0));
    }

    public static final Parcelable.Creator<Alerta> CREATOR
            = new Parcelable.Creator<Alerta>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Alerta createFromParcel(Parcel in) {
            return new Alerta(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Alerta[] newArray(int size) {
            return new Alerta[size];
        }
    };
}
