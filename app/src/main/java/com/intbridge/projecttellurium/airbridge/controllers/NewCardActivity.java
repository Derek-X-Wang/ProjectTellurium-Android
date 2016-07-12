package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.auth.CognitoHelper;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Create new name card
 * Created by Derek on 7/12/2016.
 */
public class NewCardActivity extends AutoLayoutActivity {

    @BindView(R.id.cardname_newcard)
    protected MaterialEditText cardName;
    @BindView(R.id.firstname_newcard)
    protected MaterialEditText firstName;
    @BindView(R.id.lastname_newcard)
    protected MaterialEditText lastName;
    @BindView(R.id.position_newcard)
    protected MaterialEditText position;
    @BindView(R.id.email_newcard)
    protected MaterialEditText email;
    @BindView(R.id.address_newcard)
    protected MaterialEditText address;
    @BindView(R.id.website_newcard)
    protected MaterialEditText website;
    @BindView(R.id.profile_image_newcard)
    protected CircleImageView profileImageView;

    private Drawable profileImage;

    private static final String TAG = "NewCardActivity";
    private static final int CODE_SELECT_PICTURE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcard);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_newcard);
        myToolbar.setTitleTextColor(Color.parseColor("#919192"));
        myToolbar.setTitle("New Card");
        setSupportActionBar(myToolbar);
        ButterKnife.bind(this);


        initViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_newcard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_create) {
//            CognitoCachingCredentialsProvider provider = AWSMobileClient
//                    .defaultMobileClient()
//                    .getIdentityManager()
//                    .getCredentialsProvider();
//            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(provider);
//            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
//            Card newCard = new Card();
//            newCard.setUserId("t3");
//            newCard.setCardname("Business");
//            newCard.setFirstName("Xinzhe");
//            newCard.setLastName("Wang");
//            Log.e(TAG, "onOptionsItemSelected: " + "here2");
//            mapper.save(newCard, new DynamoDBSaveExpression());
//            Log.e(TAG, "onOptionsItemSelected: " + "here1");
            new UserTask().execute();



        }

        return super.onOptionsItemSelected(item);
    }

    private class UserTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... types) {

            CognitoCachingCredentialsProvider provider = AWSMobileClient
                    .defaultMobileClient()
                    .getIdentityManager()
                    .getCredentialsProvider();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(provider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            Card newCard = new Card();
            newCard.setUserId("t3");
            newCard.setCardname("Business");
            newCard.setFirstName("Xinzhe");
            newCard.setLastName("Wang");
            Log.e(TAG, "onOptionsItemSelected: " + "here2");
            mapper.save(newCard, new DynamoDBSaveExpression());
            Log.e(TAG, "onOptionsItemSelected: " + "here1");

            return null;
        }

        protected void onPostExecute(Void result) {

            Log.e(TAG, "onPostExecute: ddddd" );

        }
    }

    private void initViews() {
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), CODE_SELECT_PICTURE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_SELECT_PICTURE) {
            if(resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                profileImageView.setImageURI(selectedImageUri);
                profileImage = getDrawableFromUri(selectedImageUri);
                Log.e(TAG, "onActivityResult: 10");
            }
        }
    }

    private Drawable getDrawableFromUri(Uri uri) {
        Drawable res;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            res = Drawable.createFromStream(inputStream, uri.toString() );
        } catch (FileNotFoundException e) {
            res = getResources().getDrawable(R.drawable.test1);
        }
        return res;
    }
}
