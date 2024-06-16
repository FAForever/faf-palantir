package com.faforever.palantir.model

import java.time.Instant

@JvmRecord
data class MapVersionModel(
    val id: Int,
    val mapId: Int,
    val version: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val description: String,
    val maxPlayers: Int,
    val width: Int,
    val height: Int,
    val folderName: String,
    val fileName: String,
    val ranked: Boolean,
    val hidden: Boolean,
    val thumbnailUrlSmall: String,
    val thumbnailUrlLarge: String,
    val downloadUrl: String,
)
