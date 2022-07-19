package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mobnius.cic.adaper.holder.url.OnSelectNewUrl;
import ru.mobnius.cic.adaper.holder.url.UrlHolder;
import ru.mobnius.cic.databinding.ItemUrlBinding;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.utils.StringUtil;

public class UrlAdapter extends RecyclerView.Adapter<UrlHolder> {
    @NonNull
    private final OnSelectNewUrl onSelectNewUrl;
    @NonNull
    private final List<String> urls;
    public UrlAdapter(final @NonNull OnSelectNewUrl onSelectNewUrl, final @NonNull List<String> urls){
        this.onSelectNewUrl = onSelectNewUrl;
        this.urls = urls;
    }

    @NonNull
    @Override
    public UrlHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemUrlBinding binding = ItemUrlBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UrlHolder(binding, onSelectNewUrl);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlHolder holder, int position) {
        final String serverUrl = urls.get(position);
        boolean currentlySelected = StringUtil.equalsIgnoreCase(GlobalSettings.BASE_URL, serverUrl);
        holder.bind(GlobalSettings.availableUrls.get(position), currentlySelected);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
