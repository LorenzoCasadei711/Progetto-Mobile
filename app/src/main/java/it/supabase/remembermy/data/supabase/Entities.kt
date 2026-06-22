package it.supabase.remembermy.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profiles(
    val id_user: String,
    val level : Float,
    val email: String,
    val nickname: String?,
    val birth_date: String?,
    val avatar_url: String?,
    val created_at: String
)
@Serializable
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
    val event_details : String?,
    @SerialName("followed_events")
    val followedEvents: List<FollowedEvents> = emptyList(),

    val opinions: List<Opinions> = emptyList()
)

@Serializable
data class FollowedEvents(
    val id_user: String,
    val id_event: String
)
@Serializable
data class Badges(
    val badge_id : String,
    val name_badge:String,

)
@Serializable
data class UserBadge(
     val badge_id: String,
     val id_user: String,
     val badgeDetails: Badges
)

@Serializable
data class Tags(
    val id_tag : String ? = null,
    val name_tag:String,
    val id_user: String
)
@Serializable
data class Opinions(
    val user_id : String,
    val event_id:String,
    val id_opinion:String? = null,
    val review_opinion: String?,
    val profile : Profiles? = null
)

@Serializable
data class EventTag(
    val id_event: String,
    val id_tag: String
)
