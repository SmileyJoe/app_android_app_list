package io.smileyjoe.applist.objects

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Stores the current logged in users information.
 * </p>
 * This is basically just a wrapper class for the [FirebaseUser] object, so
 * all constructors are private, and useage is with [User.current]
 *
 * @param id usersId from firebase. See [FirebaseUser.getUid]
 * @param name the users name. See [FirebaseUser.getDisplayName]
 * @param email the email used to log in. See [FirebaseUser.getEmail]
 * @param profileImage url to the profile image. See [FirebaseUser.getPhotoUrl]
 */
data class User private constructor(
    val id: String,
    val name: String?,
    val email: String?,
    val profileImage: Uri?
) {

    /**
     * Secondary constructor to take the [FirebaseUser] object and convert it.
     *
     * @param firebaseUser logged in firebase user
     */
    private constructor(firebaseUser: FirebaseUser) : this(
        id = firebaseUser.uid,
        name = firebaseUser.displayName,
        email = firebaseUser.email,
        profileImage = firebaseUser.photoUrl
    )

    companion object {
        /**
         * Current logged in user, or Null if no one is logged in
         */
        var current: User?
            get() {
                FirebaseAuth.getInstance().currentUser?.let {
                    return User(it)
                } ?: run {
                    return null
                }
            }
            private set(value) {}
    }

}