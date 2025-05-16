package com.coda.situlearner.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

internal class AiStateSerializer: Serializer<AiStateProto> {
    override val defaultValue: AiStateProto
        get() = AiStateProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AiStateProto =
        try {
            AiStateProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }


    override suspend fun writeTo(t: AiStateProto, output: OutputStream) {
        t.writeTo(output)
    }
}