package ru.mobnius.cic.ui.pincode.layouts;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.pincode.PinCodeDigitsCallback;

/**
 * Класс для отрисовки экрана ввода пин-кода
 */
public class PinCodeLayout extends LinearLayout {
    @NonNull
    public final MaterialButton buttonOne;
    @NonNull
    public final MaterialButton buttonTwo;
    @NonNull
    public final MaterialButton buttonThree;
    @NonNull
    public final MaterialButton buttonFour;
    @NonNull
    public final MaterialButton buttonFive;
    @NonNull
    public final MaterialButton buttonSix;
    @NonNull
    public final MaterialButton buttonSeven;
    @NonNull
    public final MaterialButton buttonEight;
    @NonNull
    public final MaterialButton buttonNine;
    @NonNull
    public final TextView textReset;
    @NonNull
    public final MaterialButton buttonZero;
    @NonNull
    public final ImageButton buttonBackspace;
    @Nullable
    private PinCodeDigitsCallback callback;
    @NonNull
    public final LinearLayoutCompat dotLayout;
    @NonNull
    public final ImageView firstDot;
    @NonNull
    public final ImageView secondDot;
    @NonNull
    public final ImageView thirdDot;
    @NonNull
    public final ImageView fourthDot;

    public int currentDotsFilled = 0;

