package com.faforever.palantir.api

import com.faforever.commons.api.dto.MapVersion
import com.faforever.commons.api.dto.Player
import com.faforever.commons.api.dto.Teamkill
import com.faforever.commons.api.elide.ElideEntity
import com.faforever.commons.api.elide.ElideNavigator
import com.faforever.commons.api.elide.ElideNavigatorOnCollection
import com.faforever.commons.api.elide.ElideNavigatorOnId
import com.faforever.palantir.FafConfig
import com.faforever.palantir.api.model.Page
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jasminb.jsonapi.ResourceConverter
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.ext.web.client.WebClient
import jakarta.inject.Singleton
import jakarta.ws.rs.core.HttpHeaders
import org.eclipse.microprofile.jwt.JsonWebToken

inline fun <reified T : ElideEntity> elideNavigator() = ElideNavigator.of(T::class.java)

@Singleton
class FafApiClient(
    private val fafConfig: FafConfig,
    private val accessToken: JsonWebToken,
    objectMapper: ObjectMapper,
) {
    private val vertx: Vertx = Vertx.vertx()
    private val client: WebClient = WebClient.create(vertx)

    private val resourceConverter =
        ResourceConverter(
            objectMapper,
            Player::class.java,
            Teamkill::class.java,
            MapVersion::class.java,
        )

    fun <IN : ElideEntity, OUT> getObject(
        navigator: ElideNavigatorOnId<IN>,
        mapper: (IN) -> OUT,
    ): Uni<OUT> = getObject(navigator.build(), navigator.dtoClass, mapper)

    fun <IN : ElideEntity, OUT> getObject(
        endpointPath: String,
        type: Class<IN>,
        mapper: (IN) -> OUT,
    ): Uni<OUT> =
        client
            .getAbs(
                fafConfig.apiBaseUrl() +
                    endpointPath
                        .replace("\"", "%22"),
            ).putHeader(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.rawToken}")
            .send()
            .onItem()
            .transform {
                resourceConverter.transformItem(it.bodyAsBuffer().bytes, type, mapper)
            }

    fun <IN : ElideEntity, OUT> getPage(
        navigator: ElideNavigatorOnCollection<IN>,
        pageNumber: Int = 1,
        pageSize: Int = 100,
        mapper: (IN) -> OUT,
    ): Uni<Page<OUT>> =
        client
            .getAbs(
                fafConfig.apiBaseUrl() +
                    navigator
                        .pageNumber(pageNumber)
                        .pageSize(pageSize)
                        .build()
                        .replace("\"", "%22"),
            ).putHeader(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.rawToken}")
            .send()
            .onItem()
            .transform {
                resourceConverter
                    .transformList(
                        payload = it.bodyAsBuffer().bytes,
                        type = navigator.dtoClass,
                        mapper = mapper,
                    ).let {
                        Page(number = pageNumber, size = pageSize, items = it)
                    }
            }

    private fun <IN, OUT> ResourceConverter.transformItem(
        payload: ByteArray,
        type: Class<IN>,
        mapper: (IN) -> OUT,
    ): OUT =
        this
            .readDocument(payload, type)
            .get()!!
            .let { mapper(it) }

    private fun <IN, OUT> ResourceConverter.transformList(
        payload: ByteArray,
        type: Class<IN>,
        mapper: (IN) -> OUT,
    ): List<OUT> =
        this
            .readDocumentCollection(payload, type)
            .get()
            ?.let { elements ->
                elements.map { mapper(it) }
            } ?: emptyList()
}
