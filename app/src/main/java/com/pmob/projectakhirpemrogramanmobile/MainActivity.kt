package com.pmob.projectakhirpemrogramanmobile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import kotlin.text.category

class MainActivity : AppCompatActivity() {

    private lateinit var contentLayout: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var repository: BookRepository

    private var allBooks = mutableListOf<Book>()
    private var filteredBooks = mutableListOf<Book>()
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentLayout = findViewById(R.id.contentLayout)
        etSearch = findViewById(R.id.etSearch)
        chipGroup = findViewById(R.id.chipGroup)
        progressBar = findViewById(R.id.progressBar)

        repository = BookRepository()

        setupSearch()
        setupChipFilters()
        loadInitialBooks()
    }

    private fun loadInitialBooks() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                // Load books dari berbagai kategori
                val categorizedBooks = repository.getPopularBooks()

                allBooks.clear()
                categorizedBooks.values.forEach { books ->
                    allBooks.addAll(books)
                }

                filteredBooks.clear()
                filteredBooks.addAll(allBooks)

                runOnUiThread {
                    showLoading(false)
                    displayCategories(categorizedBooks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    showLoading(false)
                    showError("Gagal memuat data. Periksa koneksi internet.")
                }
            }
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 3) {
                    searchBooks(query)
                } else if (query.isEmpty()) {
                    loadInitialBooks()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchBooks(query: String) {
        if (isLoading) return

        showLoading(true)

        lifecycleScope.launch {
            try {
                val books = repository.searchBooks(query)
                allBooks.clear()
                allBooks.addAll(books)

                filterBooks(query)

                runOnUiThread {
                    showLoading(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    showLoading(false)
                    showError("Gagal mencari buku")
                }
            }
        }
    }

    private fun setupChipFilters() {
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val selectedChipId = checkedIds[0]
            val query = etSearch.text.toString().trim().lowercase()

            when (selectedChipId) {
                R.id.chipAll -> {
                    filteredBooks.clear()
                    filteredBooks.addAll(allBooks)
                }
                R.id.chipFiction -> {
                    filteredBooks = allBooks.filter { book ->
                        book.genres.any { it.contains("FICTION", ignoreCase = true) } ||
                                book.category.contains("Fiction", ignoreCase = true) ||
                                book.category.contains("Fiksi", ignoreCase = true)
                    }.toMutableList()
                }
                R.id.chipClassic -> {
                    filteredBooks = allBooks.filter { book ->
                        book.genres.any { it.contains("CLASSIC", ignoreCase = true) } ||
                                book.category.contains("Classic", ignoreCase = true) ||
                                book.category.contains("Klasik", ignoreCase = true)
                    }.toMutableList()
                }
            }

            if (query.isNotEmpty()) {
                filteredBooks = filteredBooks.filter { book ->
                    book.title.lowercase().contains(query) ||
                            book.author.lowercase().contains(query)
                }.toMutableList()
            }

            updateDisplay()
        }
    }

    private fun filterBooks(query: String) {
        if (query.isEmpty()) {
            filteredBooks.clear()
            filteredBooks.addAll(allBooks)
        } else {
            val baseFilter = when (chipGroup.checkedChipId) {
                R.id.chipFiction -> {
                    allBooks.filter { book ->
                        book.genres.any { it.contains("FICTION", ignoreCase = true) } ||
                                book.category.contains("Fiction", ignoreCase = true)
                    }
                }
                R.id.chipClassic -> {
                    allBooks.filter { book ->
                        book.genres.any { it.contains("CLASSIC", ignoreCase = true) } ||
                                book.category.contains("Classic", ignoreCase = true)
                    }
                }
                else -> allBooks
            }

            filteredBooks = baseFilter.filter { book ->
                book.title.lowercase().contains(query.lowercase()) ||
                        book.author.lowercase().contains(query.lowercase())
            }.toMutableList()
        }

        updateDisplay()
    }

    private fun updateDisplay() {
        contentLayout.removeAllViews()

        if (filteredBooks.isEmpty()) {
            showEmpty()
            return
        }

        val categorizedBooks = filteredBooks.groupBy { it.category }
        displayCategories(categorizedBooks)
    }

    private fun displayCategories(categorizedBooks: Map<String, List<Book>>) {
        contentLayout.removeAllViews()

        categorizedBooks.forEach { (category, books) ->
            addCategorySection(category, books)
        }
    }

    private fun addCategorySection(category: String, books: List<Book>) {
        val categoryView = LayoutInflater.from(this)
            .inflate(R.layout.item_category_section, contentLayout, false)

        val tvCategoryTitle = categoryView.findViewById<TextView>(R.id.tvCategoryTitle)
        val rvBooks = categoryView.findViewById<RecyclerView>(R.id.rvBooks)

        tvCategoryTitle.text = category

        rvBooks.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val adapter = BookAdapter(books) { book ->
            val intent = Intent(this, DetailBookActivity::class.java)
            intent.putExtra("BOOK_ID", book.id)
            intent.putExtra("BOOK_TITLE", book.title)
            intent.putExtra("BOOK_AUTHOR", book.author)
            intent.putExtra("BOOK_COVER", book.coverUrl)
            intent.putExtra("BOOK_RATING", book.rating)
            intent.putExtra("BOOK_YEAR", book.publishedYear)
            intent.putExtra("BOOK_PAGES", book.pages)
            intent.putExtra("BOOK_SYNOPSIS", book.synopsis)
            intent.putExtra("BOOK_PRICE", book.price)
            intent.putStringArrayListExtra("BOOK_GENRES", ArrayList(book.genres))
            startActivity(intent)
        }

        rvBooks.adapter = adapter
        contentLayout.addView(categoryView)
    }

    private fun showLoading(show: Boolean) {
        isLoading = show
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmpty() {
        val emptyView = TextView(this).apply {
            text = "Tidak ada buku yang ditemukan"
            textSize = 16f
            setTextColor(getColor(android.R.color.darker_gray))
            setPadding(32, 64, 32, 32)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        contentLayout.addView(emptyView)
    }

    private fun showError(message: String) {
        val errorView = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(getColor(android.R.color.holo_red_dark))
            setPadding(32, 64, 32, 32)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        contentLayout.addView(errorView)
    }
}