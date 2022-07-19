package ru.mobnius.cic.adaper.holder.url;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.ItemUrlBinding;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.utils.StringUtil;

public class UrlHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final ItemUrlBinding binding;
    @Nullable
    private String url;

    public UrlHolder(@NonNull ItemUrlBinding binding, final @NonNull OnSelectNewUrl onSelectNewUrl) {
        super(binding.getRoot());
        this.binding = binding;
        binding.itemUrlServer.setOnClickListener(v -> {
            if (StringUtil.isEmpty(url)) {
                return;
            }
            final String enviroment = binding.itemUrlEnviroment.getCurrentSelection();
            if (StringUtil.isEmpty(enviroment)) {
                return;
            }
            if (StringUtil.equals(GlobalSettings.BASE_URL, url) && StringUtil.equals(GlobalSettings.ENVIRONMENT, enviroment)) {
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.change_url_to_make_choice), Toast.LENGTH_SHORT).show();
                return;
            }
            onSelectNewUrl.onNewUrlSelected(url, enviroment);
        });
    }

    public void bind(final @NonNull String url, final boolean currentlySelected) {
        this.url = url;
        binding.itemUrlServer.setText(url);
        if (currentlySelected) {
            binding.itemUrlEnviromentSelected.setVisibility(View.VISIBLE);
        } else {
            binding.itemUrlEnviromentSelected.setVisibility(View.GONE);
        }
        binding.itemUrlEnviroment.setData(GlobalSettings.enviromentMap, true);
        if (currentlySelected) {
            for (int i = 0; i < GlobalSettings.enviromentMap.size(); i++) {
                if (!(GlobalSettings.enviromentMap.get(i).get(Names.NAME) instanceof String)) {
                    continue;
                }
                final String enviroment = (String) GlobalSettings.enviromentMap.get(i).get(Names.NAME);
                if (StringUtil.equalsIgnoreCase(GlobalSettings.ENVIRONMENT, enviroment)) {
                    binding.itemUrlEnviroment.setSelection(i);
                    break;
                }
            }
        }else {
            binding.itemUrlEnviroment.setSelection(0);
        }

    }
}
