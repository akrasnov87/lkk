package ru.mobnius.cic.ui.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;

import ru.mobnius.cic.R;
import ru.mobnius.simple_core.utils.StringUtil;

public class HeaderFormFragment extends LinearLayout {
    private final TextFieldView mSubscrNumber;
    private final TextFieldView mSubscrName;
    private final TextFieldView mAddress;
    private final TextFieldView mElAddress;
    private final SwitchCompat mTechnical;
    private final TextFieldView mDeviceModel;
    private final TextFieldView mDeviceNumber;
    private final CicAutocompleteFieldView mDeviceModelChoose;
    private final EditText mDeviceNumberChoose;
    private final EditText mPhoneNumber;
    private final ImageView mPhoneIcon;
    private final LinearLayout mLinLay;
    private OnPhoneChangeListener listener;


    public HeaderFormFragment(final @NonNull Context context, final @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.header_form_fragment, this);
        mSubscrNumber = findViewById(R.id.standart_top_subscr_number);
        mSubscrName = findViewById(R.id.standart_top_subscr_name);
        mAddress = findViewById(R.id.standart_top_address);
        mElAddress = findViewById(R.id.standart_top_el_address);
        mTechnical = findViewById(R.id.standart_top_technical_metering);
        mDeviceModel = findViewById(R.id.standart_top_device_model);
        mDeviceNumber = findViewById(R.id.standart_top_device_number);
        mDeviceModelChoose = findViewById(R.id.standart_top_acfv_device_model);
        mDeviceNumberChoose = findViewById(R.id.standart_top_et_device_code);
        mLinLay = findViewById(R.id.standart_top_ll);
        mPhoneNumber = findViewById(R.id.standart_top_phone);
        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                disablePhone(StringUtil.isEmpty(s));
                if (listener != null && s != null) {
                    listener.onPhoneChanged(s.toString());
                }
            }
        });
        mPhoneIcon = findViewById(R.id.standart_top_phone_icon);
        mPhoneIcon.setOnClickListener(v -> {
            if (StringUtil.isEmpty(mPhoneNumber.getText())) {
                return;
            }
            String number = mPhoneNumber.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            context.startActivity(intent);
        });
    }

    public void enableViews(final @Nullable String subscrNumber, final @Nullable String subscrName,
                            final @Nullable String address, final @Nullable String elAdress,
                            final @Nullable String telephone, final boolean isCompany, final boolean isTechEnabled,
                            final @Nullable String deviceModel, final @Nullable String deviceNumber) {
        if (StringUtil.isNotEmpty(subscrNumber)) {
            mSubscrNumber.setValueText(subscrNumber);
        }
        if (StringUtil.isNotEmpty(subscrName)) {
            mSubscrName.setValueText(subscrName);
        }
        if (StringUtil.isNotEmpty(address)) {
            mAddress.setValueText(address);
        }
        if (StringUtil.isNotEmpty(telephone)) {
            mPhoneNumber.setText(telephone);
        }

        if (StringUtil.isNotEmpty(elAdress)) {
            mElAddress.setValueText(elAdress);
        }
        disablePhone(StringUtil.isEmpty(telephone));

        if (isCompany) {
            mSubscrName.setLabelText(getResources().getString(R.string.company_name));
            mTechnical.setVisibility(VISIBLE);
            mTechnical.setChecked(isTechEnabled);
        }
        if (StringUtil.isNotEmpty(deviceModel)) {
            mDeviceModel.setValueText(deviceModel);
        }
        if (StringUtil.isNotEmpty(deviceNumber)) {
            mDeviceNumber.setValueText(deviceNumber);
        }
    }

    public @NonNull
    CicAutocompleteFieldView getDeviceModelAutoComplete() {
        mDeviceModel.setVisibility(GONE);
        mLinLay.setVisibility(VISIBLE);
        return mDeviceModelChoose;
    }

    public @NonNull
    EditText getDeviceNumberEditText() {
        mDeviceNumber.setVisibility(GONE);
        mLinLay.setVisibility(VISIBLE);
        return mDeviceNumberChoose;
    }

    public void setOnPhoneChangedListener(final @NonNull OnPhoneChangeListener listener) {
        if (this.listener == null) {
            this.listener = listener;
        }
    }

    private void disablePhone(final boolean disable) {
        mPhoneIcon.setClickable(!disable);
        mPhoneIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), disable ? R.drawable.ic_phone_disabled_gray_24 : R.drawable.ic_phone_call_green_24, null));
    }

    public void setDeviceModelVisible(final boolean visible) {
        mDeviceModel.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setmDeviceNumberVisible(final boolean visible) {
        mDeviceNumber.setVisibility(visible ? VISIBLE : GONE);
    }

    public interface OnPhoneChangeListener {
        void onPhoneChanged(final @NonNull String newNumber);
    }
}

