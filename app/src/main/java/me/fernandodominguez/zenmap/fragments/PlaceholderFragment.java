package me.fernandodominguez.zenmap.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.adapters.GeneralResultsListAdapter;
import me.fernandodominguez.zenmap.helpers.DateHelper;
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.network.Host;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private final static String ARG_SECTION_NUMBER = "section_number";
    private final static String ARG_SCAN_RESULT    = "scan";

    private final static int GENERAL_SECTION_NUMBER = 1;
    private final static int _SECTION_NUMBER = 2;
    private final static int RAW_SECTION_NUMBER = 3;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, ScanResult scanResult) {
        PlaceholderFragment fragment = new PlaceholderFragment();
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
        ScanResult scanResult = (ScanResult) getArguments().getSerializable(ARG_SCAN_RESULT);
        View rootView = null;

        switch (sectionNumber) {
            case GENERAL_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_general_results_layout, container, false);
                ListView resultsListView = (ListView) rootView.findViewById(R.id.general_result_listview);

                if (scanResult instanceof NetworkScan) {
                    NetworkScan networkScan = (NetworkScan) scanResult;
                    GeneralResultsListAdapter<Host> adapter = new GeneralResultsListAdapter<>(getActivity(), networkScan.getUpHosts());
                    resultsListView.setAdapter(adapter);
                } else if (scanResult instanceof HostScan) {
                    HostScan hostScan = (HostScan) scanResult;
                    GeneralResultsListAdapter<Service> adapter = new GeneralResultsListAdapter<>(getActivity(), hostScan.getServices());
                    resultsListView.setAdapter(adapter);
                }

                View header = inflater.inflate(R.layout.scan_general_properties, container, false);
                fillHeader(header, scanResult);
                resultsListView.addHeaderView(header);
                break;

            case RAW_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_raw_results_layout, container, false);
                TextView rawOutput = (TextView) rootView.findViewById(R.id.section_label);
                rawOutput.setText(scanResult.getOutput());
                break;
        }
        return rootView;
    }

    private void fillHeader(View header, ScanResult scanResult) {
        TextView target = (TextView) header.findViewById(R.id.target);
        TextView startTime = (TextView) header.findViewById(R.id.start_time);
        TextView endTime = (TextView) header.findViewById(R.id.end_time);

        target.setText(scanResult.getTarget());
        startTime.setText( DateHelper.getDate(scanResult.getStartTime()) );
        endTime.setText( DateHelper.getDate(scanResult.getEndTime()) );
    }
}
