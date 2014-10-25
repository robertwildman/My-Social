/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.wildapps.my_social;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.WebIdentityFederationSessionCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
//Import AWS service packages here:
import com.amazonaws.services.s3.AmazonS3Client;

import android.util.Log;

/**
 * This class is used to get clients to the various AWS services. Before
 * accessing a client the credentials should be checked to ensure validity.
 */
public class AmazonClientManager {

	private static final String LOG_TAG = "AmazonClientManager";
    
	private SharedPreferences sharedPreferences = null;

	private WebIdentityFederationSessionCredentialsProvider wif = null;
	private WIFIdentityProvider idp = null;

	private String fbRoleARN = null;
	
    //Create class variables for each AWS client you use here:
    private AmazonS3Client s3 = null;
    private AmazonDynamoDBClient DDB = null;

	public AmazonClientManager(SharedPreferences settings, Bundle bundle) {
		this.sharedPreferences = settings;
		fbRoleARN = "arn:aws:iam::072893446206:role/Facebooklogin";
		
	}
    
    //Add methods for each AWS service here:
	public AmazonS3Client s3() {
		s3 = new AmazonS3Client( wif );
		return s3;
	}
	public AmazonDynamoDBClient DDB()
	{
		DDB = new AmazonDynamoDBClient(wif);
		return DDB;
		
	}
	public boolean hasCredentials() {
		return !(fbRoleARN.equals("Your Role ARN"));
	}

	public boolean isLoggedIn() {
		return ( s3 != null ); //Change to appropriate variable
	}

	public void clearCredentials() {
		synchronized (this) {
			AmazonSharedPreferencesWrapper.wipe(this.sharedPreferences);
            s3 = null; //Change to appropriate variable
            DDB = null;
		}
	}

	
	public void login( WIFIdentityProvider wifIDP ) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		idp = wifIDP;
		wif = new WebIdentityFederationSessionCredentialsProvider(idp.getToken(),idp.getProviderID(), idp.getRoleARN()); 
		wif.refresh();
        
        //Add initializations for each AWS client here:
		s3 = new AmazonS3Client( wif );
        
		AmazonSharedPreferencesWrapper.storeUsername(this.sharedPreferences, wif.getSubjectFromWIF());
		Log.d(LOG_TAG, "Logged in with user id " + wif.getSubjectFromWIF());
	}

	
	public String getUsername() {
		return AmazonSharedPreferencesWrapper.getUsername( this.sharedPreferences );
	}

	public String getFacebookRoleARN() {
		return fbRoleARN;
	}

	public void wipe() {
		AmazonSharedPreferencesWrapper.wipe( this.sharedPreferences );
	}
}
