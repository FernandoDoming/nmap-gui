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
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

/**
 * Created by fernando on 31/12/15.
 */
public class ScansListAdapter extends BaseAdapter {

    private List<ScanResult> scans;
    private Context context;

    public ScansListAdapter(Context context, List<ScanResult> scans) {
        this.scans = scans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return scans.size();
    }

    @Override
    public Object getItem(int position) {
        return scans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.scan_row, parent, false);

        TextView scanTitle = (TextView) rowView.findViewById(R.id.scan_title);
        TextView scanResult = (TextView) rowView.findViewById(R.id.scan_result);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        ScanResult result = scans.get(position);
        scanTitle.setText(result.getTitle());
        // change the icon for Windows and iPhone
        if (result instanceof NetworkScan) {
            imageView.setImageResource(R.drawable.network);
        } else if (result instanceof HostScan) {
            imageView.setImageResource(R.drawable.network_server);
        }

        return rowView;
    }

    public void addScan(ScanResult scanResult) {
        scans.add(scanResult);
        this.notifyDataSetChanged();
    }
}