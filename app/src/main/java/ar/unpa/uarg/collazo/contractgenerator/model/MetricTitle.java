package ar.unpa.uarg.collazo.contractgenerator.model;

public class MetricTitle {

    private int id;
    private String title;

    public MetricTitle(int id, String title) {
        this.id = id;
        this.title = title;
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
}
