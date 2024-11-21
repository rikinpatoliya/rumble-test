package com.rumble.network.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.rumble.network.dto.repost.Repost
import com.rumble.network.dto.video.FeedItem
import com.rumble.network.dto.video.Video
import java.lang.reflect.Type

class FeedItemDeserializer : JsonDeserializer<FeedItem> {
    private val keyField = "object_type"

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): FeedItem {
        val jsonObject = json.asJsonObject
        return if (jsonObject.has(keyField)) {
            when(FeedObjectType.getByValue(jsonObject.get(keyField).asString))  {
                FeedObjectType.Repost ->  context.deserialize(jsonObject, Repost::class.java)
                else -> context.deserialize(jsonObject, Video::class.java)
            }
        } else context.deserialize(jsonObject, Video::class.java)
    }
}