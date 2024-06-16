package com.faforever.palantir.model

import java.time.Instant

@JvmRecord
data class TeamkillModel(
    val id: Int,
    val teamkiller: UserReference,
    val victim: UserReference,
    val gameId: Int,
    val gameTime: Long,
    val reportedAt: Instant,
) {
    @JvmRecord
    data class UserReference(
        val id: Int,
        val name: String,
    )
}
