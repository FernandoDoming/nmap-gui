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
import me.fernandodominguez.zenmap.helpers.ScanHelper;
import me.fernandodominguez.zenmap.helpers.StringHelper;
import me.fernandodominguez.zenmap.holders.HostViewHolder;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Coded by fernando on 03/01/16.
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

        HostViewHolder holder;

        if (convertView == null) {      // Inflate the view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.general_results_row, parent, false);

            holder = new HostViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.general_row_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.general_row_subtitle);
            holder.extra = (TextView) convertView.findViewById(R.id.general_row_extra);
            holder.icon = (ImageView) convertView.findViewById(R.id.general_row_icon);
            convertView.setTag(holder);

        } else {        // Recycle the previously inflated view
            holder = (HostViewHolder) convertView.getTag();
        }

        T result = elements.get(position);
        if (result instanceof Host) {
            Host host = (Host) result;
            holder.title.setText(host.getTitle());
            if (host.getSubtitle() != null) {
                holder.subtitle.setText(host.getSubtitle());
            } else {
                holder.subtitle.setText(
                    context.getString(R.string.host_subtitle, host.getStatus().getState())
                );
            }
            holder.extra.setText(StringHelper.truncate(host.getStatus().getReason(), 20));
            holder.icon.setImageResource(ScanHelper.getDrawableIcon(host.getOs()));

        } else if (result instanceof Service) {
            Service service = (Service) result;
            holder.title.setText(
                    context.getString(R.string.service_title, service.getService(), service.getPort())
            );
            holder.subtitle.setText(context.getString(R.string.service_subtitle, service.getStatus().getState()));
            holder.extra.setText(StringHelper.truncate(service.getVersion(), 20));
            holder.icon.setImageResource(R.drawable.service);
        }

        return convertView;
    }
}
