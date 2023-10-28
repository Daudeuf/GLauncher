package fr.glauncher.game;

import fr.glauncher.Controller;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;

import java.util.UUID;

public class Auth
{
	private Controller ctrl;
	private AuthInfos  auth;

	public Auth( Controller ctrl )
	{
		this.ctrl = ctrl;
	}

	public boolean isAuth( boolean isOnline )
	{
		if (
				isOnline                                           &&
				this.ctrl.getSaver().get("msAccessToken")  != null &&
				this.ctrl.getSaver().get("msRefreshToken") != null
		) {
			try
			{
				MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
				MicrosoftAuthResult    response      = authenticator.loginWithRefreshToken(this.ctrl.getSaver().get("msRefreshToken"));

				this.ctrl.getSaver().set("msAccessToken",  response.getAccessToken() );
				this.ctrl.getSaver().set("msRefreshToken", response.getRefreshToken());
				this.ctrl.getSaver().save();

				this.auth = new AuthInfos(
						response.getProfile().getName(),
						response.getAccessToken(),
						response.getProfile().getId(),
						response.getXuid(),
						response.getClientId()
				);

				this.ctrl.refreshHeadImg();

				return true;
			}
			catch (MicrosoftAuthenticationException e)
			{
				this.ctrl.getSaver().remove("msAccessToken");
				this.ctrl.getSaver().remove("msRefreshToken");
				this.ctrl.getSaver().save();
			}
		}
		else if (!isOnline && ctrl.getSaver().get("offline-username") != null)
		{
			this.auth = new AuthInfos(
					this.ctrl.getSaver().get("offline-username"),
					UUID.randomUUID().toString(),
					UUID.randomUUID().toString()
			);

			this.ctrl.refreshHeadImg();

			return true;
		}

		return false;
	}

	public void auth(String mail, char[] password)
	{
		MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

		try
		{
			StringBuilder pswrd = new StringBuilder();

			for (char c : password) pswrd.append(c);

			MicrosoftAuthResult authRes = authenticator.loginWithCredentials(mail, pswrd.toString());

			this.ctrl.getSaver().set("msAccessToken", authRes.getAccessToken());
			this.ctrl.getSaver().set("msRefreshToken", authRes.getRefreshToken());
			this.ctrl.getSaver().save();

			this.auth = new AuthInfos(
					authRes.getProfile().getName(),
					authRes.getAccessToken(),
					authRes.getProfile().getId(),
					authRes.getXuid(),
					authRes.getClientId()
			);

			this.ctrl.refreshHeadImg();
			this.ctrl.getLogger().info("Hello " + authRes.getProfile().getName());
		}
		catch (MicrosoftAuthenticationException e)
		{
			this.ctrl.getLogger().err(e.getMessage());
		}
	}

	public AuthInfos getAuthInfos() { return this.auth; }
}
