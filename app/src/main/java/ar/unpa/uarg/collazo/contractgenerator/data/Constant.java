package ar.unpa.uarg.collazo.contractgenerator.data;

import java.util.ArrayList;
import java.util.List;

import ar.unpa.uarg.collazo.contractgenerator.model.MetricTitle;

public class Constant {

    public static final String PREFERENCE_METRIC_LIST_KEY = "PREFERENCE_METRIC_LIST_KEY";
    public static final String PREFERENCE_APP_KEY = "PREFERENCE_APP_KEY";
    private static List<MetricTitle> metricTitleList;

    public static List<MetricTitle> getMeticTitleList(){
        if (metricTitleList == null){
            initMetricTitleList();
        }

        return metricTitleList;
    }

    private static void initMetricTitleList(){
        metricTitleList = new ArrayList<>();
        metricTitleList.add(new MetricTitle(1, "Delay or latency"));
        metricTitleList.add(new MetricTitle(2, "Jitter"));
        metricTitleList.add(new MetricTitle(3, "Packet loss"));
        metricTitleList.add(new MetricTitle(4, "Throughput"));
        metricTitleList.add(new MetricTitle(5, "User perceived latency"));
        metricTitleList.add(new MetricTitle(6, "Connection type"));
        metricTitleList.add(new MetricTitle(7, "Signal strength"));
        metricTitleList.add(new MetricTitle(8, "CPU consumption"));
        metricTitleList.add(new MetricTitle(9, "Energy consumption"));
        metricTitleList.add(new MetricTitle(10, "Screen brightness level"));
        // ...

    }

}
