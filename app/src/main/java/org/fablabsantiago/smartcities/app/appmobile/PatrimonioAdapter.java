package org.fablabsantiago.smartcities.app.appmobile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PatrimonioAdapter extends BaseAdapter
{
    private Context context;

    public PatrimonioAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Patrimonio.ITEMS.length;
    }

    @Override
    public Patrimonio getItem(int position) {
        return Patrimonio.ITEMS[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_patrimonios, viewGroup, false);
        }

        ImageView imagenCoche = (ImageView) view.findViewById(R.id.imagen_coche);
        TextView nombreCoche = (TextView) view.findViewById(R.id.nombre_coche);

        final Patrimonio item = getItem(position);
        imagenCoche.setImageResource(item.getIdDrawable());
        nombreCoche.setText(item.getNombre());

        return view;
    }

}