package com.intbridge.projecttellurium.airbridge.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.intbridge.projecttellurium.airbridge.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AWS User Pool
 * Created by Derek on 7/10/2016.
 */
public class AWSUserPoolHelper {
    private CognitoUserPool userPool;

    public AWSUserPoolHelper(Context context) {
        // setup AWS service configuration. Choosing default configuration
        ClientConfiguration clientConfiguration = new ClientConfiguration();

        // Create a CognitoUserPool object to refer to your user pool
        userPool = new CognitoUserPool(context,
                        AWSConfiguration.AMAZON_COGNITO_USER_POOL_ID,
                        AWSConfiguration.AMAZON_COGNITO_USER_POOL_CLIENT_ID,
                        AWSConfiguration.AMAZON_COGNITO_USER_POOL_CLIENT_SECRET,
                        clientConfiguration);
    }

    public CognitoUserPool getUserPool() {
        return userPool;
    }


    public void signUp(String email, String psd) {
        String userId = email;
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("email", email);
        SignUpHandler signupCallback = new SignUpCallBack();

        userPool.signUp(userId,psd,userAttributes,null,signupCallback);

    }

    public void signIn(String email, final String psd) {
//        String userId = email;
//        // Callback handler for the sign-in process
//        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
//
//            @Override
//            public void onSuccess(CognitoUserSession cognitoUserSession) {
//                // Sign-in was successful, cognitoUserSession will contain tokens for the user
//            }
//
//            @Override
//            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
//                // The API needs user sign-in credentials to continue
//                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, psd, null);
//
//                // Pass the user sign-in credentials to the continuation
//                authenticationContinuation.setAuthenticationDetails(authenticationDetails);
//
//                // Allow the sign-in to continue
//                authenticationContinuation.continueTask();
//            }
//
//            @Override
//            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
//                // Multi-factor authentication is required, get the verification code from user
//                multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
//                // Allow the sign-in process to continue
//                multiFactorAuthenticationContinuation.continueTask();
//            }
//
//            @Override
//            public void onFailure(Exception exception) {
//                // Sign-in failed, check exception for the cause
//            }
//        };
//
//        // Sign-in the user
//        cognitoUser.getSessionInBackground(authenticationHandler);
//
//
//        // Get id token from CognitoUserSession.
//        String idToken = cognitoUserSession.getIdToken().getJWTToken();
//
//        // Create a credentials provider, or use the existing provider.
//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(context, IDENTITY_POOL_ID, REGION);
//
//        // Set up as a credentials provider.
//        Map<String, String> logins = new HashMap<String, String>();
//        logins.put("cognito-idp.us-east-1.amazonaws.com/us-east-1_123456678", cognitoUserSession.getIdToken().getJWTToken());
//        credentialsProvider.setLogins(logins);
    }

    public void signOut() {
        //user.signOut();
    }

    public class SignUpCallBack implements SignUpHandler {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            Logger.d("Sign up done");
            // Check if this user (cognitoUser) has to be confirmed
            if(!userConfirmed) {
                // This user has to be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
                Logger.d("not confirmed");
            }
            else {
                // The user has already been confirmed
                Logger.d("confirmed");
            }
        }

        @Override
        public void onFailure(Exception e) {
            // Sign-up failed, check exception for the cause
        }
    }


}
