package ar.unpa.uarg.collazo.contractgenerator.listener;

import ar.unpa.uarg.collazo.contractgenerator.model.Metric;

public interface ItemMetricListener {

    public void onItemMetricEditClick(Metric metric);

    public void onItemMetricRemoveClick(Metric metric);
}
