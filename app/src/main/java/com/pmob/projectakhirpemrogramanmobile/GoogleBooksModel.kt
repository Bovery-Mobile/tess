package com.pmob.projectakhirpemrogramanmobile

import com.google.gson.annotations.SerializedName

data class GoogleBooksModel(
    @SerializedName("items")
    val items: List<BookItem>? = null,
    @SerializedName("totalItems")
    val totalItems: Int = 0
)

data class BookItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title")
    val title: String?,
    @SerializedName("authors")
    val authors: List<String>? = null,
    @SerializedName("publisher")
    val publisher: String? = null,
    @SerializedName("publishedDate")
    val publishedDate: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("pageCount")
    val pageCount: Int? = null,
    @SerializedName("categories")
    val categories: List<String>? = null,
    @SerializedName("averageRating")
    val averageRating: Double? = null,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks? = null
)

data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("smallThumbnail")
    val smallThumbnail: String? = null
)

// Converter: BookItem -> Book (model kita)
fun BookItem.toBook(): Book {
    return Book(
        id = id.hashCode(), // Convert string id to int
        title = volumeInfo.title ?: "Unknown Title",
        author = volumeInfo.authors?.firstOrNull() ?: "Unknown Author",
        coverUrl = volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://") ?: "",
        rating = volumeInfo.averageRating ?: (3.5 + Math.random() * 1.5), // Random 3.5-5.0 jika tidak ada
        publishedYear = volumeInfo.publishedDate?.take(4)?.toIntOrNull() ?: 2024,
        pages = volumeInfo.pageCount ?: (150 + (Math.random() * 350).toInt()),
        genres = volumeInfo.categories?.map { it.uppercase() } ?: listOf("GENERAL"),
        synopsis = volumeInfo.description ?: "No description available for this book.",
        price = 5.99 + (Math.random() * 14), // Random price $5.99-$19.99
        category = volumeInfo.categories?.firstOrNull() ?: "General"
    )
}
