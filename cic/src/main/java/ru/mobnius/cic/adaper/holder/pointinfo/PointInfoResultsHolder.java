package ru.mobnius.cic.adaper.holder.pointinfo;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.ItemPointInfoResultsBinding;
import ru.mobnius.cic.ui.model.ResultItem;
import ru.mobnius.simple_core.utils.AlertDialogUtil;

public class PointInfoResultsHolder extends RecyclerView.ViewHolder {
    @Nullable
    private ResultItem resultItem;
    @NonNull
    private final ItemPointInfoResultsBinding binding;

    public PointInfoResultsHolder(final @NonNull ItemPointInfoResultsBinding binding,
                                  final @NonNull OnPointInfoResultCancelListener resultCancelListener) {
        super(binding.getRoot());
        this.binding = binding;
        final OnPointInfoResultCancelledListener listener = () -> {
            binding.itemPointInfoResultsCancel.setImageResource(R.drawable.ic_deleted_24);
            if (resultItem != null) {
                resultItem.isCancelled = true;
            }
        };
        this.binding.itemPointInfoResultsCancel.setOnClickListener(view -> {
            if (resultItem == null) {
                return;
            }
            final Context context = itemView.getContext();
            if (resultItem.isSync) {
                Toast.makeText(context, context.getString(R.string.can_not_cancel_synchronized_result), Toast.LENGTH_SHORT).show();
                return;
            }
            if (resultItem.isCancelled) {
                Toast.makeText(context, context.getString(R.string.result_already_cancelled), Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialogUtil.alert(context,
                    context.getString(R.string.cancel_result_confirm_message),
                    context.getString(R.string.yes),
                    (dialogInterface, i) -> resultCancelListener.onResultCancel(resultItem.id, listener),
                    context.getString(R.string.no),
                    (dialogInterface, i) -> dialogInterface.dismiss());
        });
    }

    public void bind(final @NonNull ResultItem item) {
        this.resultItem = item;
        binding.itemPointInfoResultsDate.setValue(resultItem.date);
        if (resultItem.isSync) {
            binding.itemPointInfoResultsCancel.setImageResource(R.drawable.ic_sync_done_green_24dp);
        } else {
            if (resultItem.isCancelled) {
                binding.itemPointInfoResultsCancel.setImageResource(R.drawable.ic_deleted_24);
            } else {
                binding.itemPointInfoResultsCancel.setImageResource(R.drawable.ic_delete_24);
            }
        }
    }

}
