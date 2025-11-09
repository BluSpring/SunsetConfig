package xyz.bluspring.sunset.serializer.json

import com.google.gson.JsonElement

class JsonComment(val comment: String) : JsonElement() {
    override fun deepCopy(): JsonElement {
        return JsonComment(comment)
    }
}