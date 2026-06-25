package it.supabase.remembermy.data.repository


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OSMPlace(
    @SerialName("place_id")
    val id: Int,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double,
    @SerialName("display_name")
    val displayName: String
)

class OSMDataSource(private val httpClient : HttpClient){
    companion object {
        private const val BASE_URL = "https://nominatim.openstreetmap.org"
    }

    suspend fun searchPlaces(query : String) : List<OSMPlace>{
        val url = "$BASE_URL/search?q=$query&format=json&limit=1"
        return httpClient.get(url).body()
    }

    suspend fun searchWithCoordinates(latitude : Double, longitude : Double) : OSMPlace?{
        val url = "$BASE_URL/reverse?lat=$latitude&lon=$longitude&format=json&limit=1"

        return try {
            val response: HttpResponse = httpClient.get(url)

            if (response.status.isSuccess()) {
                response.body<OSMPlace>()
            } else {
                when (response.status) {
                    HttpStatusCode.TooManyRequests -> {
                        println("OSM Error: You are being rate-limited. Max 1 request/sec.")
                    }
                    else -> println("OSM Error: ${response.status}")
                }
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}