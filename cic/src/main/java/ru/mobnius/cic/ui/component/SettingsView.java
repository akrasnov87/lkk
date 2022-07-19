package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import ru.mobnius.cic.R;

public class SettingsView extends LinearLayoutCompat {

    private final TextView tvLabel;
    private final TextView tvValue;
    private final AppCompatImageView ivIcon;
    private final View vTop;
    private final View vBottom;

    public SettingsView(final @NonNull Context context) {
        super(context);
        setOrientation(LinearLayoutCompat.HORIZONTAL);
        inflate(context, R.layout.settings_view, this);
        tvLabel = findViewById(R.id.sv_label);
        tvValue = findViewById(R.id.sv_value);
        ivIcon = findViewById(R.id.sv_image);
        vTop = findViewById(R.id.sv_top_line);
        vBottom = findViewById(R.id.sv_bottom_line);
    }

    public SettingsView(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayoutCompat.HORIZONTAL);

        inflate(context, R.layout.settings_view, this);

        tvLabel = findViewById(R.id.sv_label);
        tvValue = findViewById(R.id.sv_value);
        ivIcon = findViewById(R.id.sv_image);
        vTop = findViewById(R.id.sv_top_line);
        vBottom = findViewById(R.id.sv_bottom_line);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SettingsView, 0, 0);

        final String label = a.getString(R.styleable.SettingsView_settingsLabel);

        if (a.hasValue(R.styleable.SettingsView_settingsLabelIcon)) {
            ivIcon.setImageDrawable(a.getDrawable(R.styleable.SettingsView_settingsLabelIcon));
        }
        if (label != null) {
            tvLabel.setText(label);
        }
        if (a.hasValue(R.styleable.SettingsView_settingsLabelColor)) {
            tvLabel.setTextColor(a.getColor(R.styleable.SettingsView_settingsLabelColor, ContextCompat.getColor(context, R.color.color_white)));
        }

        if (a.hasValue(R.styleable.SettingsView_settingsValueVisibility)) {
            final boolean visible = a.getBoolean(R.styleable.SettingsView_settingsValueVisibility, true);
            if (!visible) {
                tvValue.setVisibility(GONE);
            }
        }

        if (a.hasValue(R.styleable.SettingsView_settingsLabelSize)) {
            final float textSize = a.getDimension(R.styleable.SettingsView_settingsLabelSize, 14) / getResources().getDisplayMetrics().density;
            tvLabel.setTextSize(textSize);
        }

        final String valueText = a.getString(R.styleable.SettingsView_settingsValue);
        if (valueText != null) {
            tvValue.setText(valueText);
        }
        if (a.hasValue(R.styleable.SettingsView_settingsValueColor)) {
            tvValue.setTextColor(a.getColor(R.styleable.SettingsView_settingsValueColor, ContextCompat.getColor(context, R.color.color_white)));
        }

        if (a.hasValue(R.styleable.SettingsView_settingsValueSize)) {
            final float textSize = a.getDimension(R.styleable.SettingsView_settingsValueSize, 16) / getResources().getDisplayMetrics().density;
            tvValue.setTextSize(textSize);
        }

        if (a.hasValue(R.styleable.SettingsView_baselineBottomVisible)) {
            final boolean baselineBottomVisible = a.getBoolean(R.styleable.SettingsView_baselineBottomVisible, false);
            if (baselineBottomVisible) {
                vBottom.setVisibility(VISIBLE);
            }
        }

        if (a.hasValue(R.styleable.SettingsView_baselineBottomColor)) {
            vBottom.setBackgroundColor(a.getColor(R.styleable.SettingsView_baselineBottomColor, ContextCompat.getColor(context, R.color.colorAccent)));
        }

        if (a.hasValue(R.styleable.SettingsView_baselineTopVisible)) {
            final boolean baselineBottomVisible = a.getBoolean(R.styleable.SettingsView_baselineTopVisible, false);
            if (baselineBottomVisible) {
                vTop.setVisibility(VISIBLE);
            }
        }

        if (a.hasValue(R.styleable.SettingsView_baselineTopColor)) {
            vBottom.setBackgroundColor(a.getColor(R.styleable.SettingsView_baselineTopColor, ContextCompat.getColor(context, R.color.colorAccent)));
        }

        a.recycle();
    }

    public void setValue(@Nullable final String text) {
        if (text == null) {
            return;
        }
        tvValue.setText(text);
    }

    public void setLabel(@Nullable final String text) {
        if (text == null) {
            return;
        }
        tvLabel.setText(text);
    }

    public void setIcon(@Nullable final Drawable icon) {
        if (icon == null) {
            return;
        }
        ivIcon.setImageDrawable(icon);
    }

    public void setIcon(@DrawableRes final int resId) {
        ivIcon.setImageResource(resId);
    }


}
