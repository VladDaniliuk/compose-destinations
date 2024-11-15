package com.ramcosta.composedestinations.bottomsheet.spec

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.bottomsheet.scope.BottomSheetDestinationScopeImpl
import com.ramcosta.composedestinations.manualcomposablecalls.DestinationLambda
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.spec.TypedDestinationSpec

@Composable
internal fun <T> ColumnScope.CallComposable(
    destination: TypedDestinationSpec<T>,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
    dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
    contentWrapper: DestinationLambda<T>?
) {
    val scope = remember(destination, navBackStackEntry, navController, this, dependenciesContainerBuilder) {
        BottomSheetDestinationScopeImpl(
            destination,
            navBackStackEntry,
            navController,
            this,
            dependenciesContainerBuilder
        )
    }

    if (contentWrapper == null) {
        with(destination) { scope.Content() }
    } else {
        contentWrapper(scope)
    }
}
