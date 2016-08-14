package org.fablabsantiago.smartcities.app.appmobile;

public class Patrimonio
{
    private String nombre;
    private int idDrawable;

    public Patrimonio(String nombre, int idDrawable) {
        this.nombre = nombre;
        this.idDrawable = idDrawable;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    public int getId() {
        return nombre.hashCode();
    }

    public static Patrimonio[] ITEMS = {
            new Patrimonio("Iglesia Convento Del Carmen Bajo", R.drawable.patrimonio1),
            new Patrimonio("Iglesia del Milagroso Niño Jesus de Praga", R.drawable.patrimonio2),
            new Patrimonio("Cité Capitol", R.drawable.patrimonio2),
            new Patrimonio("Conjunto Picarte", R.drawable.patrimonio1),
            new Patrimonio("Piscina Escolar Universidad de Chile", R.drawable.patrimonio1),
            new Patrimonio("Estación Mapocho", R.drawable.patrimonio2)
    };

    /**
     * Obtiene item basado en su identificador
     *
     * @param id identificador
     * @return Coche
     */
    public static Patrimonio getItem(int id) {
        for (Patrimonio item : ITEMS) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}