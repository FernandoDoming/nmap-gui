package me.fernandodominguez.zenmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.models.host.Port;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Created by fernando on 03/01/16.
 */
public class GeneralResultsListAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> elements;

    public GeneralResultsListAdapter(Context context, List<T> elements) {
        this.context = context;
        this.elements = elements;
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public T getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.general_results_row, parent, false);

        TextView title = (TextView) rowView.findViewById(R.id.general_row_title);
        TextView subtitle = (TextView) rowView.findViewById(R.id.general_row_subtitle);
        ImageView icon = (ImageView) rowView.findViewById(R.id.general_row_icon);

        T result = elements.get(position);
        if (result instanceof Host) {
            Host host = (Host) result;
            title.setText(host.getAddress());
            subtitle.setText(host.getStatus().getState());
            icon.setImageResource(R.drawable.desktop);
        } else if (result instanceof Port) {
            Port port = (Port) result;
            title.setText(port.getPort());
            subtitle.setText(port.getStatus().getState());
            icon.setImageResource(R.drawable.service);
        }

        return rowView;
    }
}