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

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String name = "<name>" + n + "</name><trkseg>\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (Location l : points) {
            //<time>" + df.format(new Date(l.getTime())) + "</time>
            segments += "<trkpt lat=\"" + l.getLatitude() + "\" lon=\"" + l.getLongitude() + "\"></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
            if (BuildConfig.DEBUG)
                Log.i(TAG, "Saved " + points.size() + " points.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error Writting Path",e);
        }
    }
}
