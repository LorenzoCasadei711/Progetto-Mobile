package com.example.progettomobile.data.database

import android.R
import androidx.collection.arrayMapOf
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Relation
import java.sql.Date

@Entity
data class Profiles(
    @PrimaryKey (autoGenerate = true) val user_id: Int = 0,
    @ColumnInfo val level : Float,
    @ColumnInfo val password: String,
    @ColumnInfo(index = true) val nickname: String,
    @ColumnInfo val birth_date: Date
)

@Entity (foreignKeys = [ForeignKey(
    entity = Profiles::class,
    parentColumns = arrayOf("user_id"),
    childColumns = arrayOf("organizer"),
    onDelete = ForeignKey.CASCADE)]
)
data class Events(
    @PrimaryKey (autoGenerate = true) val event_id: Int = 0,
    @ColumnInfo val status_event: String? = null,
    @ColumnInfo(index = true) val name_event: String,
    @ColumnInfo val is_private: Boolean,
    @ColumnInfo val location_event: String,
    @ColumnInfo val date_event: Date,
    @ColumnInfo(index=true) val organizer: String
)
@Entity
data class Badges(
    @PrimaryKey (autoGenerate = true) val badge_id : Int = 0,
    @ColumnInfo val name_badge:String,
    @ColumnInfo val exp_give: String
)

@Entity
data class Tags(
    @PrimaryKey (autoGenerate = true) val id_tag : Int = 0,
    @ColumnInfo val name_tag:String
)

@Entity(primaryKeys = ["user_id", "event_id"])
data class Opinions(
    val user_id : Int,
    val event_id:Int,
    @ColumnInfo
    val opinion:String?,
    val liked: Boolean?
)

@Entity(foreignKeys = [ForeignKey(
    entity = Profiles::class,
    parentColumns = arrayOf("user_id"),
    childColumns = ["user"],
    onDelete = ForeignKey.CASCADE
),
    ForeignKey(
        entity = Profiles::class,
        parentColumns = arrayOf("user_id"),
        childColumns = ["following"],
        onDelete = ForeignKey.CASCADE
    )                  ],
    primaryKeys = ["user","following"])
data class FollowList(
    val user : Int,
    val following: Int
)

@Entity(foreignKeys = [
    ForeignKey(
        entity = Badges::class,
        parentColumns = arrayOf("badge_id"),
        childColumns = arrayOf("badge"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Profiles::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("user"),
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["badge, user"]
)
data class User_Badges(
    val badge : Int,
    val user : Int
)

@Entity(foreignKeys = [
    ForeignKey(
        entity = Events::class,
        parentColumns=arrayOf("event_id"),
        childColumns = arrayOf("event"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity= Tags::class,
        parentColumns = arrayOf("tag_id"),
        childColumns = arrayOf("tag"),
        onDelete = ForeignKey.CASCADE
    )
], primaryKeys = ["event", "tag"])
data class Event_Tags(
    val event : Int,
    val tag: Int
)

@Entity(foreignKeys = [
    ForeignKey(
        entity=Profiles::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("user"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = Events::class,
        parentColumns = arrayOf("event_id"),
        childColumns = arrayOf("event"),
        onDelete = ForeignKey.CASCADE
    )
], primaryKeys = ["user", "event"],
    indices = [Index(value = ["user", "event"], unique = true)])
data class Event_Following(
    val user: Int,
    val event: Int
)

@Entity
data class Details(
    @PrimaryKey(autoGenerate = true) val details_id: Int = 0,
    @ColumnInfo val event_details : String,
    val event_id : Int
)

data class Event_Details(
    @Embedded val event : Events,
    @Relation(
        parentColumn = "event_id",
        entityColumn = "event_id"
    )
    val details : Details
)