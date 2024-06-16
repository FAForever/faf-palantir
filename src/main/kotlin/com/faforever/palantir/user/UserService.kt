package com.faforever.palantir.user

import com.faforever.commons.api.dto.Player
import com.faforever.commons.api.dto.Teamkill
import com.faforever.commons.api.elide.ElideNavigator
import com.faforever.commons.api.elide.ElideNavigatorOnCollection
import com.faforever.palantir.api.FafApiClient
import com.faforever.palantir.api.elideNavigator
import com.faforever.palantir.api.model.Page
import io.smallrye.mutiny.Uni
import jakarta.inject.Singleton

@Singleton
class UserService(
    private val apiClient: FafApiClient,
) {
    fun <T> findPlayers(
        filterKey: String,
        filterPattern: String,
        mapper: (Player) -> T,
    ): Uni<Page<T>> =
        apiClient.getPage(
            navigator =
                elideNavigator<Player>()
                    .collection()
                    .setFilter(ElideNavigator.qBuilder().string(filterKey).eq(filterPattern))
                    .addModeratorIncludes(),
            mapper = mapper,
        )

    fun <T> findLastRegistrations(mapper: (Player) -> T): Uni<Page<T>> =
        apiClient.getPage(
            navigator =
                elideNavigator<Player>()
                    .collection()
                    .addSortingRule("id", false)
                    .addModeratorIncludes(),
            pageSize = 50,
            mapper = mapper,
        )

    fun <T> findLatestTeamkills(mapper: (Teamkill) -> T): Uni<Page<T>> =
        apiClient.getPage(
            navigator =
                elideNavigator<Teamkill>()
                    .collection()
                    .addInclude("teamkiller")
                    .addInclude("teamkiller.bans")
                    .addInclude("victim")
                    .addSortingRule("id", false),
            pageSize = 50,
            mapper = mapper,
        )

    private fun ElideNavigatorOnCollection<Player>.addModeratorIncludes() =
        this
            .addInclude("names")
            .addInclude("avatarAssignments")
            .addInclude("avatarAssignments.avatar")
            .addInclude("uniqueIds")
            .addInclude("accountLinks")
            .addInclude("bans")
            .addInclude("bans.author")
            .addInclude("bans.revokeAuthor")
}
