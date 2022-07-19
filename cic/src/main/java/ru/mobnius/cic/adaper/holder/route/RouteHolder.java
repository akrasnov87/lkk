package ru.mobnius.cic.adaper.holder.route;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.databinding.ItemRouteBinding;
import ru.mobnius.cic.ui.model.routestatus.RouteStatus;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.cic.R;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.ui.model.RouteItem;

public class RouteHolder extends RecyclerView.ViewHolder {

    @Nullable
    private RouteItem routeItem;
    @NonNull
    private final ItemRouteBinding binding;

    public RouteHolder(final int typeRoute,
                       final @NonNull ItemRouteBinding binding,
                       final @NonNull OnRouteItemClickListener itemClickListener,
                       final @NonNull OnRouteInfoClickListener infoClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        final Context context = itemView.getContext();
        final Resources resources = itemView.getResources();
        this.binding.itemRouteInfo.setOnClickListener(v -> {
            if (routeItem == null) {
                return;
            }
            infoClickListener.onRouteInfoClick(routeItem);
        });
        itemView.setOnClickListener(v -> {
            if (routeItem == null) {
                return;
            }
            if (routeItem.canBeDone) {
                itemClickListener.onRouteItemClick(routeItem);
            } else {
                Toast.makeText(context, resources.getString(R.string.reserve_forbitten_message), Toast.LENGTH_LONG).show();
            }
        });

        if (typeRoute != RouteStatus.CURRENT) {
            this.binding.itemRouteProgress.setProgressDrawable(ResourcesCompat.getDrawable(resources, R.drawable.route_progress_circle_disabled, context.getTheme()));
            this.binding.itemRouteName.setTextColor(resources.getColor(R.color.colorPrimaryText));
            this.binding.itemRouteInfo.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_info_outline_disabled, context.getTheme()));
        }
    }

    public void bindRoute(final @NonNull RouteItem routeItem) {
        this.routeItem = routeItem;
        binding.itemRouteName.setText(routeItem.name);
        final String endDate = itemView.getContext().getString(R.string.before)
                + DateUtil.convertDateToCustomString(routeItem.dateEnd, DateUtil.USER_SHORT_FORMAT);
        binding.itemRouteEnd.setText(endDate);

        final int all = routeItem.totalPointsCount;

        binding.itemRouteProgress.setMax(all);

        binding.itemRouteProgress.setSecondaryProgress(all);


        final String pointCount = itemView.getContext().getString(R.string.points_quantity, all);
        binding.itemRouteCount.setText(pointCount);

        if (routeItem.isDraft) {
            binding.itemRouteProgress.setProgressDrawable(ResourcesCompat.getDrawable(itemView.getContext().getResources(),
                    R.drawable.route_progress_circle_draft, itemView.getContext().getTheme()));
            binding.itemRouteName.setTextColor(itemView.getContext().getResources().getColor(R.color.lightGreen));
        }
        if (DataManager.getInstance() == null) {
            return;
        }
        binding.itemRouteProgress.setProgress(routeItem.donePointsCount);
    }
}

