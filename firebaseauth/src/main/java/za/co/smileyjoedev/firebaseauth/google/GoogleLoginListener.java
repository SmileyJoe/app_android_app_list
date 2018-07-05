package za.co.smileyjoedev.firebaseauth.google;

/**
 * Created by cody on 2017/04/11.
 */

public interface GoogleLoginListener {

    public void onLogIn();
    public void onLogOut();
    public void onLogInFail();

}
