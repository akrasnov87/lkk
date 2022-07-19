package ru.mobnius.cic.adaper.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import ru.mobnius.cic.ui.model.SyncProgressItem;
import ru.mobnius.simple_core.utils.StringUtil;
/**
 * Класс обслуживающий обработку сравнения элементов в списке прогресса синхронизации
 */
public class SyncProgressDiffUtil extends DiffUtil.ItemCallback<SyncProgressItem> {

    @Override
    public boolean areItemsTheSame(@NonNull SyncProgressItem oldItem, @NonNull SyncProgressItem newItem) {
        return StringUtil.equals(oldItem.tid, newItem.tid);
    }

    @Override
    public boolean areContentsTheSame(@NonNull SyncProgressItem oldItem, @NonNull SyncProgressItem newItem) {
        if (StringUtil.notEquals(oldItem.status, newItem.status)) {
            return false;
        }
        if (StringUtil.notEquals(oldItem.name, newItem.name)) {
            return false;
        }

        if (StringUtil.notEquals(oldItem.transferData, newItem.transferData)) {
            return false;
        }

        if (oldItem.type != newItem.type) {
            return false;
        }
        if (oldItem.uploadProgress != newItem.uploadProgress) {
            return false;
        }
        return oldItem.downloadProgress == newItem.downloadProgress;
    }
}