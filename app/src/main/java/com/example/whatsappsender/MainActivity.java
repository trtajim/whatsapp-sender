package com.example.whatsappsender;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    TextInputEditText edNumber, edName, edMessage ;
    MaterialButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edNumber = findViewById(R.id.edNumber);
        sendButton = findViewById(R.id.sendButton);
        edMessage = findViewById(R.id.edMessage);
        edName = findViewById(R.id.edName);
        getPermission();

        
        sendButton.setOnClickListener(v->{
            String number = edNumber.getText().toString().trim();
            String message = edMessage.getText().toString().trim();
            String name = edName.getText().toString().trim();

            //check all inputss are inputed or not
            if (number.isEmpty()){

                edNumber.requestFocus();
                edNumber.setError("দয়া করে এখানে নাম্বার লিখুন ");
                return;


            }
            else if (number.length()!=11) {
                edNumber.requestFocus();
                edNumber.setError("ভুল নাম্বার লিখেছেন ");
                return;

            }
            else if (name.isEmpty()) {

                edName.requestFocus();
                edName.setError("দয়া করে নামটি লিখুন");
                return;

            }
            else if (message.isEmpty()){
                edMessage.requestFocus();
                edMessage.setError("দয়া করে একটি মেসেজ লিখুন");
                return;
            }
            else{}
            //checked, returned if null

//            AndroidUtils.makeAlert(getApplicationContext(), "মেসেজ পাঠান ?", "\nনামঃ " + name + "\nনাম্বারঃ " + number + "\nমেসেজঃ " + message + "\nমেসেজ পাঠাতে 'এগিয়ে যান' এ চাপুন", new Runnable() {
//                @Override
//                public void run() {
                    saveContact(number, name);
                    sendMessage(number, message);

//                }
//            });

        });

    }


    private void getPermission (){
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_CONTACTS
        }, 1);
    }

    private void sendMessage(String number, String message){
        String url = "https://wa.me/88"+number+"?text="+message;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

    }
    private void saveContact(String number, String name){
        ContentResolver cr = getApplicationContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);

        boolean exists = (cursor != null && cursor.moveToFirst());

        if (cursor != null) {
            cursor.close();
        }
        if (exists){
            Toast.makeText(this, "contact available, skipped save", Toast.LENGTH_SHORT).show();
            return;
       }else {
            saveContactDirectly(name, number);
        }
    }
    private void saveContactDirectly(String name, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Name
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        // Phone Number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}