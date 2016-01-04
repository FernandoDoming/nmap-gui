package me.fernandodominguez.zenmap.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.adapters.GeneralPropertiesListAdapter;
import me.fernandodominguez.zenmap.adapters.GeneralResultsListAdapter;
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.host.Port;
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
                ListView propertiesListView = (ListView) rootView.findViewById(R.id.general_properties_listview);
                GeneralPropertiesListAdapter propertiesAdapter = new GeneralPropertiesListAdapter(getActivity(), scanResult);
                propertiesListView.setAdapter(propertiesAdapter);
                if (scanResult instanceof NetworkScan) {
                    NetworkScan networkScan = (NetworkScan) scanResult;
                    GeneralResultsListAdapter<Host> adapter = new GeneralResultsListAdapter<>(getActivity(), networkScan.getHosts());
                    resultsListView.setAdapter(adapter);
                } else if (scanResult instanceof HostScan) {
                    HostScan hostScan = (HostScan) scanResult;
                    GeneralResultsListAdapter<Port> adapter = new GeneralResultsListAdapter<>(getActivity(), hostScan.getPorts());
                    resultsListView.setAdapter(adapter);
                }
                break;
            case RAW_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_raw_results_layout, container, false);
                TextView rawOutput = (TextView) rootView.findViewById(R.id.section_label);
                rawOutput.setText(scanResult.getOutput());
                break;
        }
        return rootView;
    }
}
