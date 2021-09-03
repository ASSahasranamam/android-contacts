package com.goodhealth.contacts;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.ContactsContract;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ListView;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String sessionId = getIntent().getStringExtra("username");
    private ListView listView;
    private CustomAdapter customAdapter;
    public ArrayList<ContactModel> contactModelArrayList;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Handler handler = new Handler();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();


    private DatabaseReference myRef;

    MyContentObserver contentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d(">>Tag >>", "session id" + sessionId);
        myRef = database.getReference();

        db.setFirestoreSettings(settings);
//        db.disableNetwork()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//
//// ...
//                    }
//                });


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d(">> TAG >>", "onDataChange:  " + dataSnapshot.getValue());                // ..
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(">> TAG >>>", "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(postListener);
        showContacts();

    }


    @Override
    protected void onPause() {
        super.onPause();
//        getApplicationContext().getContentResolver().unregisterContentObserver(contentObserver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                true,
                contentObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().getContentResolver().unregisterContentObserver(contentObserver);

    }
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.checkAndRequestPermissions(this,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Android version is lesser than 6.0 or the permission is already granted.

            listView = (ListView) findViewById(R.id.listView);

            contactModelArrayList = new ArrayList<>();

            contentObserver = new MyContentObserver(handler);
            getApplicationContext().getContentResolver().registerContentObserver(
                    ContactsContract.Contacts.CONTENT_URI,
                    true,
                    contentObserver);


//            testCustomFunc();




//
//            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//
//
//            while (phones.moveToNext()) {
//                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                String contact_id = phones.getString(phones.getColumnIndex(ContactsContract.Data.CONTACT_ID.toString()));
//                String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS.toString()));
//
//                ContactModel contactModel = new ContactModel();
//                contactModel.setId(contact_id);
//                contactModel.setName(name);
//                contactModel.setNumber(phoneNumber);
//                contactModel.setEmail(email);
//
//                contactModelArrayList.add(contactModel);
////                Log.d("name>>", name + "  " + phoneNumber);
////
//                db.collection("testPhoneBook").document(contact_id).set(contactModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(">> Firestore Insert", "DocumentSnapshot successfully written!");
//                    }
//                })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(">> Firestore Insert", "Error writing document", e);
//                            }
//                        });
//
////                db.collection("testPhoneBook").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                        if (task.isSuccessful()) {
////                            int count = 0;
////                            for (DocumentSnapshot document : task.getResult()) {
////                                count++;
////                            }
////                            Log.d("TAG", count + "");
////                        } else {
////                            Log.d("Error Tag", "Error getting documents: ", task.getException());
////                        }
////                    }
////                });
////                myRef.child("users").child(name);
////                myRef.child("users").child(name).child("phoneNumber").setValue(phoneNumber);
////                myRef.child("users").child(name).child("contactid").setValue(contact_id);
////                myRef.child("users").child(name).child("contactid").setValue(contact_id);
////                myRef.child("users").child(name).child("contactid").setValue(contact_id);
//
//
//            }
//            phones.close();

              getClubbedContacts();
            customAdapter = new CustomAdapter(this, contactModelArrayList);
            listView.setAdapter(customAdapter);
            printOutput();

        }
    }
    }





    private void getClubbedContacts(){
        Cursor contactCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");


        while(contactCursor.moveToNext()){

            String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contact_id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID.toString()));

            final String FIND_pHONES_FOR_ID = "( " + ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" +
                    contact_id + ")";

            final String FIND_EMAILS_FOR_ID = "( " + ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" +
                    contact_id + ")";


            Log.d(">> TAg >>", name +" /newContactsList/ "+ contact_id);
            Cursor getAllNums = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, FIND_pHONES_FOR_ID, null, null);

            ArrayList<String> PhoneNums = new ArrayList<String>();
            ArrayList<String> EmailList = new ArrayList<String>();

            String displayNumber = "";
            while(getAllNums.moveToNext()){

                String phoneNumber = getAllNums.getString(getAllNums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d(">> TAg >>", name +" /newContactsList/ "+ contact_id + " //"+ phoneNumber);
                PhoneNums.add(phoneNumber);
                displayNumber = displayNumber + phoneNumber + " \n ";

            }
            getAllNums.close();

            Cursor getAllEmails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, FIND_EMAILS_FOR_ID, null, null);


            while(getAllEmails.moveToNext()){

                String EmailADdresses = getAllEmails.getString(getAllEmails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                Log.d(">> TAg >>", name +" /newContactsList/ "+ contact_id + " //"+ EmailADdresses);
                EmailList.add(EmailADdresses);
//                displayNumber = displayNumber + phoneNumber + " \n ";

            }

            Log.d(">>Tag>>", name + "// NewTest //" + contact_id + "// Phonenums finally" + PhoneNums+ "// Phonenums finally" + PhoneNums);
            getAllEmails.close();



            ContactModel contactModel = new ContactModel();
            contactModel.setId(contact_id);
            contactModel.setName(name);
            contactModel.setNumber(displayNumber);
            contactModel.setPhoneArray(PhoneNums);

            contactModel.setEmailArray(EmailList);


            contactModelArrayList.add(contactModel);



            db.collection("todayTesting2").document(contact_id).set(contactModel).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(">> Firestore Insert", "DocumentSnapshot successfully written!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(">> Firestore Insert", "Error writing document", e);
                        }
                    });


        }


        contactCursor.close();




    }

    private  void printOutput(){
        db.collection("testPhoneBook").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        count++;
                    }
                    Log.d(">> TAG", "OfflineDAtaCount: " + count);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void testCustomFunc() {
        Log.d("TCF", "testCustomFunc: INIt");

        final String WHERE_MODIFIED = "( " + ContactsContract.RawContacts.DELETED + "=1 OR " +
                ContactsContract.RawContacts.DIRTY + "=1 )";

        Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                null,
                WHERE_MODIFIED,
                null,
                null);
        Log.d("?? TCF >>", "testCustomFunc:  COUNT " + c.getCount());
        WriteBatch batch = db.batch();

        while (c.moveToNext()) {
//                String name=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String name = c.getString(c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
            String deleteID = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));

            String dirtyCheck = c.getString(c.getColumnIndex(ContactsContract.RawContacts.DIRTY));
            String deleteCheck = c.getString(c.getColumnIndex(ContactsContract.RawContacts.DELETED));

