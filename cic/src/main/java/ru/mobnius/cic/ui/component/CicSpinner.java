package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SimpleAdapter;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.mobnius.cic.Names;
import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class CicSpinner extends LinearLayoutCompat {

    private TextInputLayout inputLayout;
    private AppCompatAutoCompleteTextView autocompleteTextView;
    private List<? extends Map<String, ?>> data;
    private OnCicSpinnerIconSelectedListener iconListener;
    private CicSpinnerModel cicSpinnerModel;
    private final int DROP_DOWN = 100;
    private final int CLEAR = 200;

    public CicSpinner(final @NonNull Context context) {
        super(context);
        inflate(context, R.layout.cic_spinner, this);
        setWillNotDraw(false);
        inputLayout = findViewById(R.id.cic_spinner_input_layout);
        inputLayout.setHintAnimationEnabled(false);
        autocompleteTextView = findViewById(R.id.cic_spinner_field_value);
        autocompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            if (data == null || data.size() <= position) {
                return;
            }
            if (data.get(position).get(Names.NAME) instanceof String) {
                String text = (String) data.get(position).get(Names.NAME);
                inputLayout.setHelperText(null);
                autocompleteTextView.setText(text, false);
                cicSpinnerModel.value = text;
                if (iconListener != null) {
                    iconListener.onCicIconSelected(data.get(position), getSpinnerId(position));
                }
            }
            setError(null);
            if (!cicSpinnerModel.isDefaultChoise) {
                cicSpinnerModel.isValueSelected = true;
                cicSpinnerModel.endIconState = CLEAR;
                inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
            }
        });

        inputLayout.setErrorIconOnClickListener(v -> {
            if (!isEnabled()) {
                return;
            }
            inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
            setError(null);
            autocompleteTextView.showDropDown();
            cicSpinnerModel.endIconState = CLEAR;
            cicSpinnerModel.isValueSelected = false;
        });

        inputLayout.setEndIconOnClickListener(v -> {
            if (!isEnabled()) {
                return;
            }
            if (cicSpinnerModel.isDefaultChoise) {
                autocompleteTextView.showDropDown();
                return;
            }
            if (cicSpinnerModel.endIconState == CLEAR) {
                inputLayout.setEndIconDrawable(cicSpinnerModel.getDropDownResId());
                cicSpinnerModel.endIconState = DROP_DOWN;
                autocompleteTextView.setText("", false);
                inputLayout.setHelperText(cicSpinnerModel.helperText);
                cicSpinnerModel.isValueSelected = false;
                cicSpinnerModel.value = null;
                if (iconListener != null) {
                    iconListener.onCicIconSelected(null, -1);
                }
                return;
            }
            if (cicSpinnerModel.endIconState == DROP_DOWN) {
                inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
                cicSpinnerModel.endIconState = CLEAR;
                autocompleteTextView.showDropDown();

            }
        });

        inputLayout.setId(ViewCompat.generateViewId());
        autocompleteTextView.setId(ViewCompat.generateViewId());
        cicSpinnerModel = new CicSpinnerModel(false, false,
                false, StringUtil.EMPTY, StringUtil.EMPTY,
                null, R.drawable.drop_down_arrow_24,
                R.drawable.cic_clear,
                R.drawable.cic_error_icon,
                R.color.colorHintDark);
    }

    public CicSpinner(final @NonNull Context context, final @Nullable AttributeSet attrs,
                      final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.cic_spinner, this);
        setWillNotDraw(false);
    }

    public CicSpinner(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.cic_spinner, this);
        setWillNotDraw(false);
        inputLayout = findViewById(R.id.cic_spinner_input_layout);
        autocompleteTextView = findViewById(R.id.cic_spinner_field_value);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CicSpinner, 0, 0);
        final String labelText = a.getString(R.styleable.CicSpinner_cicSpinnerLabel);
        final String helperText = a.getString(R.styleable.CicSpinner_cicSpinnerHelperText);
        final int dropDown = a.getResourceId(R.styleable.CicSpinner_cicSpinnerDropDownIcon, R.drawable.drop_down_arrow_24);
        final int clear = a.getResourceId(R.styleable.CicSpinner_cicSpinnerClearIcon, R.drawable.cic_clear);
        final int error = a.getResourceId(R.styleable.CicSpinner_cicSpinnerErrorIcon, R.drawable.cic_error_icon);
        final int dropColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dropColor = a.getColor(R.styleable.CicSpinner_CicSpinnerDropIconColor, getResources().getColor(R.color.colorHintDark, null));
        } else {
            dropColor = a.getColor(R.styleable.CicSpinner_CicSpinnerDropIconColor, Color.BLACK);
        }
        if (a.hasValue(R.styleable.CicSpinner_cicSpinnerTextColor)) {
            autocompleteTextView.setTextColor(a.getColor(R.styleable.CicSpinner_cicSpinnerTextColor, ContextCompat.getColor(context, R.color.colorPrimaryText)));
        }
        final boolean boxBackgroundMode = a.getBoolean(R.styleable.CicSpinner_cicSpinnerBoxBackgroundNone, false);
        a.recycle();

        if (boxBackgroundMode) {
            autocompleteTextView.setOnClickListener(view -> {
                if (!isEnabled()) {
                    return;
                }
                if (cicSpinnerModel.isDefaultChoise) {
                    autocompleteTextView.showDropDown();
                    return;
                }
                if (cicSpinnerModel.endIconState == CLEAR) {
                    inputLayout.setEndIconDrawable(cicSpinnerModel.getDropDownResId());
                    cicSpinnerModel.endIconState = DROP_DOWN;
                    autocompleteTextView.setText("", false);
                    inputLayout.setHelperText(cicSpinnerModel.helperText);
                    cicSpinnerModel.isValueSelected = false;
                    cicSpinnerModel.value = null;
                    if (iconListener != null) {
                        iconListener.onCicIconSelected(null, -1);
                    }
                    return;
                }
                if (cicSpinnerModel.endIconState == DROP_DOWN) {
                    inputLayout.setEndIconDrawable(cicSpinnerModel.clearResId);
                    inputLayout.setEndIconTintList(ColorStateList.valueOf(cicSpinnerModel.dropColor));
                    cicSpinnerModel.endIconState = CLEAR;
                    autocompleteTextView.showDropDown();

                }
            });
        }
        autocompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            if (data == null || data.size() <= position) {
                return;
            }
            if (data.get(position).get(Names.NAME) instanceof String) {
                final String text = (String) data.get(position).get(Names.NAME);
                inputLayout.setHelperText(null);
                autocompleteTextView.setText(text, false);
                cicSpinnerModel.value = text;
                if (iconListener != null) {
                    iconListener.onCicIconSelected(data.get(position), getSpinnerId(position));
                }
            }
            setError(null);
            if (!cicSpinnerModel.isDefaultChoise) {
                cicSpinnerModel.isValueSelected = true;
                cicSpinnerModel.endIconState = CLEAR;
                inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
            }
        });
        if (!boxBackgroundMode) {
            inputLayout.setErrorIconOnClickListener(v -> {
                if (!isEnabled()) {
                    return;
                }
                inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
                setError(null);
                autocompleteTextView.showDropDown();
                cicSpinnerModel.endIconState = CLEAR;
                cicSpinnerModel.isValueSelected = false;
            });

            inputLayout.setEndIconOnClickListener(v -> {
                if (!isEnabled()) {
                    return;
                }
                if (cicSpinnerModel.isDefaultChoise) {
                    autocompleteTextView.showDropDown();
                    return;
                }
                if (cicSpinnerModel.endIconState == CLEAR) {
                    inputLayout.setEndIconDrawable(cicSpinnerModel.getDropDownResId());
                    cicSpinnerModel.endIconState = DROP_DOWN;
                    autocompleteTextView.setText("", false);
                    inputLayout.setHelperText(cicSpinnerModel.helperText);
                    cicSpinnerModel.isValueSelected = false;
                    cicSpinnerModel.value = null;
                    if (iconListener != null) {
                        iconListener.onCicIconSelected(null, -1);
                    }
                    return;
                }
                if (cicSpinnerModel.endIconState == DROP_DOWN) {
                    inputLayout.setEndIconDrawable(cicSpinnerModel.clearResId);
                    inputLayout.setEndIconTintList(ColorStateList.valueOf(cicSpinnerModel.dropColor));
                    cicSpinnerModel.endIconState = CLEAR;
                    autocompleteTextView.showDropDown();

                }
            });
        }
        inputLayout.setId(ViewCompat.generateViewId());
        autocompleteTextView.setId(ViewCompat.generateViewId());

        cicSpinnerModel = new CicSpinnerModel(false, false, boxBackgroundMode,
                StringUtil.defaultEmptyString(labelText),
                StringUtil.defaultEmptyString(helperText),
                null, dropDown, clear, error, dropColor);
        if (boxBackgroundMode) {
            autocompleteTextView.setBackground(null);
            inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
        }
        inputLayout.setEndIconDrawable(dropDown);
        if (boxBackgroundMode) {
            inputLayout.setEndIconTintList(ColorStateList.valueOf(dropColor));
        }
        inputLayout.setHelperText(helperText);
        inputLayout.setHint(labelText);

    }

    public void setSpinnerLabel(final @Nullable String label) {
        cicSpinnerModel.label = label;
        inputLayout.setHint(cicSpinnerModel.label);
    }

    public void setHelperText(final @Nullable String helperText) {
        cicSpinnerModel.helperText = helperText;
        inputLayout.setHelperText(cicSpinnerModel.helperText);
    }

    public void setDropDownIcon(final int drawableResId) {
        cicSpinnerModel.dropDownResId = drawableResId;
    }

    public void setClearIcon(final int drawableResId) {
        cicSpinnerModel.clearResId = drawableResId;
    }

    public void setErrorIcon(final int drawableResId) {
        cicSpinnerModel.errorResId = drawableResId;
    }

    public void setData(final @NonNull ArrayList<Map<String, Object>> data, final boolean defaulChoise) {
        cicSpinnerModel.isDefaultChoise = defaulChoise;
        this.data = data;
        final ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < this.data.size(); i++) {
            if (this.data.get(i).get(Names.NAME) instanceof String) {
                names.add((String) this.data.get(i).get(Names.NAME));
            }
        }
        final ArrayAdapter<String> adapter = new CicArrayAdapter<>(getContext(),
                R.layout.cic_spinner_drop_down_item, R.id.cic_spinner_drop_down_text_view, names);
        autocompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setError(null);
    }

    @NonNull
    public String getCurrentSelection() {
        if (data == null || data.size() == 0) {
            return StringUtil.EMPTY;
        }
        if (StringUtil.isEmpty(cicSpinnerModel.value)) {
            return StringUtil.EMPTY;
        }
        for (int i = 0; i < data.size(); i++) {
            try {
                final Map<String, Object> map = (Map<String, Object>) data.get(i);
                final String value = (String) map.get(Names.NAME);
                if (StringUtil.equals(value, cicSpinnerModel.value)) {
                    return cicSpinnerModel.value;
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
        return StringUtil.EMPTY;
    }

    public void setCicSimpleAdapter(final @NonNull CicSimpleAdapter adapter, final boolean defaultChoise) {
        cicSpinnerModel.isDefaultChoise = defaultChoise;
        autocompleteTextView.setAdapter(adapter);
        data = adapter.getData();
    }

    public int getPositionById(final @Nullable Long id) {
        if (id == null || id < 0) {
            return -1;
        }
        for (int i = 0; i < data.size(); i++) {
            if (id == getLongOrMinus(data.get(i).get(Names.ID))) {
                return i;
            }
        }
        return -1;
    }

    public int getPositionByName(final @Nullable String name) {
        if (name == null) {
            return -1;
        }
        for (int i = 0; i < data.size(); i++) {
            if (name.equals(data.get(i).get(Names.NAME))) {
                return i;
            }
        }
        return -1;
    }

    public boolean isSelected() {
        if (cicSpinnerModel.isDefaultChoise) {
            return true;
        }
        return cicSpinnerModel.isValueSelected;
    }

    public void setError(final @Nullable String error) {
        inputLayout.setError(error);
        if (error == null) {
            inputLayout.setErrorIconDrawable(null);
        } else {
            inputLayout.setErrorIconDrawable(cicSpinnerModel.getErrorResId());
        }
    }

    public void setError() {
        inputLayout.setError(cicSpinnerModel.helperText);
        inputLayout.setErrorIconDrawable(cicSpinnerModel.getErrorResId());
    }

    public void clearError() {
        inputLayout.setError(null);
        inputLayout.setErrorIconDrawable(null);
    }

    public void setEmptyBackgroundMode() {
        cicSpinnerModel.boxBackgroundMode = true;
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
    }


    public void setSelection(final int position) {
        if (position < 0) {
            return;
        }
        if (autocompleteTextView == null) {
            return;
        }
        if (data == null || data.size() <= position) {
            return;
        }
        if (data.get(position).get(Names.NAME) instanceof String) {
            final String text = (String) data.get(position).get(Names.NAME);
            cicSpinnerModel.value = text;
            cicSpinnerModel.isValueSelected = true;
            if (!cicSpinnerModel.isDefaultChoise) {
                cicSpinnerModel.endIconState = CLEAR;
                inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
            }
            inputLayout.setHelperText(null);
            autocompleteTextView.setText(text, false);
            if (iconListener != null) {
                iconListener.onCicIconSelected(data.get(position), getSpinnerId(position));
            }
        }
    }

    public void setSelectionById(final long id) {
        if (id < 0) {
            return;
        }
        if (autocompleteTextView == null) {
            return;
        }
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            final Map<String, ?> map = data.get(i);
            if (map.get(Names.ID) instanceof Long) {
                Long realId = (Long) map.get(Names.ID);
                if (realId != null && realId == id) {
                    final String text = (String) map.get(Names.NAME);
                    cicSpinnerModel.value = text;
                    cicSpinnerModel.isValueSelected = true;
                    if (!cicSpinnerModel.isDefaultChoise) {
                        cicSpinnerModel.endIconState = CLEAR;
                        inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
                    }
                    inputLayout.setHelperText(null);
                    autocompleteTextView.setText(text, false);
                    if (iconListener != null) {
                        iconListener.onCicIconSelected(data.get(i), getSpinnerId(i));
                    }
                    break;
                }
            }
        }
    }


    public void setOnCicIconSelectedListener(final @NonNull OnCicSpinnerIconSelectedListener listener) {
        this.iconListener = listener;
    }


    @Override
    @NonNull
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (superState == null) {
            return new SavedState(new CicSpinnerModel(false, false, false,
                    StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, android.R.drawable.btn_dropdown,
                    android.R.drawable.ic_menu_close_clear_cancel, android.R.drawable.stat_notify_error,
                    android.R.color.darker_gray));
        }
        final SavedState ss = new SavedState(superState);
        ss.model = this.cicSpinnerModel;
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
        this.cicSpinnerModel = ss.model;
        autocompleteTextView.setText(cicSpinnerModel.value, false);
        inputLayout.setHint(cicSpinnerModel.label);
        inputLayout.setHelperText(cicSpinnerModel.helperText);
        if (cicSpinnerModel.endIconState == CLEAR) {
            inputLayout.setEndIconDrawable(cicSpinnerModel.getClearResId());
        }
        if (cicSpinnerModel.boxBackgroundMode) {
            inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
            inputLayout.setEndIconDrawable(cicSpinnerModel.dropDownResId);
        }
        inputLayout.setEndIconTintList(ColorStateList.valueOf(cicSpinnerModel.dropColor));
    }

    private long getSpinnerId(final int position) {
        if (data == null) {
            return -1L;
        }
        return getLongOrMinus(data.get(position).get(Names.ID));
    }

    public void clearSelection() {
        inputLayout.setEndIconDrawable(cicSpinnerModel.getDropDownResId());
        cicSpinnerModel.endIconState = DROP_DOWN;
        autocompleteTextView.setText("", false);
        inputLayout.setHelperText(cicSpinnerModel.helperText);
        cicSpinnerModel.isValueSelected = false;
        cicSpinnerModel.value = null;
    }


    private static class SavedState extends BaseSavedState {
        private CicSpinnerModel model;


        SavedState(final @NonNull CicSpinnerModel cicSpinnerModel) {
            super(cicSpinnerModel);
        }

        SavedState(final @NonNull Parcelable superState) {
            super(superState);
        }

        private SavedState(final @NonNull Parcel in) {
            super(in);
            this.model = in.readParcelable(CicSpinnerModel.class.getClassLoader());
        }

        @Override
        public void writeToParcel(final @NonNull Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(model, 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public @NonNull
                    SavedState createFromParcel(final @NonNull Parcel in) {
                        return new SavedState(in);
                    }

                    public @NonNull
                    SavedState[] newArray(final int size) {
                        return new SavedState[size];
                    }
                };
    }

    private static class CicArrayAdapter<T> extends ArrayAdapter<T> {
        private final Filter filter = new NoFilter();
        private final List<T> items;

        private CicArrayAdapter(final @NonNull Context context, final int textViewResourceId,
                                final int textViewId, final @NonNull List<T> objects) {
            super(context, textViewResourceId, textViewId, objects);
            items = objects;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return filter;
        }

        private class NoFilter extends Filter {
            @Override
            @NonNull
            protected FilterResults performFiltering(final @Nullable CharSequence arg0) {
                FilterResults result = new FilterResults();
                result.values = items;
                result.count = items.size();
                return result;
            }

            @Override
            protected void publishResults(final @Nullable CharSequence arg0, final @NonNull FilterResults arg1) {
                notifyDataSetChanged();
            }
        }
    }

    public static class CicSimpleAdapter extends SimpleAdapter {
        private final Filter filter = new NoFilter();
        private final List<? extends Map<String, ?>> items;

        public CicSimpleAdapter(final @NonNull Context context,
                                final @NonNull List<? extends Map<String, ?>> data,
                                final int resource, String[] from, final int[] to) {
            super(context, data, resource, from, to);
            items = data;
        }

        public @NonNull
        List<? extends Map<String, ?>> getData() {
            if (items == null) {
                return new ArrayList<>();
            }
            return items;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return filter;
        }

        private class NoFilter extends Filter {
            @NonNull
            @Override
            protected FilterResults performFiltering(final @Nullable CharSequence arg0) {
                FilterResults result = new FilterResults();
                result.values = items;
                result.count = items.size();
                return result;
            }

            @Override
            protected void publishResults(final @Nullable CharSequence arg0, final @NonNull FilterResults arg1) {
                notifyDataSetChanged();
            }
        }
    }

    private static class CicSpinnerModel implements Parcelable {

        private int endIconState;
        private boolean isValueSelected;
        private boolean isDefaultChoise;
        private boolean boxBackgroundMode;
        private String label;
        private String helperText;
        private String value;
        private int dropDownResId;
        private int clearResId;
        private int errorResId;
        private int dropColor;

        public CicSpinnerModel(final boolean isValueSelected,
                               final boolean isDefaultChoise,
                               final boolean boxBackgroundMode,
                               final @NonNull String label,
                               final @NonNull String helperText,
                               final @Nullable String value,
                               final int dropDownResId,
                               final int clearResId,
                               final int errorResId,
                               final int dropColor) {
            endIconState = 100;
            this.isValueSelected = isValueSelected;
            this.isDefaultChoise = isDefaultChoise;
            this.boxBackgroundMode = boxBackgroundMode;
            this.label = label;
            this.helperText = helperText;
            this.value = value;
            this.dropDownResId = dropDownResId;
            this.clearResId = clearResId;
            this.errorResId = errorResId;
            this.dropColor = dropColor;
        }

        public @DrawableRes
        int getDropDownResId() {
            if (dropDownResId == 0) {
                return android.R.drawable.btn_dropdown;
            }
            return dropDownResId;
        }

        public @DrawableRes
        int getClearResId() {
            if (clearResId == 0) {
                return android.R.drawable.ic_menu_close_clear_cancel;
            }
            return clearResId;
        }

        public @DrawableRes
        int getErrorResId() {
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
            dest.writeByte(this.isDefaultChoise ? (byte) 1 : (byte) 0);
            dest.writeByte(this.boxBackgroundMode ? (byte) 1 : (byte) 0);
            dest.writeString(this.label);
            dest.writeString(this.helperText);
            dest.writeString(this.value);
            dest.writeInt(this.dropDownResId);
            dest.writeInt(this.clearResId);
            dest.writeInt(this.errorResId);
            dest.writeInt(this.dropColor);
        }

        public void readFromParcel(final @NonNull Parcel source) {
            this.endIconState = source.readInt();
            this.isValueSelected = source.readByte() != 0;
            this.isDefaultChoise = source.readByte() != 0;
            this.boxBackgroundMode = source.readByte() != 0;
            this.label = source.readString();
            this.helperText = source.readString();
            this.value = source.readString();
            this.dropDownResId = source.readInt();
            this.clearResId = source.readInt();
            this.errorResId = source.readInt();
            this.dropColor = source.readInt();
        }

        protected CicSpinnerModel(final @NonNull Parcel in) {
            this.endIconState = in.readInt();
            this.isValueSelected = in.readByte() != 0;
            this.isDefaultChoise = in.readByte() != 0;
            this.boxBackgroundMode = in.readByte() != 0;
            this.label = in.readString();
            this.helperText = in.readString();
            this.value = in.readString();
            this.dropDownResId = in.readInt();
            this.clearResId = in.readInt();
            this.errorResId = in.readInt();
            this.dropColor = in.readInt();
        }

        public static final Creator<CicSpinnerModel> CREATOR = new Creator<CicSpinnerModel>() {
            @NonNull
            @Override
            public CicSpinnerModel createFromParcel(final @NonNull Parcel source) {
                return new CicSpinnerModel(source);
            }

            @NonNull
            @Override
            public CicSpinnerModel[] newArray(final int size) {
                return new CicSpinnerModel[size];
            }
        };
    }

    public interface OnCicSpinnerIconSelectedListener {
        void onCicIconSelected(final @Nullable Map<String, ?> map, final long id);
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
