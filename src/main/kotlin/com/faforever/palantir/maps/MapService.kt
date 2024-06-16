package com.faforever.palantir.maps

import com.faforever.commons.api.dto.MapVersion
import com.faforever.commons.api.elide.ElideNavigator
import com.faforever.palantir.api.FafApiClient
import com.faforever.palantir.api.elideNavigator
import com.faforever.palantir.api.model.Page
import io.smallrye.mutiny.Uni
import jakarta.inject.Singleton

@Singleton
class MapService(
    private val apiClient: FafApiClient,
) {
    fun <T> findMaps(
        filterKey: String,
        filterPattern: String,
        excludeHidden: Boolean,
        mapper: (MapVersion) -> T,
    ): Uni<Page<T>> {
        val routeBuilder =
            elideNavigator<MapVersion>()
                .collection()
                .addInclude("map")
                .addInclude("map.author")
                .setFilter(
                    if (excludeHidden) {
                        ElideNavigator
                            .qBuilder()
                            .string("map.$filterKey")
                            .eq(filterPattern)
                            .and()
                            .bool("hidden")
                            .isFalse()
                    } else {
                        ElideNavigator.qBuilder().string("map.$filterKey").eq(filterPattern)
                    },
                )

        return apiClient.getPage(routeBuilder, mapper = mapper)
    }

    fun <T> findRecentMaps(mapper: (MapVersion) -> T): Uni<Page<T>> {
        val routeBuilder =
            elideNavigator<MapVersion>()
                .collection()
                .addInclude("map")
                .addInclude("map.author")
                .addSortingRule("id", false)

        return apiClient.getPage(routeBuilder, mapper = mapper)
    }
}
