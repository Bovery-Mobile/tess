package com.pmob.projectakhirpemrogramanmobile.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pmob.projectakhirpemrogramanmobile.*
import com.pmob.projectakhirpemrogramanmobile.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: BookRepository

    private val allBooks = mutableListOf<Book>()
    private var filteredBooks = mutableListOf<Book>()
    private var isLoading = false

    // ===================== LIFECYCLE =====================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = BookRepository()

        setupSearch()
        setupChipFilters()
        loadInitialBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ===================== LOAD DATA =====================
    private fun loadInitialBooks() {
        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val categorizedBooks = repository.getPopularBooks()

                if (!isAdded || _binding == null) return@launch

                allBooks.clear()
                categorizedBooks.values.forEach { books ->
                    allBooks.addAll(books)
                }

                filteredBooks.clear()
                filteredBooks.addAll(allBooks)

                showLoading(false)
                displayCategories(categorizedBooks)

            } catch (e: Exception) {
                if (_binding == null) return@launch
                showLoading(false)
                showError("Gagal memuat data. Periksa koneksi internet.")
            }
        }
    }

    // ===================== SEARCH =====================
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                val query = s.toString().trim()
                when {
                    query.length >= 3 -> searchBooks(query)
                    query.isEmpty() -> loadInitialBooks()
                }
            }
        })
    }

    private fun searchBooks(query: String) {
        if (isLoading) return

        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val books = repository.searchBooks(query)

                if (_binding == null) return@launch

                allBooks.clear()
                allBooks.addAll(books)
                filteredBooks = allBooks.toMutableList()

                showLoading(false)
                updateDisplay()

            } catch (e: Exception) {
                if (_binding == null) return@launch
                showLoading(false)
                showError("Gagal mencari buku")
            }
        }
    }

    // ===================== CHIP FILTER =====================
    private fun setupChipFilters() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (_binding == null || checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val query = binding.etSearch.text.toString().trim().lowercase()

            filteredBooks = when (checkedIds[0]) {
                R.id.chipFiction -> filterByCategory("Fiction", "Fiksi")
                R.id.chipClassic -> filterByCategory("Classic", "Klasik")
                else -> allBooks.toMutableList()
            }

            if (query.isNotEmpty()) {
                filteredBooks = filteredBooks.filter {
                    it.title.lowercase().contains(query) ||
                            it.author.lowercase().contains(query)
                }.toMutableList()
            }

            updateDisplay()
        }
    }

    private fun filterByCategory(vararg keywords: String): MutableList<Book> {
        return allBooks.filter { book ->
            keywords.any { key ->
                book.category.contains(key, true) ||
                        book.genres.any { it.contains(key, true) }
            }
        }.toMutableList()
    }

    // ===================== DISPLAY =====================
    private fun updateDisplay() {
        if (_binding == null) return

        binding.contentLayout.removeAllViews()

        if (filteredBooks.isEmpty()) {
            showEmpty()
            return
        }

        displayCategories(filteredBooks.groupBy { it.category })
    }

    private fun displayCategories(categorizedBooks: Map<String, List<Book>>) {
        if (_binding == null) return

        binding.contentLayout.removeAllViews()

        categorizedBooks.forEach { (category, books) ->
            addCategorySection(category, books)
        }
    }

    private fun addCategorySection(category: String, books: List<Book>) {
        if (_binding == null) return

        val categoryView = layoutInflater
            .inflate(R.layout.item_category_section, binding.contentLayout, false)

        val tvCategoryTitle =
            categoryView.findViewById<TextView>(R.id.tvCategoryTitle)
        val rvBooks =
            categoryView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvBooks)

        tvCategoryTitle.text = category

        rvBooks.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvBooks.adapter = BookAdapter(books) { book ->
            if (context == null) return@BookAdapter

            Intent(context, DetailBookActivity::class.java).apply {
                putExtra("BOOK_ID", book.id)
                putExtra("BOOK_TITLE", book.title)
                putExtra("BOOK_AUTHOR", book.author)
                putExtra("BOOK_COVER", book.coverUrl)
                putExtra("BOOK_RATING", book.rating)
                putExtra("BOOK_YEAR", book.publishedYear)
                putExtra("BOOK_PAGES", book.pages)
                putExtra("BOOK_SYNOPSIS", book.synopsis)
                putExtra("BOOK_PRICE", book.price)
                putStringArrayListExtra("BOOK_GENRES", ArrayList(book.genres))
                startActivity(this)
            }
        }

        binding.contentLayout.addView(categoryView)
    }

    // ===================== UI STATE =====================
    private fun showLoading(show: Boolean) {
        if (_binding == null) return
        isLoading = show
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmpty() {
        if (_binding == null || context == null) return

        val emptyView = TextView(context).apply {
            text = "Tidak ada buku yang ditemukan"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            setPadding(32, 64, 32, 32)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        binding.contentLayout.addView(emptyView)
    }

    private fun showError(message: String) {
        if (_binding == null || context == null) return

        val errorView = TextView(context).apply {
            text = message
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            setPadding(32, 64, 32, 32)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        binding.contentLayout.addView(errorView)
    }
}
