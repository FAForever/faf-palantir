package com.faforever.palantir.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import io.quarkus.jackson.ObjectMapperCustomizer
import jakarta.enterprise.context.ApplicationScoped
import org.apache.maven.artifact.versioning.ComparableVersion

@ApplicationScoped
class CustomObjectMapperCustomizer : ObjectMapperCustomizer {
    override fun customize(objectMapper: ObjectMapper) {
        val comparableVersionDeserializer =
            SimpleModule()
                .addDeserializer(
                    ComparableVersion::class.java,
                    object : JsonDeserializer<ComparableVersion>() {
                        override fun deserialize(
                            p: JsonParser,
                            ctxt: DeserializationContext,
                        ): ComparableVersion = ComparableVersion(p.valueAsString)
                    },
                )

        objectMapper.registerModule(comparableVersionDeserializer)
    }
}
