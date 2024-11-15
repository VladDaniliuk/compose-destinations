package com.ramcosta.composedestinations.bottomsheet.spec

import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.get
import com.ramcosta.composedestinations.manualcomposablecalls.DestinationLambda
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCalls
import com.ramcosta.composedestinations.manualcomposablecalls.allDeepLinks
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.spec.TypedDestinationSpec

object DestinationStyleBlockedBottomSheet : DestinationStyle() {
    override fun <T> NavGraphBuilder.addComposable(
        destination: TypedDestinationSpec<T>,
        navController: NavHostController,
        dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
        manualComposableCalls: ManualComposableCalls
    ) {
        @Suppress("UNCHECKED_CAST")
        val contentWrapper = manualComposableCalls[destination.route] as? DestinationLambda<T>?

        addDestination(
            BlockedBottomSheetNavigator.Destination(
                provider[BlockedBottomSheetNavigator::class],
                { navBackStackEntry ->
                    CallComposable(
                        destination,
                        navController,
                        navBackStackEntry,
                        dependenciesContainerBuilder,
                        contentWrapper
                    )
                }
            ).apply {
                this.route = destination.route
                destination.arguments.fastForEach { (argumentName, argument) ->
                    addArgument(argumentName, argument)
                }
                destination.allDeepLinks(manualComposableCalls).fastForEach { deepLink ->
                    addDeepLink(deepLink)
                }
            }
        )
    }
}
