package it.supabase.remembermy.data.supabase

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
    val status_event: String? = null,
     val name_event: String,
    val is_private: Boolean,
    val date_event: String,
     val id_user: String,
    val event_photo: String,
    val latitude: Double,
    val longitude: Double

)
@Serializable
data class Badges(
    val badge_id : String,
    val name_badge:String
)
@Serializable
data class UserBadge(
     val badge_id: String,
     val id_user: String,
     val badgeDetails: Badges
)

@Serializable
data class Tags(
    val id_tag : String,
    val name_tag:String
)
@Serializable
data class Opinions(
    val user_id : Int,
    val event_id:Int,
    val opinion:String?,
    val liked: Boolean?
)

@Serializable
data class Details(
    val details_id: String,
    val event_details : String,
    val event_id : Int
)