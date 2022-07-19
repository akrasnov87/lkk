package ru.mobnius.cic.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.FragmentImageBinding;
import ru.mobnius.cic.ui.viewmodels.result.BaseResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.NewResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.SavedResultViewModel;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.TouchUtil;

public class ImageViewFragment extends BaseNavigationFragment implements View.OnTouchListener {
    public final static String IMAGE_DELETE_RESULT = "ru.mobnius.cic.ui.fragment.IMAGE_DELETE_RESULT";

    @NonNull
    private final Matrix matrix = new Matrix();
    @NonNull
    private final Matrix savedMatrix = new Matrix();
    @Nullable
    private BaseResultViewModel viewModel;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int currentMode = NONE;
    private float oldDist = 1f;

    @NonNull
    private final PointF start = new PointF();
    @NonNull
    private final PointF mid = new PointF();

    @Nullable
    private FragmentImageBinding binding;

    private String imageId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isPointDone = ImageViewFragmentArgs.fromBundle(getArguments()).getIsPointDone();
        imageId = ImageViewFragmentArgs.fromBundle(getArguments()).getImageId();
        if (isPointDone) {
            viewModel = new ViewModelProvider(requireActivity()).get(SavedResultViewModel.class);
        } else {
            viewModel = new ViewModelProvider(requireActivity()).get(NewResultViewModel.class);
        }
        viewModel.getImageItem(imageId, getErrorBitmap());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);
        binding.fragmentImageToolbar.setNavigationOnClickListener(v -> handleBackButton());
        if (viewModel == null || viewModel.imageViewShowingItem == null) {
            return binding.getRoot();
        }
        binding.fragmentImageView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (!isAdded()) {
                return;
            }
            centerImage(viewModel.imageViewShowingItem.bitmap);
            binding.fragmentImageView.setImageMatrix(matrix);
            binding.fragmentImageView.setImageBitmap(viewModel.imageViewShowingItem.bitmap);
        });
        binding.fragmentImageView.setOnTouchListener(this);
        binding.fragmentImageDelete.setEnabled(!viewModel.canNotUpdateResult());
        binding.fragmentImageDelete.setOnClickListener(view ->
                confirmDialog((dialog, which) -> {
                    final NavController controller = NavHostFragment.findNavController(this);
                    final NavBackStackEntry backStackEntry = controller.getPreviousBackStackEntry();
                    if (backStackEntry == null) {
                        NavHostFragment.findNavController(this).navigateUp();
                        return;
                    }
                    backStackEntry.getSavedStateHandle().set(IMAGE_DELETE_RESULT, imageId);
                    NavHostFragment.findNavController(this).navigateUp();
                }));

        binding.fragmentImageCenter.setOnClickListener(view -> {
            centerImage(viewModel.imageViewShowingItem.bitmap);
            binding.fragmentImageView.setImageMatrix(matrix);
            binding.fragmentImageView.setImageBitmap(viewModel.imageViewShowingItem.bitmap);
        });
        binding.fragmetnImageRotate.setOnClickListener(view -> {
            final float centerVertical = (float) binding.fragmentImageView.getHeight() / 2;
            final float centerHorizontal = (float) binding.fragmentImageView.getWidth() / 2;
            matrix.postRotate(90.0F, centerHorizontal, centerVertical);
            binding.fragmentImageView.setImageMatrix(matrix);
        });

        binding.fragmentImageToolbar.setTitle(viewModel.imageViewShowingItem.imageType.typeName);
        binding.fragmentImageToolbar.setSubtitle(DateUtil.convertDateToCustomString(viewModel.imageViewShowingItem.date, DateUtil.USER_SHORT_FORMAT));

        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (binding == null) {
            return false;
        }
        binding.fragmentImageView.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // палец на экране
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                currentMode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // палец убран с экрана

            case MotionEvent.ACTION_POINTER_UP: // второй палец убран с экрана

                currentMode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // оба пальца на экране

                oldDist = TouchUtil.spaceCalculation(event);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    TouchUtil.midPoint(mid, event);
                    currentMode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int displayHeight = requireActivity().getWindow().getDecorView().getHeight() - 500;
                if (currentMode == DRAG && event.getY() > 0 && event.getY() < displayHeight) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (currentMode == ZOOM) {

                    float newDist = TouchUtil.spaceCalculation(event);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        binding.fragmentImageView.setImageMatrix(matrix); // отображение изменений на экране

        return true;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void centerImage(final @NonNull Bitmap bitmap) {
        if (binding == null) {
            return;
        }
        final float width = (float) bitmap.getWidth();
        final float displayWidth = (float) (requireActivity().getWindow()).getDecorView().getWidth();
        final float index = displayWidth / width;
        final float offsetX = (binding.fragmentImageView.getWidth() - bitmap.getWidth()) / 2F;
        final float offsetY = (binding.fragmentImageView.getHeight() - bitmap.getHeight()) / 2F;

        final float centerX = binding.fragmentImageView.getWidth() / 2F;
        final float centerY = binding.fragmentImageView.getHeight() / 2F;

        matrix.setScale(index, index, centerX, centerY);
        matrix.preTranslate(offsetX, offsetY);
    }

    private void confirmDialog(DialogInterface.OnClickListener listener) {
        if (viewModel == null || viewModel.imageViewShowingItem == null) {
            return;
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());
        adb.setPositiveButton(getResources().getString(R.string.yes), listener);

        adb.setNegativeButton(getResources().getString(R.string.exit), null);

        AlertDialog alert = adb.create();
        alert.setTitle("Вы уверены что хотите удалить " + "изображение" + "?");
        alert.show();
    }

    @NonNull
    public Bitmap getErrorBitmap() {
        final Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.cic_error_icon);
        if (drawable == null) {
            final Bitmap.Config config = Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(1, 1, config);
        }
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Обработка нажатия системной кнопки назад
     */
    @NonNull
    @Override
    public OnBackPressedCallback getBackPressedCallBack() {
        return new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButton();
            }
        };
    }

    /**
     * Проверка является ли текущий фрагмент подходящим для навигации
     */
    @Override
    public boolean isCurrentDestination() {
        final NavDestination destination = NavHostFragment.findNavController(this).getCurrentDestination();
        if (destination == null) {
            return false;
        }
        return destination.getId() == R.id.image_view_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).popBackStack(R.id.image_view_fragment, true);
        }
    }
}
