package com.coda.situlearner.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

internal class PlayerStateSerializer : Serializer<PlayerStateProto> {
    override val defaultValue: PlayerStateProto
        get() = PlayerStateProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PlayerStateProto =
        try {
            PlayerStateProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: PlayerStateProto, output: OutputStream) {
        t.writeTo(output)
    }
}