// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import battleship.model.*
import battleship.storage.MongoStorage
import game.battleship.ui.BattleshipApp
import mongoDB.MongoDriver

fun main() {
    MongoDriver().use { drv ->
        application(exitProcessOnExit = false) {
            Window(
                title = "Battleship",
                onCloseRequest = ::exitApplication,
                state = WindowState(position = WindowPosition(Alignment.Center), size = DpSize.Unspecified)
            ) {
                BattleshipApp(MongoStorage(drv), onExit = ::exitApplication)
            }
        }
    }
}