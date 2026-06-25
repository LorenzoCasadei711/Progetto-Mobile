package it.supabase.remembermy.data.supabase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Profiles(
    val id_user: String,
    val level : Float,
    val email: String,
    val nickname: String?,
    val birth_date: String?,
    val avatar_url: String?,
    val created_at: String
) : Parcelable
@Serializable
@Parcelize
data class Events(
    val id_event: String? = null,
    val status_event: String? = null,
     val name_event: String,
    val is_private: Boolean,
    val date_event: String,
     val id_user: String,
    val event_photo: String? = null,
    val latitude: Double,
    val longitude: Double,
    val place_name : String?,
    val event_details : String?,
    @SerialName("followed_events")
    val followedEvents: List<FollowedEvents> = emptyList(),

    val opinions: List<Opinions> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class FollowedEvents(
    val id_user: String,
    val id_event: String
) : Parcelable
@Serializable
data class Badges(
    val id_badge : String,
    val name_badge:String,

)
@Serializable
data class UserBadge(
     val id_badge: String,
     val id_user: String,
     val badges: Badges
)

@Serializable
data class Tags(
    val id_tag : String ? = null,
    val name_tag:String,
    val id_user: String
)
@Serializable
@Parcelize
data class Opinions(
    val id_user : String,
    val id_event: String,
    val id_opinion:String? = null,
    val review_opinion: String?,
    val profiles : Profiles? = null
) : Parcelable

@Serializable
data class EventTag(
    val id_event: String,
    val id_tag: String
)
