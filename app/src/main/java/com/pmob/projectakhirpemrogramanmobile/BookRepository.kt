package com.pmob.projectakhirpemrogramanmobile

class BookRepository {
    private val apiService = GoogleBooksApiService.create()

    suspend fun searchBooks(query: String): List<Book> {
        return try {
            val response = apiService.searchBooks(query)
            response.items?.map { it.toBook() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getBooksByCategory(category: String): List<Book> {
        return try {
            val response = apiService.searchBooks("subject:$category")
            response.items?.map { it.toBook() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPopularBooks(): Map<String, List<Book>> {
        return try {
            val categories = listOf(
                "Rekomendasi" to "bestseller",
                "Fiksi Populer" to "fiction",
                "Klasik & Romantis" to "classic+romance",
                "Teknologi & Programming" to "programming"
            )

            val result = mutableMapOf<String, List<Book>>()

            categories.forEach { (categoryName, query) ->
                val books = searchBooks(query)
                if (books.isNotEmpty()) {
                    result[categoryName] = books.take(10) // Ambil 10 buku per kategori
                }
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }
}