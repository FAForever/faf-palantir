package com.faforever.palantir.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import java.time.Instant

@JvmRecord
data class UserModel(
    val name: String,
    val id: Int,
    val email: String,
    val accountLinks: List<AccountLink>,
    val ipAddress: String?,
    val registeredAt: Instant,
    val lastLoginAt: Instant?,
    val lastUpdatedAt: Instant?,
    val uids: List<UniqueId>,
) {
    @JvmRecord
    data class AccountLink(
        val id: String,
        val type: Type,
        val serviceId: String,
    ) {
        enum class Type {
            STEAM,
            GOG,
            DISCORD,
            PATREON,

            @JsonEnumDefaultValue
            UNKNOWN,
        }
    }

    @JvmRecord
    data class UniqueId(
        val hash: String?,
        val uuid: String?,
        val memorySerialNumber: String?,
        val deviceId: String?,
        val manufacturer: String?,
        val name: String?,
        val processorId: String?,
        val smbiosbiosVersion: String?,
        val serialNumber: String?,
        val volumeSerialNumber: String?,
    )
}
