package ru.mobnius.cic.ui.component;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import ru.mobnius.cic.R;

public class YearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1945;
    public final static String YEAR_PICKER_DIALOG_TAG = "ru.mobnius.cic.ui.component.YEAR_PICKER_DIALOG_TAG";
    private DatePickerDialog.OnDateSetListener listener;
    private int minYear = MIN_YEAR;
    private int maxYear = MAX_YEAR;
    private NumberPicker yearPicker;

    public void setListener(final @NonNull DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public void setMaxYear(final int maxYear) {
        this.maxYear = maxYear;
        if (yearPicker != null) {
            yearPicker.setMaxValue(this.maxYear);
        }
    }

    public void setMinYear(final int minYear) {
        this.minYear = minYear;
        if (yearPicker != null) {
            yearPicker.setMaxValue(this.minYear);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final Calendar cal = Calendar.getInstance();
        final View dialog = inflater.inflate(R.layout.date_picker_dialog, null);
        yearPicker = dialog.findViewById(R.id.picker_year);

        final int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setValue(year);

        builder.setView(dialog).setPositiveButton(R.string.ready, (dialog1, id) -> {
            if (listener == null) {
                return;
            }
            listener.onDateSet(null, yearPicker.getValue(), 0, 0);
        })
                .setNegativeButton(R.string.cancel, (dialog12, id) -> {
                    if (YearPickerDialog.this.getDialog() != null) {
                        YearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}