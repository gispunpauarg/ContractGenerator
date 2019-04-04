package ar.unpa.uarg.collazo.contractgenerator.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Toast;

import com.ajithvgiri.searchdialog.SearchListItem;
import com.ajithvgiri.searchdialog.SearchableDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import ar.unpa.uarg.collazo.contractgenerator.R;
import ar.unpa.uarg.collazo.contractgenerator.data.ContractService;
import ar.unpa.uarg.collazo.contractgenerator.model.Metric;
import ar.unpa.uarg.collazo.contractgenerator.model.MetricTitle;
import ar.unpa.uarg.collazo.contractgenerator.view.HomeActivity;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class BottomDialogViewModel extends Observable {

    private Metric metric;
    private Context context;
    private BottomSheetDialog dialog;

    public ObservableField<String> title;

    private int metricId;
    public ObservableField<String> buttonText;

    public ObservableBoolean optionRegular;
    public ObservableBoolean optionMiddle;
    public ObservableBoolean optionBad;

    public ObservableBoolean actionMonitor;
    public ObservableBoolean alertInf;
    public ObservableBoolean alertConf;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BottomDialogViewModel(Metric metric, BottomSheetDialog dialog, boolean edit, Context context) {
        this.metric = metric;
        this.dialog = dialog;
        this.context = context;

        title = new ObservableField<>(context.getString(R.string.title_add_botton_dialog));
        buttonText = new ObservableField<>("");
        optionRegular = new ObservableBoolean(false);
        optionMiddle = new ObservableBoolean(false);
        optionBad = new ObservableBoolean(false);
        actionMonitor = new ObservableBoolean(false);
        alertInf = new ObservableBoolean(false);
        alertConf = new ObservableBoolean(false);

        if (edit) {
            title.set(context.getString(R.string.title_edit_botton_dialog));
        }

        if (metric == null) {
            buttonText.set(context.getString(R.string.hint_select_metric));
            metricId = -1;
            optionMiddle.set(true);
        } else {
            metricId = metric.getId();
            buttonText.set(metric.getTitle());
            optionRegular.set(metric.getOptions()[0]);
            optionMiddle.set(metric.getOptions()[1]);
            optionBad.set(metric.getOptions()[2]);
            actionMonitor.set(metric.isMonitoring());
            alertInf.set(metric.isAlertInf());
            alertConf.set(metric.isAlertConf());
        }
    }

    public void onCancelClick(View view) {
        dialog.dismiss();
    }

    public void onAceptClick(View view) {

        String errorMsg = "";

        if (metricId == -1) {
            errorMsg = "- You must select a property.\n";
        }
        if (!actionMonitor.get() && !alertInf.get() && !alertConf.get()) {
            errorMsg += "- You must select at less an action or an alert.\n";
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

            Metric actualMetric = new Metric(metricId, buttonText.get()
                    , new boolean[]{optionRegular.get(), optionMiddle.get(), optionBad.get()}
                    , actionMonitor.get(), alertInf.get(), alertConf.get());

            Single<Boolean> todosSingle;
            if (metric != null) { // Edit
                todosSingle = ContractService.editMetric(metric.getId(), actualMetric);
            } else { // Add
                todosSingle = ContractService.addMetric(actualMetric);
            }

            Disposable disposable = todosSingle.subscribeWith(new DisposableSingleObserver<Boolean>() {

                @Override
                public void onSuccess(Boolean success) {
                    if (success) {
                        setChanged();
                        notifyObservers();
                        ((Activity) context).runOnUiThread(() -> {
                            ((HomeActivity) context).update(BottomDialogViewModel.this, null);
                        });

                    } else {
                        ((Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "The action could not be completed", Toast.LENGTH_LONG);
                        });
                    }
                    dialog.dismiss();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            compositeDisposable.add(disposable);

        }
    }

    public void onSelectMetricClick(View view) {

        Single<List<MetricTitle>> todosSingle = ContractService.loadMetricsTitles();

        Disposable disposable = todosSingle.subscribeWith(new DisposableSingleObserver<List<MetricTitle>>() {

            @Override
            public void onSuccess(List<MetricTitle> todos) {
                ((Activity) context).runOnUiThread(() -> {
                    showSearchDialog(todos);
                });
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        compositeDisposable.add(disposable);
    }

    private void showSearchDialog(final List<MetricTitle> metricTitles) {

        List<SearchListItem> searchListItems = new ArrayList<>();
        for (int i = 0; i < metricTitles.size(); i++) {
            searchListItems.add(new SearchListItem(metricTitles.get(i).getId(), metricTitles.get(i).getTitle(), null));
        }

        SearchableDialog searchableDialog = new SearchableDialog(((Activity) context), searchListItems, context.getString(R.string.title_search_dialog_property));

        searchableDialog.setOnItemSelected((position, searchListItem) -> {
            buttonText.set(metricTitles.get(position).getTitle());
            metricId = metricTitles.get(position).getId();
        });
        searchableDialog.show();
    }

    public void reset() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

}
