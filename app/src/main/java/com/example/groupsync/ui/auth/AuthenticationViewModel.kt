import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel : ViewModel() {
    private val authRepository = AuthenticationRepository()
    private val _userLiveData = MutableLiveData<FirebaseUser?>()

    val userLiveData: LiveData<FirebaseUser?> = _userLiveData

    fun loginUser(email: String, password: String) {
        authRepository.loginUser(email, password).addOnCompleteListener { task ->
            _userLiveData.value = if (task.isSuccessful) {
                authRepository.currentUser
            } else {
                null
            }
        }
    }

    fun createUser(email: String, password: String) {
        authRepository.createUser(email, password).addOnCompleteListener { task ->
            _userLiveData.value = if (task.isSuccessful) {
                authRepository.currentUser
            } else {
                null
            }
        }
    }

    fun logoutUser() {
        authRepository.logoutUser()
        _userLiveData.value = null
    }
}
