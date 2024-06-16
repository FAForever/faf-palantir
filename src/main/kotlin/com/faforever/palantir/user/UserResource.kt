package com.faforever.palantir.user

import com.faforever.commons.api.dto.LinkedServiceType
import com.faforever.commons.api.dto.Player
import com.faforever.palantir.model.UserModel
import com.faforever.palantir.templating.QuteFormatter
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.quarkus.security.Authenticated
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType

@Path("/user")
@Authenticated
class UserController(
    val quteFormatter: QuteFormatter,
    val userInfo: Template,
    val userSearch: Template,
    val userService: UserService,
) {
    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_HTML)
    fun getUserInfo(): TemplateInstance = quteFormatter.render(userInfo)

    @GET
    @Path("/search")
    @Produces(MediaType.TEXT_HTML)
    fun searchUsers(
        @QueryParam("search-for") searchFor: String,
        @QueryParam("query") query: String,
    ): Uni<TemplateInstance> =
        userService
            .findPlayers(
                filterKey = searchFor,
                filterPattern = query,
            ) { it.toModel() }
            .onItem()
            .transform { users ->
                quteFormatter.render(userSearch, mapOf("users" to users))
            }
}

fun Player.toModel() =
    UserModel(
        name = login,
        id = id.toInt(),
        email = email,
        accountLinks =
            accountLinks.map {
                UserModel.AccountLink(
                    id = it.id,
                    type =
                        when (it.serviceType) {
                            null -> UserModel.AccountLink.Type.UNKNOWN
                            LinkedServiceType.STEAM -> UserModel.AccountLink.Type.STEAM
                            LinkedServiceType.GOG -> UserModel.AccountLink.Type.GOG
                            LinkedServiceType.DISCORD -> UserModel.AccountLink.Type.DISCORD
                            LinkedServiceType.PATREON -> UserModel.AccountLink.Type.PATREON
                            LinkedServiceType.UNKNOWN -> UserModel.AccountLink.Type.UNKNOWN
                        },
                    serviceId = it.serviceId,
                )
            },
        ipAddress = recentIpAddress,
        registeredAt = this.createTime.toInstant(),
        lastLoginAt = this.lastLogin?.toInstant(),
        lastUpdatedAt = this.updateTime?.toInstant(),
        uids =
            uniqueIds.map {
                UserModel.UniqueId(
                    hash = it.hash,
                    uuid = it.uuid,
                    memorySerialNumber = it.memorySerialNumber,
                    deviceId = it.deviceId,
                    name = it.name,
                    processorId = it.processorId,
                    manufacturer = it.manufacturer,
                    smbiosbiosVersion = it.smbiosbiosVersion,
                    serialNumber = it.serialNumber,
                    volumeSerialNumber = it.volumeSerialNumber,
                )
            },
    )
