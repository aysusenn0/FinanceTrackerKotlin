# Finance Tracker (Android Bütçe Takip Uygulaması)

[cite_start]Bu proje, **Finance Tracker** adlı çoklu para birimli bütçe yönetim sisteminin [cite: 1, 4] Android (Kotlin) istemci uygulamasıdır.

Uygulama, gelir ve giderleri takip etmek, güncel kurları görmek ve bütçe özeti almak için merkezi bir **.NET 8 Backend API**'sine bağlanacak şekilde tasarlanmıştır.

## 📱 Mimari ve Kullanılan Teknolojiler

Bu Android uygulaması, modern ve sürdürülebilir bir yapı için aşağıdaki teknolojileri kullanır:

* **Dil:** Kotlin
* **Asenkron:** Kotlin Coroutines (Korutinler)
* **Mimari:** MVVM (Model-View-ViewModel)
* **UI Yönetimi:** ViewModel ve StateFlow
* **Ağ (Networking):** Retrofit & OkHttp (Backend API'si ile konuşmak için)
* **JSON Çözümleme:** Gson (veya Moshi)

## ⚙️ Projenin Çalıştırılması (Çok Önemli)

Bu uygulama tek başına çalışmaz. Bir backend (sunucu) uygulamasına bağlanması gerekir. Projeyi tam fonksiyonlu çalıştırmak için iki bileşenin de ayakta olması gerekir.

### 1. Adım: Backend (.NET 8 API)

[cite_start]Bu projenin ihtiyaç duyduğu API, .NET 8, MS SQL Server [cite: 50] [cite_start]ve Redis [cite: 64] kullanan ayrı bir projedir.

1.  Backend projesini Visual Studio 2022'de açın.
2.  [cite_start]Gerekli veritabanı (SQL Express) [cite: 50] [cite_start]ve cache (Redis) [cite: 64] servislerinin çalıştığından emin olun.
3.  API projesini başlatın.
4.  Tarayıcınızda `http://localhost:5000/swagger` veya `https://localhost:5001/swagger` adresinin çalıştığını doğrulayın.

### 2. Adım: Android Uygulaması (Bu Proje)

Backend API'si ayaktayken:

1.  Bu projeyi Android Studio'da açın.
2.  `network/RetrofitClient.kt` dosyasındaki `BASE_URL` sabitini kontrol edin.
3.  **Emülatör Kullanıyorsanız:** Emülatörler `localhost`'a `10.0.2.2` IP'si üzerinden erişir. `BASE_URL` şu şekilde olmalıdır:
    ```kotlin
    private const val BASE_URL = "[http://10.0.2.2:5000/](http://10.0.2.2:5000/)"
    ```
    *(Not: `http` (güvenli olmayan) bağlantı için `AndroidManifest.xml` dosyasında `usesCleartextTraffic="true"` izni ayarlanmıştır.)*
4.  Uygulamayı çalıştırın. Artık API'den veri çekebiliyor olmalıdır.
