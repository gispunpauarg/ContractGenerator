package ar.unpa.uarg.collazo.contractgenerator.model;

import android.graphics.drawable.Drawable;

public class App {

    private Drawable icon;
    private String title;
    private String packageName;

    public App() {
    }

    public App(Drawable icon, String title, String packageName) {
        this.icon = icon;
        this.title = title;
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
