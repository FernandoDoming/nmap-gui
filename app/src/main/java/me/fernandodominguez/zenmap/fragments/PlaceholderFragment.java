package me.fernandodominguez.zenmap.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.models.ScanResult;

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

                break;
            case RAW_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.fragment_scan_detail, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(scanResult.getOutput());
                break;
        }
        return rootView;
    }
}
