package ru.mobnius.cic.adaper.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.cic.databinding.ItemMeterReadingsBinding;
import ru.mobnius.cic.ui.component.CicEditText;
import ru.mobnius.cic.ui.verification.MeterDigitVerification;
import ru.mobnius.cic.ui.verification.VerificationManager;
import ru.mobnius.cic.ui.model.MeterItem;

public class ReadingsHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final CicEditText.CicMetersUpdateListener listener;
    @NonNull
    private final VerificationManager verificationManager;
    @NonNull
    private final ItemMeterReadingsBinding binding;

    public ReadingsHolder(final @NonNull ItemMeterReadingsBinding binding,
                          final @NonNull CicEditText.CicMetersUpdateListener listener,
                          final @NonNull VerificationManager verificationManager) {
        super(binding.getRoot());
        this.binding = binding;
        this.verificationManager = verificationManager;
        this.listener = listener;
    }

    public void bindReadings(final @NonNull MeterItem meterItem, final int position) {
        final MeterDigitVerification meterDigitVerification = new MeterDigitVerification(binding.itemMeterReadingCurrent, 100 + position,
                meterItem.digitRank, meterItem.previousValue, meterItem.previousDate);
        verificationManager.addFieldToBeVerified(meterDigitVerification, meterDigitVerification.getOrder());
        binding.itemMeterReadingPreviousType.setText(meterItem.name);
        binding.itemMeterReadingPrevious.setText(meterItem.getPreviousValueText());
        binding.itemMeterReadingCurrent.setText(meterItem.currentValueText);
        binding.itemMeterReadingCurrent.addCicMeterUpdateListener(listener, meterItem.id);
    }

}
