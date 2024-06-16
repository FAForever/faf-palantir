package com.faforever.palantir

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName

@ConfigMapping(prefix = "faf")
interface FafConfig {
    @WithName("api-base-url")
    fun apiBaseUrl(): String
}
