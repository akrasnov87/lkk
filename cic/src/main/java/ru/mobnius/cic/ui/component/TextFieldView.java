package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.LinearLayoutCompat;


import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class TextFieldView extends LinearLayoutCompat {

    private final TextView tvLabel;
    private final TextView tvValue;

    public TextFieldView(final @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayoutCompat.VERTICAL);

        inflate(context, R.layout.text_field, this);

        tvLabel = findViewById(R.id.text_field_label);
        tvValue = findViewById(R.id.text_field_value);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TextFieldView, 0, 0);

        final String labelText = a.getString(R.styleable.TextFieldView_labelText);
        setLabelText(labelText);

        if (a.hasValue(R.styleable.TextFieldView_labelTextColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setLabelTextColor(a.getColor(R.styleable.TextFieldView_labelTextColor, getResources().getColor(android.R.color.darker_gray, null)));
            } else {
                setLabelTextColor(a.getColor(R.styleable.TextFieldView_labelTextColor, Color.GRAY));
            }
        }

        if (a.hasValue(R.styleable.TextFieldView_labelTextSize)) {
            float textSize = a.getDimension(R.styleable.TextFieldView_labelTextSize, getResources().getDimension(R.dimen.font_s)) / getResources().getDisplayMetrics().density;
            setLabelTextSize(textSize);
        }

        final String valueText = a.getString(R.styleable.TextFieldView_valueText);
        setValueText(valueText);

        if (a.hasValue(R.styleable.TextFieldView_valueTextColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setValueTextColor(a.getColor(R.styleable.TextFieldView_valueTextColor, getResources().getColor(android.R.color.black, null)));
            } else {
                setValueTextColor(a.getColor(R.styleable.TextFieldView_valueTextColor, Color.BLACK));
            }
        }

        if (a.hasValue(R.styleable.TextFieldView_valueTextSize)) {
            float textSize = a.getDimension(R.styleable.TextFieldView_valueTextSize, getResources().getDimension(R.dimen.font_m)) / getResources().getDisplayMetrics().density;
            setValueTextSize(textSize);
        }
        a.recycle();
    }

    public TextFieldView(final @NonNull Context context) {
        this(context, null);
        setOrientation(LinearLayoutCompat.VERTICAL);
        inflate(context, R.layout.text_field, this);
    }

    /**
     * Установка сообщения
     *
     * @param text текст сообщения
     */
    public void setLabelText(final @Nullable String text) {
        if (StringUtil.isEmpty(text)) {
            tvLabel.setText(StringUtil.EMPTY);
            return;
        }
        tvLabel.setText(text);
    }

    /**
     * Установка размера текста
     *
     * @param textSize размер текст
     */
    public void setLabelTextSize(final float textSize) {
        tvLabel.setTextSize(textSize);
    }

    /**
     * Установка цвета сообщения
     *
     * @param textColor цвет сообщения
     */
    public void setLabelTextColor(final int textColor) {
        tvLabel.setTextColor(textColor);
    }

    /**
     * Установка значения
     *
     * @param text текст значения
     */
    public void setValueText(final @Nullable String text) {
        if (StringUtil.isEmpty(text)) {
            tvValue.setText(StringUtil.EMPTY);
            return;
        }
        tvValue.setText(text);
    }

    public void setValueHtml(final @Nullable Spanned value) {
        if (StringUtil.isEmpty(value)) {
            tvValue.setText(StringUtil.EMPTY);
            return;
        }
        tvValue.setText(value);
    }

    /**
     * Установка размера значения
     *
     * @param textSize размер значения
     */
    public void setValueTextSize(final float textSize) {
        tvValue.setTextSize(textSize);
    }

    /**
     * Установка цвета значения
     *
     * @param textColor цвет значения
     */
    public void setValueTextColor(final int textColor) {
        tvValue.setTextColor(textColor);
    }

    public void setValueVisible(final int visible) {
        tvValue.setVisibility(visible);
    }

    @VisibleForTesting
    @NonNull
    public String getValue() {
        if (tvValue.getText() == null) {
            return StringUtil.EMPTY;
        }
        return tvValue.getText().toString();
    }
}
