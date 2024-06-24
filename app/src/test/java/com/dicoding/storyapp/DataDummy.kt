package com.dicoding.storyapp

import com.dicoding.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = mutableListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "photo_url_$i",
                createdAt = "2024-06-30",
                name = "Author $i",
                description = "Quote $i",
                lon = 0.0,
                id = i.toString(),
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}