    public PinCodeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.pin_code_digits, this);

        firstDot = findViewById(R.id.dot_line_first_dot);
        secondDot = findViewById(R.id.dot_line_second_dot);
        thirdDot = findViewById(R.id.dot_line_third_dot);
        fourthDot = findViewById(R.id.dot_line_fourth_dot);
        dotLayout = findViewById(R.id.dots_layout);

        buttonOne = findViewById(R.id.pin_digits_btn_one);
        buttonOne.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.one_digit));
            }
        });
        buttonTwo = findViewById(R.id.pin_digits_btn_two);
        buttonTwo.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.two_digit));
            }
        });
        buttonThree = findViewById(R.id.pin_digits_btn_three);
        buttonThree.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.three_digit));
            }
        });
        buttonFour = findViewById(R.id.pin_digits_btn_four);
        buttonFour.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.four_digit));
            }
        });
        buttonFive = findViewById(R.id.pin_digits_btn_five);
        buttonFive.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.five_digit));
            }
        });
        buttonSix = findViewById(R.id.pin_digits_btn_six);
        buttonSix.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.six_digit));
            }
        });
        buttonSeven = findViewById(R.id.pin_digits_btn_seven);
        buttonSeven.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.seven_digit));
            }
        });
        buttonEight = findViewById(R.id.pin_digits_btn_eight);
        buttonEight.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.eight_digit));
            }
        });
        buttonNine = findViewById(R.id.pin_digits_btn_nine);
        buttonNine.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.nine_digit));
            }
        });
        textReset = findViewById(R.id.pin_digits_btn_reset);
        textReset.setOnClickListener(v -> {
            if (callback != null) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setPositiveButton(context.getString(R.string.clear), (dialog, which) -> callback.onPinDropConfirmed())
                        .setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
                AlertDialog alert = adb.create();
                alert.setTitle(context.getString(R.string.attention));
                alert.setMessage(context.getString(R.string.reset_pin_code_warning));
                alert.setCancelable(true);
                alert.show();

            }
        });
        buttonZero = findViewById(R.id.pin_digits_btn_zero);
        buttonZero.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDigitEntered(context.getString(R.string.zero_digit));
            }
        });
        buttonBackspace = findViewById(R.id.pin_digits_btn_backspace);
        buttonBackspace.setOnClickListener(v -> {
            if (callback != null) {
                callback.onBackspacePressed();
            }
        });
    }

    public void setDigitCallback(final @NonNull PinCodeDigitsCallback callback) {
        this.callback = callback;
    }

    public void hideDrop() {
        textReset.setVisibility(GONE);
    }

    public void clearDots() {
        firstDot.setImageResource(R.drawable.vector_pin_empty_dot);
        secondDot.setImageResource(R.drawable.vector_pin_empty_dot);
        thirdDot.setImageResource(R.drawable.vector_pin_empty_dot);
        fourthDot.setImageResource(R.drawable.vector_pin_empty_dot);
    }


    public void switchDot(int length) {
        switch (length) {
            case 0:
                clearDots();
            case 1:
                if (currentDotsFilled < length) {
                    animateDotFill(firstDot);
                } else {
                    secondDot.setImageResource(R.drawable.vector_pin_empty_dot);
                }
                currentDotsFilled = length;
                break;
            case 2:
                if (currentDotsFilled < length) {
                    animateDotFill(secondDot);
                } else {
                    thirdDot.setImageResource(R.drawable.vector_pin_empty_dot);
                }
                currentDotsFilled = length;
                break;
            case 3:
                if (currentDotsFilled < length) {
                    animateDotFill(thirdDot);
                } else {
                    fourthDot.setImageResource(R.drawable.vector_pin_empty_dot);
                }
                currentDotsFilled = length;
                break;
            case 4:
                if (currentDotsFilled < length) {
                    animateDotFill(fourthDot);
                    currentDotsFilled = 0;
                }
                break;
        }
    }


    public void wrongClear() {
        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
        animateDotsShake();
        clearDots();
    }

    private void animateDotFill(final @NonNull ImageView dot) {
        dot.setImageResource(R.drawable.vector_pin_filled_dot);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(dot, View.SCALE_X, 0f, 1.1f, 1f))
                .with(ObjectAnimator.ofFloat(dot, View.SCALE_Y, 0f, 1.1f, 1f));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void animateDotsShake() {
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(dotLayout, View.TRANSLATION_X, 0, 30f, -30f, 25f, -25f, 15f, -15f, 6f, -6f, 0));
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }


    public void clearOneDot(int length) {
        switch (length) {
            case 0:
                firstDot.setImageResource(R.drawable.vector_pin_empty_dot);
                break;
            case 1:
                secondDot.setImageResource(R.drawable.vector_pin_empty_dot);
                break;
            case 2:
                thirdDot.setImageResource(R.drawable.vector_pin_empty_dot);
                break;
            case 3:
                fourthDot.setImageResource(R.drawable.vector_pin_empty_dot);
                break;
        }
    }

    public void fillDot(final int length) {
        switch (length) {
            case 1:
                animateDotFill(firstDot);
                break;
            case 2:
                animateDotFill(secondDot);
                break;
            case 3:
                animateDotFill(thirdDot);
                break;
            case 4:
                animateDotFill(fourthDot);
                break;
        }
    }

    public void fillDotsUntil(final int length) {
        if (length <= 0) {
            return;
        }
        for (int i = 1; i <= length; i++) {
            fillDot(i);
        }
    }

    public void blockDigits() {
        buttonOne.setEnabled(false);
        buttonTwo.setEnabled(false);
        buttonThree.setEnabled(false);
        buttonFour.setEnabled(false);
        buttonFive.setEnabled(false);
        buttonSix.setEnabled(false);
        buttonSeven.setEnabled(false);
        buttonEight.setEnabled(false);
        buttonNine.setEnabled(false);
        textReset.setEnabled(false);
        buttonZero.setEnabled(false);
        buttonBackspace.setEnabled(false);
    }

    public void activateDigits() {
        buttonOne.setEnabled(true);
        buttonTwo.setEnabled(true);
        buttonThree.setEnabled(true);
        buttonFour.setEnabled(true);
        buttonFive.setEnabled(true);
        buttonSix.setEnabled(true);
        buttonSeven.setEnabled(true);
        buttonEight.setEnabled(true);
        buttonNine.setEnabled(true);
        textReset.setEnabled(true);
        buttonZero.setEnabled(true);
        buttonBackspace.setEnabled(true);
    }
}
