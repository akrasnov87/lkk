package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class CicCheckableTextView extends LinearLayoutCompat {
    @NonNull
    private final AppCompatCheckedTextView checkedTextView;
    @Nullable
    private OnCheckableClickListener listener;

    public CicCheckableTextView(@NonNull Context context) {
        super(context);
        inflate(context, R.layout.cic_checkable_text, this);
        setOrientation(LinearLayoutCompat.VERTICAL);
        checkedTextView = findViewById(R.id.cic_checkable_text_view);
    }

    public CicCheckableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.cic_checkable_text, this);
        checkedTextView = findViewById(R.id.cic_checkable_text_view);
        init(context, attrs);
    }

    public CicCheckableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.cic_checkable_text, this);
        checkedTextView = findViewById(R.id.cic_checkable_text_view);
        init(context, attrs);
    }

    private void init(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        setOrientation(LinearLayoutCompat.VERTICAL);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CicCheckableTextView, 0, 0);
        final String fieldLabel = StringUtil.defaultEmptyString(a.getString(R.styleable.CicCheckableTextView_cctv_text));
        final int endPositiveIcon = a.getResourceId(R.styleable.CicCheckableTextView_cctv_end_drawable, R.drawable.ic_done_24);
        final int endNegativeIcon = a.getResourceId(R.styleable.CicCheckableTextView_cctv_end_drawable, R.drawable.ic_clear_red_24);

        final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ripple_effect, null);
        this.setBackground(drawable);
        checkedTextView.setCheckMarkDrawable(endPositiveIcon);
        checkedTextView.setText(fieldLabel);
        this.setOnClickListener(view -> {
            if (listener == null) {
                return;
            }
            listener.onClick();
        });
        a.recycle();
    }

    public boolean isChecked() {
        return checkedTextView.isChecked();
    }

    public void setChecked(final boolean checked) {
        if (checked) {
            checkedTextView.setCheckMarkDrawable(R.drawable.ic_done_24);
        } else {
            checkedTextView.setCheckMarkDrawable(R.drawable.ic_clear_red_24);
        }
        checkedTextView.setChecked(checked);
    }

    public void select() {
        checkedTextView.setCheckMarkDrawable(R.drawable.ic_done_24);
        checkedTextView.setChecked(true);
    }

    public void unSelect() {
        checkedTextView.setCheckMarkDrawable(R.drawable.ic_clear_red_24);
        checkedTextView.setChecked(false);
    }

    public void setEndIcon(final @DrawableRes int iconId) {
        checkedTextView.setCheckMarkDrawable(iconId);
    }

    public void setOnCheckableClickListener(final @NonNull OnCheckableClickListener listener) {
        this.listener = listener;
    }

    public interface OnCheckableClickListener {
        void onClick();
    }
}
