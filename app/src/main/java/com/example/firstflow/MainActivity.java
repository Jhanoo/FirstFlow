package com.example.firstflow;

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

}