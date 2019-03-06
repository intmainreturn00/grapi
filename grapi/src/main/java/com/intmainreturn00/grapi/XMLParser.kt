package com.intmainreturn00.grapi

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader


fun parseUserId(xml: String): UserId {
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
            "user" -> id = parseArg(parser, "id")
            "name" -> name = parseText(parser)
            "link" -> link = parseText(parser)
        }
    }

    return UserId(id, name, link)
}


fun parseUserShelves(xml: String): UserShelves {
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
                start = parseArg(parser, "start").toInt()
                end = parseArg(parser, "end").toInt()
                total = parseArg(parser, "total").toInt()
            }
            "user_shelf" -> shelves.add(readShelf(parser))
        }
    }

    return UserShelves(start, end, total, shelves)
}


private fun readShelf(parser: XmlPullParser): Shelf {
    var id = ""
    var name = ""
    var bookCount = 0

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = parseText(parser)
            "name" -> name = parseText(parser)
            "book_count" -> bookCount = parseText(parser).toInt()
        }
    }

    return Shelf(id, name, bookCount)
}


fun parseReviewList(xml: String): ReviewList {
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
                start = parseArg(parser, "start").toInt()
                end = parseArg(parser, "end").toInt()
                total = parseArg(parser, "total").toInt()
            }
            "review" -> reviews.add(readReview(parser))
        }
    }

    return ReviewList(start, end, total, reviews)
}


private fun readReview(parser: XmlPullParser): Review {
    var id = ""
    var book: Book? = null
    var rating: Int? = null
    var readCount: Int? = null
    var body = ""
    var owned = false
    var readAt = ""
    var startedAt = ""
    var dateAdded = ""
    var dateUpdated = ""

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = parseText(parser)
            "rating" -> rating = parseInt(parser)
            "read_count" -> readCount = parseInt(parser)
            "body" -> body = parseText(parser)
            "owned" -> owned = parseText(parser) == "1"
            "book" -> book = readBook(parser)
            "read_at" -> readAt = parseText(parser)
            "started_at" -> startedAt = parseText(parser)
            "date_added" -> dateAdded = parseText(parser)
            "date_updated" -> dateUpdated = parseText(parser)
            else -> skip(parser)
        }
    }

    return Review(id, book!!, rating, readCount, body, owned, readAt, startedAt, dateAdded, dateUpdated)
}


private fun readBook(parser: XmlPullParser): Book {
    var id = ""
    var isbn = ""
    var isbn13 = ""
    var title = ""
    var textReviewsCount: Int? = null
    var titleWithoutSeries = ""
    var imageUrl = ""
    var imageUrlSmall = ""
    var imageUrlLarge = ""
    var link = ""
    var numPages: Int? = null
    var format = ""
    var publisher = ""
    var editionInformation = ""
    var publicationDay: Int? = null
    var publicationYear: Int? = null
    var publicationMonth: Int? = null
    var averageRating: Float? = null
    var ratingsCount: Int? = null
    var description = ""
    var authors = mutableListOf<Author>()
    var workId = ""


    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = parseText(parser)
            "isbn" -> isbn = parseText(parser)
            "isbn13" -> isbn13 = parseText(parser)
            "title" -> title = parseText(parser)
            "text_reviews_count" -> textReviewsCount = parseInt(parser)
            "title_without_series" -> titleWithoutSeries = parseText(parser)
            "image_url" -> imageUrl = parseText(parser)
            "small_image_url" -> imageUrlSmall = parseText(parser)
            "large_image_url" -> imageUrlLarge = parseText(parser)
            "link" -> link = parseText(parser)
            "num_pages" -> numPages = parseInt(parser)
            "format" -> format = parseText(parser)
            "publisher" -> publisher = parseText(parser)
            "edition_information" -> editionInformation = parseText(parser)
            "publication_day" -> publicationDay = parseInt(parser)
            "publication_month" -> publicationMonth = parseInt(parser)
            "publication_year" -> publicationYear = parseInt(parser)
            "average_rating" -> averageRating = parseFloat(parser)
            "ratings_count" -> ratingsCount = parseInt(parser)
            "description" -> description = parseText(parser)
            "authors" -> authors = readAuthors(parser)
            "work" -> workId = readWorkId(parser)
            else -> skip(parser)
        }
    }


    return Book(
        id,
        isbn,
        isbn13,
        textReviewsCount,
        title,
        titleWithoutSeries,
        imageUrl,
        imageUrlSmall,
        imageUrlLarge,
        link,
        numPages,
        format,
        publisher,
        editionInformation,
        publicationDay,
        publicationYear,
        publicationMonth,
        averageRating,
        ratingsCount,
        description,
        authors,
        workId
    )
}


private fun readAuthors(parser: XmlPullParser): MutableList<Author> {
    val authors = mutableListOf<Author>()
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "author" -> authors.add(readAuthor(parser))
            else -> skip(parser)
        }
    }
    return authors
}


private fun readAuthor(parser: XmlPullParser): Author {
    var id = ""
    var name = ""
    var role = ""
    var imageUrl = ""
    var imageUrlSmall = ""
    var link = ""
    var averageRating: Float? = null
    var ratingsCount: Int? = null
    var textReviewsCount: Int? = null

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = parseText(parser)
            "name" -> name = parseText(parser)
            "role" -> role = parseText(parser)
            "image_url" -> imageUrl = parseText(parser)
            "small_image_url" -> imageUrlSmall = parseText(parser)
            "link" -> link = parseText(parser)
            "average_rating" -> averageRating = parseFloat(parser)
            "ratings_count" -> ratingsCount = parseInt(parser)
            "text_reviews_count" -> textReviewsCount = parseInt(parser)
            else -> skip(parser)
        }
    }

    return Author(id, name, role, imageUrl, imageUrlSmall, link, averageRating, ratingsCount, textReviewsCount)
}


private fun readWorkId(parser: XmlPullParser): String {
    var id = ""
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = parseText(parser)
            else -> skip(parser)
        }
    }
    return id
}



private fun parseText(parser: XmlPullParser): String {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}


private fun parseInt(parser: XmlPullParser): Int? {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    if (result.isEmpty()) {
        return null
    } else {
        return result.toInt()
    }
}


private fun parseFloat(parser: XmlPullParser): Float? {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    if (result.isEmpty()) {
        return null
    } else {
        return result.toFloat()
    }
}


private fun parseArg(parser: XmlPullParser, name: String): String =
    parser.getAttributeValue(null, name)


private fun skip(parser: XmlPullParser) {
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}