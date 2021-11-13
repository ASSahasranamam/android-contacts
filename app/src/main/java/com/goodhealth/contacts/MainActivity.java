package com.goodhealth.contacts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    String username;
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



    public ArrayList<String> allPhoneNumbers = new ArrayList<String>();
    public  ArrayList<String> mutualPhones = new ArrayList<String>();



    public void getAllMatchingContacts(String username) {
//        String postUrl = "http://localhost:3000/users/mutualcontacts/";
//
//
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("username", username);
//            postData.put("phoneNums", allPhoneNumbers);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                System.out.println(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        requestQueue.add(jsonObjectRequest);

        mutualContactsReq object = new mutualContactsReq();

        object.setUsername(username);
        object.setPhoneNums(allPhoneNumbers);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<mutualContactsres> response = apiInterface.getMutualContactsDocs(object);

        response.enqueue(new Callback<mutualContactsres>() {
            @Override
            public void onResponse(Call<mutualContactsres> call, Response<mutualContactsres> response) {
                mutualContactsres getbeanlist = response.body();

                Log.w("TAG", "onResponse: Response Rec" +getbeanlist);

                Log.w("tag", getbeanlist.getMutualDocs().toString());

                mutualPhones.addAll(getbeanlist.getMutualDocs());

                Log.i(">>", " MutualPh Size" + mutualPhones.size());

                if(customAdapter == null){
                    customAdapter = new CustomAdapter(MainActivity.this, contactModelArrayList, mutualPhones);

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This is in getMutualContacts" + mutualPhones+ mutualPhones.size() );
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                    listView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();

                } else{

                    customAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onFailure(Call<mutualContactsres> call, Throwable t) {
                System.out.println("Response FAILED" +t.getMessage());

            }
        });
    }

    MyContentObserver contentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("username");

        Log.d(">>Tag >>", "username phonenumber" + username);

        Map<String, Object> loginDetails = new HashMap<>();
        loginDetails.put("phoneNumber", username);
        loginDetails.put("username", username);

        db.collection("loginDetails").document(username).set(loginDetails, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Log.d(">> Firestore Insert", "Login Details DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(">> Firestore Insert", "Error writing document", e);
                    }
                });



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
//                Log.w(">> TAG >>>", "loadPost:onCancelled", databaseError.toException());
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


//            
//            printOutput();

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


//            List<String> PhoneNums = new ArrayList<String>();
//            List<String> EmailList = new ArrayList<String>();
//            List test1 = new ArrayList(Arrays.asList());


            String displayNumber = "";
            String stringPhoneWords = "";
            String stringEmailWords = "";


            while(getAllNums.moveToNext()){

                String phoneNumber = getAllNums.getString(getAllNums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d(">> TAg >>", name +" /newContactsList/ "+ contact_id + " //"+ phoneNumber);
//                phoneStringArray.add(phoneNumber);
                allPhoneNumbers.add(phoneNumber);
//                test1.add((phoneNumber));
                displayNumber = displayNumber + phoneNumber + " \n ";
                stringPhoneWords = stringPhoneWords + phPurifier(phoneNumber) + ", ";
            }
            getAllNums.close();


            Cursor getAllEmails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, FIND_EMAILS_FOR_ID, null, null);


            while(getAllEmails.moveToNext()){

                String EmailADdresses = getAllEmails.getString(getAllEmails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                Log.d(">> TAg >>", name +" /newContactsList/ "+ contact_id + " //"+ EmailADdresses);
//                EmailList.add(EmailADdresses);
//                displayNumber = displayNumber + phoneNumber + " \n ";
                stringEmailWords = stringEmailWords + EmailADdresses.replace(" ","").replace("-","") + ", ";


            }

//            Log.d(">>Tag>>", name + "// NewTest //" + contact_id + "// Phonenums finally" + PhoneNums+ "// Phonenums finally" + PhoneNums);
            getAllEmails.close();

            String[] phoneStringArray = stringPhoneWords.split(",");
            String[] emailStringArray = stringEmailWords.split(",");



            ContactModel contactModel = new ContactModel();
            contactModel.setId(contact_id);
            contactModel.setName(name);
            contactModel.setNumber(displayNumber);
            contactModel.setPhoneArray((phoneStringArray));
            contactModel.setEmailArray((emailStringArray));

//            contactModel.setEmailArray(EmailList);



            Map<String, Object> data4 = new HashMap<>();
            data4.put("name", name);
            data4.put("id", contact_id);

            data4.put("emails",Arrays.asList(emailStringArray));


            data4.put("phN",  Arrays.asList(phoneStringArray));

//            contactModelArrayList.add({"phone" : Arrays.asList(phoneStringArray)});
            contactModelArrayList.add(contactModel);


            db.collection(username).document(contact_id).set(data4, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {

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

        getAllMatchingContacts(username);

        Log.i(">> TAG >>",mutualPhones +  " mPhones");







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

    private void getMutualContacts(){}

    private String phPurifier(String unPurifiedPhoneNumber ) {
        unPurifiedPhoneNumber = unPurifiedPhoneNumber.replace(" ","");
        unPurifiedPhoneNumber = unPurifiedPhoneNumber.replace("-","");
        unPurifiedPhoneNumber = unPurifiedPhoneNumber.replace("(","");
        unPurifiedPhoneNumber = unPurifiedPhoneNumber.replace(")","");
        return unPurifiedPhoneNumber;
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

