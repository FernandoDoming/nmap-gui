package me.fernandodominguez.zenmap.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
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

    private Context context;
    private List<ScanResult> scans;
    private SparseBooleanArray selectedItemsIds;

    public ScansListAdapter(Context context, List<ScanResult> scans) {
        this.scans = scans;
        this.context = context;
        this.selectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return scans.size();
    }

    @Override
    public ScanResult getItem(int position) {
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
        scanResult.setText(result.getResult());

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

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public int getSelectedCount() {
        return selectedItemsIds.size();
    }

    private void selectView(int position, boolean value) {
        if (value) {
            selectedItemsIds.put(position, value);
        } else {
            selectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }

    public void delete(ScanResult toDelete) {
        // Delete database record
        toDelete.delete();
        // Delete from ListView
        scans.remove(toDelete);
        notifyDataSetChanged();
    }
}
