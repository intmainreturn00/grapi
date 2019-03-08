package com.intmainreturn00.grapi

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader


internal inline fun <reified T : Model> parse(xml: String) = when (T::class) {
    UserId::class -> parseUserId(xml)
    UserShelves::class -> parseUserShelves(xml)
    SearchResults::class -> parseSearchResults(xml)
    Book::class -> parseBook(xml)
    ReviewList::class -> parseReviewList(xml)
    else -> throw Exception("can't match proper parser")
} as T



internal fun parseUserId(xml: String): UserId {
    val parser = Xml.newPullParser()
    parser.setInput(StringReader(xml))
    var id = ""
    var name = ""
    var link = ""

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "user" -> id = readArg(parser, "id")
            "name" -> name = readText(parser)
            "link" -> link = readText(parser)
        }
    }

    return UserId(id, name, link)
}

internal fun parseUserShelves(xml: String): UserShelves {
    val parser = Xml.newPullParser()
    parser.setInput(StringReader(xml))
    var start = 0
    var end = 0
    var total = 0
    val shelves = mutableListOf<Shelf>()

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "shelves" -> {
                start = readArg(parser, "start").toInt()
                end = readArg(parser, "end").toInt()
                total = readArg(parser, "total").toInt()
            }
            "user_shelf" -> shelves.add(readShelf(parser))
        }
    }

    return UserShelves(start, end, total, shelves)
}

internal fun parseSearchResults(xml: String): SearchResults {
    val parser = Xml.newPullParser()
    parser.setInput(StringReader(xml))

    var searchResults: SearchResults? = null

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "search" -> searchResults = readSearchResults(parser)
        }
    }

    return searchResults!!
}

internal fun parseBook(xml: String): Book {
    val parser = Xml.newPullParser()
    parser.setInput(StringReader(xml))

    var book: Book? = null

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "book" -> book = readBook(parser)
        }
    }

    return book!!
}

internal fun parseReviewList(xml: String): ReviewList {
    val parser = Xml.newPullParser()
    parser.setInput(StringReader(xml))

    var start = 0
    var end = 0
    var total = 0
    val reviews = mutableListOf<Review>()

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "reviews" -> {
                start = readArg(parser, "start").toInt()
                end = readArg(parser, "end").toInt()
                total = readArg(parser, "total").toInt()
            }
            "review" -> reviews.add(readReview(parser))
        }
    }

    return ReviewList(start, end, total, reviews)
}
