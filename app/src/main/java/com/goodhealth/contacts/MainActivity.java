package com.goodhealth.contacts;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import java.util.ArrayList;
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter customAdapter;
    public ArrayList<ContactModel> contactModelArrayList;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showContacts();

    }


    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.

            listView = (ListView) findViewById(R.id.listView);

            contactModelArrayList = new ArrayList<>();

            MyContentObserver contentObserver = new MyContentObserver(ContactsContract.Contacts.CONTENT_URI);
            getApplicationContext().getContentResolver().registerContentObserver(
                    ContactsContract.Contacts.CONTENT_URI,
                    true,
                    contentObserver);


            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");



            while (phones.moveToNext())
            {
                String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                ContactModel contactModel = new ContactModel();
                contactModel.setName(name);
                contactModel.setNumber(phoneNumber);
                contactModelArrayList.add(contactModel);
                Log.d("name>>",name+"  "+phoneNumber);
            }
            phones.close();

            customAdapter = new CustomAdapter(this,contactModelArrayList);
            listView.setAdapter(customAdapter);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private class MyContentObserver extends ContentObserver {
        Uri uri;


        public MyContentObserver(Uri uri) {
            super(null);
            this.uri =uri;
        }


//        @Override
//        public void onChange(boolean selfChange, Uri uri) {
//            Cursor c = getContentResolver().query(uri, null, null, null, null);
//            while(c.moveToNext()){
//                Log.d(">> URI","DIRYY?  " + c.getString(c.getColumnIndex(ContactsContract.RawContacts.DIRTY.toString())));
//                Log.d(">> URI","DELETED??  " + c.getString(c.getColumnIndex(ContactsContract.RawContacts.DELETED.toString())));
//
//            }
//
//        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(this.getClass().getSimpleName(), "A change has happened");
            final String ACCOUNT_TYPE = "com.android.account.youraccounttype";
            final String WHERE_MODIFIED = "( "+ ContactsContract.RawContacts.DELETED + "=1 OR "+
                    ContactsContract.RawContacts.DIRTY + "=1 )";

            Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    null,
                    WHERE_MODIFIED,
                    null,
                    null);

            while (c.moveToNext())
            {
//                String name=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String name = c.getString(c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));

                ContactModel contactModel = new ContactModel();
                contactModel.setName(name);
//                contactModel.setNumber(phoneNumber);
                contactModelArrayList.add(contactModel);
                Log.d("name >>",name+"  " );
            }
            c.close();
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

    }

}

