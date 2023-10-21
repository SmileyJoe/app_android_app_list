package io.smileyjoe.applist.`object`

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth

class User {

    lateinit var id: String
    var name: String? = null
    var email: String? = null
    var profileImage: Uri? = null

    companion object {
        @JvmStatic
        fun getCurrent(): User? {
            var firebaseUser = FirebaseAuth.getInstance().currentUser

            if (firebaseUser != null) {
                var user = User()
                user.id = firebaseUser.uid
                user.email = firebaseUser.email
                user.name = firebaseUser.displayName
                user.profileImage = firebaseUser.photoUrl
                return user
            } else {
                return null;
            }
        }
    }

}