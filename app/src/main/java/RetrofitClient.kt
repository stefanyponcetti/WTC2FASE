import android.os.Build
import com.example.wtcapp.login.UserApi
import com.example.wtcapp.redefinicaoSenha.RedefinirSenhaApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.lowercase().contains("vbox")
                || Build.FINGERPRINT.lowercase().contains("test-keys")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }

//    private val BASE_URL = if (isEmulator()) {
//        "http://10.0.2.2:5255/"
//    } else {
//        //"http://192.168.15.9:5255/"
//        "http://localhost:5255/"
//    }

    private val BASE_URL = "http://10.0.2.2:5255/"

    val api: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    val api2: RedefinirSenhaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedefinirSenhaApi::class.java)
    }
}