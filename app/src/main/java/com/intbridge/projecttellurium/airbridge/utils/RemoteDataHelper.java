package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.squareup.picasso.Picasso;

import java.io.File;

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

    private CognitoCachingCredentialsProvider getCredentialsProvider(){
        return AWSMobileClient
                .defaultMobileClient()
                .getIdentityManager()
                .getCredentialsProvider();
    }
}
