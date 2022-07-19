package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.LinearLayoutCompat;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class LabelValueView extends LinearLayoutCompat {

    private final TextView value;
    private String label;

    public LabelValueView(final @NonNull Context context) {
        super(context);
        inflate(context, R.layout.label_value, this);
        value = findViewById(R.id.label_value_value);
    }

    public LabelValueView(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayoutCompat.HORIZONTAL);
        inflate(context, R.layout.label_value, this);

        value = findViewById(R.id.label_value_value);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.LabelValueView, 0, 0);

        final String templabel = a.getString(R.styleable.LabelValueView_label);
        if (templabel != null) {
            if (!templabel.endsWith(": ")) {
                label = templabel + ": ";
            } else {
                label = templabel;
            }
            final String sourceString = "<b>" + label + "</b> ";
            value.setText(Html.fromHtml(sourceString));
        } else {
            label = "";
        }

        if (a.hasValue(R.styleable.LabelValueView_valueColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                value.setTextColor(a.getColor(R.styleable.LabelValueView_valueColor, getResources().getColor(android.R.color.black, null)));
            } else {
                value.setTextColor(a.getColor(R.styleable.LabelValueView_valueColor, Color.BLACK));
            }
        }

        if (a.hasValue(R.styleable.LabelValueView_valueSize)) {
            float textSize = a.getDimension(R.styleable.LabelValueView_labelSize, getResources().getDimension(R.dimen.font_s)) / getResources().getDisplayMetrics().density;
            value.setTextSize(textSize);
        }
        a.recycle();
    }

    public void setValue(final @Nullable String text) {
        if (text == null) {
            return;
        }
        final String sourceString = "<b>" + label + "</b> " + text;
        value.setText(Html.fromHtml(sourceString));

    }

    public void setValueWithLineLength(final @Nullable String text, final int lineLength) {
        if (text == null) {
            return;
        }
        final String sourceString = "<b>" + label + "</b> " + cutString(text, lineLength);
        value.setText(Html.fromHtml(sourceString));

    }

    public void setLabel(@Nullable String text) {
        if (text == null) {
            return;
        }
        if (!text.endsWith(": ")) {
            text = text + ": ";
        }
        label = text;
        final String sourceString = "<b>" + label + "</b> ";
        value.setText(Html.fromHtml(sourceString));

    }


    private String cutString(@NonNull String str, final int lineLength) {
        if (StringUtil.containsNotIgnoreCase(str, StringUtil.SPACE)) {
            return str;
        }
        String[] splited = str.split("<br>");
        if (splited[splited.length - 1].length() < lineLength) {
            return str;
        }
        final String newOne = splited[splited.length - 1];
        int cutLength = str.length() - newOne.length();
        final String thirtyChars = newOne.substring(0, lineLength);
        if (StringUtil.containsNotIgnoreCase(thirtyChars, StringUtil.SPACE) || thirtyChars.lastIndexOf(StringUtil.SPACE) < 10) {
            return str;
        }
        int spaceIndex = thirtyChars.lastIndexOf(StringUtil.SPACE);
        final String oneMore = newOne.substring(0, spaceIndex) + "<br>" + newOne.substring(spaceIndex);
        str = str.substring(0, cutLength) + oneMore;
        return cutString(str, lineLength);
    }

}
