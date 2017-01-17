package org.fablabsantiago.smartcities.app.appmobile.Deprecated;


import android.database.Cursor;

public class RoutePoint
{
     /* Fields
	 *
	 */
    private int seqNum;
    private String tiempo;
    private Float lat;
    private Float lng;

    /* Constructor
	 *
	 */
    public RoutePoint(Cursor cursor) {
        seqNum = cursor.getInt(0);
        tiempo = cursor.getString(1);
        lat = cursor.getFloat(2);
        lng = cursor.getFloat(3);
    }

    /* Methods
	 *
	 */
    public int getSeqNum() {
        return seqNum;
    }
    public String getTime() {
        return tiempo;
    }
    public Float getLat() {
        return lat;
    }
    public Float getLng() {
        return lng;
    }
}
