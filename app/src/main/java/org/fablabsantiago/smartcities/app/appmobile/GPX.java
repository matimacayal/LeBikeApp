package org.fablabsantiago.smartcities.app.appmobile;


import android.location.Location;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GPX
{
    private static final String TAG = GPX.class.getName();

    public static void writePath(File file, String n, List<Location> points) {
        Log.i("GPX.writePath", "- in");
        //String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        //String name = "<name>" + n + "</name><trkseg>\n";

        String header = "<?xml version=\"1.0\"?>" + "\n" +
                        "<gpx creator=\"GPS Visualizer http://www.gpsvisualizer.com/\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">" + "\n";
        String inicio = "<wpt lat=\"" + String.valueOf(points.get(0).getLatitude()) + "\" lon=\"" +
                        String.valueOf(points.get(0).getLongitude()) + "\">" + "\n" +
                        "  <name>" + String.valueOf(points.get(0).getLatitude()) + ", " +
                        String.valueOf(points.get(0).getLongitude()) + "</name>" + "\n" +
                        "  <desc> </desc>" + "\n" +
                        "</wpt>\n";
        String fin = "<wpt lat=\"" + String.valueOf(points.get(points.size()-1).getLatitude()) + "\" lon=\"" +
                     String.valueOf(points.get(points.size()-1).getLongitude()) + "\">" + "\n" +
                     "  <name>" + String.valueOf(points.get(points.size()-1).getLatitude()) + ", " +
                     String.valueOf(points.get(points.size()-1).getLongitude()) + "</name>" + "\n" +
                     "  <desc> </desc>" + "\n" +
                     "</wpt>\n";
        String trk = "<trk>\n  <name>Punto Inicio - Punto Destino</name>\n  <desc> </desc>\n" +
                     "  <trkseg>" + "\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (Location l : points) {
            //<time>" + df.format(new Date(l.getTime())) + "</time>
            segments += "    <trkpt lat=\"" + l.getLatitude() + "\" lon=\"" + l.getLongitude() + "\"></trkpt>\n";
        }

        String footer = "  </trkseg>\n</trk>\n</gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(inicio);
            writer.append(fin);
            writer.append(trk);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            Log.i("GPX","writePath(): ended, " + points.size() + " points");
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Saved " + points.size() + " points.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error Writting Path",e);
        }
    }
}
