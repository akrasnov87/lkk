package ru.mobnius.cic.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.RouteTypeAdapter;
import ru.mobnius.cic.adaper.holder.route.OnRouteInfoClickListener;
import ru.mobnius.cic.adaper.holder.route.OnRouteItemClickListener;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.ServerTimeTask;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.databinding.FragmentRouteBinding;
import ru.mobnius.cic.ui.model.ProfileItem;
import ru.mobnius.cic.ui.model.concurent.LoadRoutesResult;
import ru.mobnius.cic.ui.model.concurent.TimeRequestResult;
import ru.mobnius.cic.ui.viewmodels.RouteViewModel;
import ru.mobnius.simple_core.utils.DeviceUtil;
import ru.mobnius.simple_core.utils.NetworkInfoUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class RouteFragment extends BaseNavigationFragment {

    @Nullable
    private FragmentRouteBinding binding;
    @Nullable
    private RouteViewModel viewModel;
    @Nullable
    private RouteTypeAdapter routeTypeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RouteViewModel.class);
        viewModel.loadingLiveData.observe(this, (loading) -> {
            if (binding == null) {
                return;
            }
            if (loading) {
                binding.fragmentRouteList.setVisibility(View.GONE);
                binding.fragmentRouteProgress.setVisibility(View.VISIBLE);
                binding.fragmentRouteSync.setVisibility(View.GONE);
            } else {
                binding.fragmentRouteList.setVisibility(View.VISIBLE);
                binding.fragmentRouteProgress.setVisibility(View.GONE);
                if (viewModel.currentResult.isEmpty()) {
                    binding.fragmentRouteSync.setVisibility(View.VISIBLE);
                } else {
                    binding.fragmentRouteSync.setVisibility(View.GONE);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRouteBinding.inflate(inflater, container, false);
        binding.fragmentRouteToolbar.setNavigationOnClickListener(v -> binding.fragmentRouteDrawerLayout.openDrawer(GravityCompat.START));
        if (viewModel == null) {
            return binding.getRoot();
        }
        inflateMenu();
        binding.fragmentRouteList.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (routeTypeAdapter == null) {
            final OnRouteItemClickListener itemClickListener = routeItem -> {
                final RouteFragmentDirections.ActionRouteToPoint actionRouteToPoint =
                        RouteFragmentDirections.actionRouteToPoint(routeItem.id, routeItem.name);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(actionRouteToPoint);
                }
            };
            final OnRouteInfoClickListener infoClickListener = routeItem -> {
                final RouteFragmentDirections.ActionRouteToRouteInfo action =
                        RouteFragmentDirections.actionRouteToRouteInfo().setRouteItem(routeItem);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(action);
                }
            };
            routeTypeAdapter = new RouteTypeAdapter(viewModel.currentResult, itemClickListener, infoClickListener);
        }
        binding.fragmentRouteList.setAdapter(routeTypeAdapter);
        loadRoutes();


        binding.fragmentRouteNavigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigationDrawerSynchronization) {
                binding.fragmentRouteDrawerLayout.closeDrawer(GravityCompat.START);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_route_to_sync);
                }
                return false;
            }
            if (item.getItemId() == R.id.navigationDrawerSettings) {
                binding.fragmentRouteDrawerLayout.closeDrawer(GravityCompat.START);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_route_to_settings);
                }
                return false;
            }
            if (item.getItemId() == R.id.navigationDrawerProfile) {
                binding.fragmentRouteDrawerLayout.closeDrawer(GravityCompat.START);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_route_to_profile);
                }
                return false;
            }
            if (item.getItemId() == R.id.navigationDrawerExit) {
                binding.fragmentRouteDrawerLayout.closeDrawer(GravityCompat.START);
                alert(getString(R.string.confirmExit),
                        getString(R.string.yes), (dialog, which) -> {
                            ((MobniusApplication) requireActivity().getApplication()).unAuthorized(true);
                            if (isCurrentDestination()) {
                                NavHostFragment.findNavController(this).popBackStack(R.id.route_fragment, true);
                                final NavController navController = Navigation.findNavController(requireActivity(), R.id.auth_nav_host);
                                final NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
                                navGraph.setStartDestination(R.id.auth_nav);
                                navController.setGraph(navGraph);
                            }
                        },
                        getString(R.string.no), (dialog, which) -> dialog.dismiss());
                return false;
            }
            if (item.getItemId() == R.id.navigationDrawerMaps) {
                binding.fragmentRouteDrawerLayout.closeDrawer(GravityCompat.START);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(R.id.action_route_to_map);
                }
                return false;
            }
            /**
             if (item.getItemId() == R.id.navigationDrawerSearch) {
             binding.fragmentRouteDrawerLayout.closeDrawer(GravityCompat.START);
             if (isCurrentDestination()) {
             NavHostFragment.findNavController(this).navigate(R.id.action_route_to_search);
             }
             return false;
             }
             */
            return false;
        });

        binding.fragmentRouteSync.setOnClickListener(view -> {
            if (isCurrentDestination()) {
                NavHostFragment.findNavController(this).navigate(R.id.action_route_to_sync);
            }
        });

        setProfile();

        showUnsyncMessage();

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (NetworkInfoUtil.isNetworkAvailable(requireContext())) {
            //TODO пока это убираем
            final MainTaskExecutor mainTaskExecutor = new MainTaskExecutor();
            final ServerTimeTask serverTimeTask = new ServerTimeTask();
            mainTaskExecutor.executeAsync(serverTimeTask, new MainTaskExecutorCallback<TimeRequestResult>() {
                @Override
                public void onCallableComplete(@NonNull TimeRequestResult result) {
                    if (!isAdded()) {
                        return;
                    }
                    requireActivity().runOnUiThread(() -> {
                        if (result.isError) {
                            timeProblemAlert(result.message);
                        }
                    });
                }

                @Override
                public void onCallableError(@NonNull String error) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());

                }
            });
        }
        //TODO: Необходимо где то реализовать проверку на версии (AppVesionTask уже готов), несколько асинхронных операций идущих одна за другой могут привести к ошибкам и запутать пользователя
        DeviceUtil.checkDevices(requireActivity());
    }


    protected void timeProblemAlert(final @NonNull String message) {
        new AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS))).show();
    }

    private void showUnsyncMessage() {
        if (viewModel == null || DataManager.getInstance() == null || binding == null) {
            return;
        }
        final int unsyncCount = DataManager.getInstance().getUnSyncCount();
        if (unsyncCount <= 0) {
            return;
        }
        if (viewModel.unsyncCount != unsyncCount) {
            viewModel.isUnsyncMessageShown = false;
            viewModel.unsyncCount = unsyncCount;
        }
        if (viewModel.isUnsyncMessageShown) {
            return;
        }
        final String message = getResources().getQuantityString(R.plurals.plurals_routes, unsyncCount, unsyncCount);
        Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        viewModel.isUnsyncMessageShown = true;
    }

    private void inflateMenu() {
        if (binding == null || viewModel == null) {
            return;
        }
        binding.fragmentRouteToolbar.inflateMenu(R.menu.menu_route);
        final Menu menu = binding.fragmentRouteToolbar.getMenu();
        if (menu == null) {
            return;
        }
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        if (menuItem == null || !(menuItem.getActionView() instanceof SearchView)) {
            return;
        }

        final SearchView searchView = (SearchView) menuItem.getActionView();
        final EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white));
        }
        searchView.setQuery(viewModel.query, false);

        searchView.setOnCloseListener(() -> {
            final Observer<LoadRoutesResult> refreshObserver = result -> {
                if (!isAdded() || viewModel == null || binding == null || routeTypeAdapter == null) {
                    return;
                }
                routeTypeAdapter.refresh(result);
            };
            viewModel.query = StringUtil.EMPTY;
            viewModel.refreshRoutes().observe(getViewLifecycleOwner(), refreshObserver);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@Nullable String query) {
                if (routeTypeAdapter == null) {
                    return false;
                }
                if (StringUtil.isEmpty(query)) {
                    viewModel.query = StringUtil.EMPTY;
                } else {
                    final Observer<LoadRoutesResult> searchObs = result -> {
                        if (!isAdded()) {
                            return;
                        }
                        routeTypeAdapter.refresh(result);
                    };
                    viewModel.query = query;
                    viewModel.searchAsync().observe(getViewLifecycleOwner(), searchObs);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(@Nullable String newText) {
                return false;
            }
        });


    }

    private void loadRoutes() {
        if (viewModel == null || binding == null) {
            return;
        }
        final Observer<LoadRoutesResult> routeObserver = result -> {
            if (!isAdded() || viewModel == null || binding == null) {
                return;
            }

            if (routeTypeAdapter == null) {
                viewModel.deleteTempImages(result.getAllRouteItems(), requireContext().getFilesDir());
            } else {
                routeTypeAdapter.refresh(result);
            }

        };
        viewModel.refreshRoutes().observe(getViewLifecycleOwner(), routeObserver);
    }

    private void setProfile() {
        if (binding == null || DataManager.getInstance() == null) {
            return;
        }
        final View headerView = binding.fragmentRouteNavigationView.getHeaderView(0);
        if (headerView == null) {
            return;
        }
        final TextView tvDescription = headerView.findViewById(R.id.nav_user_name);
        final ProfileItem profile = DataManager.getInstance().getProfile();
        if (profile != null) {
            tvDescription.setText(profile.fio);
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }
    }

    private void alert(final @NonNull String message,
                       final @NonNull String positiveText,
                       final @NonNull DialogInterface.OnClickListener positiveListener,
                       final @Nullable String negativeText,
                       final @Nullable DialogInterface.OnClickListener negativeListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveText, positiveListener);
        if (StringUtil.isNotEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, negativeListener);
        }
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding == null) {
            return;
        }
        binding.fragmentRouteList.setAdapter(null);
        routeTypeAdapter = null;
        binding = null;
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
                NavHostFragment.findNavController(RouteFragment.this).popBackStack(R.id.route_fragment, true);
                requireActivity().finish();
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
        return destination.getId() == R.id.route_fragment;
    }

}

