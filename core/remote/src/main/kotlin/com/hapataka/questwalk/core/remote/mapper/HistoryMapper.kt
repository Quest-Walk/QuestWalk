package com.hapataka.questwalk.core.remote.mapper

import com.hapataka.questwalk.core.model.History
import com.hapataka.questwalk.core.model.Location
import com.hapataka.questwalk.core.remote.model.QuestResultDto
import com.hapataka.questwalk.core.remote.util.decryptECB
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

internal fun QuestResultDto.toModel(key: String): History.QuestResult {
    return History.QuestResult(
        id = this.resultId,
        userId = this.userId,
        registerAt = LocalDateTime.parse(this.registerAt),
        questKeyword = this.questKeyword,
        duration = this.duration,
        distance = this.distance,
        step = this.step,
        isSuccess = this.isSuccess,
        route = this.route.toRoute(key),
        successLocation = this.successLocation?.toLocation(key),
        imageUrl = this.imageUrl,
    )
}

private fun String.toRoute(key: String): List<Location> {
    val json = Json {
        ignoreUnknownKeys = true
    }

    val pairs = json.decodeFromString(ListSerializer(FloatPairSerializer), this.decryptECB(key))
    return pairs.map { Location(latitude = it.first, longitude = it.second) }
}

private fun String.toLocation(key: String): Location {
    val json = Json {
        ignoreUnknownKeys = true
    }

    val pair = json.decodeFromString(FloatPairSerializer, this.decryptECB(key))
    return Location(latitude = pair.first, longitude = pair.second)
}

object FloatPairSerializer : KSerializer<Pair<Float, Float>> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("FloatPair") {
            element<Float>("first")
            element<Float>("second")
        }

    override fun serialize(encoder: Encoder, value: Pair<Float, Float>) {
        encoder.encodeStructure(descriptor) {
            encodeFloatElement(descriptor, 0, value.first)
            encodeFloatElement(descriptor, 1, value.second)
        }
    }

    override fun deserialize(decoder: Decoder): Pair<Float, Float> {
        var f = 0f
        var s = 0f

        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    0 -> f = decodeFloatElement(descriptor, 0)
                    1 -> s = decodeFloatElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $i")
                }
            }
        }

        return f to s
    }
}