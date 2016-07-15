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
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
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
import com.intbridge.projecttellurium.airbridge.models.Contact;
import com.intbridge.projecttellurium.airbridge.models.Discover;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * AWS DB and storage helper
 * Created by Derek on 7/12/2016.
 */
public class RemoteDataHelper {

    // TODO: re-packaging everything and find a better structure

    private static final String TAG = "RemoteDataHelper";
    private Context context;
    private File imageFile;
    private String cardName;

    public RemoteDataHelper(Context context) {
        this.context = context;
    }

    private File getFile(String key) {
        return new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
    }

    private TransferObserver initTransferObserver(String key) {
        Log.e(TAG, "initTransferObserver: " );
        AmazonS3 s3 = new AmazonS3Client(getCredentialsProvider());
        TransferUtility transferUtility = new TransferUtility(s3, context);
        imageFile = getFile(key);
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
        if(!isImageCached(key)) {
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

    public List<Discover> findPeopleNearby(String userId) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<Discover> result = mapper.scan(Discover.class, scanExpression);
        // remove card of myself
        List<Discover> resultWithoutMe = new ArrayList<>();
        for (Discover d : result) {
            if(!d.getUserId().equals(userId))
              resultWithoutMe.add(d);
        }
        Log.e(TAG, "find People nearby: "+result.size());
        return resultWithoutMe;
    }

    public List<Card> getMyContactCards(String userId) {
        Contact contactBook = getMyContact(userId);
        List<String> list = contactBook.getContacts();
        List<Card> cardList = new ArrayList<>();
        if (list != null) {
            for (String key : list) {
                // TODO: prevent from having card name include "_"
                String[] set = key.split("_");
                Card card = getMyCard(set[0],set[1]);
                cardList.add(card);
            }
        }
        return cardList;
    }

    public void getMyContactCardsInBackground(String userId) {
        new GetMyContactCardsTask().execute(userId);
    }

    public Contact getMyContact(String userId) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        Contact contact = new Contact();
        contact.setUserId(userId);
        return mapper.load(contact);
    }

    public void addToContact(String sendToWhoUserId, String myCardKey) {
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        Contact contact = mapper.load(Contact.class, sendToWhoUserId);
        if (contact == null) {
            contact = new Contact();
            contact.setUserId(sendToWhoUserId);
        }
        List<String> list = contact.getContacts();
        if (list == null) list = new ArrayList<>();
        boolean exist = false;
        for (String sample : list) {
            if (sample.equals(myCardKey)){
                exist = true;
            }
        }
        if (!exist) {
            list.add(myCardKey);
            contact.setContacts(list);
            mapper.save(contact);
        }

    }

    public void addToContactInBackground(String sendToWhoUserId, String myCardKey) {
        new AddToContactTask().execute(sendToWhoUserId,myCardKey);
    }


    public void setDiscoverPresence(String id) {
        new SetPresenceTask().execute(id);
    }

    public void removeDiscoverPresence(String id) {
        new RemovePresenceTask().execute(id);
    }

    public void setImage (final ImageView image, String key) {
        if(isImageCached(key)) {
            setImageWithCache(image, key);
        } else {
            setImageWithDownload(image, key);
        }
    }

    public void setBlurImage (final ImageView image, String key, BlurHelper.BlurFactor factor) {
        if(isImageCached(key)) {
            setBlurImageWithCache(image, key, factor);
        } else {
            setBlurImageWithDownload(image, key, factor);
        }
    }

    private void setImageWithCache (ImageView image, String key) {
        imageFile = getFile(key);
        Picasso.with(context).load(imageFile).into(image);
    }

    private void setBlurImageWithCache (ImageView image, String key, BlurHelper.BlurFactor factor) {
        imageFile = getFile(key);
        BlurHelper.with(context).blurFactor(factor).PicassoWithOption(imageFile, image);
    }

