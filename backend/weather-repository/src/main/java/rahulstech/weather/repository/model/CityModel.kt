package rahulstech.weather.repository.model

data class CityModel(
    val id: Long,
    val name: String,
    val country: String,
    val longitude: Float,
    val latitude: Float,
    val remoteId: String
)
