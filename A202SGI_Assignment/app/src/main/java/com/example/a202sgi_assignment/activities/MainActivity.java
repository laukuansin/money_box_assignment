package com.example.a202sgi_assignment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.cores.AppController;
import com.example.a202sgi_assignment.cores.BaseActivity;
import com.example.a202sgi_assignment.fragments.AccountFragment;
import com.example.a202sgi_assignment.fragments.CategoryFragment;
import com.example.a202sgi_assignment.fragments.CategoryImageFragment;
import com.example.a202sgi_assignment.fragments.MainFragment;
import com.example.a202sgi_assignment.fragments.ReportFragment;
import com.example.a202sgi_assignment.hanlders.SessionHandler;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends BaseActivity implements ActionBar.OnNavigationListener {
    private Fragment _fragment;
    private AppController _appController;
    private BottomNavigationView btmNavBar;
    private int menuRes = R.menu.export_only;
    private boolean disableMenu = false;
    private int index = 0;
    private FloatingActionButton _addButton;
    private String[] dropDownValue;
    private ActionBar actionBar;
    @Override
    protected int ContentView() {
        return R.layout.activity_bottom_navigation;
    }

    @Override
    protected void AttemptSave() {

    }

    @Override
    protected void AttemptDelete() {

    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {
        if(index==3)
        {
            Intent intent=new Intent(this,AddCategoryActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void AttemptFilter() {

    }

    @Override
    protected void AttemptRefresh() {

    }


    @Override
    protected int MenuResource() {
        return menuRes;
    }

    @Override
    protected boolean DisableActionMenu() {
        return disableMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _appController = (AppController)getApplicationContext();

        _addButton=(FloatingActionButton)findViewById(R.id.add_button) ;
        findViewById(R.id.toolbar).bringToFront();
        isStoragePermissionGranted();
        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //actionbar
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);


        btmNavBar = (BottomNavigationView)findViewById(R.id.navBar);
        btmNavBar.getMenu().getItem(0).setEnabled(false);

        btmNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                actionBar.setDisplayShowTitleEnabled(true);

                for (int i = 0; i < 5; i++) {
                    btmNavBar.getMenu().getItem(i).setEnabled(true);
                }
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        actionBar.setTitle(R.string.app_name);
                        _fragment = MainFragment.newInstance();
                        loadFragment(_fragment);
                        menuRes = R.menu.export_only;
                        disableMenu = false;
                        index = 0;
                        btmNavBar.getMenu().getItem(index).setEnabled(false);
                        break;
                    case R.id.report:
                        actionBar.setTitle("Report");
                        _fragment = ReportFragment.newInstance();
                        loadFragment(_fragment);
                        menuRes = R.menu.calendar_only;
                        disableMenu = false;
                        index = 1;
                        btmNavBar.getMenu().getItem(index).setEnabled(false);
                        break;

                    case R.id.category:
                        actionBar.setDisplayShowTitleEnabled(false);
                        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                        dropDownValue=getResources().getStringArray(R.array.category_type);
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(actionBar.getThemedContext(),R.layout.custom_spinner_item_2,android.R.id.text1,dropDownValue);
                        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
                        actionBar.setListNavigationCallbacks(adapter,MainActivity.this);


                        index = 3;
                        btmNavBar.getMenu().getItem(index).setEnabled(false);

                        break;
                    case R.id.account:
                        actionBar.setTitle("Account");
                        _fragment = AccountFragment.newInstance();
                        loadFragment(_fragment);
                        menuRes = 0;
                        disableMenu = true;

                        index = 4;
                        btmNavBar.getMenu().getItem(index).setEnabled(false);
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Default frag", Toast.LENGTH_SHORT).show();
                        break;
                }
                btmNavBar.setSelectedItemId(menuItem.getItemId());

                return loadFragment(_fragment);
            }
        });

        _addButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddTransactionActivity.class);
                startActivity(intent);

            }
        });
        if (savedInstanceState == null ) {
            _fragment = MainFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();

        }
    }
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_export:
                if(index==0)
                    ((MainFragment)_fragment).ExportData();
                return true;
            case R.id.action_calendar:
                if(index==1)
                    ((ReportFragment)_fragment).dateRangeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Fragment fragment = CategoryFragment.newInstance(dropDownValue[itemPosition]);
        loadFragment(fragment);
        menuRes = R.menu.add_only;
        disableMenu = false;
        return false;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED&&checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }
    }

}
