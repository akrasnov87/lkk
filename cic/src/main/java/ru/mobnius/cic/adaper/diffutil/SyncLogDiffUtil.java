package ru.mobnius.cic.adaper.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import ru.mobnius.cic.ui.model.SyncLogItem;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Класс обслуживающий обработку сравнения элементов в списке логов синхронизации
 */
public class SyncLogDiffUtil extends DiffUtil.ItemCallback<SyncLogItem> {

    @Override
    public boolean areItemsTheSame(@NonNull SyncLogItem oldItem, @NonNull SyncLogItem newItem) {
        return oldItem.d_date.getTime() == newItem.d_date.getTime();
    }

    @Override
    public boolean areContentsTheSame(@NonNull SyncLogItem oldItem, @NonNull SyncLogItem newItem) {
        if (oldItem.b_error != newItem.b_error) {
            return false;
        }
        return StringUtil.equals(oldItem.c_message, newItem.c_message);
    }
}