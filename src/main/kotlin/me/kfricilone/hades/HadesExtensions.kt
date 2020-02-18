/*
 * Copyright (c) 2020, Kyle Fricilone <kfricilone@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.kfricilone.hades

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import me.kfricilone.hades.lua.*
import java.util.zip.Adler32

/**
 * Created by Kyle Fricilone on Feb 16, 2020.
 */

const val TYPE_NIL = 0x2D
const val TYPE_FALSE = 0x30
const val TYPE_TRUE = 0x31
const val TYPE_NUMBER = 0x4E
const val TYPE_STRING = 0x53
const val TYPE_TABLE = 0x54
const val HADES_PADDING = 3145720


fun ByteBuf.readHades(): HadesSave {
    val header = readHadesHeader()
    val metadata = readHadesMetadata()

    return HadesSave(header, metadata)
}

fun ByteBuf.writeHades(value: HadesSave) {
    val meta = Unpooled.buffer()
    meta.writeHadesMetadata(value.metadata)

    writeHadesHeader(value.header, meta.getChecksum())
    writeBytes(meta)
}

fun ByteBuf.readHadesHeader(): HadesHeader {
    val signature = readSignature()
    val checksum = readIntLE()

    return HadesHeader(signature, checksum)
}

fun ByteBuf.writeHadesHeader(value: HadesHeader, checksum: Int) {
    writeSignature(value.signature)
    writeIntLE(checksum)
}

fun ByteBuf.readHadesMetadata(): HadesMetadata {
    val version = readIntLE()
    val location = readStringUTF8()
    val runs = readIntLE()
    val activeMetaPoints = readIntLE()
    val activeShrinePoints = readIntLE()
    val godModeEnabled = readBoolean()
    val hellModeEnabled = readBoolean()
    val luaKeys = readUTF8Array()
    val currentMapName = readStringUTF8()
    val startNextMap = readStringUTF8()

    val luaBuf = readBytes(readIntLE())
    val luaState = luaBuf.readLuabins()

    return HadesMetadata(
        version,
        location,
        runs,
        activeMetaPoints,
        activeShrinePoints,
        godModeEnabled,
        hellModeEnabled,
        luaKeys,
        currentMapName,
        startNextMap,
        luaState
    )
}

fun ByteBuf.writeHadesMetadata(value: HadesMetadata) {
    writeIntLE(value.version)
    writeStringUTF8(value.location)
    writeIntLE(value.runs)
    writeIntLE(value.activeMetaPoints)
    writeIntLE(value.activeShrinePoints)
    writeBoolean(value.godModeEnabled)
    writeBoolean(value.hellModeEnabled)
    writeUTF8Array(value.luaKeys)
    writeStringUTF8(value.currentMapName)
    writeStringUTF8(value.startNextMap)

    val luaBuf = Unpooled.buffer()
    luaBuf.writeLuabins(value.luaState)

    writeIntLE(luaBuf.readableBytes())
    writeBytes(luaBuf)

    val remain = HADES_PADDING - writerIndex()
    repeat(remain) { writeByte(0) }
}

fun ByteBuf.readLuabins(): Luabins {
    val length = readUnsignedByte().toInt()
    return Luabins(Array(length) { readLuaValue() })
}

fun ByteBuf.writeLuabins(value: Luabins) {
    writeByte(value.values.size)
    repeat(value.values.size) { writeLuaValue(value.values[it]) }
}

fun ByteBuf.readLuaValue(): LuaValue {
    val type = readUnsignedByte().toInt()
    return when (type) {
        TYPE_FALSE -> LuaBoolean(false)
        TYPE_TRUE -> LuaBoolean(true)
        TYPE_NUMBER -> LuaNumber(readDoubleLE())
        TYPE_STRING -> LuaString(readStringASCII())
        TYPE_TABLE -> readLuaTable()
        else -> LuaNil()
    }
}

fun ByteBuf.writeLuaValue(value: LuaValue) {
    when (value) {
        is LuaBoolean -> {
            if (value.value) {
                writeByte(TYPE_TRUE)
            } else {
                writeByte(TYPE_FALSE)
            }
        }
        is LuaNumber -> {
            writeByte(TYPE_NUMBER)
            writeDoubleLE(value.value)
        }
        is LuaString -> {
            writeByte(TYPE_STRING)
            writeStringASCII(value.value)
        }
        is LuaTable -> {
            writeByte(TYPE_TABLE)
            writeLuaTable(value)
        }
        else -> writeByte(TYPE_NIL)
    }
}

fun ByteBuf.readLuaTable(): LuaTable {
    val arraySize = readIntLE()
    val hashSize = readIntLE()
    val pairs = Array(arraySize + hashSize) { Pair(readLuaValue(), readLuaValue()) }

    return LuaTable(arraySize, hashSize, hashMapOf(*pairs))
}

fun ByteBuf.writeLuaTable(value: LuaTable) {
    writeIntLE(value.arraySize)
    writeIntLE(value.hashSize)
    value.table.forEach { (t, u) ->
        writeLuaValue(t)
        writeLuaValue(u)
    }
}

fun ByteBuf.getChecksum(): Int {
    val crc = Adler32()
    markReaderIndex()
    while (isReadable) {
        crc.update(readByte().toInt())
    }
    resetReaderIndex()
    return crc.value.toInt()
}

fun ByteBuf.readSignature(): String {
    var sig = ""
    repeat(4) { sig += readByte().toChar() }
    return sig
}

fun ByteBuf.writeSignature(value: String) {
    repeat(value.length) { writeByte(value[it].toInt()) }
}

fun ByteBuf.readStringASCII(): String {
    return readCharSequence(readIntLE(), Charsets.US_ASCII).toString()
}

fun ByteBuf.writeStringASCII(value: String) {
    writeIntLE(value.length)
    writeCharSequence(value, Charsets.US_ASCII)
}

fun ByteBuf.readStringUTF8(): String {
    return readCharSequence(readIntLE(), Charsets.UTF_8).toString()
}

fun ByteBuf.writeStringUTF8(value: String) {
    writeIntLE(value.length)
    writeCharSequence(value, Charsets.UTF_8)
}

fun ByteBuf.readUTF8Array(): Array<String> {
    return Array(readIntLE()) { readStringUTF8() }
}

fun ByteBuf.writeUTF8Array(value: Array<String>) {
    writeIntLE(value.size)
    repeat(value.size) { writeStringUTF8(value[it]) }
}
