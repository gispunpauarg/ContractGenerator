package ar.unpa.uarg.collazo.contractgenerator.view;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import java.util.Collections;
import java.util.List;

import ar.unpa.uarg.collazo.contractgenerator.R;
import ar.unpa.uarg.collazo.contractgenerator.databinding.ItemMetricBinding;
import ar.unpa.uarg.collazo.contractgenerator.listener.ItemMetricListener;
import ar.unpa.uarg.collazo.contractgenerator.model.Metric;
import ar.unpa.uarg.collazo.contractgenerator.viewmodel.ItemMetricViewModel;

public class MetricAdapter extends RecyclerView.Adapter<MetricAdapter.MetricAdapterViewHolder> {

    private List<Metric> metricList;
    private ItemMetricListener listener;

    public MetricAdapter() {
        this.metricList = Collections.emptyList();
    }

    @Override public MetricAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMetricBinding itemMetricBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_metric,
                        parent, false);
        return new MetricAdapterViewHolder(itemMetricBinding);
    }

    @Override public void onBindViewHolder(MetricAdapterViewHolder holder, int position) {
        holder.bindMetric(metricList.get(position));
    }

    @Override public int getItemCount() {
        return metricList.size();
    }

    public void setMetricList(List<Metric> metricList) {
        this.metricList = metricList;
        notifyDataSetChanged();
    }

    public void setListener(ItemMetricListener listener){
        this.listener = listener;
    }

    public class MetricAdapterViewHolder extends RecyclerView.ViewHolder {
        ItemMetricBinding itemMetricBinding;

        public MetricAdapterViewHolder(ItemMetricBinding itemMetricBinding) {
            super(itemMetricBinding.cardView);
            this.itemMetricBinding = itemMetricBinding;
        }

        void bindMetric(Metric metric) {
            if (itemMetricBinding.getMetricViewModel() == null) {
                itemMetricBinding.setMetricViewModel(
                        new ItemMetricViewModel(metric, listener, itemView.getContext()));
            } else {
                itemMetricBinding.getMetricViewModel().setMetric(metric);
            }
        }
    }
}