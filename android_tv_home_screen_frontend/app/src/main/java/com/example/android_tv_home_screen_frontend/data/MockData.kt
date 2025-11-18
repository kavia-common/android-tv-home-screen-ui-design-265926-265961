package com.example.android_tv_home_screen_frontend.data

/**
 * PUBLIC_INTERFACE
 * Provides static mock data for the Home screen rows and items.
 */
data class MovieItem(
    val id: String,
    val title: String,
    val year: Int,
    val rating: String,
    val durationMinutes: Int,
    val genres: List<String>,
    val synopsis: String,
    val colorHex: String // used as placeholder poster background color
)

/**
 * PUBLIC_INTERFACE
 * Model for a horizontal row of movies.
 */
data class MovieRow(
    val id: String,
    val title: String,
    val items: List<MovieItem>
)

/**
 * PUBLIC_INTERFACE
 * Returns a list of rows with mock items for the Home screen.
 */
fun buildMockRows(): List<MovieRow> {
    val lorem =
        "A gripping tale that explores ambition, loyalty, and the human spirit in the face of adversity."
    val genres = listOf("Drama", "Action", "Sci-Fi", "Thriller", "Adventure", "Comedy", "Family")

    fun item(index: Int): MovieItem {
        val colors = listOf("#CBD5E1", "#E5E7EB", "#D1D5DB", "#9CA3AF", "#94A3B8", "#F3F4F6")
        val g1 = genres[index % genres.size]
        val g2 = genres[(index + 2) % genres.size]
        return MovieItem(
            id = "m$index",
            title = "Movie $index",
            year = 2015 + (index % 10),
            rating = listOf("G", "PG", "PG-13", "R")[index % 4],
            durationMinutes = 85 + (index % 40),
            genres = listOf(g1, g2),
            synopsis = "$lorem ($index)",
            colorHex = colors[index % colors.size]
        )
    }

    val continueWatching = MovieRow(
        id = "row_continue",
        title = "Continue Watching",
        items = (0 until 8).map { item(it) }
    )
    val trending = MovieRow(
        id = "row_trending",
        title = "Trending Now",
        items = (8 until 18).map { item(it) }
    )
    val newReleases = MovieRow(
        id = "row_new",
        title = "New Releases",
        items = (18 until 28).map { item(it) }
    )
    val criticallyAcclaimed = MovieRow(
        id = "row_critics",
        title = "Critically Acclaimed",
        items = (28 until 36).map { item(it) }
    )

    return listOf(continueWatching, trending, newReleases, criticallyAcclaimed)
}
