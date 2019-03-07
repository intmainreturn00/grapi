package com.intmainreturn00.grapi

data class UserId(val id: String, val name: String, val link: String)

data class UserShelves(val start: Int, val end: Int, val total: Int, val shelves: List<Shelf>)

data class Shelf(val id: String, val name: String, val bookCount: Int)

data class ReviewList(
    val start: Int,
    val end: Int,
    val total: Int,
    val reviews: List<Review>
)

data class Review(
    val id: String,
    val book: Book,
    val rating: Int?,
    val readCount: Int?,
    val body: String,
    val owned: Boolean,
    val readAt: String,
    val startedAt: String,
    val dateAdded: String,
    val dateUpdated: String
)

data class Book(
    val id: String,
    val isbn: String,
    val isbn13: String,
    val textReviewsCount: Int?,
    val title: String,
    val titleWithoutSeries: String,
    val imageUrl: String,
    val imageUrlSmall: String,
    val imageUrlLarge: String,
    val link: String,
    val numPages: Int?,
    val format: String,
    val publisher: String,
    val editionInformation: String,
    val publicationDay: Int?,
    val publicationYear: Int?,
    val publicationMonth: Int?,
    val averageRating: Float?,
    val ratingsCount: Int?,
    val description: String,
    val authors: List<Author>,
    val workId: String
)

data class Author(
    val id: String,
    val name: String,
    val role: String,
    val imageUrl: String,
    val imageUrlSmall: String,
    val link: String,
    val averageRating: Float?,
    val ratingsCount: Int?,
    val textReviewsCount: Int?
)

data class SearchResults(
    val start: Int,
    val end: Int,
    val total: Int,
    val results: List<SearchResult>
)

data class SearchResult(
    val workId: String,
    val bookId: String,
    val bookTitle: String,
    val authorId: String,
    val authorName: String,
    val imageUrl: String,
    val imageUrlSmall: String,
    val averageRating: Float?,
    val ratingsCount: Int?,
    val textReviewsCount: Int?
)


enum class Sort {
    EMPTY,
    TITLE, AUTHOR, COVER, RATING, YEAR_PUB, DATE_PUB,
    DATE_PUB_EDITION, DATE_STARTED, DATE_READ, DATE_UPDATED,
    DATE_ADDED, RECOMMENDER, AVG_RATING, NUM_RATINGS,
    REVIEW, READ_COUNT, VOTES, RANDOM, COMMENTS, NOTES,
    ISBN, ISBN13, ASIN, NUM_PAGES, FORMAT, POSITION, SHELVES,
    OWNED, DATE_PURCHASED, PURCHASE_LOCATION, CONDITION
}

enum class Order {
    ASCENDING, DESCENDING
}


