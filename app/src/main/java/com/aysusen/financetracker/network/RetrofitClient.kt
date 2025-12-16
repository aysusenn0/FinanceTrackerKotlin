import com.aysusen.financetracker.service.RetrofitAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://172.20.0.54:5210/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor).build()
    val instance: RetrofitAPI by lazy {  //tek bir sefer başlatmak için lazy kullandım.il erişiline kadar geciktir
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Loglama yapan istemci
            .addConverterFactory(GsonConverterFactory.create()) // Gson'u (tercümanı) ekle
            .build()
            .create(RetrofitAPI::class.java)
    }
}