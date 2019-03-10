package com.intmainreturn00.grapi

import org.xmlpull.v1.XmlPullParser

internal fun readShelves(parser: XmlPullParser): MutableList<Shelf> {
    val shelves = mutableListOf<Shelf>()

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "user_shelf" -> shelves.add(readShelf(parser))
            else -> skip(parser)
        }
    }

    return shelves
}


internal fun readShelf(parser: XmlPullParser): Shelf {
    var id = ""
    var name = ""
    var bookCount = 0

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = readText(parser)
            "name" -> name = readText(parser)
            "book_count" -> bookCount = readText(parser).toInt()
            else -> skip(parser)
        }
    }

    return Shelf(id, name, bookCount)
}


internal fun readSearchResults(parser: XmlPullParser): SearchResults {
    var start = 0
    var end = 0
    var total = 0
    var results = listOf<SearchResult>()

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "results-start" -> start = readText(parser).toInt()
            "results-end" -> end = readText(parser).toInt()
            "total-results" -> total = readText(parser).toInt()
            "results" -> results = readSearchResultsInner(parser)
            else -> skip(parser)
        }
    }

    return SearchResults(start, end, total, results)
}


internal fun readSearchResultsInner(parser: XmlPullParser): List<SearchResult> {
    val results = mutableListOf<SearchResult>()

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "work" -> results.add(readSearchResult(parser))
            else -> skip(parser)
        }
    }
    return results
}


internal data class BestBookAuthor(val id: String, val name: String)
internal data class BestBook(
    val id: String,
    val title: String,
    val imageUrl: String,
    val imageUrlSmall: String,
    val author: BestBookAuthor
)

internal fun readSearchResult(parser: XmlPullParser): SearchResult {
    var workId = ""
    var bookId = ""
    var bookTitle = ""
    var authorId = ""
    var authorName = ""
    var imageUrl = ""
    var imageUrlSmall = ""
    var averageRating: Float? = null
    var ratingsCount: Int? = null
    var textReviewsCount: Int? = null

    var bestBook: BestBook? = null

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> workId = readText(parser)
            "ratings_count" -> ratingsCount = readInt(parser)
            "average_rating" -> averageRating = readFloat(parser)
            "text_reviews_count" -> textReviewsCount = readInt(parser)
            "best_book" -> bestBook = readBestBook(parser)
            else -> skip(parser)
        }
    }

    if (bestBook != null) {
        bookId = bestBook.id
        imageUrl = bestBook.imageUrl
        imageUrlSmall = bestBook.imageUrlSmall
        bookTitle = bestBook.title
        authorId = bestBook.author.id
        authorName = bestBook.author.name
    }

    return SearchResult(
        workId,
        bookId,
        bookTitle,
        authorId,
        authorName,
        imageUrl,
        imageUrlSmall,
        averageRating,
        ratingsCount,
        textReviewsCount
    )
}


internal fun readBestBook(parser: XmlPullParser): BestBook {
    var id = ""
    var title = ""
    var imageUrl = ""
    var imageUrlSmall = ""
    var author: BestBookAuthor? = null

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = readText(parser)
            "title" -> title = readText(parser)
            "image_url" -> imageUrl = readText(parser)
            "small_image_url" -> imageUrlSmall = readText(parser)
            "author" -> author = readBestBookAuthor(parser)
            else -> skip(parser)
        }
    }

    return BestBook(id, title, imageUrl, imageUrlSmall, author!!)
}


internal fun readBestBookAuthor(parser: XmlPullParser): BestBookAuthor {
    var id = ""
    var name = ""

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = readText(parser)
            "name" -> name = readText(parser)
            else -> skip(parser)
        }
    }

    return BestBookAuthor(id, name)
}


internal fun readReview(parser: XmlPullParser): Review {
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
            "id" -> id = readText(parser)
            "rating" -> rating = readInt(parser)
            "read_count" -> readCount = readInt(parser)
            "body" -> body = readText(parser)
            "owned" -> owned = readText(parser) == "1"
            "book" -> book = readBook(parser)
            "read_at" -> readAt = readText(parser)
            "started_at" -> startedAt = readText(parser)
            "date_added" -> dateAdded = readText(parser)
            "date_updated" -> dateUpdated = readText(parser)
            else -> skip(parser)
        }
    }

    return Review(id, book!!, rating, readCount, body, owned, readAt, startedAt, dateAdded, dateUpdated)
}


