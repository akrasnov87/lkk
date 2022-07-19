package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.R;

public abstract class CicBaseSpinnerAdapter extends SimpleAdapter {
    protected ArrayList<Map<String, Object>> mMaps;

    public CicBaseSpinnerAdapter(final @NonNull Context context, final @NonNull ArrayList<Map<String, Object>> items,
                                 final @NonNull String[] from, final int[] to) {
        super(context, items, R.layout.cic_simple_type_item, from, to);
        mMaps = items;
    }

    public long getId(final int position) {
        HashMap<?, ?> m = (HashMap<?, ?>) getItem(position);
        return getLongOrMinus(m.get(Names.ID));
    }

    public @NonNull
    ArrayList<Map<String, Object>> getMaps() {
        if (mMaps == null) {
            return new ArrayList<>();
        }
        return mMaps;
    }

    protected void addItem(final long id, final @NonNull String name) {
        Map<String, Object> m = new HashMap<>();
        m.put(Names.NAME, name);
        m.put(Names.ID, id);

        getMaps().add(m);
    }

    protected void addItem(final @NonNull String id, final @NonNull String name) {
        Map<String, Object> m = new HashMap<>();
        m.put(Names.NAME, name);
        m.put(Names.ID, id);

        getMaps().add(m);
    }

    private long getLongOrMinus(@Nullable Object value) {
        if (value == null) {
            return -1L;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1L;
        }
    }

}

