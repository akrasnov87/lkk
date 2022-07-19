package ru.mobnius.cic.ui.verification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.CicEditText;
import ru.mobnius.simple_core.utils.StringUtil;

/**
 * Специальный класс для валидации текстового поля с показаниями приборов учета
 */
public class MeterDigitVerification implements VerifiableField<CicEditText> {
    @NonNull
    private final CicEditText editText;
    @NonNull
    private final Context context;
    @NonNull
    private String wrongRating;
    @NonNull
    private final Date previousDate;

    private int softValidationType;
    private int order;
    private int beforeDot;
    private int afterDot;
    private final double previousValue;

    /**
     * Конструктор класса. Может принимать значения null вместо соответствующих полей,
     * предусмотрена корректная обработка таких ситуаций
     *
     * @param editText      - текстовое поле для ввода пользователем показаний приборов учета
     * @param order         - порядок текстового поля в иерархии экрана - нужен только для правильной сортировки
     * @param rating        - разрядность - сколько у прибора чисел до запятой и сколько после (например для 5.1 - будет характерно показание 12345.6)
     * @param previousValue - предыдущее известное показание
     * @param previousDate  - дата предыдущего показания
     */
    public MeterDigitVerification(@NotNull CicEditText editText, int order, @Nullable Double rating, @Nullable Double previousValue, @Nullable Date previousDate) {
        this.editText = editText;
        this.order = order;
        if (rating == null) {
            rating = 8.6;
        }
        context = editText.getContext();
        if (previousValue == null) {
            this.previousValue = 0.0;
        } else {
            this.previousValue = previousValue;
        }
        if (previousDate == null) {
            this.previousDate = new Date();
        } else {
            this.previousDate = previousDate;
        }
        wrongRating = StringUtil.EMPTY;
        initAfterAndBefore(rating);
        initWrongRating();
        configureInputFields();

    }

