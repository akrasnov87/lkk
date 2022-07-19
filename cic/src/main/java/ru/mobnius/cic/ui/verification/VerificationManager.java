package ru.mobnius.cic.ui.verification;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Специальны класс, в котором предоставляется возможность упорядочить проверяемые поля
 * сверху вниз. Так как поведение RecyclerView нестандартно, добавление происходит позже всех остальных View елементов на экране,
 * то необходимо передавать для него параметр order кратный 100, а поля которые выше и ниже
 * должны получать параметр order меньше и больше этого числа соответственно. Добавление
 * происходит с проверкой на наличие поля с таким же order, чтобы не было дублирования.
 */
public class VerificationManager {
    @NonNull
    private final List<VerifiableField<? extends View>> verifiableFieldsList;


    public VerificationManager() {
        verifiableFieldsList = new ArrayList<>();
    }

    /**
     * Метод для добавление валидируемого поля
     * Перед добавление првоеряет на наличие полей с таким же order и удаляет если есть
     *
     * @param field по сути это объект содеражащий вьюху которую надо валидировать и методы валидации
     * @param order это порядок нахождения вью элемента на экране - должно быть чем выше положение тем меньше order
     */
    @SuppressWarnings("ComparatorCombinators")
    public void addFieldToBeVerified(VerifiableField<? extends View> field, int order) {
        field.setOrder(order);
        int fieldToBeRemoved = -1;
        for (int i = 0; i < verifiableFieldsList.size(); i++) {
            if (verifiableFieldsList.get(i).getOrder() == order) {
                fieldToBeRemoved = i;
                break;
            }
        }
        if (fieldToBeRemoved >= 0) {
            verifiableFieldsList.remove(fieldToBeRemoved);
        }
        verifiableFieldsList.add(field);
        Collections.sort(verifiableFieldsList, (o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder()));
    }

    @SuppressWarnings("ComparatorCombinators")
    public void removeFieldByOrder(int order) {
        int fieldToBeRemoved = -1;
        for (int i = 0; i < verifiableFieldsList.size(); i++) {
            if (verifiableFieldsList.get(i).getOrder() == order) {
                fieldToBeRemoved = i;
                break;
            }
        }
        if (fieldToBeRemoved >= 0) {
            verifiableFieldsList.remove(fieldToBeRemoved);
            Collections.sort(verifiableFieldsList, (o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder()));
        }
    }

    @SuppressWarnings("ComparatorCombinators")
    public void removeIfClearable() {
        Iterator<VerifiableField<? extends View>> i = verifiableFieldsList.iterator();
        while (i.hasNext()) {
            VerifiableField<? extends View> verifiableField = i.next();
            if (verifiableField.isClearable()) {
                i.remove();
            }
        }
        Collections.sort(verifiableFieldsList, (o1, o2) -> Integer.compare(o1.getOrder(), o2.getOrder()));
    }

    /**
     * @param scrollView для движения экрана к невалидному вью элементу
     * @return константу для того чтобы определить валидны ли все поля в акте и если нет,
     * то какое сообщение выводить пользователю
     * @see VerifiableField
     */
    public int verify(final @NonNull NestedScrollView scrollView, boolean isMobileCauseSelected) {
        int solidVerification = -1;
        int softVerification = -1;
        int validSoftType = 0;
        for (int i = 0; i < verifiableFieldsList.size(); i++) {
            VerifiableField<? extends View> field = verifiableFieldsList.get(i);
            if (field.validateSolid()) {
                if (!field.validateSoft()) {
                    if (softVerification < 0) {
                        validSoftType = field.getValidSoftType();
                        softVerification = i;
                    }
                }
            } else {
                if (solidVerification < 0) {
                    solidVerification = i;
                }
            }
        }
        if (isMobileCauseSelected) {
            return VerifiableField.CAN_SAVE;
        }
        if (solidVerification >= 0) {
            View v = verifiableFieldsList.get(solidVerification).getView();
            scrollToField(v, scrollView);
            return VerifiableField.CAN_NOT_SAVE;
        }
        if (softVerification >= 0) {
            View v = verifiableFieldsList.get(softVerification).getView();
            scrollToField(v, scrollView);
            if (validSoftType != 0) {
                return validSoftType;
            }
        }
        return VerifiableField.CAN_SAVE;
    }

    public int verify() {
        int solidVerification = -1;
        for (int i = 0; i < verifiableFieldsList.size(); i++) {
            VerifiableField<? extends View> field = verifiableFieldsList.get(i);
            if (!field.validateSolid()) {
                if (solidVerification < 0) {
                    solidVerification = i;
                }
            }
        }
        if (solidVerification >= 0) {
            return VerifiableField.CAN_NOT_SAVE;
        }
        return VerifiableField.CAN_SAVE;
    }

    public boolean verifyInvisible(boolean isSolid) {
        int solidVerification = -1;
        for (int i = 0; i < verifiableFieldsList.size(); i++) {
            VerifiableField<? extends View> field = verifiableFieldsList.get(i);
            if (!field.validateInvisible()) {
                if (solidVerification < 0) {
                    solidVerification = i;
                }
            }
        }
        if (isSolid) {
            return solidVerification < 0;
        }
        return true;
    }

    private void scrollToField(View v, NestedScrollView scrollView) {
        scrollView.invalidate();
        Point realPosition = new Point();
        setRealPointPosition(scrollView, v.getParent(), v, realPosition);
        scrollView.smoothScrollTo(0, realPosition.y, 500);
    }

    private void setRealPointPosition(ViewGroup mainParent, ViewParent parent, View child, Point accumulatedOffset) {
        if (parent instanceof ViewGroup) {
            ViewGroup parentGroup = (ViewGroup) parent;
            accumulatedOffset.x += child.getLeft();
            accumulatedOffset.y += child.getTop();
            if (parentGroup.equals(mainParent)) {
                return;
            }
            setRealPointPosition(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
        }
    }

    @TestOnly
    public int getVerifiableFieldsListSize() {
        return verifiableFieldsList.size();
    }

    @NonNull
    @TestOnly
    public List<VerifiableField<? extends View>> getVerifiableFieldsList() {
        return verifiableFieldsList;
    }
}
