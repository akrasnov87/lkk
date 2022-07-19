package ru.mobnius.cic.adaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mobnius.cic.adaper.holder.ReadingsHolder;
import ru.mobnius.cic.databinding.ItemMeterReadingsBinding;
import ru.mobnius.cic.ui.component.CicEditText;
import ru.mobnius.cic.ui.verification.VerificationManager;
import ru.mobnius.cic.ui.model.MeterItem;

public class ReadingsAdapter extends RecyclerView.Adapter<ReadingsHolder> {
    @NonNull
    private final List<MeterItem> meterItems;
    @NonNull
    private final VerificationManager verificationManager;
    @NonNull
    private final CicEditText.CicMetersUpdateListener listener;

    public ReadingsAdapter(final @NonNull List<MeterItem> meterItems,
                           final @NonNull CicEditText.CicMetersUpdateListener listener,
                           final @NonNull VerificationManager verificationManager) {
        this.verificationManager = verificationManager;
        this.listener = listener;
        this.meterItems = meterItems;
    }

    @NonNull
    @Override
    public ReadingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemMeterReadingsBinding binding = ItemMeterReadingsBinding.inflate(inflater, parent, false);
        return new ReadingsHolder(binding, listener, verificationManager);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingsHolder holder, int position) {
        holder.bindReadings(meterItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return meterItems.size();
    }

}

