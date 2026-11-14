package com.achrefdiabi.donghuaarabic

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class DonghuaArabicPlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(DonghuaArabicProvider())
    }
}
