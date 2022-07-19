package ru.mobnius.cic.adaper;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.mobnius.cic.adaper.holder.image.OnImageChangeClickListener;
import ru.mobnius.cic.adaper.holder.image.OnImageItemClickListener;
import ru.mobnius.cic.adaper.holder.image.OnPlaceholderClickListener;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.simple_core.utils.StringUtil;

public class ImageAdapterTest {
    private ImageAdapter imageAdapter;
    private ImageItem singleItem;
    private List<ImageItem> imageItems;
    private String singleItemId;

    @Before
    public void setUp() {
        imageAdapter = new ImageAdapter(
                new OnImageItemClickListener() {
                    @Override
                    public void onImageClicked(@NonNull ImageItem item) {

                    }
                },
                new OnImageChangeClickListener() {
                    @Override
                    public void onImageChangeClick(@NonNull ImageItem item) {

                    }
                },
                new OnPlaceholderClickListener() {
                    @Override
                    public void onPlaceHolderClicked() {

                    }
                });
        final Bitmap.Config config = Bitmap.Config.ARGB_8888;
        final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
        singleItemId = UUID.randomUUID().toString();
        final ImageType imageType = new ImageType(1L, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, false);
        imageType.imageId = singleItemId;
        final Location location = new Location("GPS");
        singleItem = new ImageItem(singleItemId,
                StringUtil.EMPTY,
                StringUtil.EMPTY,
                new Date(),
                emptyBitmap,
                imageType,
                location,
                false
        );
        imageItems = new ArrayList<>();
        imageItems.add(new ImageItem(UUID.randomUUID().toString(),
                StringUtil.EMPTY,
                StringUtil.EMPTY,
                new Date(),
                emptyBitmap,
                imageType,
                location,
                false
        ));
        imageItems.add(new ImageItem(UUID.randomUUID().toString(),
                StringUtil.EMPTY,
                StringUtil.EMPTY,
                new Date(),
                emptyBitmap,
                imageType,
                location,
                false
        ));
        imageItems.add(new ImageItem(UUID.randomUUID().toString(),
                StringUtil.EMPTY,
                StringUtil.EMPTY,
                new Date(),
                emptyBitmap,
                imageType,
                location,
                false
        ));
    }

    @Test
    public void testImageAdapter() {
        imageAdapter.insert(singleItem);
        assertEquals(1, imageAdapter.getItemCount());
        final ImageType imageType = new ImageType(2L, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, false);
        imageType.imageId = singleItemId;
        imageAdapter.updateImageItem(imageType);
        assertEquals(1, imageAdapter.getItemCount());
        imageAdapter.removeImageItem(singleItemId);
        assertEquals(0, imageAdapter.getItemCount());
        imageAdapter.insert(singleItem);
        assertEquals(1, imageAdapter.getItemCount());
        imageAdapter.swapList(imageItems);
        assertEquals(3, imageAdapter.getItemCount());

    }

}