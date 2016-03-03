package me.fernandodominguez.zenmap.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.HostDetailActivity;
import me.fernandodominguez.zenmap.adapters.GeneralResultsListAdapter;
import me.fernandodominguez.zenmap.constants.Extras;
import me.fernandodominguez.zenmap.constants.Version;
import me.fernandodominguez.zenmap.helpers.DateHelper;
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.network.Host;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScanDetailFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private final static String ARG_SECTION_NUMBER = "section_number";
    private final static String ARG_SCAN_RESULT    = "scan";

    private final static int GENERAL_SECTION_NUMBER = 1;
    private final static int NETWORK_MAP_SECTION_NUMBER = 2;
    private final static int RAW_SECTION_NUMBER = 3;

    public ScanDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ScanDetailFragment newInstance(int sectionNumber, ScanResult scanResult) {
        ScanDetailFragment fragment = new ScanDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_SCAN_RESULT, scanResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        final ScanResult scanResult = (ScanResult) getArguments().getSerializable(ARG_SCAN_RESULT);
        View rootView = null;

        switch (sectionNumber) {
            case GENERAL_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_general_results_layout, container, false);
                ListView resultsListView = (ListView) rootView.findViewById(R.id.general_result_listview);

                GeneralResultsListAdapter<Host> adapter = new GeneralResultsListAdapter<>(getActivity(), scanResult.getHosts());
                resultsListView.setAdapter(adapter);

                resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Host host = scanResult.getHosts().get(position - 1);
                        showHostDetail(host);
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

    private void showHostDetail(Host host) {
        Intent intent = new Intent(getContext(), HostDetailActivity.class);
        intent.putExtra(Extras.HOST_ID_EXTRA, host.getId());
        startActivity(intent);
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
