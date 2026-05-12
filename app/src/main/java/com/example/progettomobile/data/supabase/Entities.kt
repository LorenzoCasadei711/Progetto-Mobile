package com.example.progettomobile.data.supabase

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Profiles(
    val id_user: String,
    val level : Float,
    val email: String,
    val nickname: String,
    val birth_date: String,
    val avatar_url: String,
    val createdAt: String
)
@Serializable
data class Events(
    val event_id: String,
    val status_event: String? = null,
     val name_event: String,
    val is_private: Boolean,
    val location_event: String,
    val date_event: String,
     val organizer: String
)
@Serializable
data class Badges(
    val badge_id : String,
    val name_badge:String,
    val exp_give: String
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