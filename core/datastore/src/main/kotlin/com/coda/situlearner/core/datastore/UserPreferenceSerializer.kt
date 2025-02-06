package com.coda.situlearner.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

internal class UserPreferenceSerializer : Serializer<UserPreferenceProto> {
    override val defaultValue: UserPreferenceProto
        get() = UserPreferenceProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferenceProto =
        try {
            UserPreferenceProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserPreferenceProto, output: OutputStream) {
        t.writeTo(output)
    }
}