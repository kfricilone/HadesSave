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

import me.kfricilone.hades.lua.LuaNumber
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Created by Kyle Fricilone on Feb 17, 2020.
 */
class HadesSaveTest {

    val SAV_LOC = System.getProperty("user.home") + "\\Documents\\Saved Games\\Hades\\"

    @Test
    fun test() {
        val hades = HadesSave.load(File(SAV_LOC, "Profile2.sav"))

        var e = hades.metadata.luaState.findEntry("GameState", "Resources", "SuperGiftPoints")
        e?.setValue(LuaNumber(1000.0))

        e = hades.metadata.luaState.findEntry("GameState", "Resources", "MetaPoints")
        e?.setValue(LuaNumber(1000000.0))

        e = hades.metadata.luaState.findEntry("GameState", "Resources", "Gems")
        e?.setValue(LuaNumber(2000.0))

        e = hades.metadata.luaState.findEntry("GameState", "Resources", "GiftPoints")
        e?.setValue(LuaNumber(3000.0))

        e = hades.metadata.luaState.findEntry("GameState", "Resources", "SuperGems")
        e?.setValue(LuaNumber(4000.0))

        e = hades.metadata.luaState.findEntry("GameState", "Resources", "LockKeys")
        e?.setValue(LuaNumber(5000.0))

        e = hades.metadata.luaState.findEntry("GameState", "Resources", "SuperLockKeys")
        e?.setValue(LuaNumber(6000.0))

        e = hades.metadata.luaState.findEntry("GameState", "AccumulatedMetaPointsCache")
        e?.setValue(LuaNumber(1000000.0))

        hades.save(File(SAV_LOC, "Profile2.sav"))
    }

}