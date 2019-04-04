package ar.unpa.uarg.collazo.contractgenerator.model;

public class Metric {

    private int id;
    private String title;
    private boolean[] options;
    private boolean monitoring;
    private boolean alertInf;
    private boolean alertConf;

    public Metric(int id, String title, boolean[] options, boolean monitoring, boolean alertInf, boolean alertConf) {
        this.id = id;
        this.title = title;
        this.options = options;
        this.monitoring = monitoring;
        this.alertInf = alertInf;
        this.alertConf = alertConf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean[] getOptions() {
        return options;
    }

    public void setOptions(boolean[] options) {
        this.options = options;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    public boolean isAlertInf() {
        return alertInf;
    }

    public void setAlertInf(boolean alertInf) {
        this.alertInf = alertInf;
    }

    public boolean isAlertConf() {
        return alertConf;
    }

    public void setAlertConf(boolean alertConf) {
        this.alertConf = alertConf;
    }

    public String getOptionText(){
        String option = "Regular";
        if (options[1]){
            option = "Middle";
        }
        if (options[2]){
            option = "Bad";
        }
        return option;
    }
}