    private void setImageWithDownload (final ImageView image, String key) {
        TransferObserver observer = initTransferObserver(key);
        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state == TransferState.COMPLETED) {
                    Log.e(TAG, "Success: download worked" );
                    Picasso.with(context).load(imageFile).into(image);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "onError: setImage download failed" );
            }

        });
    }

    private void setBlurImageWithDownload (final ImageView image, String key, final BlurHelper.BlurFactor factor) {
        TransferObserver observer = initTransferObserver(key);
        observer.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if(state == TransferState.COMPLETED) {
                    Log.e(TAG, "Success: download worked" );
                    BlurHelper.with(context).blurFactor(factor).PicassoWithOption(imageFile, image);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                Log.e(TAG, "onError: setBlurImage download failed" );
            }

        });
    }

    public void deleteContact(String userId, String selectedContactKey) {
        // delete in DynamoDB
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        Contact contact = mapper.load(Contact.class, userId);
        List<String> list = contact.getContacts();
        List<String> newList =  new ArrayList<>();
        for (String s : list)
            if (!s.equals(selectedContactKey))
                newList.add(s);
        contact.setContacts(newList);
        mapper.save(contact);
        // delete image in local
        File deleteFile = getFile(selectedContactKey);
        if (deleteFile.delete()) {
            Log.e(TAG, "deleteContact: success delete local");
        } else {
            Log.e(TAG, "deleteContact: fail delete local");
        }
    }

    public void deleteMyCard(String userId, String cardName) {
        // delete in DynamoDB
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        Card card = mapper.load(Card.class, userId, cardName);
        mapper.delete(card);
        // delete image in local
        String imageRef = userId+"_"+cardName;
        File deleteFile = getFile(imageRef);
        if (deleteFile.delete()) {
            Log.e(TAG, "deleteMyCard: success delete local");
        } else {
            Log.e(TAG, "deleteMyCard: fail delete local");
        }
        // delete image in S3
        AmazonS3 s3 = new AmazonS3Client(getCredentialsProvider());
        s3.deleteObject(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, imageRef);
    }

    public void deleteContactInBackground(String userId, String selectedContactKey) {
        new DeleteContactTask().execute(userId,selectedContactKey);
    }

    public void deleteMyCardInBackground(String userId, String cardName) {
        new DeleteCardTask().execute(userId,cardName);
    }

    private CognitoCachingCredentialsProvider getCredentialsProvider(){
        return AWSMobileClient
                .defaultMobileClient()
                .getIdentityManager()
                .getCredentialsProvider();
    }

    private class FindMyCardsTask extends AsyncTask<String, Void, PaginatedQueryList<Card>> {

        protected PaginatedQueryList<Card> doInBackground(String... id) {
            return findMyCards(id[0]);
        }

        protected void onPostExecute(PaginatedQueryList<Card> result) {
            myCardsCallback.done(result);
            Log.e(TAG, "onPostExecute: FindMyCardsTask" );

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

    private class AddToContactTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... s) {
            addToContact(s[0],s[1]);
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.e(TAG, "onPostExecute: ddddd" );
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

    private class SetPresenceTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... userId) {
            PaginatedQueryList<Card> myCards = findMyCards(userId[0]);
            if(myCards.size() == 0) {
                return null;
            }
            Card card = myCards.get(0);
            Discover presence = new Discover();
            presence.setUserId(card.getUserId());
            presence.setCardname(card.getCardname());
            presence.setImageRef(card.getImageRef());
            presence.setFirstName(card.getFirstName());
            presence.setLastName(card.getLastName());
            presence.setPosition(card.getPosition());
            cardName = card.getCardname();

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            mapper.save(presence);
            return null;
        }

        protected void onPostExecute(Void result) {

            Log.e(TAG, "onPostExecute: save presence" );

        }
    }

    private class RemovePresenceTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... userId) {
            PaginatedQueryList<Card> myCards = findMyCards(userId[0]);
            if(myCards.size() == 0) {
                return null;
            }
            Discover presence = new Discover();
            presence.setUserId(userId[0]);
            presence.setCardname(cardName);

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(getCredentialsProvider());
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            mapper.delete(presence);
            return null;
        }

        protected void onPostExecute(Void result) {

            Log.e(TAG, "onPostExecute: delete presence" );

        }
    }

    private class DeleteContactTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... s) {
            deleteContact(s[0],s[1]);
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.e(TAG, "onPostExecute: delete contact" );
        }
    }

    private class DeleteCardTask extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... s) {
            deleteMyCard(s[0],s[1]);
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.e(TAG, "onPostExecute: delete card" );

        }
    }

    private class GetMyContactCardsTask extends AsyncTask<String, Void, List<Card>> {

        protected List<Card> doInBackground(String... s) {
            return getMyContactCards(s[0]);
        }

        protected void onPostExecute(List<Card> result) {
            contactsCallback.done(result);
            Log.e(TAG, "onPostExecute: get contact cards" );
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

    private MyCardsCallback myCardsCallback = null;

    private ContactsCallback contactsCallback = null;

    public void setContactsCallback(ContactsCallback contactsCallback) {
        this.contactsCallback = contactsCallback;
    }

    public void setMyCardsCallback(MyCardsCallback myCardsCallback) {
        this.myCardsCallback = myCardsCallback;
    }

    public void setCallback(Callback c){
        this.callback = c;
    }

    public interface Callback {
        abstract void done(Card card);
    }

    public interface MyCardsCallback {
        abstract void done(PaginatedQueryList<Card> cards);
    }

    public interface ContactsCallback {
        abstract void done(List<Card> cards);
    }
}