    /**
     * Метод для присвоения значений beforeDot - количество цифр в счетчике до запятой, afterDot - после запятой
     *
     * @param rating - разрядность - сколько у прибора чисел до запятой и сколько после (например для 5.1 - будет характерно показание 12345.6)
     */
    private void initAfterAndBefore(double rating) {
        String[] splitedRating = String.valueOf(rating).split("[.,]");
        if (splitedRating.length != 2) {
            beforeDot = 8;
            afterDot = 6;
            return;
        }
        String beforeString = splitedRating[0];
        String afterString = splitedRating[1];
        try {
            beforeDot = Integer.parseInt(beforeString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            beforeDot = 8;
        }
        try {
            afterDot = Integer.parseInt(afterString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            afterDot = 6;
        }
    }

    /**
     * Инициализация сообщения о несоответствии введенных показний рейтингу прибора (например 12345.67 для рейтинга 5.1)
     */
    private void initWrongRating() {
        final String resTemplate = context.getString(R.string.not_valid_rating);
        wrongRating = resTemplate + " " + beforeDot + "." + afterDot;
    }

    @NonNull
    @Override
    public CicEditText getView() {
        return editText;
    }

    @Override
    public int getValidSoftType() {
        return softValidationType;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Метод для валидации введенных показаний по второстепенным криетриям,
     * который срабатывает после того как пройдена валидация по обязательным критериям
     *
     * @return - true если пройдена и false в обратном случае
     */
    @Override
    public boolean validateSoft() {
        if (editText.getText() == null) {
            return false;
        }
        String currentConsumption = editText.getText();
        if (StringUtil.isEmpty(currentConsumption)) {
            return false;
        }
        double value;
        try {
            value = Double.parseDouble(currentConsumption);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        //тут считаем сколько максимум потребление у данного счетчика,
        //beforeDot это сколько знаков в максимум в счетчике до запятой
        double temp = 1;
        for (int i = 0; i < beforeDot; i++) {
            temp = temp * 10;
        }
        //halfValue - это половина от максимального значения для данного
        //счетичка. Согласно постановке - в случае если текущее показание меньше  предыдущего
        //если разница в показаниях меньше,чем половина от максимальных - то это переход через ноль,
        //в противоположном случае - это реверс
        double halfValue = temp / 2;
        if (isZeroJump(value, temp, halfValue) || isReverse(value, temp, halfValue)) {
            editText.setSoftError(context.getString(R.string.current_consumption_less_then_previous));
            softValidationType = VerifiableField.CAN_SAVE_AFTER_NEW_LESS_OLD_MESSAGE;
            return false;
        }
        if (isTooBigConsumption(currentConsumption)) {
            editText.setSoftError(context.getString(R.string.avg_high));
            softValidationType = VerifiableField.CAN_SAVE_AFTER_MOUNT_AVG_MESSAGE;
            return false;
        }
        return true;
    }

    /**
     * Метод для обязательной валидации введенных показаний
     *
     * @return - true если пройдена и false в обратном случае
     */
    @Override
    public boolean validateSolid() {
        if (StringUtil.isEmpty(editText.getText())) {
            editText.setError(context.getString(R.string.must_to_be_not_empty));
            return false;
        }
        if (isNotValidRating()) {
            editText.setError(wrongRating);
            return false;
        }
        return true;
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    public boolean validateInvisible() {
        if (StringUtil.isEmpty(editText.getText())) {
            return false;
        }
        return !isNotValidRating();
    }

    /**
     * Метод для выввода подсказок для пользователя по поводу вводимых показаний
     */
    private void configureInputFields() {
        editText.addCicTextChangedListener(editable -> {
            if (editable == null) {
                return;
            }
            if (editable.toString().isEmpty()) {
                editText.setHelperText(context.getString(R.string.must_to_be_not_empty));
                return;
            }
            if (!StringUtil.isDouble(editable)) {
                editText.setHelperText(context.getString(R.string.suppose_to_be_digit));
                return;
            }
            if (isNotValidRating()) {
                editText.setHelperText(wrongRating);
                return;
            }
            if (!isMoreThenPrevious()) {
                editText.setHelperText(context.getString(R.string.current_consumption_less_then_previous));
                return;
            }
            if (isTooBigConsumption(editable.toString())) {
                editText.setHelperText(context.getString(R.string.avg_high));
                return;
            }
            editText.setHelperText(null);
        });
    }

    /**
     * Метод для валидации разрядности
     *
     * @return - true если пройдена и false в обратном случае
     */
    private boolean isNotValidRating() {
        if (editText.getText() == null) {
            return true;
        }
        if (beforeDot == 0) {
            return true;
        }
        String txt = editText.getText();
        String[] array = txt.split("[.,]");
        String afterString = "";
        if (array.length == 2) {
            afterString = array[1];
            try {
                Integer.parseInt(afterString);
            } catch (NumberFormatException e) {
                return true;
            }
        } else {
            if (array.length != 1) {
                return true;
            }
        }
        String beforeString = array[0];
        try {
            Integer.parseInt(beforeString);
        } catch (NumberFormatException e) {
            return true;
        }

        if (afterString.length() <= afterDot
                && beforeString.length() <= beforeDot) {
            return false;
        } else {
            if (beforeString.length() <= beforeDot) {
                // такая проверка нужна в случае разрядности 5.0 например, так как
                // из блока if выше получается в этом случае after
                // вернет 0, а afterString.length будет равен 1.
                int aft = -1;
                try {
                    aft = Integer.parseInt(afterString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return afterDot != 0 || aft != 0;
            } else {
                return true;
            }
        }
    }

    /**
     * Метод для проверки является ли текущее показние больше предыдущего
     *
     * @return - true если текущее значение больше предыдущего и false в обратном случае
     */
    private boolean isMoreThenPrevious() {
        if (editText.getText() == null) {
            return false;
        }
        String currentConsumption = editText.getText();
        if (currentConsumption.isEmpty()) {
            return false;
        }
        double value;
        try {
            value = Double.parseDouble(currentConsumption);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        //тут считаем сколько максимум потребление у данного счетчика,
        //beforeDot это сколько знаков в максимум в счетчике до запятой
        double temp = 1;
        for (int i = 0; i < beforeDot; i++) {
            temp = temp * 10;
        }
        //halfValue - это половина от максимального значения для данного
        //счетичка. Согласно постановке - в случае если текущее показание меньше  предыдущего
        //если разница в показаниях меньше,чем половина от максимальных - то это переход через ноль,
        //в противоположном случае - это реверс
        double halfValue = temp / 2;
        return !isZeroJump(value, temp, halfValue) && !isReverse(value, temp, halfValue);
    }

    /**
     * Метод для проверки был ли 'переход через ноль' (например прошлое показание - 99999.0, а новое показние - 150.0 - значит был 'переход через ноль')
     *
     * @return - true если был 'переход через ноль' и false в обратном случае
     */
    private boolean isZeroJump(double value, double max, double half) {
        if (value < previousValue) {
            return !((value + max - previousValue) > half);
        } else {
            return false;
        }
    }

    /**
     * Метод для проверки был ли 'реверса' (например прошлое показание - 44000.0, а новое показние - 42000.0 - значит 'реверс')
     * Понятие 'реверс' подразумевает под собой скорее всего то что прошлое показание было ошибочно завышено
     *
     * @return - true если был 'реверс' и false в обратном случае
     */

    private boolean isReverse(double value, double temp, double halfValue) {
        if (value < previousValue) {
            return (value + temp - previousValue) > halfValue;
        } else {
            return false;
        }
    }


    /**
     * Метод для проверки того что среднемесячное потребление не превышает 5000 кВт
     *
     * @return - true если превышает и false в обратном случае
     */
    private boolean isTooBigConsumption(String current) {
        if (current.isEmpty()) {
            return false;
        }
        double value;
        try {
            value = Double.parseDouble(current);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        if (value > previousValue) {
            long meterDiff = (long) (value - previousValue);
            long diff = new Date().getTime() - previousDate.getTime();
            long diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            if (diffInDays == 0) {
                diffInDays = 1L;
            }
            long result = (meterDiff / diffInDays) * 30;
            return result > 5000L;
        }
        return false;
    }

}
