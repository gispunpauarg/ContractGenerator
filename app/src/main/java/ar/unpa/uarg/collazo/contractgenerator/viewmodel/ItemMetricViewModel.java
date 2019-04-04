package ar.unpa.uarg.collazo.contractgenerator.viewmodel;


import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import ar.unpa.uarg.collazo.contractgenerator.listener.ItemMetricListener;
import ar.unpa.uarg.collazo.contractgenerator.model.Metric;

public class ItemMetricViewModel extends BaseObservable {

    private static final String[] OPTIONS_TITLES = {"Regular", "Middle", "Bad"};
    private static final String[] OPTIONS_COLORS = {"#f44336", "#ffb300", "#4caf50"};
    private static final String MONITORING = "Monitoring";
    private static final String INFORMATIVE_ALERT = "Informative Alert";
    private static final String CONFIRMATIVE_ALERT = "Confirmative Alert";

    private Metric metric;
    private Context context;

    public ObservableField<String> titleMetric;
    public ObservableField<String> optionMetric;
//    public ObservableField<String> optionMetricColor;
    public ObservableField<String> actionMetric;
    public ObservableInt color;
//    public ObservableBackgroundKt optionMetricColor;

    private ItemMetricListener listener;

    public ItemMetricViewModel(Metric metric, ItemMetricListener listener, Context context) {
        this.metric = metric;
        this.context = context;
        this.listener = listener;

        titleMetric = new ObservableField<>(getTitle());
        optionMetric = new ObservableField<>(getOption());
//        optionMetricColor = new ObservableField<>(getOptionColor());
        actionMetric = new ObservableField<>(getAction());
        color = new ObservableInt(Color.parseColor(getOptionColor()));
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
        titleMetric.set(getTitle());
        optionMetric.set(getOption());
//        optionMetricColor.set(getOptionColor());
        actionMetric.set(getAction());
        color.set(Color.parseColor(getOptionColor()));
    }

    public String getTitle() {
        return metric.getTitle();
    }

    public String getOption() {
        String option = "";
        for (int i = 0; i < metric.getOptions().length; i++) {
            if (metric.getOptions()[i]) {
                option = OPTIONS_TITLES[i];
                break;
            }
        }
        return option;
    }

    public String getOptionColor() {
        String optionColor = "";
        for (int i = 0; i < metric.getOptions().length; i++) {
            if (metric.getOptions()[i]) {
                optionColor = OPTIONS_COLORS[i];
                break;
            }
        }
        return optionColor;
    }

    public String getAction() {
        String action = "";

        if (metric.isMonitoring()) {
            action += MONITORING;
        }

        if (metric.isAlertInf()) {
            if (!action.isEmpty()) action += " - ";
            action += INFORMATIVE_ALERT;
        }

        if (metric.isAlertConf()) {
            if (!action.isEmpty()) action += " - ";
            action += CONFIRMATIVE_ALERT;
        }

        return action;
    }

    public void setListener(ItemMetricListener listener) {
        this.listener = listener;
    }

    public void onRemoveClick(View view) {
        if (listener != null) {
            listener.onItemMetricRemoveClick(metric);
        }
        Log.e("MY_DEBUG", "Remove Click");
    }

    public void onEditClick(View view) {
        if (listener != null) {
            listener.onItemMetricEditClick(metric);
        }
        Log.e("MY_DEBUG", "Edit Click");
    }


    /*@BindingAdapter("android:background")
    public void setBackground(View view, ObservableField<String> color){
        view.setBackgroundColor(Color.parseColor(color.get()));
    }*/

}
