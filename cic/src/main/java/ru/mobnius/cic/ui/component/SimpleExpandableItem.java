package ru.mobnius.cic.ui.component;

import androidx.annotation.NonNull;

import java.util.Date;

import ru.mobnius.cic.ui.component.ExpandableTextLayout;
import ru.mobnius.simple_core.utils.DateUtil;

public class SimpleExpandableItem
        implements ExpandableTextLayout.OnExpandableItem {
    @NonNull
    private final String field;
    @NonNull
    private final String value;

    public SimpleExpandableItem(final @NonNull String field, final @NonNull String value) {
        this.field = field;
        this.value = value;
    }

    public SimpleExpandableItem(final @NonNull String field, final @NonNull Date value) {
        this.field = field;
        this.value = DateUtil.getNonNullDateTextMiddle(value);
    }

    @NonNull
    @Override
    public String toHtml() {
        return String.format("<b>%s</b><br />%s", field, value);
    }

    @NonNull
    @Override
    public String toAltHtml() {
        return String.format("<b>%s</b>: %s</br>", field, value);
    }
}
