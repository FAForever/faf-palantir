package com.faforever.palantir.api.model

data class Page<T>(
    val number: Int,
    val size: Int,
    val items: List<T>,
) : Iterable<T> by items {
    fun next() =
        copy(
            number = number + 1,
        )
}