internal fun readBook(parser: XmlPullParser): Book {
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
            "id" -> id = readText(parser)
            "isbn" -> isbn = readText(parser)
            "isbn13" -> isbn13 = readText(parser)
            "title" -> title = readText(parser)
            "text_reviews_count" -> textReviewsCount = readInt(parser)
            "title_without_series" -> titleWithoutSeries = readText(parser)
            "image_url" -> imageUrl = readText(parser)
            "small_image_url" -> imageUrlSmall = readText(parser)
            "large_image_url" -> imageUrlLarge = readText(parser)
            "link" -> link = readText(parser)
            "num_pages" -> numPages = readInt(parser)
            "format" -> format = readText(parser)
            "publisher" -> publisher = readText(parser)
            "edition_information" -> editionInformation = readText(parser)
            "publication_day" -> publicationDay = readInt(parser)
            "publication_month" -> publicationMonth = readInt(parser)
            "publication_year" -> publicationYear = readInt(parser)
            "average_rating" -> averageRating = readFloat(parser)
            "ratings_count" -> ratingsCount = readInt(parser)
            "description" -> description = readText(parser)
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


internal fun readAuthors(parser: XmlPullParser): MutableList<Author> {
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


internal fun readAuthor(parser: XmlPullParser): Author {
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
            "id" -> id = readText(parser)
            "name" -> name = readText(parser)
            "role" -> role = readText(parser)
            "image_url" -> imageUrl = readText(parser)
            "small_image_url" -> imageUrlSmall = readText(parser)
            "link" -> link = readText(parser)
            "average_rating" -> averageRating = readFloat(parser)
            "ratings_count" -> ratingsCount = readInt(parser)
            "text_reviews_count" -> textReviewsCount = readInt(parser)
            else -> skip(parser)
        }
    }

    return Author(id, name, role, imageUrl, imageUrlSmall, link, averageRating, ratingsCount, textReviewsCount)
}


internal fun readUser(parser: XmlPullParser): User {
    var id = ""
    var name = ""
    var username = ""
    var link = ""
    var imageUrl = ""
    var imageUrlSmall = ""
    var about = ""
    var age: Int? = null
    var gender = ""
    var location = ""
    var website = ""
    var joined = ""
    var lastActive = ""
    var interests = ""
    var favoriteBooks = ""
    var rssUpdates = ""
    var rssReviews = ""
    var friendsCount: Int? = null
    var groupsCount: Int? = null
    var reviewsCount: Int? = null
    var shelves = mutableListOf<Shelf>()

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = readText(parser)
            "name" -> name = readText(parser)
            "username" -> username = readText(parser)
            "link" -> link = readText(parser)
            "image_url" -> imageUrl = readText(parser)
            "small_image_url" -> imageUrlSmall = readText(parser)
            "about" -> about = readText(parser)
            "age" -> age = readInt(parser)
            "gender" -> gender = readText(parser)
            "location" -> location = readText(parser)
            "website" -> website = readText(parser)
            "joined" -> joined = readText(parser)
            "last_active" -> lastActive = readText(parser)
            "interests" -> interests = readText(parser)
            "favorite_books" -> favoriteBooks = readText(parser)
            "updates_rss_url" -> rssUpdates = readText(parser)
            "reviews_rss_url" -> rssReviews = readText(parser)
            "friends_count" -> friendsCount = readInt(parser)
            "groups_count" -> groupsCount = readInt(parser)
            "reviews_count" -> reviewsCount = readInt(parser)
            "user_shelves" -> shelves = readShelves(parser)
            else -> skip(parser)
        }
    }

    return User(
        id,
        name,
        username,
        link,
        imageUrl,
        imageUrlSmall,
        about,
        age,
        gender,
        location,
        website,
        joined,
        lastActive,
        interests,
        favoriteBooks,
        rssUpdates,
        rssReviews,
        friendsCount,
        groupsCount,
        reviewsCount,
        shelves
    )

}


internal fun readWorkId(parser: XmlPullParser): String {
    var id = ""
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "id" -> id = readText(parser)
            else -> skip(parser)
        }
    }
    return id
}


internal fun readText(parser: XmlPullParser): String {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}


internal fun readInt(parser: XmlPullParser): Int? {
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


internal fun readFloat(parser: XmlPullParser): Float? {
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


internal fun readArg(parser: XmlPullParser, name: String): String =
    parser.getAttributeValue(null, name)


internal fun skip(parser: XmlPullParser) {
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}