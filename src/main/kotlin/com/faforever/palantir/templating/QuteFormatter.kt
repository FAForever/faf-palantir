package com.faforever.palantir.templating

import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import java.time.ZoneOffset
import java.util.Locale

@RequestScoped
class QuteFormatter {
    @Context
    private lateinit var httpHeaders: HttpHeaders

    /**
     * Checks whether request is from htmx and either just renders the content or embeds into the base layout
     */
    fun render(
        template: Template,
        data: Map<String, Any> = emptyMap(),
    ): TemplateInstance {
        val instance = template.withHtmxContext()

        return data
            .withDefaultData()
            .entries
            .fold(instance) { acc, (key, value) ->
                acc.data(key, value)
            }
    }

    private fun Template.withHtmxContext() =
        if (httpHeaders.requestHeaders.contains("HX-Request")) {
            this.getFragment("content").instance()
        } else {
            this.instance()
        }

    private fun Map<String, Any>.withDefaultData(): Map<String, Any> =
        this +
            mapOf(
                "englishLocale" to Locale.ENGLISH,
                "timezoneUTC" to ZoneOffset.UTC,
            )
}
