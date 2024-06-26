package com.faforever.palantir.security

import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.SecurityIdentityAugmentor
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.json.JsonString
import org.eclipse.microprofile.jwt.JsonWebToken

@ApplicationScoped
class FafPermissionsAugmentor : SecurityIdentityAugmentor {
    override fun augment(
        identity: SecurityIdentity,
        context: AuthenticationRequestContext,
    ): Uni<SecurityIdentity> = Uni.createFrom().item(build(identity))

    @Suppress("UNCHECKED_CAST")
    private fun build(identity: SecurityIdentity): SecurityIdentity =
        if (identity.isAnonymous) {
            identity
        } else {
            val builder = QuarkusSecurityIdentity.builder(identity)
            when (val principal = identity.principal) {
                is JsonWebToken -> {
                    val roles =
                        principal
                            .claim<List<JsonString>>("roles")
                            .orElse(emptyList())
                            .map { it.string }
                            .toSet()

                    val scopes =
                        principal
                            .claim<List<JsonString>>("scp")
                            .map { it.map { jsonString -> jsonString.string }.toSet() }
                            .orElse(setOf())

                    builder.addPermissionChecker { requiredPermission ->
                        val hasRole = roles.contains(requiredPermission.name)
                        val hasScopes = requiredPermission.actions.split(",").all { scopes.contains(it) }
                        Uni.createFrom().item(hasRole && hasScopes)
                    }

                    builder.addRoles(roles)
                }
            }
            builder.build()
        }
}
