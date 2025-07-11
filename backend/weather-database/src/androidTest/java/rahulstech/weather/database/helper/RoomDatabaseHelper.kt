package rahulstech.weather.database.helper

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import rahulstech.weather.database.WeatherDB
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun createInMemoryWeatherDB(fakeData: FakeData? = null): WeatherDB {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val builder = Room.inMemoryDatabaseBuilder(context, WeatherDB::class.java).allowMainThreadQueries()
    builder.addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            fakeData?.insert(db)
        }
    })
    return builder.build()
}

fun <T> testLiveData(liveData: LiveData<T>, timeoutMillis: Long = 3000, onResult: (T?)->Unit) {
    val mainHandler = Handler(Looper.getMainLooper())
    val latch = CountDownLatch(1)

    val observer = Observer<T> { result ->
        onResult(result)
        latch.countDown()
    }

    mainHandler.post { liveData.observeForever(observer) }
    latch.await(timeoutMillis, TimeUnit.MILLISECONDS)
    mainHandler.post { liveData.removeObserver(observer) }
}