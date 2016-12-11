package me.fernandodominguez.zenmap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.HostDetailActivity;
import me.fernandodominguez.zenmap.adapters.GeneralResultsListAdapter;
import me.fernandodominguez.zenmap.constants.Extras;
import me.fernandodominguez.zenmap.constants.Requests;
import me.fernandodominguez.zenmap.helpers.DateHelper;
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScanDetailFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private final static int GENERAL_SECTION_NUMBER = 1;
    private final static int NETWORK_MAP_SECTION_NUMBER = 2;
    private final static int RAW_SECTION_NUMBER = 3;

    private GeneralResultsListAdapter<Host> adapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ScanDetailFragment newInstance(int sectionNumber, ScanResult scanResult) {
        ScanDetailFragment fragment = new ScanDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Extras.ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(Extras.ARG_SCAN_RESULT, scanResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int sectionNumber = getArguments().getInt(Extras.ARG_SECTION_NUMBER);
        final ScanResult scanResult = (ScanResult) getArguments().getSerializable(Extras.ARG_SCAN_RESULT);
        View rootView = null;

        if (scanResult == null) return null;

        switch (sectionNumber) {
            case GENERAL_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_general_results_layout, container, false);
                ListView resultsListView = (ListView) rootView.findViewById(R.id.general_result_listview);

                adapter = new GeneralResultsListAdapter<>(getActivity(), scanResult.getHosts());
                resultsListView.setAdapter(adapter);

                resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = position - 1;
                    Host host = scanResult.getHosts().get(pos);
                    showHostDetail(host, pos);
                    }
                });

                View header = inflater.inflate(R.layout.scan_general_properties, container, false);
                fillHeader(header, scanResult);
                resultsListView.addHeaderView(header);
                break;

            case NETWORK_MAP_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_network_map_layout, container, false);
                break;

            case RAW_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_raw_results_layout, container, false);
                TextView rawOutput = (TextView) rootView.findViewById(R.id.section_label);
                rawOutput.setText(scanResult.getOutput());
                break;
        }
        return rootView;
    }

    public GeneralResultsListAdapter getAdapter() {
        return adapter;
    }

    public void updateItem(int position, Host host) {
        adapter.updateItem(position, host);
    }

    private void showHostDetail(Host host, int pos) {
        Intent intent = new Intent(getContext(), HostDetailActivity.class);
        intent.putExtra(Extras.HOST_EXTRA, host);
        intent.putExtra(Extras.HOST_POSITION_EXTRA, pos);
        startActivityForResult(intent, Requests.UPDATE_HOST);
    }

    private void fillHeader(View header, ScanResult scanResult) {
        TextView target = (TextView) header.findViewById(R.id.target);
        TextView startTime = (TextView) header.findViewById(R.id.start_time);
        TextView endTime = (TextView) header.findViewById(R.id.end_time);
        TextView elapsedTime = (TextView) header.findViewById(R.id.elapsed_time);
        TextView summary = (TextView) header.findViewById(R.id.summary);

        target.setText(scanResult.getTarget());
        startTime.setText( DateHelper.getDate(scanResult.getStartTime()) );
        endTime.setText( DateHelper.getDate(scanResult.getEndTime()) );
        elapsedTime.setText( scanResult.getElapsed() + " seconds" );
        summary.setText( scanResult.getSummary() );
    }
}
