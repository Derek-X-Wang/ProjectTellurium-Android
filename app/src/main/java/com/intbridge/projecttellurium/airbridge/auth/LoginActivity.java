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
    @BindView(R.id.username)
    protected EditText mUsernameView;
    @BindView(R.id.email)
    protected EditText mEmailView;
    @BindView(R.id.password)
    protected EditText mPasswordView;

    @BindView(R.id.username_layout)
    protected LinearLayout mUsernameLayout;
    @BindView(R.id.email_layout)
    protected TextInputLayout mEmailLayout;
    @BindView(R.id.password_layout)
    protected TextInputLayout mPasswordLayout;


    @BindView(R.id.login_progress)
    protected View mProgressView;
    @BindView(R.id.login_form)
    protected View mLoginFormView;
    @BindView(R.id.email_sign_in_button)
    protected Button mEmailSignInButton;
    @BindView(R.id.email_sign_up_button)
    protected Button mEmailSignUpButton;

    @BindView(R.id.toggle_signin)
    protected TextView toSignIn;
    @BindView(R.id.toggle_signup)
    protected TextView toSignUp;

    @BindView(R.id.sign_up_layout)
    protected LinearLayout toggleSignInLayout;
    @BindView(R.id.sign_in_layout)
    protected LinearLayout toggleSignUpLayout;

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
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                signInUser();
            }
        });
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpNewUser();
            }
        });
        mEmailLayout.setErrorEnabled(true);
        mPasswordLayout.setErrorEnabled(true);
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSignInLayout.setVisibility(View.GONE);
                mEmailSignInButton.setVisibility(View.GONE);
                mEmailSignUpButton.setVisibility(View.VISIBLE);
                toggleSignUpLayout.setVisibility(View.VISIBLE);
                mUsernameLayout.setVisibility(View.VISIBLE);
            }
        });
        toSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailSignUpButton.setVisibility(View.GONE);
                toggleSignUpLayout.setVisibility(View.GONE);
                mUsernameLayout.setVisibility(View.GONE);
                toggleSignInLayout.setVisibility(View.VISIBLE);
                mEmailSignInButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void signUpNewUser() {
//        Intent registerActivity = new Intent(this, RegisterUser.class);
//        startActivityForResult(registerActivity, 1);
        username = mUsernameView.getText().toString();
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        showWaitDialog("Signing up...");
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("email", email);
        CognitoHelper.getPool().signUpInBackground(username, password, userAttributes, null, signUpHandler);
    }

    private void signInUser() {
        email = mEmailView.getText().toString();
        if(email == null || email.length() < 1) {
//            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
//            label.setText(mEmailView.getHint()+" cannot be empty");
//            mEmailView.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        CognitoHelper.setUser(username);

        password = mPasswordView.getText().toString();
        if(password == null || password.length() < 1) {
//            TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
//            label.setText(mPasswordView.getHint()+" cannot be empty");
//            mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("Signing in...");
        CognitoHelper.getPool().getUser(email).getSessionInBackground(authenticationHandler);
    }

    private void launchUser() {
        Intent userActivity = new Intent(this, MainActivity.class);
        userActivity.putExtra("name", username);
        //startActivityForResult(userActivity, 4);
        startActivity(userActivity);
        finish();
    }

    private void findCurrent() {
        CognitoUser user = CognitoHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if(username != null) {
            CognitoHelper.setUser(username);
            mEmailView.setText(user.getUserId());
            user.getSessionInBackground(authenticationHandler);
        }
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            this.username = username;
            CognitoHelper.setUser(username);
        }
        if(this.password == null) {
            mEmailView.setText(username);
            password = mPasswordView.getText().toString();
            if(password == null) {
//                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
//                label.setText(mPasswordView.getHint()+" enter password");
//                mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }

            if(password.length() < 1) {
//                TextView label = (TextView) findViewById(R.id.textViewUserPasswordMessage);
//                label.setText(mPasswordView.getHint()+" enter password");
//                mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void clearInput() {
        mEmailView.setText("");
        mEmailView.requestFocus();
//        mEmailView.setBackground(getDrawable(R.drawable.text_border_selector));
        mPasswordView.setText("");
//        mPasswordView.setBackground(getDrawable(R.drawable.text_border_selector));
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
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession) {
            Logger.d("auth on success");
            CognitoHelper.setCurrSession(cognitoUserSession);
            closeWaitDialog();
            launchUser();
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
//            TextView label = (TextView) findViewById(R.id.textViewUserIdMessage);
//            label.setText("Sign-in failed");
//            mPasswordView.setBackground(getDrawable(R.drawable.text_border_error));
//
//            label = (TextView) findViewById(R.id.textViewUserIdMessage);
//            label.setText("Sign-in failed");
//            mEmailView.setBackground(getDrawable(R.drawable.text_border_error));

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
