package me.fernandodominguez.zenmap.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.HostDetailActivity;
import me.fernandodominguez.zenmap.adapters.GeneralResultsListAdapter;
import me.fernandodominguez.zenmap.constants.Version;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * A placeholder fragment containing a simple view.
 */
public class HostDetailFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private final static String ARG_SECTION_NUMBER = "section_number";
    private final static String ARG_SCAN_RESULT    = "scan";

    private final static int GENERAL_SECTION_NUMBER = 1;
    private final static int NETWORK_MAP_SECTION_NUMBER = 2;
    private final static int RAW_SECTION_NUMBER = 3;

    private static String NMAP_BINARY_FILE;

    public HostDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HostDetailFragment newInstance(int sectionNumber, Host host) {
        HostDetailFragment fragment = new HostDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_SCAN_RESULT, host);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        final Host host = (Host) getArguments().getSerializable(ARG_SCAN_RESULT);
        View rootView = null;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        NMAP_BINARY_FILE
                = sharedPrefs.getString(getString(R.string.nmap_binary_path),
                                        getContext().getFilesDir().getParent() + "/bin/nmap");

        switch (sectionNumber) {
            case GENERAL_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_general_results_layout, container, false);
                ListView resultsListView =
                        (ListView) rootView.findViewById(R.id.general_result_listview);
                GeneralResultsListAdapter<Service> adapter =
                        new GeneralResultsListAdapter<>(getActivity(), host.getServices());
                resultsListView.setAdapter(adapter);

                View header = inflater.inflate(R.layout.host_general_properties, container, false);
                fillHeader(header, host);
                resultsListView.addHeaderView(header, null, false);

                resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Service service = host.getServices().get(position - 1);
                        showServiceDetail(service);
                    }
                });
                break;

            case NETWORK_MAP_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_network_map_layout, container, false);
                break;

            case RAW_SECTION_NUMBER:
                rootView = inflater.inflate(R.layout.scan_raw_results_layout, container, false);
                break;
        }
        return rootView;
    }

    private void showServiceDetail(Service service) {
        MaterialDialog dialog = showDetailDialog(service.getService());
        View view = dialog.getCustomView();
        addServiceDetails( (ViewGroup) view, service );
    }

    private MaterialDialog showDetailDialog(String title) {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(title)
                .customView(R.layout.item_detail_dialog, true)
                .positiveText(R.string.dismiss)
                .show();

        return dialog;
    }

    private void fillHeader(View header, Host host) {
        TextView target = (TextView) header.findViewById(R.id.host_address);
        target.setText(host.getAddress());

        ViewGroup viewGroup = (ViewGroup) header;
        if (getContext() instanceof HostDetailActivity) {

            HostDetailActivity activity = (HostDetailActivity) getContext();
            activity.addHostDetails(viewGroup, host);
        }
    }

    private void addServiceDetails(ViewGroup viewGroup, Service service) {
        // TODO: Use reflection
        LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (service.getService() != null) {
            View portView = layoutInflater.inflate(R.layout.general_property, null);
            ((TextView) portView.findViewById(R.id.property_key)).setText( getString(R.string.service) );
            ((TextView) portView.findViewById(R.id.property_value)).setText(service.getService());
            viewGroup.addView(portView);
        }

        if (service.getVersion() != null) {
            View portView = layoutInflater.inflate(R.layout.general_property, null);
            ((TextView) portView.findViewById(R.id.property_key)).setText( getString(R.string.service_version) );
            ((TextView) portView.findViewById(R.id.property_value)).setText(service.getVersion());
            viewGroup.addView(portView);

            if (!service.getVersion().equals(Version.UNKNOWN)) {
                Button button = new Button(getContext());
                button.setText(R.string.check_vulnerabiities);
                viewGroup.addView(button);
            }
        }

        if (service.getPort() != null) {
            View portView = layoutInflater.inflate(R.layout.general_property, null);
            ((TextView) portView.findViewById(R.id.property_key)).setText( getString(R.string.port) );
            ((TextView) portView.findViewById(R.id.property_value)).setText(service.getPort());
            viewGroup.addView(portView);
        }

        if (service.getProtocol() != null) {
            View protocolView = layoutInflater.inflate(R.layout.general_property, null);
            ((TextView) protocolView.findViewById(R.id.property_key)).setText( getString(R.string.protocol) );
            ((TextView) protocolView.findViewById(R.id.property_value)).setText(service.getProtocol());
            viewGroup.addView(protocolView);
        }

        if (service.getStatus() != null) {
            View statusView = layoutInflater.inflate(R.layout.general_property, null);
            ((TextView) statusView.findViewById(R.id.property_key)).setText( getString(R.string.status) );
            ((TextView) statusView.findViewById(R.id.property_value)).setText(service.getStatus().getState());
            viewGroup.addView(statusView);

            View statusReasonView = layoutInflater.inflate(R.layout.general_property, null);
            ((TextView) statusReasonView.findViewById(R.id.property_key)).setText( getString(R.string.status_reason) );
            ((TextView) statusReasonView.findViewById(R.id.property_value)).setText(service.getStatus().getReason());
            viewGroup.addView(statusReasonView);
        }

    }
}
