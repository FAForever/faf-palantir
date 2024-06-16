package com.faforever.palantir.recentactivity

import com.faforever.commons.api.dto.MapVersion
import com.faforever.commons.api.dto.Teamkill
import com.faforever.palantir.maps.MapService
import com.faforever.palantir.model.MapVersionModel
import com.faforever.palantir.model.TeamkillModel
import com.faforever.palantir.templating.QuteFormatter
import com.faforever.palantir.user.UserService
import com.faforever.palantir.user.toModel
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("")
@RolesAllowed("USER")
class RecentActivityResource(
    private val quteFormatter: QuteFormatter,
    private val recentActivity: Template,
    private val userService: UserService,
    private val mapService: MapService,
) {
    @GET
    @Path("")
    @Produces(MediaType.TEXT_HTML)
    fun getIndex(): Uni<TemplateInstance> = getRecentActivity()

    @GET
    @Path("/recent-activity")
    @Produces(MediaType.TEXT_HTML)
    fun getRecentActivity(): Uni<TemplateInstance> =
        Uni
            .combine()
            .all()
            .unis(
                userService.findLastRegistrations { it.toModel() },
                mapService.findRecentMaps { it.toModel() },
                userService.findLatestTeamkills { it.toModel() },
            ).asTuple()
            .onItem()
            .transform { tuple ->
                quteFormatter.render(
                    recentActivity,
                    mapOf(
                        "users" to tuple.item1,
                        "mapUploads" to tuple.item2,
                        "teamkills" to tuple.item3,
                    ),
                )
            }
}

fun MapVersion.toModel() =
    MapVersionModel(
        id = id.toInt(),
        mapId = map.id.toInt(),
        createdAt = createTime.toInstant(),
        updatedAt = updateTime.toInstant(),
        description = description,
        maxPlayers = maxPlayers,
        width = width,
        height = height,
        version = version.toString(),
        folderName = folderName,
        fileName = filename,
        ranked = ranked,
        hidden = hidden,
        thumbnailUrlSmall = thumbnailUrlSmall.toString(),
        thumbnailUrlLarge = thumbnailUrlLarge.toString(),
        downloadUrl = downloadUrl.toString(),
    )

fun Teamkill.toModel() =
    TeamkillModel(
        id = id.toInt(),
        teamkiller =
            TeamkillModel.UserReference(
                id = teamkiller.id.toInt(),
                name = teamkiller.login,
            ),
        victim =
            TeamkillModel.UserReference(
                id = victim.id.toInt(),
                name = victim.login,
            ),
        gameId = game.id.toInt(),
        gameTime = gameTime,
        reportedAt = reportedAt.toInstant(),
    )
