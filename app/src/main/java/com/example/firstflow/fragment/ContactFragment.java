package com.example.firstflow.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firstflow.adapter.ContactRecyclerAdapter;
import com.example.firstflow.R;
import com.example.firstflow.dto.Contact;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private ContactRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        adapter = new ContactRecyclerAdapter();

        // 리사이클러뷰 띄우기
        RecyclerView recyclerView = v.findViewById(R.id.fragmentContactRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // 리사이클러뷰 어댑터 연결
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        // 데이터 가져오기
        ArrayList<Contact> a = getContactList();
        getData(a);


        return v;
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


        Cursor contactCursor = getActivity().getContentResolver().query(uri, projection, null,
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