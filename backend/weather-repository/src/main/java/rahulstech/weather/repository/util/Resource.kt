package rahulstech.weather.repository.util

sealed class Resource<T>(val data: T? = null,val error: Throwable? = null) {

    // set partial or cached data while loading
    class Loading<T>(data: T? = null): Resource<T>(data)

    class Success<T>(data: T?): Resource<T>(data)

    class Error<T>(cause: Throwable): Resource<T>(error = cause)
}