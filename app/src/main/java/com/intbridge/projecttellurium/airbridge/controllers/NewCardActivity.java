package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.auth.CognitoHelper;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.utils.RemoteDataHelper;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.haha.perflib.Main;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.File;
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
    @BindView(R.id.phone_newcard)
    protected MaterialEditText phone;
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

    @BindView(R.id.button_delete)
    protected Button button;

    private Drawable profileImage;
    private Uri selectedImageUri;
    private File imageFile;
    private boolean modeView = false;

    private static final String TAG = "NewCardActivity";
    private static final int CODE_SELECT_PICTURE = MainActivity.CODE_SELECT_PICTURE;

    private Card newCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcard);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_newcard);
        myToolbar.setTitleTextColor(Color.parseColor("#919192"));
        myToolbar.setTitle("New Card");
        setSupportActionBar(myToolbar);
        ButterKnife.bind(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            button.setVisibility(View.VISIBLE);
            if(extras.getBoolean(MainActivity.MODE_VIEW)){
                viewMode();
            } else {
                editMode();
            }
        }

        initViews();
        CognitoHelper.init(getApplicationContext());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (modeView) {
            getMenuInflater().inflate(R.menu.app_bar_viewcard, menu);
            getSupportActionBar().setTitle("Contact");
        } else {
            getMenuInflater().inflate(R.menu.app_bar_newcard, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_create) {
            Log.e(TAG, "onOptionsItemSelected: "+CognitoHelper.getCurrUser() );
            newCard = new Card();
            newCard.setUserId(CognitoHelper.getCurrUser());
            newCard.setCardname(cardName.getText().toString());
            newCard.setFirstName(firstName.getText().toString());
            newCard.setLastName(lastName.getText().toString());
            newCard.setPhone(phone.getText().toString());
            newCard.setPosition(position.getText().toString());
            newCard.setEmail(email.getText().toString());
            newCard.setAddress(address.getText().toString());
            newCard.setWebsite(website.getText().toString());
            newCard.setImageRef(newCard.getUserId() + "_" + newCard.getCardname());

            new SaveCardTask().execute();

            Intent intent = new Intent();
            intent.putExtra(MainActivity.CARD_NAME,newCard.getCardname());
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void editMode() {
        Bundle extras = getIntent().getExtras();
        String imageRef = extras.getString(MainActivity.USER_ID)+"_"+extras.getString(MainActivity.CARD_NAME);
        RemoteDataHelper helper = new RemoteDataHelper(this);
        cardName.setEnabled(false);
        profileImageView.setOnClickListener(null);
        helper.setCallback(new RemoteDataHelper.Callback() {
            @Override
            public void done(Card card) {
                cardName.setText(card.getCardname());
                firstName.setText(card.getFirstName());
                lastName.setText(card.getLastName());
                phone.setText(card.getPhone());
                position.setText(card.getPosition());
                email.setText(card.getEmail());
                address.setText(card.getAddress());
                website.setText(card.getWebsite());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });
        helper.getMyCardInBackground(imageRef);
        helper.setNewCardImageView(profileImageView, imageRef);
    }

    private void viewMode() {
        modeView = true;
        Bundle extras = getIntent().getExtras();
        String imageRef = extras.getString(MainActivity.USER_ID)+"_"+extras.getString(MainActivity.CARD_NAME);
        cardName.setEnabled(false);
        cardName.setVisibility(View.GONE);
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        phone.setEnabled(false);
        position.setEnabled(false);
        email.setEnabled(false);
        address.setEnabled(false);
        website.setEnabled(false);
        RemoteDataHelper helper = new RemoteDataHelper(this);
        profileImageView.setOnClickListener(null);
        helper.setCallback(new RemoteDataHelper.Callback() {
            @Override
            public void done(Card card) {
                cardName.setText(card.getCardname());
                firstName.setText(card.getFirstName());
                lastName.setText(card.getLastName());
                phone.setText(card.getPhone());
                position.setText(card.getPosition());
                email.setText(card.getEmail());
                address.setText(card.getAddress());
                website.setText(card.getWebsite());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });
        helper.getMyCardInBackground(imageRef);
        helper.setNewCardImageView(profileImageView, imageRef);
    }

    private class SaveCardTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... card) {

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            Log.e(TAG, "onOptionsItemSelected: " + "here2");
            String key = newCard.getImageRef();
            mapper.save(newCard, new DynamoDBSaveExpression());
            Log.e(TAG, "onOptionsItemSelected: " + "here1");
            if(selectedImageUri != null) {
                AmazonS3 s3 = new AmazonS3Client(getCredentialsProvider());
                TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
                TransferObserver observer = transferUtility.upload(
                        AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET,     /* The bucket to upload to */
                        key,    /* The key for the uploaded object */
                        new File(getPath(selectedImageUri))        /* The file where the data to upload exists */
                );
                Log.e(TAG, "doInBackground: wwww" );
            }
            //new RemoteDataHelper(getApplicationContext()).setImageView(profileImageView, "ddd_cardddd");
            Log.e(TAG, "doInBackground: dddd" );
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
                selectedImageUri = data.getData();
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

    private String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private CognitoCachingCredentialsProvider getCredentialsProvider(){
        return AWSMobileClient
                .defaultMobileClient()
                .getIdentityManager()
                .getCredentialsProvider();
    }
}
