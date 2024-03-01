import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginUser(email: String, password: String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun createUser(email: String, password: String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
}
