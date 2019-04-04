package ar.unpa.uarg.collazo.contractgenerator.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Looper;

import com.appizona.yehiahd.fastsave.FastSave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ar.unpa.uarg.collazo.contractgenerator.model.App;
import ar.unpa.uarg.collazo.contractgenerator.model.Metric;
import ar.unpa.uarg.collazo.contractgenerator.model.MetricTitle;
import io.reactivex.Single;

public class ContractService {

    private static List<Metric> metricList;
    private static App app;


    public static Single<List<Metric>> loadMetrics() {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    if (metricList == null) {
                        loadMetricListFromStorage();
                    }

//                    metricList.add(new Metric(1, "Latency", new boolean[]{true, false, false}, true, false, true));
//                    metricList.add(new Metric(1, "Jitter", new boolean[]{false, true, false}, false, true, false));

                    Looper.prepare();
                    emitter.onSuccess(metricList);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    public static Single<App> loadApp(Context context) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    if (app == null) {
                        loadAppFromStorage(context);
                    }

//                    metricList.add(new Metric(1, "Latency", new boolean[]{true, false, false}, true, false, true));
//                    metricList.add(new Metric(1, "Jitter", new boolean[]{false, true, false}, false, true, false));
                    Looper.prepare();
                    emitter.onSuccess(app);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    public static Single<Boolean> addMetric(Metric metric) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    boolean flag = true;
                    for (Metric m : metricList) {
                        if (m.getId() == metric.getId()) {
                            flag = false;
                        }
                    }

                    if (flag) {
                        metricList.add(metric);
                        saveDataToStorage();
                    }
                    emitter.onSuccess(flag);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    public static Single<Boolean> removeMetric(int id) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    boolean flag = false;
                    int position = -1;
                    for (int i = 0; i < metricList.size(); i++) {
                        if (metricList.get(i).getId() == id) {
                            position = i;
                            flag = true;
                        }
                    }

                    if (flag) {
                        metricList.remove(position);
                        saveDataToStorage();
                    }
                    emitter.onSuccess(flag);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    public static Single<Boolean> editMetric(int id, Metric metric) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    boolean flag = false;
                    int position = -1;
                    for (int i = 0; i < metricList.size(); i++) {
                        if (metricList.get(i).getId() == id) {
                            position = i;
                            flag = true;
                        }
                    }

                    if (flag) {
                        metricList.set(position, metric);
                        saveDataToStorage();
                    }
                    emitter.onSuccess(flag);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    public static void setApp(App a) {
        app = a;
        saveDataToStorage();
    }

    public static Single<List<MetricTitle>> loadMetricsTitles() {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {

                    List<MetricTitle> metricTitleList = Constant.getMeticTitleList();

                    emitter.onSuccess(metricTitleList);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });

    }

    private static void loadMetricListFromStorage() {
        try {
            metricList = FastSave.getInstance().getObjectsList(Constant.PREFERENCE_METRIC_LIST_KEY, Metric.class);
            if (metricList == null) {
                metricList = new ArrayList<>();
            }
        } catch (Exception e) {
        }
    }

    private static void loadAppFromStorage(Context context) {
        try {
            app = FastSave.getInstance().getObject(Constant.PREFERENCE_APP_KEY, App.class);
            if (app != null) {
                getAppIconDrawable(context, app.getPackageName());
            }
        } catch (Exception e) {
            Logger.getLogger(ContractService.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void getAppIconDrawable(Context context, String packageName) {
        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
            app.setIcon(icon);
        } catch (PackageManager.NameNotFoundException e) {
            app.setIcon(null);
        }
    }

    private static void saveDataToStorage() {
        try {
            FastSave.getInstance().saveObjectsList(Constant.PREFERENCE_METRIC_LIST_KEY, metricList);
            if (app != null) {
                app.setIcon(null);
            }
            FastSave.getInstance().saveObject(Constant.PREFERENCE_APP_KEY, app);
        } catch (Exception e) {

        }
    }

    public static void generateContractXml() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";

        xml += String.format("<contract app=\"%s\">\n", app.getPackageName());

        for (Metric m : metricList) {

            xml += String.format("\t<property name=\"%s\">\n", m.getTitle());

            String range = m.getOptionText();
            xml += String.format("\t\t<range>%s</range>\n", range);

            xml += "\t\t<actions>\n";

            if (m.isMonitoring()) {
                xml += "\t\t\t<monitoring/>\n";
            }
            if (m.isAlertInf()) {
                xml += "\t\t\t<informative/>\n";
            }
            if (m.isAlertConf()) {
                xml += "\t\t\t<confirtmatory/>\n";
            }

            xml += "\t\t</actions>\n";

            xml += "\t</property>\n";
        }

        xml += "</contract>\n";



        File file = new File(Environment.getExternalStorageDirectory(), "contractXml.xml");
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(xml);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Log.i(TAG, "******* File not found. Did you" +
//                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static Single<List<App>> loadApplications(Context context) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                List<App> apps = new ArrayList<>();

                try {

                    PackageManager packageManager = context.getPackageManager();
                    List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

                    for (ApplicationInfo appInfo : list) {
                        Drawable icon = appInfo.loadIcon(packageManager);
                        String title = appInfo.loadLabel(packageManager).toString();
                        String packageName = appInfo.packageName;
                        apps.add(new App(icon, title, packageName));
                    }

                    emitter.onSuccess(apps);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });

    }

}
