package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mobnius.cic.R;

public class MapHelpFragment extends Fragment {
    public final static String IS_MANUAL_LOCATION_ENABLED = "ru.mobnius.cic.ui.fragment.IS_MANUAL_LOCATION_ENABLED";
    public final static String MAP_HELP_DIALOG_TAG = "ru.mobnius.cic.ui.fragment.MAP_HEL_DIALOG_TAG";
    private boolean isManualLocationEnabled;

    public static MapHelpFragment getInstance(boolean isManualLocationEnabled) {
        Bundle args = new Bundle();
        args.putBoolean(IS_MANUAL_LOCATION_ENABLED, isManualLocationEnabled);
        MapHelpFragment mapHelpFragment = new MapHelpFragment();
        mapHelpFragment.setArguments(args);
        return mapHelpFragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        isManualLocationEnabled = getArguments().getBoolean(IS_MANUAL_LOCATION_ENABLED);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_map_help, container, false);
        if (isManualLocationEnabled) {
            TextView tvCurrentLocation = v.findViewById(R.id.map_help_current_location);
            tvCurrentLocation.setText(R.string.previously_determined_location);

            TextView tvManualPoint = v.findViewById(R.id.map_help_undone_point_label);
            tvManualPoint.setText(getString(R.string.manual_point));

            TextView tvDonePoint = v.findViewById(R.id.map_help_done_point_label);
            tvDonePoint.setVisibility(View.GONE);

            ImageView ivDonePoint = v.findViewById(R.id.map_help_done_point_image);
            ivDonePoint.setVisibility(View.GONE);

            RelativeLayout rlDoneCluster = v.findViewById(R.id.map_help_done_cluster_rl);
            rlDoneCluster.setVisibility(View.GONE);

            RelativeLayout rlCurrentCluster = v.findViewById(R.id.map_help_user_cluster_rl);
            rlCurrentCluster.setVisibility(View.GONE);

            RelativeLayout rlUnDoneCluster = v.findViewById(R.id.map_help_undone_cluster_rl);
            rlUnDoneCluster.setVisibility(View.GONE);

            TextView tvDoneLabel = v.findViewById(R.id.map_help_done_cluster_label);
            tvDoneLabel.setVisibility(View.GONE);

            TextView tvUnDoneLabel = v.findViewById(R.id.map_help_undone_cluster_label);
            tvUnDoneLabel.setVisibility(View.GONE);

            TextView tvUserClusterLabel = v.findViewById(R.id.map_help_area_current_location);
            tvUserClusterLabel.setVisibility(View.GONE);
        }
        return v;
    }


}
