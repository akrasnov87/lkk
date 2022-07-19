package ru.mobnius.cic.ui.verification;

import androidx.annotation.NonNull;

public interface VerifiableField<T> {
    int CAN_SAVE = 1001;
    int CAN_NOT_SAVE = 1002;
    int CAN_SAVE_AFTER_NEW_LESS_OLD_MESSAGE = 1003;
    int CAN_SAVE_AFTER_MOUNT_AVG_MESSAGE = 1004;

    /**
     * Возвращает вью компонент который необходиомо валидировать
     * @return View
     */
    @NonNull
    T getView();

    /**
     * Тип собщения которое нужно показать в случае если метод validateSoft() вернул false
     * @return CAN_SAVE_AFTER_NEW_LESS_OLD_MESSAGE или CAN_SAVE_AFTER_MOUNT_AVG_MESSAGE
     */
    int getValidSoftType();

    /**
     * Установка порядкового номера в списке полей
     */
    void setOrder(int order);

    /**
     * Порядковый номер
     * @return порядковый номер в списке валидируемых полей
     */
    int getOrder();

    /**
     * Проверка валидности поля, после которой можно сохранять если она не пройдена
     * Всегда нужно возвращать true если данная проверка не нужна
     * @return true если проверка пройдена
     */
    boolean validateSoft();

    /**
     * Проверка валидности поля, после которой нельзя сохранять если она не пройдена
     * @return true если проверка пройдена
     */
    boolean validateSolid();

    /**
     * Проверка нужно ли удалять этот элемент при перестроении адаптера (только для полей находящихся в RecyclerView
     * во всех остальных случаях должен возвращать false)
     *
     * @return true если необходимо удалять элемент
     */
    boolean isClearable();


    /**
     * Проверка валидности поля, без визуального отображения
     * @return true если проверка пройдена
     */
    boolean validateInvisible();
}
