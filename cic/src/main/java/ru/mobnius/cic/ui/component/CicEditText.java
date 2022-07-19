package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.SimpleTextWatcher;
import ru.mobnius.simple_core.utils.StringUtil;

public class CicEditText extends LinearLayoutCompat {
    private TextInputLayout inputLayout;
    private TextInputEditText editText;
    private CicTextChangedListener textChangedListener;
    private CicMetersUpdateListener meterUpdateListener;
    private CicNewMetersUpdateListener newMetersUpdateListener;
    private CicEditTextModel model;

    public CicEditText(final @NonNull Context context) {
        super(context);
        inflate(context, R.layout.cic_edit_text, this);
        setWillNotDraw(false);
        inputLayout = findViewById(R.id.cic_edit_input_layout);
        inputLayout.setHintAnimationEnabled(false);
        editText = findViewById(R.id.cic_edit_text_field);
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                setError(null);
                setSoftError(null);

                if (StringUtil.isEmpty(s)) {
                    if (getContext().getString(R.string.must_to_be_not_empty).equals(model.helperText)) {
                        setHelperText(model.helperText);
                    }
                }

                if (StringUtil.isNotEmpty(s) &&
                        getContext().getString(R.string.must_to_be_not_empty).equals(model.helperText)) {
                        inputLayout.setHelperText(null);
                }

                if (StringUtil.isNotEmpty(s)&&s.length()>=model.maxLength){
                    inputLayout.setHelperText(context.getString(R.string.max_length_reached, model.maxLength));
                }

                if (s == null) {
                    model.value = null;
                } else {
                    model.value = s.toString();
                }
                if (meterUpdateListener != null) {
                    meterUpdateListener.onMeterUpdated(model.value, model.meterId);
                }
                if (newMetersUpdateListener != null &&
                        StringUtil.isNotEmpty(model.newReadingsName) &&
                        StringUtil.isNotEmpty(model.tariffZoneConst)) {
                    newMetersUpdateListener.onNewMeterUpdated(s, model.newReadingsName, model.tariffZoneConst);
                }
                if (textChangedListener == null) {
                    return;
                }
                textChangedListener.afterTextChanged(s);
            }
        });
        inputLayout.setId(ViewCompat.generateViewId());
        editText.setId(ViewCompat.generateViewId());

        model = new CicEditTextModel(null, StringUtil.EMPTY,
                StringUtil.EMPTY, null, 100,
                R.drawable.cic_error_icon, R.drawable.cic_warning_icon);
    }

    public CicEditText(final @NonNull Context context, final @Nullable AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.cic_edit_text, this);
        setWillNotDraw(false);
    }

    public CicEditText(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.cic_edit_text, this);
        setWillNotDraw(false);
        inputLayout = findViewById(R.id.cic_edit_input_layout);
        editText = findViewById(R.id.cic_edit_text_field);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CicEditText, 0, 0);
        final String labelText = StringUtil.defaultEmptyString(a.getString(R.styleable.CicEditText_cicEditTextLabel));
        final String helperText = StringUtil.defaultEmptyString(a.getString(R.styleable.CicEditText_cicEditTextHelper));
        final int maxLength = a.getInt(R.styleable.CicEditText_cicEditTextMaxLength, 100);
        final boolean isDecimalFormat = a.getBoolean(R.styleable.CicEditText_cicEditTextDecimalFormat, false);
        final boolean isDateFormat = a.getBoolean(R.styleable.CicEditText_cicEditTextDateFormat, false);
        final boolean isSingleLine = a.getBoolean(R.styleable.CicEditText_cicEditTextSingleLine, false);
        a.recycle();
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                setError(null);
                setSoftError(null);

                if (StringUtil.isEmpty(s)) {
                    if (getContext().getString(R.string.must_to_be_not_empty).equals(model.helperText)) {
                        setHelperText(model.helperText);
                    }
                }

                if (StringUtil.isNotEmpty(s) &&
                        getContext().getString(R.string.must_to_be_not_empty).equals(model.helperText)) {
                        inputLayout.setHelperText(null);
                }

                if (StringUtil.isNotEmpty(s)&&s.length()>=model.maxLength){
                    inputLayout.setHelperText(context.getString(R.string.max_length_reached, model.maxLength));
                }

                if (s == null) {
                    model.value = null;
                } else {
                    model.value = s.toString();
                }
                if (meterUpdateListener != null) {
                    meterUpdateListener.onMeterUpdated(model.value, model.meterId);
                }
                if (newMetersUpdateListener != null &&
                        StringUtil.isNotEmpty(model.newReadingsName) &&
                        StringUtil.isNotEmpty(model.tariffZoneConst)) {
                    newMetersUpdateListener.onNewMeterUpdated(s, model.newReadingsName, model.tariffZoneConst);
                }
                if (textChangedListener == null) {
                    return;
                }
                textChangedListener.afterTextChanged(s);
            }
        });
        inputLayout.setId(ViewCompat.generateViewId());
        editText.setId(ViewCompat.generateViewId());
        model = new CicEditTextModel(null, labelText,
                helperText, null, maxLength,
                R.drawable.cic_error_icon, R.drawable.cic_warning_icon);
        inputLayout.setHint(labelText);
        inputLayout.setHelperText(helperText);
        final InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(model.maxLength);
        editText.setFilters(fArray);
        if (isDecimalFormat) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        if (isDateFormat) {
            editText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        }
        editText.setSingleLine(isSingleLine);
    }

    public @NonNull
    TextInputEditText getEditText() {
        return editText;
    }

    public void setText(final @Nullable String text) {
        if (StringUtil.isEmpty(text)) {
            editText.setText(StringUtil.EMPTY);
            return;
        }
        editText.setText(text);
    }

    public void setLabel(final @Nullable String textLabel) {
        if (StringUtil.isEmpty(textLabel)) {
            return;
        }
        inputLayout.setHint(textLabel);
    }

    public void addCicTextChangedListener(final @NonNull CicTextChangedListener listener) {
        this.textChangedListener = listener;
    }

    public void addCicMeterUpdateListener(final @NonNull CicMetersUpdateListener listener, final @Nullable String someId) {
        this.meterUpdateListener = listener;
        model.meterId = someId;
    }

    public void addNewCicValueUpdateListener(final @NonNull CicNewMetersUpdateListener listener,
                                             final @NonNull String readingsName, final @NonNull String tariffZoneConst) {
        this.newMetersUpdateListener = listener;
        model.tariffZoneConst = tariffZoneConst;
        model.newReadingsName = readingsName;
    }

    public void setError(final @Nullable String error) {
        inputLayout.setErrorTextColor(ColorStateList.valueOf
                (ResourcesCompat.getColor(getResources(), R.color.colorSecondaryAccent, null)));
        inputLayout.setError(error);
        if (error == null) {
            inputLayout.setErrorIconDrawable(null);
            model.isError = false;
        } else {
            model.isError = true;
            inputLayout.setErrorIconDrawable(model.errorResId);
        }
    }

    public void setSoftError(final @Nullable String softError) {
        inputLayout.setError(softError);
        if (softError == null) {
            inputLayout.setErrorTextColor(ColorStateList.valueOf
                    (ResourcesCompat.getColor(getResources(), R.color.colorSecondaryAccent, null)));
            inputLayout.setErrorIconDrawable(null);
            model.isSoftError = false;
        } else {
            inputLayout.setErrorTextColor(ColorStateList.valueOf
                    (ResourcesCompat.getColor(getResources(), R.color.colorSecondaryAccent, null)));
            model.isSoftError = true;
            inputLayout.setErrorIconDrawable(model.softErrorId);
        }
    }

    public @Nullable
    String getText() {
        return model.value;
    }

    @NonNull
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (superState == null) {
            return new SavedState(new CicEditTextModel(StringUtil.EMPTY, StringUtil.EMPTY,
                    StringUtil.EMPTY, StringUtil.EMPTY, 100,
                    android.R.drawable.stat_notify_error, android.R.drawable.stat_notify_error));
        }
        final SavedState ss = new SavedState(superState);
        ss.model = this.model;
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
        this.model = ss.model;
        editText.setText(model.value);
        inputLayout.setHint(model.label);
        inputLayout.setHelperText(model.helperText);
    }

    public void setHelperText(final @Nullable String string) {
        model.helperText = StringUtil.defaultEmptyString(string);
        inputLayout.setHelperText(model.helperText);
    }

    public void clearData() {
        model.value = null;
        editText.setText(null);
    }

    private static class SavedState extends BaseSavedState {
        @NonNull
        private CicEditTextModel model = new CicEditTextModel(StringUtil.EMPTY, StringUtil.EMPTY,
                StringUtil.EMPTY, StringUtil.EMPTY, 100,
                android.R.drawable.stat_notify_error, android.R.drawable.stat_notify_error);

        SavedState(final @NonNull Parcelable superState) {
            super(superState);
        }

        private SavedState(final @NonNull Parcel in) {
            super(in);
            final CicEditTextModel temp = in.readParcelable(CicEditTextModel.class.getClassLoader());
            if (temp != null) {
                model = temp;
            }
        }

        @Override
        public void writeToParcel(final @NonNull Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(model, 0);
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

    private static class CicEditTextModel implements Parcelable {
        @Nullable
        private String value;
        @Nullable
        private String meterId;
        @Nullable
        private String tariffZoneConst;
        @Nullable
        private String newReadingsName;
        @NonNull
        private String label;
        @NonNull
        private String helperText;
        private boolean isError;
        private boolean isSoftError;
        private int maxLength;
        private @DrawableRes
        int errorResId;
        private @DrawableRes
        int softErrorId;

        public CicEditTextModel(final @Nullable String value, final @NonNull String label, final @NonNull String helperText,
                                final @Nullable String meterId, int maxLength, final int errorResId, final int softErrorId) {
            this.value = value;
            this.label = label;
            this.helperText = helperText;
            this.errorResId = errorResId;
            this.softErrorId = softErrorId;
            this.meterId = meterId;
            this.maxLength = maxLength;
        }

        public @DrawableRes
        int getErrorResId() {
            if (errorResId == 0) {
                return android.R.drawable.stat_notify_error;
            }
            return errorResId;
        }

        public @DrawableRes
        int getSoftErrorResId() {
            if (softErrorId == 0) {
                return android.R.drawable.stat_notify_error;
            }
            return softErrorId;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final @NonNull Parcel dest, final int flags) {
            dest.writeString(this.value);
            dest.writeString(this.label);
            dest.writeString(this.helperText);
            dest.writeString(this.meterId);
            dest.writeByte(this.isError ? (byte) 1 : (byte) 0);
            dest.writeByte(this.isSoftError ? (byte) 1 : (byte) 0);
            dest.writeInt(this.maxLength);
            dest.writeInt(this.errorResId);
            dest.writeInt(this.softErrorId);
        }

        public void readFromParcel(final @NonNull Parcel source) {
            this.value = source.readString();
            this.label = StringUtil.defaultEmptyString(source.readString());
            this.helperText = StringUtil.defaultEmptyString(source.readString());
            this.meterId = source.readString();
            this.isError = source.readByte() != 0;
            this.isSoftError = source.readByte() != 0;
            this.maxLength = source.readInt();
            this.errorResId = source.readInt();
            this.softErrorId = source.readInt();
        }

        protected CicEditTextModel(final @NonNull Parcel in) {
            this.value = in.readString();
            this.label = StringUtil.defaultEmptyString(in.readString());
            this.helperText = StringUtil.defaultEmptyString(in.readString());
            this.meterId = in.readString();
            this.isError = in.readByte() != 0;
            this.isSoftError = in.readByte() != 0;
            this.maxLength = in.readInt();
            this.errorResId = in.readInt();
            this.softErrorId = in.readInt();
        }

        public static final Creator<CicEditTextModel> CREATOR = new Creator<CicEditTextModel>() {
            @NonNull
            @Override
            public CicEditTextModel createFromParcel(final @NonNull Parcel source) {
                return new CicEditTextModel(source);
            }

            @NonNull
            @Override
            public CicEditTextModel[] newArray(final int size) {
                return new CicEditTextModel[size];
            }
        };
    }

    public interface CicTextChangedListener {
        void afterTextChanged(final @Nullable Editable editable);
    }

    public interface CicMetersUpdateListener {
        void onMeterUpdated(final @Nullable String value,
                            final @Nullable String someId);
    }

    public interface CicNewMetersUpdateListener {
        void onNewMeterUpdated(final @Nullable Editable editable,
                               final @NonNull String readingsName,
                               final @NonNull String tariffZoneConst);
    }
}
