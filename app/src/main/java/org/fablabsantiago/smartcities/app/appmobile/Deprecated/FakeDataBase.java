package org.fablabsantiago.smartcities.app.appmobile.Deprecated;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

public class FakeDataBase
{
    public static Destination createDestinationObject(String name) {
        Destination destination = null;

        switch(name)
        {
            case "Casa":
                Log.i("FakeDataBase","switch case 'casa'");
                destination = new Destination(name, new LatLng(-33.4126792,-70.5986989));
                destination.fileName = "casa";
                destination.address = "Vasco de Gama 4840, Las Condes";
                Log.i("FakeDataBase","switch case 'casa': adding hotspots");
                destination.addNegativeHotspot(
                        new LatLng(-33.43019, -70.62451),
                        true,
                        "Via peligrosa :s",
                        "Calle acechada por los males",
                        "negruta1Vias_");
                destination.addNegativeHotspot(
                        new LatLng(-33.42466, -70.59732),
                        true,
                        "Me acechan fantasmas pasados",
                        "Calle acechada por los males",
                        "negruta2Ciclo");
                destination.addPositiveHotspot(
                        new LatLng(-33.41922, -70.59937),
                        true,
                        "Ciclovía",
                        "Ciclovía en  buen estado",
                        "posruta2Ciclo");
                destination.addNegativeHotspot(
                        new LatLng(-33.41641, -70.60045),
                        true,
                        "He sido atacado por tanta vegetación.",
                        "Esto debe acabar",
                        "negruta2Veget");
                destination.addNegativeHotspot(
                        new LatLng(-33.42693,-70.60258),
                        true,
                        "Muy contamindado por construccion.",
                        "Esto debe acabar",
                        "negruta2Otros");

                destination.addPositiveHotspot(
                        new LatLng(-33.436836, -70.610898),
                        true,
                        "Ciclovía",
                        "Ciclovía en  buen estado",
                        "posruta2Ciclo");
                destination.addPositiveHotspot(
                        new LatLng(-33.415570, -70.599686),
                        true,
                        "Bien",
                        "Todo tranquilo :).",
                        "posruta2Otros");
                destination.addPositiveHotspot(
                        new LatLng(-33.44164, -70.62972),
                        true,
                        "Plantas",
                        "Todo tranquilo, agradable vegetación.",
                        "posruta2Veget");
                destination.addPositiveHotspot(
                        new LatLng(-33.44164, -70.62972),
                        true,
                        "Ciclovia buena",
                        "Ciclovía fluida y sin peatones '#@!.",
                        "posruta2Ciclo");
                destination.addPositiveHotspot(
                        new LatLng(-33.4337, -70.62647),
                        true,
                        "Taller Céntrico",
                        "Justo pinche rueda y me raje!.",
                        "posruta1Mante");
                destination.addNegativeHotspot(
                        new LatLng(-33.46031, -70.65735),
                        false,
                        "Lleno de peatones imprudentes",
                        "Y se cruzan autos por al frente.",
                        "negruta2Peato");
                destination.addPositiveHotspot(
                        new LatLng(-33.4253, -70.62166),
                        true,
                        "Todo bien",
                        "Relajado.",
                        "posruta1Otros");
                destination.addNegativeHotspot(
                        new LatLng(-33.439271, -70.621557),
                        false,
                        "Cruce Peligroso",
                        "Se cruzan autos por al frente.",
                        "negruta2Autos");
                destination.addPositiveHotspot(
                        new LatLng(-33.41603,-70.60671),
                        true,
                        "Calle no peligrosa",
                        "Los autos son bastante precavidos",
                        "posruta1Vias_");
                destination.addNegativeHotspot(
                        new LatLng(-33.432028, -70.602107),
                        false,
                        "Autos Asesinos",
                        "Pasan muy rápido y la calle es angosta.",
                        "negruta2Autos");
                destination.addNegativeHotspot(
                        new LatLng(-33.44686, -70.62852),
                        false,
                        "Autos Asesinos",
                        "Imprecabidos maleantes sueltos.",
                        "negruta2Autos");
                break;
            case "Beauchef 850":
                Log.i("FakeDataBase","switch case 'beauchef850'");
                destination = new Destination(name, new LatLng(-33.4577491, -70.6634021));
                destination.fileName = "beauchef850";
                destination.address = "Beauchef 850, Santiago";
                //RUTA1
                destination.addPositiveHotspot(
                        new LatLng(-33.44686, -70.62852),
                        true,
                        "Ciclovía",
                        "Buena ciclovía, rápida.",
                        "posruta1Ciclo");
                destination.addNegativeHotspot(
                        new LatLng(-33.44928, -70.63952),
                        false,
                        "Cruce Peligroso",
                        "Mucho auto y poca señalización.",
                        "negruta1Autos");
                destination.addNegativeHotspot(
                        new LatLng(-33.45246, -70.65276),
                        false,
                        "Vía en mal estado",
                        "La ciclovía esta rota con grandes baches en ciertos lugares.",
                        "negruta1Ciclo");
                destination.addNegativeHotspot(
                        new LatLng(-33.45311, -70.65705),
                        true,
                        "Via peligrosa :s",
                        "Calle acechada por los males",
                        "negruta1Vias_");
                destination.addNegativeHotspot(
                        new LatLng(-33.4503,-70.62763),
                        true,
                        "Me acechan fantasmas pasados",
                        "Calle acechada por los males",
                        "negruta2Ciclo");
                destination.addPositiveHotspot(
                        new LatLng(-33.45177,-70.62924),
                        true,
                        "Ciclovía",
                        "Ciclovía en  buen estado",
                        "posruta2Ciclo");
                destination.addNegativeHotspot(
                        new LatLng(-33.4539,-70.62867),
                        true,
                        "He sido atacado por tanta vegetación.",
                        "Esto debe acabar",
                        "negruta2Veget");
                destination.addNegativeHotspot(
                        new LatLng(-33.45559,-70.63076),
                        true,
                        "Muy contamindado por construccion.",
                        "Esto debe acabar",
                        "negruta2Otros");


                destination.addPositiveHotspot(
                        new LatLng(-33.45707, -70.63771),
                        true,
                        "Ciclovía",
                        "Ciclovía en  buen estado",
                        "posruta2Ciclo");
                destination.addPositiveHotspot(
                        new LatLng(-33.45913,-70.64766),
                        true,
                        "Bien",
                        "Todo tranquilo :).",
                        "posruta2Otros");
                destination.addPositiveHotspot(
                        new LatLng(-33.46035,-70.65666),
                        true,
                        "Plantas",
                        "Todo tranquilo, agradable vegetación.",
                        "posruta2Veget");
                destination.addPositiveHotspot(
                        new LatLng(-33.45906,-70.65746),
                        true,
                        "Ciclovia buena",
                        "Ciclovía fluida y sin peatones '#@!.",
                        "posruta2Ciclo");
                destination.addPositiveHotspot(
                        new LatLng(-33.44985,-70.64153),
                        true,
                        "Taller Céntrico",
                        "Justo pinche rueda y me raje!.",
                        "posruta1Mante");
                destination.addNegativeHotspot(
                        new LatLng(-33.45867, -70.64539),
                        false,
                        "Lleno de peatones imprudentes",
                        "Y se cruzan autos por al frente.",
                        "negruta2Peato");
                destination.addPositiveHotspot(
                        new LatLng(-33.45268, -70.65923),
                        true,
                        "Todo bien",
                        "Relajado.",
                        "posruta1Otros");
                destination.addNegativeHotspot(
                        new LatLng(-33.45817, -70.65807),
                        false,
                        "Cruce Peligroso",
                        "Se cruzan autos por al frente.",
                        "negruta2Autos");
                destination.addPositiveHotspot(
                        new LatLng(-33.45335, -70.66154),
                        true,
                        "Calle no peligrosa",
                        "Los autos son bastante precavidos",
                        "posruta1Vias_");
                destination.addNegativeHotspot(
                        new LatLng(-33.45827, -70.66044),
                        false,
                        "Autos Asesinos",
                        "Pasan muy rápido y la calle es angosta.",
                        "negruta2Autos");
                destination.addNegativeHotspot(
                        new LatLng(-33.45852, -70.66392),
                        false,
                        "Autos Asesinos",
                        "Imprecabidos maleantes sueltos.",
                        "negruta2Autos");
                break;
            case "Estacion Mapocho":
                Log.i("FakeDataBase","switch case 'estacionmapocho'");
                destination = new Destination(name, new LatLng(-33.432336,-70.653274));
                destination.fileName = "estacionmapocho";
                destination.address = "Av. Presidente Balmaceda,Santiago";

                destination.addPositiveHotspot(
                        new LatLng(-33.44774, -70.62832),
                        true,
                        "Calle no peligrosa",
                        "Los autos son bastante precavidos",
                        "posruta1Vias_");
                destination.addNegativeHotspot(
                        new LatLng(-33.43272, -70.65328),
                        false,
                        "Autos Asesinos",
                        "Pasan muy rápido y la calle es angosta.",
                        "negruta1Autos");


                destination.addPositiveHotspot(
                        new LatLng(-33.43892, -70.63038),
                        true,
                        "Calle no peligrosa",
                        "Los autos son bastante precavidos",
                        "posruta2Vias_");
                destination.addNegativeHotspot(
                        new LatLng(-33.43641, -70.63538),
                        false,
                        "Autos Asesinos",
                        "Pasan muy rápido y la calle es angosta.",
                        "negruta2Autos");

                break;
            case "Palacio La Moneda":
                Log.i("FakeDataBase","switch case 'estacionmapocho'");
                destination = new Destination(name, new LatLng(-33.432336,-70.653274));
                destination.fileName = "estacionmapocho";
                destination.address = "Moneda 1302, Santiago";
                break;
            case "Parque Forestal":
                Log.i("FakeDataBase","switch case 'estacionmapocho'");
                destination = new Destination(name, new LatLng(-33.432336,-70.653274));
                destination.fileName = "estacionmapocho";
                destination.address = "Cardenal José María Caro 213, Santiago";
                break;
            case "Matucana 100":
                Log.i("FakeDataBase","switch case 'estacionmapocho'");
                destination = new Destination(name, new LatLng(-33.432336,-70.653274));
                destination.fileName = "estacionmapocho";
                destination.address = "Av. Matucana 100, Santiago";
                break;
            case "Santuario Nacional de Maipú":
                Log.i("FakeDataBase","switch case 'estacionmapocho'");
                destination = new Destination(name, new LatLng(-33.432336,-70.653274));
                destination.fileName = "estacionmapocho";
                destination.address = "El Carmen 1777, Maipú";
                break;
            default:
                Log.i("FakeDataBase","destionationLatLng switch Error - invalid 'name'");
        }

        return destination;
    }

    public static ArrayList<String> getDestinos()
    {
        return new ArrayList<String>(Arrays.asList(
                "Casa",
                "Beauchef 850",
                "Estacion Mapocho",
                "Palacio La Moneda",
                "Parque Forestal",
                "Matucana 100",
                "Santuario Nacional de Maipú"));
    }



}
