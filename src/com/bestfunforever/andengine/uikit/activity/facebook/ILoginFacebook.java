package com.bestfunforever.andengine.uikit.activity.facebook;

import com.facebook.Session;
import com.facebook.SessionState;

public interface ILoginFacebook {
	public void onLoginFacebookSuccess();
	
	public void onLoginFacebookFail(Session session, SessionState state,
			Exception exception);
}
