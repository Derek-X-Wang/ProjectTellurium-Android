package com.intbridge.projecttellurium.airbridge.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    // sign in
    @BindView(R.id.signin_form)
    protected LinearLayout signInForm;

    @BindView(R.id.username_signin)
    protected EditText usernameSignInView;
    @BindView(R.id.password_signin)
    protected EditText passwordSignInView;

    @BindView(R.id.username_signin_textinputlayout)
    protected TextInputLayout usernameSignInLayout;
    @BindView(R.id.password_signin_textinputlayout)
    protected TextInputLayout passwordSignInLayout;

    @BindView(R.id.sign_in_button)
    protected Button signInButton;
    @BindView(R.id.toggle_to_signup)
    protected TextView toSignUp;

    // sign up
    @BindView(R.id.signup_form)
    protected LinearLayout signUpForm;

    @BindView(R.id.username_signup)
    protected EditText usernameSignUpView;
    @BindView(R.id.email_signup)
    protected EditText emailSignUpView;
    @BindView(R.id.password_signup)
    protected EditText passwordSignUpView;

    @BindView(R.id.username_signup_textinputlayout)
    protected TextInputLayout usernameSignUpLayout;
    @BindView(R.id.email_signup_textinputlayout)
    protected TextInputLayout emailSignUpLayout;
    @BindView(R.id.password_signup_textinputlayout)
    protected TextInputLayout passwordSignUpLayout;

    @BindView(R.id.sign_up_button)
    protected Button signUpButton;
    @BindView(R.id.toggle_to_signin)
    protected TextView toSignIn;




    // User Details
    private String username;
    private String email;
    private String password;

    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        initViews();

        // Initialize application
        CognitoHelper.init(getApplicationContext());
        findCurrent();

    }

    private void initViews() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                signInUser();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpNewUser();
            }
        });
//        emailLayout.setErrorEnabled(true);
//        mPasswordLayout.setErrorEnabled(true);
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInForm.setVisibility(View.GONE);
                signUpForm.setVisibility(View.VISIBLE);
            }
        });
        toSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpForm.setVisibility(View.GONE);
                signInForm.setVisibility(View.VISIBLE);
            }
        });
    }

    private void signUpNewUser() {
//        Intent registerActivity = new Intent(this, RegisterUser.class);
//        startActivityForResult(registerActivity, 1);
        username = usernameSignUpView.getText().toString();
        email = emailSignUpView.getText().toString();
        password = passwordSignUpView.getText().toString();
        showWaitDialog("Signing up...");
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("email", email);
        CognitoHelper.getPool().signUpInBackground(username, password, userAttributes, null, signUpHandler);
    }

    private void signInUser() {
        username = usernameSignInView.getText().toString();
        if(email == null || email.length() < 1) {
//            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
//            label.setText(mEmailView.getHint()+" cannot be empty");
//            mEmailView.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        CognitoHelper.setUser(username);

        password = passwordSignInView.getText().toString();
        if(password == null || password.length() < 1) {
//            TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
//            label.setText(mPasswordView.getHint()+" cannot be empty");
//            mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("Signing in...");
        CognitoHelper.getPool().getUser(email).getSessionInBackground(authenticationHandler);
    }

    private void launchMain() {
        Log.e("Launch", "launchUser: "+CognitoHelper.getPool().getCurrentUser().getUserId());
        Intent userActivity = new Intent(this, MainActivity.class);
        userActivity.putExtra("name",
                CognitoHelper.getPool().getCurrentUser().getUserId());

        //startActivityForResult(userActivity, 4);
        startActivity(userActivity);
        finish();
    }

    private void findCurrent() {
        CognitoUser user = CognitoHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if(username != null) {
            CognitoHelper.setUser(username);
            usernameSignInView.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            this.username = username;
            CognitoHelper.setUser(username);
        }
//        if(this.password == null) {
//            mEmailView.setText(username);
//            password = mPasswordView.getText().toString();
//            if(password == null) {
////                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
////                label.setText(mPasswordView.getHint()+" enter password");
////                mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
//                return;
//            }
//
//            if(password.length() < 1) {
////                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
////                label.setText(mPasswordView.getHint()+" enter password");
////                mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
//                return;
//            }
//        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void clearInput() {
        usernameSignInView.setText("");
        usernameSignUpView.setText("");
        passwordSignInView.setText("");
        passwordSignUpView.setText("");
        emailSignUpView.setText("");
        //mEmailView.requestFocus();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
//    private void attemptLogin() {
//
//        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);
//
//        // Store values at the time of the login attempt.
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
//
//        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }
//
//        if (cancel) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            focusView.requestFocus();
//        } else {
//            // Show a progress spinner, and kick off a background task to
//            // perform the user login attempt.
//            //showProgress(true);
////            mAuthTask = new UserLoginTask(email, password);
////            mAuthTask.execute((Void) null);
//        }
//    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession) {
            Logger.d("auth on success");
            CognitoHelper.setCurrSession(cognitoUserSession);
            closeWaitDialog();
            launchMain();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            closeWaitDialog();
            Logger.e("getMFACode",username);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            closeWaitDialog();
            Logger.e("getMFACode");
            //mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();

            showDialogMessage("Sign-in failed", CognitoHelper.formatException(e));
        }
    };

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            closeWaitDialog();
            Boolean regState = signUpConfirmationState;
            if (signUpConfirmationState) {
                // User is already confirmed
                showDialogMessage("Sign up successful!", "Confirmed");
            }
            else {
                // User is not confirmed
                showDialogMessage("Sign up successful!", "not Confirmed");
            }
        }

        @Override
        public void onFailure(Exception e) {
            showDialogMessage("Sign up failed!", "not Confirmed");
            Logger.e(e.getMessage());
        }
    };

}
