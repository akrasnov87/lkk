package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.view.ViewCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class CicAutocompleteFieldView extends LinearLayout {

    private TextInputLayout inputLayout;
    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private CicAutoCompleteFieldAdapter adapter;
    private CicAutocompleteModel model;
    private CicOnAutoCompleteSelectedListener listener;
    private final int DROP_DOWN = 100;
    private final int CLEAR = 200;

    public CicAutocompleteFieldView(final @NonNull Context context) {
        super(context);
        inflate(context, R.layout.cic_autocomplete, this);
        inputLayout = findViewById(R.id.cic_auto_complete_input_layout);
        autoCompleteTextView = findViewById(R.id.cic_auto_complete_field_value);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            final Map<String, ?> map = adapter.getMapAtSpecificPosition(position);
            if (map == null) {
                return;
            }
            if (map.get(Names.NAME) instanceof String) {
                model.text = (String) map.get(Names.NAME);
                if (StringUtil.isEmpty(model.text)) {
                    model.text = StringUtil.EMPTY;
                }
                autoCompleteTextView.setText(model.text);
            }
            if (map.get(Names.ID) instanceof Long) {
                model.value = Long.toString(getLongOrZero(map.get(Names.ID)));
            } else {
                if (map.get(Names.ID) instanceof String) {
                    model.value = (String) map.get(Names.ID);
                } else {
                    model.value = StringUtil.EMPTY;
                }
            }
            model.isValueSelected = true;
            inputLayout.setEndIconDrawable(model.getClearResId());
            model.endIconState = CLEAR;
            if (listener != null) {
                listener.onAutoCompleteSelected(model.value, model.text);
            }
            setError(null);
            inputLayout.setHelperText(null);
        });

        inputLayout.setErrorIconOnClickListener(v -> {
            inputLayout.setEndIconDrawable(model.getClearResId());
            setError(null);
            autoCompleteTextView.showDropDown();
            model.endIconState = CLEAR;
            model.isValueSelected = false;
        });

        inputLayout.setEndIconOnClickListener(v -> {
            if (model.endIconState == CLEAR) {
                inputLayout.setEndIconDrawable(model.getDropDownResId());
                autoCompleteTextView.setText(StringUtil.EMPTY);
                model.isValueSelected = false;
                inputLayout.setHelperText(model.helperText);
                model.value = null;
                model.endIconState = DROP_DOWN;
                if (listener != null) {
                    listener.onAutoCompleteSelected(null, StringUtil.EMPTY);
                }
            } else {
                model.endIconState = CLEAR;
                inputLayout.setEndIconDrawable(model.getClearResId());
                autoCompleteTextView.showDropDown();
            }
        });
        inputLayout.setId(ViewCompat.generateViewId());
        autoCompleteTextView.setId(ViewCompat.generateViewId());
        model = new CicAutocompleteModel(false, R.drawable.drop_down_arrow_24, R.drawable.cic_clear, R.drawable.cic_error_icon);
    }

    public CicAutocompleteFieldView(final @NonNull Context context, final @Nullable AttributeSet attrs,
                                    final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.cic_autocomplete, this);
    }

    public CicAutocompleteFieldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.cic_autocomplete, this);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CicAutocompleteFieldView, 0, 0);
        final String fieldLabel = a.getString(R.styleable.CicAutocompleteFieldView_cicAutoCompleteLabel);
        final String helperText = a.getString(R.styleable.CicAutocompleteFieldView_cicAutoCompleteHelperText);
        final int dropDown = a.getResourceId(R.styleable.CicAutocompleteFieldView_cicAutoCompleteDropDownIcon, R.drawable.drop_down_arrow_24);
        final int clear = a.getResourceId(R.styleable.CicAutocompleteFieldView_cicAutoCompleteClearIcon, R.drawable.cic_clear);
        final int error = a.getResourceId(R.styleable.CicAutocompleteFieldView_cicAutoCompleteErrorIcon, R.drawable.cic_error_icon);
        a.recycle();

        inputLayout = findViewById(R.id.cic_auto_complete_input_layout);
        autoCompleteTextView = findViewById(R.id.cic_auto_complete_field_value);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            final Map<String, ?> map = adapter.getMapAtSpecificPosition(position);
            if (map == null) {
                return;
            }
            if (map.get(Names.NAME) instanceof String) {
                model.text = (String) map.get(Names.NAME);
                if (StringUtil.isEmpty(model.text)) {
                    model.text = StringUtil.EMPTY;
                }
                autoCompleteTextView.setText(model.text);
            }
            if (map.get(Names.ID) instanceof Long) {
                model.value = Long.toString(getLongOrZero(map.get(Names.ID)));
            } else {
                if (map.get(Names.ID) instanceof String) {
                    model.value = (String) map.get(Names.ID);
                } else {
                    model.value = StringUtil.EMPTY;
                }
            }
            model.isValueSelected = true;
            inputLayout.setEndIconDrawable(model.getClearResId());
            model.endIconState = CLEAR;
            if (listener != null) {
                listener.onAutoCompleteSelected(model.value, model.text);
            }
            setError(null);
            inputLayout.setHelperText(null);
        });

        inputLayout.setErrorIconOnClickListener(v -> {
            inputLayout.setEndIconDrawable(model.getClearResId());
            setError(null);
            autoCompleteTextView.showDropDown();
            model.endIconState = CLEAR;
            model.isValueSelected = false;
        });

        inputLayout.setEndIconOnClickListener(v -> {
            if (model.endIconState == CLEAR) {
                inputLayout.setEndIconDrawable(model.getDropDownResId());
                autoCompleteTextView.setText(StringUtil.EMPTY);
                model.isValueSelected = false;
                inputLayout.setHelperText(model.helperText);
                model.value = null;
                model.endIconState = DROP_DOWN;
                if (listener != null) {
                    listener.onAutoCompleteSelected(null, StringUtil.EMPTY);
                }
            } else {
                model.endIconState = CLEAR;
                inputLayout.setEndIconDrawable(model.getClearResId());
                autoCompleteTextView.showDropDown();
            }
        });
        inputLayout.setId(ViewCompat.generateViewId());
        autoCompleteTextView.setId(ViewCompat.generateViewId());
        model = new CicAutocompleteModel(false, dropDown, clear, error);
        inputLayout.setHint(fieldLabel);
        inputLayout.setHelperText(helperText);
    }

    public void setError(final @Nullable String error) {
        inputLayout.setError(error);
        if (error == null) {
            inputLayout.setErrorIconDrawable(null);
        } else {
            inputLayout.setErrorIconDrawable(model.getErrorResId());
        }
    }

    public void setError() {
        if (StringUtil.isEmpty(model.helperText)) {
            inputLayout.setError(getContext().getString(R.string.must_make_choise));
        } else {
            inputLayout.setError(model.helperText);
        }
        inputLayout.setErrorIconDrawable(model.getErrorResId());
    }

    public void clearError() {
        inputLayout.setError(null);
        inputLayout.setErrorIconDrawable(null);
    }

    public void setHelperText(final @Nullable String helperText) {
        model.helperText = helperText;
        inputLayout.setHelperText(model.helperText);
    }

    public void setFieldText(final @Nullable String text) {
        if (StringUtil.isEmpty(text)) {
            model.text = StringUtil.EMPTY;
        } else {
            model.text = text;
        }
        autoCompleteTextView.setText(model.text);
    }

    public void setDropDownIcon(final @DrawableRes int drawableResId) {
        model.dropDownResId = drawableResId;
    }

    public void setClearIcon(final @DrawableRes int drawableResId) {
        model.clearResId = drawableResId;
    }

    public void setErrorIcon(final @DrawableRes int drawableResId) {
        model.errorResId = drawableResId;
    }

    public void setFieldValue(final @Nullable Object value) {
        if (value == null) {
            model.value = null;
            model.text = null;
            autoCompleteTextView.setText(StringUtil.EMPTY);
            model.isValueSelected = false;
            return;
        }
        if (adapter != null) {
            final Map<String, ?> map = adapter.getMapById(value);
            if (map == null) {
                model.value = String.valueOf(value);
                return;
            }
            String val = null;
            if (map.get(Names.ID) instanceof Long) {
                val = Long.toString(getLongOrZero(map.get(Names.ID)));
            } else {
                if (map.get(Names.ID) instanceof String) {
                    val = (String) map.get(Names.ID);
                }
            }
            if (val != null && val.equals(value)) {
                model.value = val;
            }
            if (map.get(Names.NAME) instanceof String) {
                model.text = (String) map.get(Names.NAME);
            }
            model.isValueSelected = true;
            inputLayout.setEndIconDrawable(model.getClearResId());
            model.endIconState = CLEAR;
            inputLayout.setHelperText(null);
            autoCompleteTextView.setText(model.text);
        }
    }

    public void setAdapter(final @NonNull CicAutoCompleteFieldAdapter adapter) {
        this.adapter = adapter;
        autoCompleteTextView.setAdapter(adapter);
        setError(null);
    }

    public @NonNull
    String getValue() {
        if (StringUtil.isEmpty(autoCompleteTextView.getText())) {
            if (StringUtil.isEmpty(model.value)) {
                model.value = StringUtil.EMPTY;
                return model.value;
            }
        }
        String text = autoCompleteTextView.getText().toString();
        if (autoCompleteTextView.getText().length() > 0 && model.value == null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Map<String, ?> m = adapter.getMapAtSpecificPosition(i);
                if (m == null) {
                    continue;
                }
                if (m.get(Names.ID) == null) {
                    continue;
                }
                String result = null;
                if (m.get(Names.ID) instanceof String) {
                    result = (String) m.get(Names.NAME);
                }
                if (result == null) {
                    continue;
                }
                if (result.equals(text)) {
                    return result;
                }
            }
        }
        if (StringUtil.isEmpty(model.value)) {
            model.value = StringUtil.EMPTY;
        }
        return model.value;
    }

    public @NonNull
    String getFieldText() {
        if (StringUtil.isEmpty(model.text)) {
            model.text = StringUtil.EMPTY;
        }
        return model.text;
    }

    public boolean isSelected() {
        return model.isValueSelected && !StringUtil.isEmpty(model.value);
    }

    public void setCicOnAutoCompleteSelected(final @NonNull CicOnAutoCompleteSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (superState == null) {
            return new SavedState(new CicAutocompleteModel(false, android.R.drawable.btn_dropdown,
                    android.R.drawable.ic_menu_close_clear_cancel, android.R.drawable.stat_notify_error));
        }
        final SavedState ss = new SavedState(superState);
        ss.autocompleteModel = this.model;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(final @NonNull Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.model = ss.autocompleteModel;
        autoCompleteTextView.setText(model.text);
        inputLayout.setHint(model.label);
        inputLayout.setHelperText(model.helperText);
        if (model.endIconState == CLEAR) {
            inputLayout.setEndIconDrawable(model.getClearResId());
        }
    }

    private static class SavedState extends BaseSavedState {
        @Nullable
        private CicAutocompleteModel autocompleteModel;

        SavedState(final @NonNull Parcelable superState) {
            super(superState);
        }

        private SavedState(final @NonNull Parcel in) {
            super(in);
            this.autocompleteModel = in.readParcelable(CicAutocompleteModel.class.getClassLoader());

        }

        @Override
        public void writeToParcel(final @NonNull Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(autocompleteModel, 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @NonNull
                    public SavedState createFromParcel(final @NonNull Parcel in) {
                        return new SavedState(in);
                    }

                    @NonNull
                    public SavedState[] newArray(final int size) {
                        return new SavedState[size];
                    }
                };
    }

    public static class CicAutoCompleteFieldAdapter extends CicBaseSpinnerAdapter implements Filterable {

        private static final String[] from = {Names.NAME};
        private static final int[] to = {R.id.cic_simple_type_item_name};
        private ArrayList<Map<String, ?>> tempMaps;

        public CicAutoCompleteFieldAdapter(final @NonNull Context context) {
            super(context, new ArrayList<>(), from, to);
            tempMaps = new ArrayList<>();
        }

        public CicAutoCompleteFieldAdapter(final @NonNull Context context, final @NonNull ArrayList<Map<String, Object>> items) {
            super(context, items, from, to);
            mMaps = items;
            tempMaps = new ArrayList<>();
            tempMaps.addAll(mMaps);
        }

        public void updateItems(final @NonNull ArrayList<Map<String, Object>> items) {
            mMaps.clear();
            mMaps.addAll(items);
            tempMaps.clear();
            tempMaps.addAll(mMaps);
            notifyDataSetChanged();
        }

        public @Nullable
        Map<String, ?> getMapAtSpecificPosition(final int position) {
            if (getMaps().size() == 0) {
                return null;
            }
            if (position >= getMaps().size() || position < 0) {
                return null;
            }
            return getMaps().get(position);
        }

        public int getPositionById(final @NonNull String id) {
            if (getMaps().size() == 0) {
                return -1;
            }
            for (int i = 0; i < getCount(); i++) {
                Map<String, ?> m = getMaps().get(i);
                if (m == null) {
                    continue;
                }
                if (m.get(Names.ID) == null) {
                    continue;
                }
                String resultId = null;
                if (m.get(Names.ID) instanceof Long) {
                    resultId = Long.toString(getLongOrZero(m.get(Names.ID)));
                } else {
                    if (m.get(Names.ID) instanceof String) {
                        resultId = (String) m.get(Names.ID);
                    }
                }
                if (resultId != null && resultId.equals(id)) {
                    return i;
                }
            }
            return -1;
        }

        public @Nullable
        Map<String, ?> getMapById(final @NonNull Object id) {
            if (getMaps().size() == 0) {
                return null;
            }
            for (int i = 0; i < getCount(); i++) {
                final Map<String, ?> m = getMaps().get(i);
                if (m == null) {
                    continue;
                }
                if (m.get(Names.ID) == null) {
                    continue;
                }
                String resultId = null;
                if (m.get(Names.ID) instanceof Long) {
                    resultId = Long.toString(getLongOrZero(m.get(Names.ID)));
                } else {
                    if (m.get(Names.ID) instanceof String) {
                        resultId = (String) m.get(Names.ID);
                    }
                }
                if (resultId != null && resultId.equals(id)) {
                    return m;
                }
            }
            return null;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return cicAdapterFilter;
        }

        private final Filter cicAdapterFilter = new Filter() {
            @NonNull
            @Override
            public CharSequence convertResultToString(final @NonNull Object resultValue) {
                if (resultValue instanceof Map) {
                    final Map<?, ?> m = (Map<?, ?>) resultValue;
                    if (m.get(Names.ID) != null && m.get(Names.ID) instanceof CharSequence) {
                        final CharSequence sequence = (CharSequence) m.get(Names.ID);
                        if (StringUtil.isNotEmpty(sequence)) {
                            return sequence;
                        }
                    }
                }
                return StringUtil.EMPTY;
            }

            @NonNull
            @Override
            protected FilterResults performFiltering(final @NonNull CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (tempMaps == null) {
                    tempMaps = new ArrayList<>(getMaps());
                }
                if (StringUtil.isEmpty(constraint)) {
                    final ArrayList<Map<String, ?>> list = tempMaps;
                    results.values = list;
                    results.count = list.size();
                } else {
                    final List<Map<String, ?>> suggestions = new ArrayList<>();
                    for (Map<String, ?> m : tempMaps) {
                        final String valueText = (String) m.get(Names.NAME);
                        if (StringUtil.containsIgnoreCase(valueText, constraint)) {
                            suggestions.add(m);
                        }
                    }
                    results.values = suggestions;
                    results.count = suggestions.size();
                }
                return results;
            }

            @Override
            protected void publishResults(final @NonNull CharSequence constraint, final @NonNull FilterResults results) {
                if (results.values instanceof List) {
                    try {
                        final List<Map<String, Object>> maps = (List<Map<String, Object>>) results.values;
                        mMaps.clear();
                        mMaps.addAll(maps);
                        if (results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private static class CicAutocompleteModel implements Parcelable {

        private int endIconState;
        private boolean isValueSelected;
        private String label;
        private String helperText;
        private String value;
        private String text;
        private @DrawableRes
        int dropDownResId;
        private @DrawableRes
        int clearResId;
        private @DrawableRes
        int errorResId;

        public CicAutocompleteModel(final boolean isValueSelected, final @DrawableRes int dropDownResId,
                                    final @DrawableRes int clearResId, final @DrawableRes int errorResId) {
            endIconState = 100;
            this.isValueSelected = isValueSelected;
            this.dropDownResId = dropDownResId;
            this.clearResId = clearResId;
            this.errorResId = errorResId;
        }

        public int getDropDownResId() {
            if (dropDownResId == 0) {
                return android.R.drawable.btn_dropdown;
            }
            return dropDownResId;
        }

        public int getClearResId() {
            if (clearResId == 0) {
                return android.R.drawable.ic_menu_close_clear_cancel;
            }
            return clearResId;
        }

        public int getErrorResId() {
            if (errorResId == 0) {
                return android.R.drawable.stat_notify_error;
            }
            return errorResId;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final @NonNull Parcel dest, final int flags) {
            dest.writeInt(this.endIconState);
            dest.writeByte(this.isValueSelected ? (byte) 1 : (byte) 0);
            dest.writeString(this.label);
            dest.writeString(this.helperText);
            dest.writeString(this.value);
            dest.writeString(this.text);
            dest.writeInt(this.dropDownResId);
            dest.writeInt(this.clearResId);
            dest.writeInt(this.errorResId);
        }

        public void readFromParcel(final @NonNull Parcel source) {
            this.endIconState = source.readInt();
            this.isValueSelected = source.readByte() != 0;
            this.label = StringUtil.defaultEmptyString(source.readString());
            this.helperText = StringUtil.defaultEmptyString(source.readString());
            this.value = StringUtil.defaultEmptyString(source.readString());
            this.text = StringUtil.defaultEmptyString(source.readString());
            this.dropDownResId = source.readInt();
            this.clearResId = source.readInt();
            this.errorResId = source.readInt();
        }

        protected CicAutocompleteModel(final @NonNull Parcel in) {
            this.endIconState = in.readInt();
            this.isValueSelected = in.readByte() != 0;
            this.label = StringUtil.defaultEmptyString(in.readString());
            this.helperText = StringUtil.defaultEmptyString(in.readString());
            this.value = StringUtil.defaultEmptyString(in.readString());
            this.text = StringUtil.defaultEmptyString(in.readString());
            this.dropDownResId = in.readInt();
            this.clearResId = in.readInt();
            this.errorResId = in.readInt();
        }

        public static final Creator<CicAutocompleteModel> CREATOR = new Creator<CicAutocompleteModel>() {
            @NonNull
            @Override
            public CicAutocompleteModel createFromParcel(final @NonNull Parcel source) {
                return new CicAutocompleteModel(source);
            }

            @NonNull
            @Override
            public CicAutocompleteModel[] newArray(final int size) {
                return new CicAutocompleteModel[size];
            }
        };
    }

    public interface CicOnAutoCompleteSelectedListener {
        void onAutoCompleteSelected(final @Nullable String value, final @NonNull String text);
    }

    /**
     * Функции для безопасной обработки Long
     */
    private static long getLongOrMinus(@Nullable Object value) {
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

    private static long getLongOrZero(@Nullable Object value) {
        if (getLongOrMinus(value) < 0) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0L;
        }
    }

}
