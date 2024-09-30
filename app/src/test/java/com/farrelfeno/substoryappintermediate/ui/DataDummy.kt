package com.farrelfeno.substoryappintermediate.ui

import com.farrelfeno.substoryappintermediate.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1699437652701__P-tYD-a.jpg",
                "2023-11-08T10:00:52.703Z",
                "ronaldo",
                "siuuu",
                "null",
                "story-K0ym26Zj1Da2Kd4U",
                "null",
            )
            items.add(story)
        }
        return items
    }
}