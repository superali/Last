package com.baitran.ali.last.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by ali on 24/12/15.
 */
public class MyAuthenticator  extends AbstractAccountAuthenticator {

    public MyAuthenticator(Context context) {
        super(context);
    }

    /**
     * Editing properties is not supported
     * @param response
     * @param accountType
     * @return
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        throw new UnsupportedOperationException();
    }

    /**
     * Don't add additional accounts
     * @param response
     * @param accountType
     * @param authTokenType
     * @param requiredFeatures
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType,
                             String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {

        return null;
    }

    /**
     * Ignore attempts to confirm credentials
     * @param response
     * @param account
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account,
                                     Bundle options) throws NetworkErrorException {

        return null;
    }

    /**
     * Getting an authentication token is not supported
     * @param response
     * @param account
     * @param authTokenType
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account,
                               String authTokenType,
                               Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    /**
     * Getting a label for the auth token is not supported
     * @param authTokenType
     * @return
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    /**
     * Updating user credentials is not supported
     * @param response
     * @param account
     * @param authTokenType
     * @param options
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    /**
     * Checking features for the account is not supported
     * @param response
     * @param account
     * @param features
     * @return
     * @throws NetworkErrorException
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account,
                              String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
