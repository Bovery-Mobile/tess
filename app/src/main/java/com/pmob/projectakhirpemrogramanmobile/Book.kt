package com.pmob.projectakhirpemrogramanmobile

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val coverUrl: String,
    val rating: Double,
    val publishedYear: Int,
    val pages: Int,
    val genres: List<String>,
    val synopsis: String,
    val price: Double,
    val category: String
)

// Dummy Data
object BookData {
    fun getBooks(): List<Book> {
        return listOf(
            Book(
                id = 1,
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverUrl = "https://example.com/gatsby.jpg",
                rating = 4.8,
                publishedYear = 1925,
                pages = 218,
                genres = listOf("CLASSIC", "DRAMA", "ROMANCE"),
                synopsis = "The Great Gatsby, F. Scott Fitzgerald's third book, stands as the supreme achievement of his career. This exemplary novel of the Jazz Age has been acclaimed by generations of readers. The story of the fabulously wealthy Jay Gatsby and his new love for the beautiful Daisy Buchanan, of the lavish parties on Long Island...",
                price = 9.99,
                category = "Klasik & Romantis"
            ),
            Book(
                id = 2,
                title = "Dunia",
                author = "Tere Liye",
                coverUrl = "https://example.com/dunia.jpg",
                rating = 4.5,
                publishedYear = 2020,
                pages = 320,
                genres = listOf("FIKSI", "PETUALANGAN"),
                synopsis = "Dunia adalah buku pertama dari serial Bumi karya Tere Liye yang mengisahkan petualangan seru...",
                price = 8.99,
                category = "Rekomendasi"
            ),
            Book(
                id = 3,
                title = "Psychology of Money",
                author = "Morgan Housel",
                coverUrl = "https://example.com/psychology.jpg",
                rating = 4.7,
                publishedYear = 2020,
                pages = 256,
                genres = listOf("NON-FIKSI", "BISNIS"),
                synopsis = "Doing well with money isn't necessarily about what you know. It's about how you behave...",
                price = 12.99,
                category = "Rekomendasi"
            ),
            Book(
                id = 4,
                title = "Atomic Habits",
                author = "James Clear",
                coverUrl = "https://example.com/atomic.jpg",
                rating = 4.9,
                publishedYear = 2018,
                pages = 320,
                genres = listOf("SELF-HELP", "PRODUCTIVITY"),
                synopsis = "No matter your goals, Atomic Habits offers a proven framework for improving every day...",
                price = 11.99,
                category = "Rekomendasi"
            ),
            Book(
                id = 5,
                title = "Sapiens",
                author = "Yuval Noah Harari",
                coverUrl = "https://example.com/sapiens.jpg",
                rating = 4.6,
                publishedYear = 2011,
                pages = 443,
                genres = listOf("HISTORY", "SCIENCE"),
                synopsis = "From a renowned historian comes a groundbreaking narrative of humanity's creation...",
                price = 13.99,
                category = "Fiksi Populer"
            ),
            Book(
                id = 6,
                title = "The Hobbit",
                author = "J.R.R. Tolkien",
                coverUrl = "https://example.com/hobbit.jpg",
                rating = 4.8,
                publishedYear = 1937,
                pages = 310,
                genres = listOf("FANTASY", "ADVENTURE"),
                synopsis = "The Hobbit is a tale of high adventure, undertaken by a company of dwarves in search of dragon-guarded gold...",
                price = 10.99,
                category = "History, Fantasi & Horror"
            ),
            Book(
                id = 7,
                title = "It",
                author = "Stephen King",
                coverUrl = "https://example.com/it.jpg",
                rating = 4.5,
                publishedYear = 1986,
                pages = 1138,
                genres = listOf("HORROR", "THRILLER"),
                synopsis = "Welcome to Derry, Maine. It's a small city, a place as hauntingly familiar as your own hometown...",
                price = 14.99,
                category = "History, Fantasi & Horror"
            ),
            Book(
                id = 8,
                title = "The Catcher in the Rye",
                author = "J.D. Salinger",
                coverUrl = "https://example.com/catcher.jpg",
                rating = 4.3,
                publishedYear = 1951,
                pages = 277,
                genres = listOf("CLASSIC", "COMING-OF-AGE"),
                synopsis = "The hero-narrator of The Catcher in the Rye is an ancient child of sixteen, a native New Yorker named Holden Caulfield...",
                price = 9.99,
                category = "Fiksi Populer"
            ),
            Book(
                id = 9,
                title = "Pride and Prejudice",
                author = "Jane Austen",
                coverUrl = "https://example.com/pride.jpg",
                rating = 4.7,
                publishedYear = 1813,
                pages = 432,
                genres = listOf("CLASSIC", "ROMANCE"),
                synopsis = "Since its immediate success in 1813, Pride and Prejudice has remained one of the most popular novels...",
                price = 8.99,
                category = "Klasik & Romantis"
            ),
            Book(
                id = 10,
                title = "Moby Dick",
                author = "Herman Melville",
                coverUrl = "https://example.com/moby.jpg",
                rating = 4.2,
                publishedYear = 1851,
                pages = 635,
                genres = listOf("CLASSIC", "ADVENTURE"),
                synopsis = "Call me Ishmael. So begins one of the most famous journeys in literature...",
                price = 11.99,
                category = "Klasik & Romantis"
            )
        )
    }

    fun getCategorizedBooks(): Map<String, List<Book>> {
        val books = getBooks()
        return mapOf(
            "Rekomendasi Untukmu" to books.filter { it.category == "Rekomendasi" },
            "Fiksi Populer" to books.filter { it.category == "Fiksi Populer" },
            "Klasik & Romantis" to books.filter { it.category == "Klasik & Romantis" },
            "History, Fantasi & Horror" to books.filter { it.category == "History, Fantasi & Horror" }
        )
    }
}