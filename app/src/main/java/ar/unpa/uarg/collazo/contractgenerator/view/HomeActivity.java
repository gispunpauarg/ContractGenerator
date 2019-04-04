package ar.unpa.uarg.collazo.contractgenerator.view;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ar.unpa.uarg.collazo.contractgenerator.R;
import ar.unpa.uarg.collazo.contractgenerator.data.ContractService;
import ar.unpa.uarg.collazo.contractgenerator.databinding.ActivityHomeBinding;
import ar.unpa.uarg.collazo.contractgenerator.databinding.DialogMetricBinding;
import ar.unpa.uarg.collazo.contractgenerator.model.Metric;
import ar.unpa.uarg.collazo.contractgenerator.viewmodel.BottomDialogViewModel;
import ar.unpa.uarg.collazo.contractgenerator.viewmodel.HomeViewModel;
import ir.mtajik.android.advancedPermissionsHandler.PermissionHandlerActivity;

public class HomeActivity extends PermissionHandlerActivity implements Observer {

    private ActivityHomeBinding homeActivityBinding;
    private HomeViewModel homeViewModel;

    private BottomDialogViewModel bottomDialogViewModel;
    private DialogMetricBinding dialogMetricBinding;

    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);*/

        initDataBinding();
        setSupportActionBar(homeActivityBinding.toolbar);

        setupAdapter(homeActivityBinding.recyclerView);
        sertupObserver(homeViewModel);

        loadData();

    }

    private void initDataBinding() {
        homeActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        homeViewModel = new HomeViewModel(this);
        homeActivityBinding.setHome(homeViewModel);
    }

    private void setupAdapter(RecyclerView recyclerView) {
        MetricAdapter adapter = new MetricAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setListener(homeViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void sertupObserver(Observable observable) {
        observable.addObserver(this);
    }

    public void loadData(){
        homeViewModel.loadApp();
        homeViewModel.loadMetrics();
    }

    public void showBottomMetricDialog(Metric metric){
        dialogMetricBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_metric,
                        null, false);

        boolean edit = (metric != null);
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(dialogMetricBinding.getRoot());
        bottomDialogViewModel = new BottomDialogViewModel(metric, mBottomSheetDialog, edit, this);
        dialogMetricBinding.setBottomDialogViewModel(bottomDialogViewModel);

        mBottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(android.support.design.R.id.design_bottom_sheet);

            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        mBottomSheetDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export_contract) {
            String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

            boolean stickyMode = true;

            askForPermission(permissions , stickyMode, new PermissionCallBack() {

                @Override
                public void onPermissionsGranted() {
                    homeViewModel.exportContractXml();
                    Toast.makeText(HomeActivity.this, "ContractXml generated", Toast.LENGTH_SHORT);
                    Log.i("mahdi", "onPermissionsGranted: ");
                }

                @Override
                public void onPermissionsDenied(String[] permissions) {
                    Toast.makeText(HomeActivity.this, "Permission Denied", Toast.LENGTH_SHORT);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homeViewModel.reset();
        if (bottomDialogViewModel != null){
            bottomDialogViewModel.reset();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof HomeViewModel || observable instanceof BottomDialogViewModel) {
            MetricAdapter metricAdapter = (MetricAdapter) homeActivityBinding.recyclerView.getAdapter();
//            HomeViewModel homeViewModel = (HomeViewModel) observable;
            metricAdapter.setMetricList(homeViewModel.getMetricList());
//            metricAdapter.setListener();
        }
    }
}
