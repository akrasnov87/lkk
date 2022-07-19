package ru.mobnius.cic.ui.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.MeterItem;
import ru.mobnius.simple_core.utils.LongUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class StartActPicture {

    @NonNull
    public String savedNotice;
    @NonNull
    public List<ImageItem> savedImages;
    @NonNull
    public List<MeterItem> metersShowing;
    public long mobileCauseId;
    public boolean isReceipt;
    public boolean isNotificationOrp;
    public boolean isLoaded;

    public StartActPicture() {
        this.mobileCauseId = LongUtil.MINUS;
        this.savedNotice = StringUtil.EMPTY;
        savedImages = new ArrayList<>();
        metersShowing = new ArrayList<>();
        isReceipt = false;
        isNotificationOrp = false;
        isLoaded = false;
    }

    public void setSavedImages(final List<ImageItem> images) {
        if (isLoaded) {
            return;
        }
        this.savedImages = images;
        isLoaded = true;
    }

    public boolean compareImagesNotSame(final @NonNull List<ImageItem> imagesShowing) {
        for (final ImageItem savedImage : savedImages) {
            boolean notContains = true;
            for (final ImageItem image : imagesShowing) {
                if (image.isPlaceholder()) {
                    continue;
                }
                if (StringUtil.equalsIgnoreCase(savedImage.id, image.id)) {
                    notContains = false;
                    if (savedImage.imageType.typeId != image.imageType.typeId ||
                            StringUtil.notEquals(savedImage.imageType.notice, image.imageType.notice)) {
                        return true;
                    }
                }
            }
            if (notContains) {
                return true;
            }
        }
        return false;
    }
}
