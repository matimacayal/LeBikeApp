package org.fablabsantiago.smartcities.app.appmobile.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Destino;
import org.fablabsantiago.smartcities.app.appmobile.Clases.Ruta;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME    = "LeBikeDatabase.db";
    public static final int    DATABASE_VERSION = 1;

    public static final String RUTAS_TABLE    = "tabladerutas";
    public static final String RUTA_TABLE     = "tabladeruta";
    public static final String ALERTAS_TABLE  = "tabladealertas";
    public static final String TALERTA_TABLE  = "tabladetiposdealerta";
    public static final String DESTINOS_TABLE = "tabladedestinos";

    // Campos Tabla de Destinos
    public static final String TD_NOMBRE    = "nombre";
    public static final String TD_DIRECCION = "direccion";
    public static final String TD_ID        = "id";
    public static final String TD_LATITUDE  = "latitude";
    public static final String TD_LONGITUDE = "longitude";

    // Campos Tabla de Rutas
    public static final String TRS_ID        = "id";
    public static final String TRS_DESTID    = "destid";
    public static final String TRS_NOMBRE    = "nombre";
    public static final String TRS_NUMPOS    = "numpos";
    public static final String TRS_NUMNEG    = "numneg";
    public static final String TRS_HORA      = "hora";
    public static final String TRS_FECHA     = "fecha";
    public static final String TRS_DURACION  = "duracion";
    public static final String TRS_DISTANCIA = "distancia";

    // Campos Tabla de Ruta
    public static final String TR_SEQNUM    = "seqnum";
    public static final String TR_TIEMPO    = "tiempo";
    public static final String TR_LATITUDE  = "latitude";
    public static final String TR_LONGITUDE = "longitude";

    // Campos Tabla de Alertas
    public static final String TA_POSNEG      = "posneg";
    public static final String TA_TIPOALERTA  = "tipodealerta";
    public static final String TA_HORA        = "hora";
    public static final String TA_FECHA       = "fecha";
    public static final String TA_TITULO      = "titulo";
    public static final String TA_DESCRIPCION = "descripcion";
    public static final String TA_VERSION     = "version";
    public static final String TA_IDRUTA      = "idruta";
    public static final String TA_LATITUDE    = "latitude";
    public static final String TA_LONGITUDE   = "longitude";
    public static final String TA_ESTADO      = "estado";
    public static final String TA_ID          = "id";

    // Comandos para crear tablas
    private static final String CREATE_DESTINOS_TABLE_COMMAND =
            "CREATE TABLE " +
            DESTINOS_TABLE + "(" +
                TD_NOMBRE    + " TEXT, " +
                TD_DIRECCION + " TEXT, " +
                TD_ID        + " INTEGER, " +
                TD_LATITUDE  + " DOUBLE, " +
                TD_LONGITUDE + " DOUBLE);";

    private static final String CREATE_RUTAS_TABLE_COMMAND =
            "CREATE TABLE " +
            RUTAS_TABLE + "(" +
                TRS_ID        + " INTEGER PRIMARY KEY, " +
                TRS_DESTID    + " INTEGER, " +
                TRS_NOMBRE    + " TEXT, " +
                TRS_NUMPOS    + " INTEGER, " +
                TRS_NUMNEG    + " INTEGER, " +
                TRS_HORA      + " TEXT, " +
                TRS_FECHA     + " TEXT, " +
                TRS_DURACION  + " INTEGER, " + // en segundos
                TRS_DISTANCIA + " FLOAT);";

    private static final String CREATE_RUTA_TABLE_COMMAND =
            "CREATE TABLE " +
            RUTA_TABLE + "(" +
                TR_SEQNUM    + " INTEGER, " +
                TR_TIEMPO    + " INTEGER, " + // segundos desde el comienzo
                TR_LATITUDE  + " DOUBLE, " +
                TR_LONGITUDE + " DOUBLE);";

    private static final String CREATE_ALERTAS_TABLE_COMMAND =
            "CREATE TABLE " +
            ALERTAS_TABLE + "(" +
                TA_ID          + " INTEGER, " +
                TA_POSNEG      + " INTEGER, " +
                TA_LATITUDE    + " DOUBLE, " +
                TA_LONGITUDE   + " DOUBLE," +
                TA_TIPOALERTA  + " TEXT, " +
                TA_HORA        + " TEXT, " +
                TA_FECHA       + " TEXT, " +
                TA_TITULO      + " TEXT, " +
                TA_DESCRIPCION + " TEXT, " +
                TA_IDRUTA      + " INTEGER, " +
                TA_VERSION     + " INTEGER, " +
                TA_ESTADO      + " TEXT);";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DESTINOS_TABLE_COMMAND);
        db.execSQL(CREATE_RUTAS_TABLE_COMMAND);
        db.execSQL(CREATE_RUTA_TABLE_COMMAND);
        db.execSQL(CREATE_ALERTAS_TABLE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DESTINOS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RUTAS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RUTA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ALERTAS_TABLE);

        onCreate(db);
    }

    /*----------------------------------------------------------------------------------------------------*/
    /*------------------------------------------ Escribir Datos ------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------*/

    /*------------------------------------------------------*/
    /*-------------------- Ruta(Puntos) --------------------*/
    public void addTrackPoint(int seqnum, int tiempo, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TR_SEQNUM   , seqnum);
        values.put(TR_TIEMPO   , tiempo);
        values.put(TR_LATITUDE , latitude);
        values.put(TR_LONGITUDE, longitude);

        db.insert(RUTA_TABLE, null, values);
        db.close();
    }

    public void eraseTrackPoints() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ RUTA_TABLE);
    }

    /*------------------------------------------------------*/
    /*---------------------- Destinos ----------------------*/
    public void newDestiny(Destino destino) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TD_NOMBRE   , destino.getName());
        values.put(TD_DIRECCION, destino.getDirection());
        values.put(TD_ID       , destino.getId());
        values.put(TD_LATITUDE , (double) destino.getLatitude());
        values.put(TD_LONGITUDE, (double) destino.getLongitude());

        db.insert(DESTINOS_TABLE, null, values);
        db.close();
    }

    public void updateDestino(String nombre, String direccion, int id, Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TD_NOMBRE   , nombre);
        values.put(TD_DIRECCION, direccion);
        values.put(TD_LATITUDE , lat);
        values.put(TD_LONGITUDE, lon);

        db.update(DESTINOS_TABLE, values, TD_ID + " = " + Integer.toString(id), null);
    }

    public boolean deleteDestino(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DESTINOS_TABLE, TD_ID + " = " + Integer.toString(id), null) > 0;
    }

    /*------------------------------------------------------*/
    /*------------------------ Rutas -----------------------*/
    public void startRoute(int id, int destId, String name, String hora, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRS_ID       , id);        // -
        values.put(TRS_DESTID   , destId);    // -
        values.put(TRS_NOMBRE   , name);      // - , '-' => al comienzo
        values.put(TRS_HORA     , hora);      // -
        values.put(TRS_FECHA    , fecha);     // -

        db.insert(RUTAS_TABLE, null, values);
    }

    public void endRoute(int rutaId, int numPos, int numNeg, int duracion, float distancia) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRS_NUMPOS   , numPos);    // * , '*' => al fin
        values.put(TRS_NUMNEG   , numNeg);    // *
        values.put(TRS_DURACION , duracion);  // *
        values.put(TRS_DISTANCIA, distancia); // *

        db.update(RUTAS_TABLE, values, TRS_ID + " = " + Integer.toString(rutaId), null);
    }

    public void newRuta(Ruta ruta) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(TRS_ID       , ruta.getId());        // -
        values.put(TRS_DESTID   , ruta.getDestId());    // -
        values.put(TRS_NOMBRE   , ruta.getName());      // - , '-' => al comienzo
        values.put(TRS_HORA     , ruta.getHora());      // -
        values.put(TRS_FECHA    , ruta.getFecha());     // -
        values.put(TRS_NUMPOS   , ruta.getNumPos());    // * , '*' => al fin
        values.put(TRS_NUMNEG   , ruta.getNumNeg());    // *
        values.put(TRS_DURACION , ruta.getDuration());  // *
        values.put(TRS_DISTANCIA, ruta.getDistancia()); // *

        db.insert(RUTAS_TABLE, null, values);
    }

    public boolean deleteRoute(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RUTAS_TABLE, TRS_ID + " = " + Integer.toString(id), null) > 0;
    }

    /*------------------------------------------------------*/
    /*----------------------- Alertas ----------------------*/
    public void newAlerta(Alerta alerta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TA_ID         , alerta.getId());
        values.put(TA_POSNEG     , (alerta.getPosNeg()) ? 1:0);
        values.put(TA_LATITUDE   , alerta.getLat());
        values.put(TA_LONGITUDE  , alerta.getLng());
        values.put(TA_TIPOALERTA , alerta.getTipoAlerta());
        values.put(TA_HORA       , alerta.getHora());
        values.put(TA_FECHA      , alerta.getFecha());
        values.put(TA_TITULO     , alerta.getTitulo());
        values.put(TA_DESCRIPCION, alerta.getDescrption());
        values.put(TA_IDRUTA     , alerta.getIdRuta());
        values.put(TA_VERSION    , alerta.getVersion());
        values.put(TA_ESTADO     , alerta.getEstado());

        db.insert(ALERTAS_TABLE, null, values);
    }

    public void updateAlerta(Alerta alerta) {
        int nuevaVersion = alerta.getVersion() + 1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TA_POSNEG     , (alerta.getPosNeg()) ? 1:0);
        values.put(TA_LATITUDE   , alerta.getLat());
        values.put(TA_LONGITUDE  , alerta.getLng());
        values.put(TA_TIPOALERTA , alerta.getTipoAlerta());
        values.put(TA_HORA       , alerta.getHora());
        values.put(TA_FECHA      , alerta.getFecha());
        values.put(TA_TITULO     , alerta.getTitulo());
        values.put(TA_DESCRIPCION, alerta.getDescrption());
        values.put(TA_IDRUTA     , alerta.getIdRuta());
        values.put(TA_VERSION    , nuevaVersion);
        values.put(TA_ESTADO     , alerta.getEstado());

        db.update(ALERTAS_TABLE, values, TA_ID + " = " + Integer.toString(alerta.getId()), null);
    }

    public boolean deleteAlerta(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ALERTAS_TABLE, TA_ID + " = " + Integer.toString(id), null) > 0;
    }

    /*----------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------- Leer Datos --------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------*/

    /*------------------------------------------------------*/
    /*-------------------- Ruta(Puntos) --------------------*/
    public List<Location> getRoutePoints() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TR_LATITUDE + ", " + TR_LONGITUDE + " FROM " + RUTA_TABLE;// + " ORDER BY " + TR_SEQNUM + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        List<Location> routePoints = new ArrayList<Location>();
        if (cursor.moveToFirst()) {
            do {
                Location loc = new Location("");
                loc.setLatitude(cursor.getDouble(0));
                loc.setLongitude(cursor.getDouble(1));
                routePoints.add(loc);
            } while(cursor.moveToNext());
        }
        cursor.close();

        return routePoints;
    }

    public int getLastSeqNum() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT MAX(" + TR_SEQNUM + ") FROM " + RUTA_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        int biggestSeqNum = -1;
        if(cursor.moveToFirst()) {
            biggestSeqNum = cursor.getInt(0);
        }
        cursor.close();

        return biggestSeqNum;
    }

    /*------------------------------------------------------*/
    /*---------------------- Destinos ----------------------*/
    public Destino getDestinationByName(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TD_NOMBRE + ", "
                                 + TD_DIRECCION + ", "
                                 + TD_ID + ", "
                                 + TD_LATITUDE + ", "
                                 + TD_LONGITUDE +
                       " FROM " + DESTINOS_TABLE +
                       " WHERE " + TD_NOMBRE + " = " + nombre;

        Cursor cursor = db.rawQuery(query, null);

        Destino destino = null;
        if (cursor.moveToFirst()) {
            destino = new Destino(cursor);
        }
        cursor.close();

        return destino;
    }
    public Destino getDestinationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TD_NOMBRE + ", "
                + TD_DIRECCION + ", "
                + TD_ID + ", "
                + TD_LATITUDE + ", "
                + TD_LONGITUDE +
                " FROM " + DESTINOS_TABLE +
                " WHERE " + TD_ID + " = " + Integer.toString(id);

        Cursor cursor = db.rawQuery(query, null);

        Destino destino = null;
        if (cursor.moveToFirst()) {
            destino = new Destino(cursor);
        }
        cursor.close();

        return destino;
    }
    public List<Destino> getDestinations() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TD_NOMBRE + ", "
                                 + TD_DIRECCION + ", "
                                 + TD_ID + ", "
                                 + TD_LATITUDE + ", "
                                 + TD_LONGITUDE +
                       " FROM " + DESTINOS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        List<Destino> destinos = new ArrayList<Destino>();
        if(cursor.moveToFirst()) {
            do {
                destinos.add(new Destino(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return destinos;
    }
    public List<String> getDestinationNames() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TD_NOMBRE + " FROM " + DESTINOS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        List<String> destinos = new ArrayList<String>();
        if(cursor.moveToFirst()) {
            do {
                destinos.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return destinos;
    }

    public int getLastDestinationId() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT MAX(" + TD_ID + ") FROM " + DESTINOS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        int biggestDestId = -1;
        if(cursor.moveToFirst()) {
            biggestDestId = cursor.getInt(0);
        }
        cursor.close();

        return biggestDestId;
    }

    /*------------------------------------------------------*/
    /*------------------------ Rutas -----------------------*/
    public List<Ruta> getRutas() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + RUTAS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        List<Ruta> rutas = new ArrayList<Ruta>();
        if(cursor.moveToFirst()) {
            do {
                rutas.add(new Ruta(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return rutas;
    }

    public Ruta getRouteById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + RUTAS_TABLE + " WHERE " + TRS_ID + " = " + Integer.toString(id);

        Cursor cursor = db.rawQuery(query, null);

        Ruta ruta = null;
        if(cursor.moveToFirst()) {
            ruta = new Ruta(cursor);
        }
        cursor.close();

        return ruta;
    }

    public List<Ruta> getRoutesByDestId(int destId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + RUTAS_TABLE + " WHERE " + TRS_DESTID + " = " + Integer.toString(destId);

        Cursor cursor = db.rawQuery(query, null);

        List<Ruta> rutas = new ArrayList<Ruta>();
        if(cursor.moveToFirst()) {
            do {
                rutas.add(new Ruta(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return rutas;
    }

    public int getLastRutasId() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT MAX(" + TRS_ID + ") FROM " + RUTAS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        int biggestId = -1;
        if(cursor.moveToFirst()) {
            biggestId = cursor.getInt(0);
        }
        cursor.close();

        return biggestId;
    }

    /*------------------------------------------------------*/
    /*----------------------- Alertas ----------------------*/
    public List<Alerta> getAlertas() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + ALERTAS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        List<Alerta> alertas = new ArrayList<Alerta>();
        if (cursor.moveToFirst()) { //returns 'false' if cursor is empty
            do {
                alertas.add(new Alerta(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return alertas;
    }
    public List<Alerta> getAlertasByEstado(String type) {
        SQLiteDatabase db = this.getReadableDatabase();

        //TODO: hacer variable publica en clase alerta -> Alerta.COMPLETA o Alerta.PENDIENTE;
        String tipoalerta = type;

        String query = "SELECT * FROM " + ALERTAS_TABLE + " WHERE " + TA_ESTADO + " = '" + tipoalerta + "'";

        Cursor cursor = db.rawQuery(query, null);

        List<Alerta> alertas = new ArrayList<Alerta>();
        if (cursor.moveToFirst()) { //returns 'false' if cursor is empty
            do {
                alertas.add(new Alerta(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return alertas;
    }
    public List<Alerta> getAlertasByIdRuta(int idruta) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + ALERTAS_TABLE + " WHERE " + TA_IDRUTA + " = " + idruta;

        Cursor cursor = db.rawQuery(query, null);

        List<Alerta> alertas = new ArrayList<Alerta>();
        if (cursor.moveToFirst()) { //returns 'false' if cursor is empty
            do {
                alertas.add(new Alerta(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();

        return alertas;
    }

    public int getLastAlertaId() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT MAX(" + TA_ID + ") FROM " + ALERTAS_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        int biggestId = -1;
        if(cursor.moveToFirst()) {
            biggestId = cursor.getInt(0);
        }
        cursor.close();

        return biggestId;
    }

    public void eraseAlertasTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ALERTAS_TABLE);
        //TODO: Esto está mal hecho en verdad ya que debería haber un if aquí ("si es que lo anterior funcionó")
        db.execSQL(CREATE_ALERTAS_TABLE_COMMAND);
    }
}
