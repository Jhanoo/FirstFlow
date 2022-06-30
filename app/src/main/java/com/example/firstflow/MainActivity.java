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

import androidx.annotation.NonNull;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstflow.dto.Contact;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ContactRecyclerAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkVerify(Manifest.permission.READ_CONTACTS);
        } else {
            startApp();
        }
    }

    public void startApp() {
        init();

        ArrayList<Contact> a = getContactList();

        getData(a);
    }


    private void init() {
        RecyclerView recyclerView = findViewById(R.id.mainContactRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ContactRecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData(ArrayList<Contact> a) {
        for (int i = 0; i < a.size(); i++) {
            Contact data = new Contact();
            data.setName(a.get(i).getName());
            data.setPhoneNum(a.get(i).getPhoneNum());

            adapter.addItem(data);
        }

        adapter.notifyDataSetChanged();
    }


    private ArrayList<Contact> getContactList() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사
                ContactsContract.CommonDataKinds.Phone.NUMBER, // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // 연락처 이름.


        String[] selectionArgs = null;


        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";


        Cursor contactCursor = getContentResolver().query(uri, projection, null,
                selectionArgs, sortOrder);


        ArrayList<Contact> contactList = new ArrayList<Contact>();


        if (contactCursor.moveToFirst()) {
            do {
                String phoneNumber = contactCursor.getString(1).replaceAll("-",
                        "");
                if (phoneNumber.length() == 10) {
                    phoneNumber = phoneNumber.substring(0, 3) + "-"
                            + phoneNumber.substring(3, 6) + "-"
                            + phoneNumber.substring(6);
                } else if (phoneNumber.length() > 8) {
                    phoneNumber = phoneNumber.substring(0, 3) + "-"
                            + phoneNumber.substring(3, 7) + "-"
                            + phoneNumber.substring(7);
                }


                Contact tmpContact = new Contact();
                tmpContact.setPhotoId(contactCursor.getLong(0));
                tmpContact.setPhoneNum(phoneNumber);
                tmpContact.setName(contactCursor.getString(2));


                contactList.add(tmpContact);
            } while (contactCursor.moveToNext());
        }


        return contactList;
    }



    /*
     * =================== 권한 요청 코드 ==================
     *
     * */


    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify(String permissionName) {
        if (
                checkSelfPermission(permissionName) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(permissionName)) {
                // ...
            }
            requestPermissions(new String[]{permissionName},
                    1);
        } else {
            startApp();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        MainActivity.this.finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                        getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                }).setCancelable(false).show();

                        return;
                    }
                }
                startApp();
            }
        }
    }


}