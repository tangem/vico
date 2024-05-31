/*
 * Copyright 2024 by Patryk Goworowski and Patrick Michalik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tangem.vico.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patrykandpatrick.vico.compose.common.VicoTheme

@Composable
fun DemoApp() {
  val navController = rememberNavController()
  DemoVicoTheme {
    NavHost(navController = navController, startDestination = "chart/1") {
//    NavHost(navController = navController, startDestination = "chartList") {
      composable("chartList") { ChartList(navController) }
      composable(
          "chart/{chartId}",
          listOf(
              navArgument("chartId") { type = NavType.IntType },
          ),
      ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        ChartHost(chartId = arguments.getInt("chartId"))
      }
    }
  }
}
