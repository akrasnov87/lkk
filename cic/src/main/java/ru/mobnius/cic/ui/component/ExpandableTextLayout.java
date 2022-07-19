package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class ExpandableTextLayout extends LinearLayout
        implements View.OnClickListener {
    @NonNull
    private final TextView tvTitle;
    @NonNull
    private final TextView tvContent;
    @NonNull
    private final AppCompatImageView ivArrow;
    @NonNull
    private ExpandableTextModel model;

    public ExpandableTextLayout(final @NonNull Context context, final @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);

        inflate(context, R.layout.expandable_text_layout, this);

        final TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.ExpandableTextLayout, 0, 0);
        final String titleText = a.getString(R.styleable.ExpandableTextLayout_titleText);
        final String contentText = a.getString(R.styleable.ExpandableTextLayout_contentText);
        final boolean expanded = a.getBoolean(R.styleable.ExpandableTextLayout_expanded, true);
        a.recycle();

        model = new ExpandableTextModel(StringUtil.defaultEmptyString(titleText),
                StringUtil.defaultEmptyString(contentText), expanded);
        tvTitle = findViewById(R.id.expandable_layout_title);
        ivArrow = findViewById(R.id.expandable_layout_arrow);
        setTitle(titleText);
        tvContent = findViewById(R.id.expandable_layout_content);
        setContent(contentText);
        setExpanded(expanded);

        ivArrow.setOnClickListener(this);

    }

    public void setTitle(final @Nullable String title) {
        model.title = StringUtil.defaultEmptyString(title);
        tvTitle.setText(model.title);
    }

    public void setContent(final @Nullable String content) {
        if (StringUtil.isEmpty(content)) {
            model.content = StringUtil.EMPTY;
            updateDrawable(false);
            return;
        }
        model.content = content;
        tvContent.setText(Html.fromHtml(content));
    }

    public void setContent(final @NonNull List<ExpandableTextLayout.OnExpandableItem> items) {
        final String HTML_BR = "<br />";
        final StringBuilder stringBuilder = new StringBuilder();

        for (ExpandableTextLayout.OnExpandableItem item : items) {
            stringBuilder.append(item.toHtml()).append(HTML_BR);
        }
        if (stringBuilder.length() > 0) {
            int lenght = stringBuilder.length();
            setContent(stringBuilder.substring(0, lenght - HTML_BR.length()));
        }
    }

    public void setAltContent(final @Nullable String content) {
        if (StringUtil.isEmpty(content)) {
            model.content = StringUtil.EMPTY;
            return;
        }
        model.content = content;
        tvContent.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        tvContent.setTextSize(14);
        tvContent.setText(Html.fromHtml(model.content));
    }

    public void setAltContent(final @NonNull List<ExpandableTextLayout.OnExpandableItem> items) {
        final String HTML_BR = "<br />";
        final StringBuilder stringBuilder = new StringBuilder();

        for (ExpandableTextLayout.OnExpandableItem item : items) {
            stringBuilder.append(item.toAltHtml()).append(HTML_BR);
        }
        if (stringBuilder.length() > 0) {
            tvContent.setTextColor(getResources().getColor(R.color.colorPrimaryText));
            tvContent.setTextSize(14);
            int lenght = stringBuilder.length();
            setContent(stringBuilder.substring(0, lenght - HTML_BR.length()));
        }
    }

    public void setExpanded(final boolean expanded) {
        model.expanded = expanded;
        updateDrawable(model.expanded);
        tvContent.setVisibility(model.expanded ? VISIBLE : GONE);
    }

    @Override
    public void onClick(final @NonNull View v) {
        if (model.expanded) {
            tvContent.setVisibility(GONE);
            updateDrawable(false);
        } else {
            tvContent.setVisibility(VISIBLE);
            updateDrawable(true);
        }
        model.expanded = !model.expanded;
    }

    private void updateDrawable(final boolean expanded) {
        ivArrow.setImageDrawable(expanded ?
                ContextCompat.getDrawable(getContext(), R.drawable.arrow_down_24) :
                ContextCompat.getDrawable(getContext(), R.drawable.arrow_up_24));
    }

    @NonNull
    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (superState == null) {
            return new SavedState(new ExpandableTextModel(StringUtil.EMPTY, StringUtil.EMPTY, false));
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
        setTitle(model.title);
        setContent(model.content);
        setExpanded(model.expanded);
    }

    private static class SavedState extends BaseSavedState {
        @NonNull
        private ExpandableTextModel model;

        SavedState(final @NonNull Parcelable superState) {
            super(superState);
            model = new ExpandableTextModel(StringUtil.EMPTY, StringUtil.EMPTY, false);
        }

        private SavedState(final @NonNull Parcel in) {
            super(in);
            final ExpandableTextModel temp = in.readParcelable(ExpandableTextModel.class.getClassLoader());
            if (temp == null) {
                this.model = new ExpandableTextModel(StringUtil.EMPTY, StringUtil.EMPTY, false);
            } else {
                this.model = temp;
            }
        }

        @Override
        public void writeToParcel(final @NonNull Parcel out, final int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(model, 0);
        }

        @NonNull
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

    private static class ExpandableTextModel implements Parcelable {
        @NonNull
        public String title;
        @NonNull
        public String content;
        public boolean expanded;

        private ExpandableTextModel(final @NonNull String title,
                                    final @NonNull String content, final boolean expanded) {
            this.title = title;
            this.content = content;
            this.expanded = expanded;
        }

        protected ExpandableTextModel(final @NonNull Parcel in) {
            title = StringUtil.defaultEmptyString(in.readString());
            content = StringUtil.defaultEmptyString(in.readString());
            expanded = in.readByte() != 0;
        }

        @NonNull
        public static final Creator<ExpandableTextModel> CREATOR = new Creator<ExpandableTextModel>() {
            @Override
            public ExpandableTextModel createFromParcel(@NonNull Parcel in) {
                return new ExpandableTextModel(in);
            }

            @Override
            public ExpandableTextModel[] newArray(int size) {
                return new ExpandableTextModel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(title);
            parcel.writeString(content);
            parcel.writeByte((byte) (expanded ? 1 : 0));
        }
    }

    public interface OnExpandableItem {
        @NonNull
        String toHtml();

        @NonNull
        String toAltHtml();
    }
}
