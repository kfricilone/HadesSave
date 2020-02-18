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

import me.kfricilone.hades.lua.Luabins

/**
 * Created by Kyle Fricilone on Feb 16, 2020.
 */
data class HadesMetadata(
    val version: Int,
    val location: String,
    val runs: Int,
    val activeMetaPoints: Int,
    val activeShrinePoints: Int,
    val godModeEnabled: Boolean,
    val hellModeEnabled: Boolean,
    val luaKeys: Array<String>,
    val currentMapName: String,
    val startNextMap: String,
    val luaState: Luabins
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HadesMetadata

        if (version != other.version) return false
        if (location != other.location) return false
        if (runs != other.runs) return false
        if (activeMetaPoints != other.activeMetaPoints) return false
        if (activeShrinePoints != other.activeShrinePoints) return false
        if (godModeEnabled != other.godModeEnabled) return false
        if (hellModeEnabled != other.hellModeEnabled) return false
        if (!luaKeys.contentEquals(other.luaKeys)) return false
        if (currentMapName != other.currentMapName) return false
        if (startNextMap != other.startNextMap) return false
        if (luaState != other.luaState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + location.hashCode()
        result = 31 * result + runs
        result = 31 * result + activeMetaPoints
        result = 31 * result + activeShrinePoints
        result = 31 * result + godModeEnabled.hashCode()
        result = 31 * result + hellModeEnabled.hashCode()
        result = 31 * result + luaKeys.contentHashCode()
        result = 31 * result + currentMapName.hashCode()
        result = 31 * result + startNextMap.hashCode()
        result = 31 * result + luaState.hashCode()
        return result
    }
}