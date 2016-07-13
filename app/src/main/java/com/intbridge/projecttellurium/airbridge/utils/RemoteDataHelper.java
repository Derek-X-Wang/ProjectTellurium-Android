package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * AWS DB and storage helper
 * Created by Derek on 7/12/2016.
 */
public class RemoteDataHelper {
    private static final String TAG = "RemoteDataHelper";
    Context context;
    private File imageFile;

    public RemoteDataHelper(Context context) {
        this.context = context;
    }

    private TransferObserver initTransferObserver(String key) {
        Log.e(TAG, "initTransferObserver: " );
        AmazonS3 s3 = new AmazonS3Client(getCredentialsProvider());
        TransferUtility transferUtility = new TransferUtility(s3, context);
        imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        return transferUtility.download(
                AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET,     /* The bucket to upload to */
                key,    /* The key for the uploaded object */
                imageFile        /* The file where the data to upload exists */
        );
    }

    public void setImageView(final ImageView v, String key) {
        AmazonS3 s3 = new AmazonS3Client(getCredentialsProvider());
        TransferUtility transferUtility = new TransferUtility(s3, context);
        imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + "eee");
        Log.d(TAG, "doInBackground: save to "+Environment.getExternalStorageDirectory().toString());
        TransferObserver observer = transferUtility.download(
                AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET,     /* The bucket to upload to */
                key,    /* The key for the uploaded object */
                imageFile        /* The file where the data to upload exists */
        );
        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state == TransferState.COMPLETED) {
                    Log.e(TAG, "Success: download worked" );
                    v.setImageDrawable(Drawable.createFromPath(imageFile.getAbsolutePath()));
                    Picasso.with(context).load(imageFile).into(v);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                //Display percentage transfered to user
                Log.d(TAG, "onProgressChanged: "+percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                Log.e(TAG, "onError: download failed" );
            }

        });
    }

    public void setMyCardItem(View v, String key) {
        TransferObserver observer = initTransferObserver(key);
        Log.e(TAG, "setMyCardItem: " );
        final CircleImageView image = (CircleImageView)v.findViewById(R.id.my_card_gridview_image);
        final ImageView imageBackground = (ImageView)v.findViewById(R.id.my_card_gridview_bg);
        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state == TransferState.COMPLETED) {
                    Log.e(TAG, "Success: download worked" );
                    Picasso.with(context).load(imageFile).into(image);
                    BlurHelper.with(context).radius(25).Picasso(imageFile, imageBackground);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                //int percentage = (int) (bytesCurrent/bytesTotal * 100);
                //Display percentage transfered to user
                //Log.d(TAG, "onProgressChanged: "+percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                Log.e(TAG, "onError: download failed" );
            }

        });
    }

    public boolean isImageCached(String key) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        return  file.exists();
    }

    public void setMyCardItemCache(View v, String key) {
        imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        CircleImageView image = (CircleImageView)v.findViewById(R.id.my_card_gridview_image);
        ImageView imageBackground = (ImageView)v.findViewById(R.id.my_card_gridview_bg);
        Picasso.with(context).load(imageFile).into(image);
        BlurHelper.with(context).radius(25).Picasso(imageFile, imageBackground);
    }

    public void setMyCardItemAutoOption(View v, String key) {
        if(isImageCached(key)) {
            setMyCardItemCache(v, key);
        } else {
            setMyCardItem(v, key);
        }
    }

    public void setNewCardImageView(final ImageView v, String key) {
        if(isImageCached(key)) {
            TransferObserver observer = initTransferObserver(key);
            observer.setTransferListener(new TransferListener(){

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if(state == TransferState.COMPLETED) {
                        Picasso.with(context).load(imageFile).into(v);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e(TAG, "onError: download failed" );
                }

            });
        } else {
            imageFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
            Picasso.with(context).load(imageFile).into(v);
        }
    }

    public PaginatedQueryList<Card> findMyCards(String userId) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        Card card = new Card();
        card.setUserId(userId);

        DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                .withHashKeyValues(card)
                .withConsistentRead(false);

        PaginatedQueryList<Card> result = mapper.query(Card.class, queryExpression);
        Log.e(TAG, "findMyCards: "+result.size());
        return result;
    }

    public void findMyCardsInBackground(String userId) {
        new FindMyCardsTask().execute(userId);
    }

    public Card getMyCard(String userId, String cardName) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
         return mapper.load(Card.class, userId, cardName);
    }

    public void getMyCardInBackground(String imageRef) {
        new GetCardTask().execute(imageRef);
    }

    private CognitoCachingCredentialsProvider getCredentialsProvider(){
        return AWSMobileClient
                .defaultMobileClient()
                .getIdentityManager()
                .getCredentialsProvider();
    }

    private class FindMyCardsTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... id) {
            findMyCards(id[0]);
            return null;
        }

        protected void onPostExecute(Void result) {

            Log.e(TAG, "onPostExecute: ddddd" );

        }
    }

    private class GetCardTask extends AsyncTask<String, Void, Card> {

        protected Card doInBackground(String... s) {
            String[] sl = s[0].split("_");
            return getMyCard(sl[0], sl[1]);
        }

        protected void onPostExecute(Card result) {
            Log.e(TAG, "onPostExecute: ddddd" );
            callback.done(result);
        }
    }

    private class SetMyCardTask extends AsyncTask<TaskInfo, Void, Void> {

        protected Void doInBackground(TaskInfo... info) {
            TaskInfo i = info[0];
            setMyCardItem(i.v, i.key);
            return null;
        }

        protected void onPostExecute(Void result) {

            Log.e(TAG, "onPostExecute: ddddd" );

        }
    }

    public class TaskInfo {
        public View v;
        public String key;
        public TaskInfo(View layout, String id) {
            this.v = layout;
            this.key = id;
        }
    }

    private Callback callback = null;


    public void setCallback(Callback c){
        this.callback = c;
    }

    public interface Callback {
        abstract void done(Card card);
    }
}
