package ar.unpa.uarg.collazo.contractgenerator.viewmodel;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ajithvgiri.searchdialog.SearchListItem;
import com.ajithvgiri.searchdialog.SearchableDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import ar.unpa.uarg.collazo.contractgenerator.R;
import ar.unpa.uarg.collazo.contractgenerator.data.ContractService;
import ar.unpa.uarg.collazo.contractgenerator.listener.ItemMetricListener;
import ar.unpa.uarg.collazo.contractgenerator.model.App;
import ar.unpa.uarg.collazo.contractgenerator.model.Metric;
import ar.unpa.uarg.collazo.contractgenerator.model.MetricTitle;
import ar.unpa.uarg.collazo.contractgenerator.view.HomeActivity;
import dmax.dialog.SpotsDialog;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class HomeViewModel extends Observable implements ItemMetricListener {

    private Context context;
    private List<Metric> metricList;
    private App app;

    public ObservableField<String> appName;
    public ObservableField<Drawable> appIcon;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public HomeViewModel(Context context) {
        this.context = context;
        metricList = new ArrayList<>();
        app = null;

        appName = new ObservableField<>(context.getString(R.string.hint_select_app));
        appIcon = new ObservableField<>(ContextCompat.getDrawable(context, R.drawable.ic_default_icon));

    }

    public void loadMetrics() {
        Single<List<Metric>> todosSingle = ContractService.loadMetrics();

        Disposable disposable = todosSingle.subscribeWith(new DisposableSingleObserver<List<Metric>>() {

            @Override
            public void onSuccess(List<Metric> todos) {
                metricList = todos;
                notifyChanges();
            }

            @Override
            public void onError(Throwable e) {
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        compositeDisposable.add(disposable);
    }

    public void loadApp() {
        Single<App> todosSingle = ContractService.loadApp(context);

        Disposable disposable = todosSingle.subscribeWith(new DisposableSingleObserver<App>() {

            @Override
            public void onSuccess(App todos) {
                app = todos;
                if (app != null) {
                    appIcon.set(app.getIcon());
                    appName.set(app.getTitle());
                }
            }

            @Override
            public void onError(Throwable e) {

//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        compositeDisposable.add(disposable);
    }

    public void notifyChanges() {
        setChanged();
        notifyObservers();
    }

    public void onFabClick(View view) {
        ((HomeActivity) context).showBottomMetricDialog(null);
    }

    public List<Metric> getMetricList() {
        return metricList;
    }

    public void reset() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onItemMetricEditClick(Metric metric) {
        ((HomeActivity) context).showBottomMetricDialog(metric);
        Toast.makeText(context, "EditClick", Toast.LENGTH_SHORT);
    }

    @Override
    public void onItemMetricRemoveClick(Metric metric) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.message_remove_dialog)
                .setTitle(R.string.title_remove_dialog)
                .setPositiveButton(R.string.title_yes_button_dialog, (dialog, which) -> {
                    deleteItemMetric(metric.getId());
                })
                .setNegativeButton(R.string.title_no_button_dialog, (dialog, which) -> {
                    dialog.dismiss();
                });

        builder.create().show();


    }

    private void deleteItemMetric(int id) {
        Single<Boolean> todosSingle = ContractService.removeMetric(id);

        Disposable disposable = todosSingle.subscribeWith(new DisposableSingleObserver<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {
                if (success) {
//                    notifyChanges();
                    ((Activity) context).runOnUiThread(() -> {
                        ((HomeActivity) context).update(HomeViewModel.this, null);
                    });
                } else {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "The action could not be completed", Toast.LENGTH_LONG);
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        compositeDisposable.add(disposable);
    }

    public void onAppBtnClick(View view) {

        AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(context)
                .setMessage(R.string.title_loading_dialog)
                .setCancelable(false)
                .build();
        dialog.show();

        Single<List<App>> todosSingle = ContractService.loadApplications(context);

        Disposable disposable = todosSingle.subscribeWith(new DisposableSingleObserver<List<App>>() {

            @Override
            public void onSuccess(List<App> todos) {
                dialog.dismiss();
                ((Activity) context).runOnUiThread(() -> {
                    showSearchDialog(todos);
                });
            }

            @Override
            public void onError(Throwable e) {
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        compositeDisposable.add(disposable);
    }

    private void showSearchDialog(final List<App> apps) {

        List<SearchListItem> searchListItems = new ArrayList<>();
        for (int i = 0; i < apps.size(); i++) {
            searchListItems.add(new SearchListItem(i, apps.get(i).getTitle(), apps.get(i).getIcon()));
        }

        SearchableDialog searchableDialog = new SearchableDialog(((Activity) context), searchListItems, context.getString(R.string.title_search_dialog_app));

        searchableDialog.setOnItemSelected((position, searchListItem) -> {
            app = apps.get(position);
            appName.set(apps.get(position).getTitle());
            appIcon.set(apps.get(position).getIcon());
            ContractService.setApp(apps.get(position));
//            buttonText.set(apps.get(position).getTitle());
//            metricId = apps.get(position).getId();
        });
        searchableDialog.show();
    }

    public void exportContractXml(){
        String errorMsg = "";

        if (app == null) {
            errorMsg = "- You must select an application.\n";
        }
        if (metricList.size() == 0) {
            errorMsg += "- You must add at less an property.\n";
        }

        if (errorMsg.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(R.string.title_error_dialog)
                    .setMessage(errorMsg)
                    .setPositiveButton(R.string.title_ok_button_dialog, (dialog, which) -> {
                        dialog.dismiss();
                    });

            builder.create().show();
        } else {
            ContractService.generateContractXml();
        }
    }
}
