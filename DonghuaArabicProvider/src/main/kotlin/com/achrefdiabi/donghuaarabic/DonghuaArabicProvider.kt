package com.achrefdiabi.donghuaarabic

import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SearchResponseList
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newMovieLoadResponse
import com.lagradost.cloudstream3.newMovieSearchResponse
import com.lagradost.cloudstream3.toNewSearchResponseList
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.StringUtils.encodeUri
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Initial catalogue adapter.
 *
 * The website is a JavaScript-heavy custom site. Selectors are deliberately
 * defensive and will need adjustment against live page markup before release.
 * No protected player or DRM flow is bypassed.
 */
class DonghuaArabicProvider : MainAPI() {
    override var mainUrl = "https://www.donghuarabic.com"
    override var name = "DonghuaArabic"
    override var lang = "ar"
    override val supportedTypes = setOf(TvType.Anime)
    override val hasMainPage = true

    private fun absolute(value: String?): String? {
        if (value.isNullOrBlank()) return null
        return when {
            value.startsWith("http://") || value.startsWith("https://") -> value
            value.startsWith("/") -> "$mainUrl$value"
            else -> "$mainUrl/$value"
        }
    }

    private fun Document.toCards(): List<SearchResponse> {
        val seen = mutableSetOf<String>()
        val cards = mutableListOf<SearchResponse>()

        // Try common card containers first; then fall back to linked images.
        val candidates = select(
            "article a[href], .anime-card a[href], .item a[href], .post a[href], a[href]:has(img)"
        )

        for (anchor in candidates) {
            val href = absolute(anchor.attr("href")) ?: continue
            if (!href.startsWith(mainUrl) || href == mainUrl || !seen.add(href)) continue
            if (href.contains("/category/") || href.contains("/tag/") ||
                href.contains("/page/") || href.contains("#")) continue

            val image = anchor.selectFirst("img")
            val title = sequenceOf(
                anchor.attr("title"),
                image?.attr("alt"),
                anchor.text()
            ).firstOrNull { it.isNotBlank() }?.trim()?.replace(Regex("\\s+"), " ")
                ?: continue

            if (title.length < 2) continue
            val poster = absolute(
                image?.let {
                    it.attr("data-src").ifBlank {
                        it.attr("data-lazy-src").ifBlank { it.attr("src") }
                    }
                }
            )

            cards.add(
                newMovieSearchResponse(title, href, TvType.Anime) {
                    this.posterUrl = poster
                }
            )
        }
        return cards
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) mainUrl else "$mainUrl/page/$page/"
        val document = app.get(url).document
        val items = document.toCards()
        return newHomePageResponse(
            listOf(HomePageList("أحدث الدونغهوا", items, true))
        )
    }

    override suspend fun search(query: String, page: Int): SearchResponseList? {
        val url = "$mainUrl/?s=${query.encodeUri()}"
        val document = app.get(url).document
        return document.toCards().toNewSearchResponseList()
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text()?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: document.selectFirst("meta[property=og:title]")?.attr("content")
                ?.takeIf { it.isNotBlank() }
            ?: return null

        val poster = document.selectFirst("meta[property=og:image]")?.attr("content")
            ?.takeIf { it.isNotBlank() }
        val description = document.selectFirst(
            "meta[name=description], meta[property=og:description]"
        )?.attr("content")?.takeIf { it.isNotBlank() }
            ?: document.selectFirst(".description, .entry-content, .post-content")?.text()

        return newMovieLoadResponse(title, url, TvType.Anime, url) {
            this.posterUrl = poster
            this.plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // Playback extraction is intentionally disabled until the site's
        // permitted player/embed interface is documented and tested.
        return false
    }
}
