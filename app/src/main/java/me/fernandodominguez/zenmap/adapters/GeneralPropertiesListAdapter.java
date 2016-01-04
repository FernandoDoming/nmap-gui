package me.fernandodominguez.zenmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * Created by fernando on 04/01/16.
 */
public class GeneralPropertiesListAdapter extends BaseAdapter {

    private Context context;
    private HashMap<String, String> properties;

    public GeneralPropertiesListAdapter(Context context, ScanResult scanResult) {
        this.context = context;
        this.properties = buildProperties(scanResult);
    }

    @Override
    public int getCount() {
        return properties.entrySet().size();
    }

    @Override
    public String getItem(int position) {
        List<String> values = new ArrayList<>(properties.values());
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.general_properties_row, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.property_name);
            viewHolder.value = (TextView) rowView.findViewById(R.id.property_value);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        List<String> keys = new ArrayList<>(properties.keySet());
        List<String> values = new ArrayList<>(properties.values());
        viewHolder.name.setText(keys.get(position));
        viewHolder.value.setText(values.get(position));

        return rowView;
    }

    private HashMap<String, String> buildProperties(ScanResult scanResult) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("End time", String.valueOf(scanResult.getEndTime()));
        properties.put("Start time", String.valueOf(scanResult.getStartTime()));
        properties.put("Target", scanResult.getTarget());
        return properties;
    }

    static class ViewHolder {
        public TextView name;
        public TextView value;
    }
}