//            Uri lookup = c.getString(c.getColumnIndex(ContactsContract.Data.getContactLookupUri().get));

            if (deleteCheck == "1") {
                db.collection("testPhoneBook").document(deleteID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(">> Firestore DELETE", "DocumentSnapshot DELETED written!");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(">> Firestore Insert", "Error writing document", e);
                            }
                        });


            } else if (dirtyCheck == "1") {
                Cursor updateCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "( " + ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + deleteID + ")",
                        null,
                        null);
                String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                String email = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                ContactModel updateModel = new ContactModel();
                updateModel.setName(name);
                updateModel.setId(deleteID);
//                updateModel.setNumber(number);
//                updateModel.setEmail(email);

                db.collection("testPhoneBook").document(deleteID).set(updateModel);
//                db.collection("testPhoneBook").document(deleteID).update("number",number);
//                db.collection("testPhoneBook").document(deleteID).update("id",deleteID);
//                db.collection("testPhoneBook").document(deleteID).update("email",email);
//



            }

       //                contactModel.setNumber(phoneNumber);
//                contactModelArrayList.(setNumbercontactModel);
            Log.d("name >>", "TEST CUSTOM FUNC -> NAME//id" + name + "  " + deleteID + "///"  );

        }
        c.close();

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.d("TAG", "TASK DONE" + task.getResult());

                            printOutput();
                        } else {
                            Log.d("Error Tag", "BATCH ERROR : ", task.getException());
                        }            }
        });

        Log.d("TCF", "testCustomFunc: END");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
//        Uri Uriuri;

        public MyContentObserver(Handler h) {
            super(h);
//            this.uri = uri;
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
            this.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(this.getClass().getSimpleName(), "A change has happened");
            super.onChange(selfChange);
            final String WHERE_Deleted = "( " + ContactsContract.RawContacts.DELETED + "= 1 )";
            final String WHERE_MODIFIED = "( " + ContactsContract.RawContacts.DIRTY + "= 1  AND " + ContactsContract.RawContacts.DELETED + "= 0 )";
            Log.d(">> TAG >>", "onChange:"+ uri);
//            testCustomFunc();



            Cursor c = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    null,
                    WHERE_MODIFIED,
                    null,
                    null);


//            if (c != null ) {
//                while (cursor.moveToNext()) {
//                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
//                    if (Boolean.parseBoolean(hasPhone)) {
//                        // You know have the number so now query it like this
//                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
//                                null, null);
//
//                        while (phones.moveToNext()) {
//                            String phoneNumber = phones.getString(
//                                    phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        }
//                        phones.close();
//                    }
//                }
//

                while(c.moveToNext()) {
//                    c.moveToFirst();
//                String name=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    String deleteID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));


                    Log.d("name >>", name + " // Modified // " + deleteID);

                    ContactModel updateModel = new ContactModel();
                    updateModel.setName(name);

                    Cursor phone_no = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + deleteID, null, null);
                    while (phone_no.moveToNext()) {
                       String phoneNumber = phone_no.getString(phone_no.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d(" >> TAG >>", "VV anna code: " +  phoneNumber);

                        updateModel.setId(deleteID);
                        updateModel.setNumber(phoneNumber);

                        db.collection("testPhoneBook").document(deleteID).set(updateModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error deleting document", e);
                                    }
                                });

                    }



                        //                updateModel.setNumber(number);

                    phone_no.close();




                }

            c.close();
            Cursor c2 = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    null,
                    WHERE_Deleted,
                    null,
                    null);

            while (c2 != null && c2.moveToNext()) {
                String name = c2.getString(c2.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY));
                String deleteID = c2.getString(c2.getColumnIndex(ContactsContract.RawContacts._ID));

                ContactModel contactModel = new ContactModel();
                contactModel.setName(name);
//                contactModel.setNumber(phoneNumber);
//                contactModelArrayList.(setNumbercontactModel);
                Log.d("name >>", name + " // Deleting //  " + deleteID);
                Log.d("DEL", "onChange:"+ c2.getString(c2.getColumnIndex(ContactsContract.RawContacts.DELETED)));

                db.collection("testPhoneBook").document(deleteID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error deleting document", e);
                                }
                            });

                }
            c2.close();
//            super.onChange(selfChange);


        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

    }

}

