package com.example.firstflow;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.firstflow.fragment.CloudFragment;
import com.example.firstflow.fragment.ContactFragment;
import com.example.firstflow.fragment.GalleryFragment;
import com.example.firstflow.fragment.PermissionErrorFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    public static final String[] PERMISSIONS = new String[]{
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE
    };
    public static HashMap<String, Boolean> isAllowed = new HashMap<String, Boolean>();

    BottomNavigationView btmNaviView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmNaviView = findViewById(R.id.bottomNavigationView);
        btmNaviView.setSelectedItemId(R.id.tab_contact);

        // Permission Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkVerify(PERMISSIONS);
        }

        if(isAllowed.containsKey(PERMISSIONS[0]) && isAllowed.containsKey(PERMISSIONS[1]) && isAllowed.get(PERMISSIONS[0]) && isAllowed.get(PERMISSIONS[1])) {
            changeFragment(new ContactFragment());
        }else{
            changeFragment(new PermissionErrorFragment());
        }

        SettingListener();
    }

    // =================== 권한 요청 코드 ==================

    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify(String[] permissions) {
        Log.d("checkVerify", "checkVerify 진입");
        ArrayList<String> notAllowedPermissions = new ArrayList<>();

        for(String permissionName : permissions){
            if(checkSelfPermission(permissionName) != PackageManager.PERMISSION_GRANTED){
                // TODO : 거절했을 때 할 액션
                // if (shouldShowRequestPermissionRationale(permissionName)) {}

                notAllowedPermissions.add(permissionName);
                isAllowed.put(permissionName, false);
            }else{
                isAllowed.put(permissionName, true);
            }
        }

        if(notAllowedPermissions.size() > 0){
            requestPermissions(notAllowedPermissions.toArray(new String[0]),1);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; ++i) {
                isAllowed.put(permissions[i], (grantResults[i] == PackageManager.PERMISSION_GRANTED));
            }
        }

        // 초기 화면이 연락처 fragment(권한 두 개가 필요함)이므로 만약 두 권한이 있다면 ContactFragment를 띄워야 한다.
        if(isAllowed.get(PERMISSIONS[0]) && isAllowed.get(PERMISSIONS[1])) {
            changeFragment(new ContactFragment());
        }
    }

    public void activityRefresh(){
        finish();
        overridePendingTransition(0, 0);
        Intent intent = getIntent();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    private void SettingListener() {
        btmNaviView.setOnItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab_contact: {
                    if(isAllowed.get(Manifest.permission.READ_CONTACTS) && isAllowed.get(Manifest.permission.CALL_PHONE)){
                        changeFragment(new ContactFragment());
                    }else{
                        changeFragment(new PermissionErrorFragment());
                    }
                    return true;
                }
                case R.id.tab_gallery: {
                    changeFragment(new GalleryFragment());
                    return true;
                }
                case R.id.tab_cloud: {
                    changeFragment(new CloudFragment());
                    return true;
                }
            }

            return false;
        }
    }

    public void changeFragment(Fragment fragment){
        try{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_ly, fragment)
                    .commit();
        }catch(SecurityException e){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_ly, new PermissionErrorFragment())
                    .commit();
        }
    }

}