package com.ramcosta.composedestinations.bottomsheet.spec

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.navigation.BottomSheetNavigatorSheetState
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastForEach
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorState
import androidx.navigation.compose.LocalOwnersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.transform
import kotlin.coroutines.cancellation.CancellationException

@Navigator.Name("blockedBottomSheet")
public class BlockedBottomSheetNavigator(
    internal val sheetState: ModalBottomSheetState
) : Navigator<BlockedBottomSheetNavigator.Destination>() {

    private var attached by mutableStateOf(false)

    /**
     * Get the back stack from the [state]. In some cases, the [sheetContent] might be composed
     * before the Navigator is attached, so we specifically return an empty flow if we aren't
     * attached yet.
     */
    private val backStack: StateFlow<List<NavBackStackEntry>>
        get() = if (attached) {
            state.backStack
        } else {
            MutableStateFlow(emptyList())
        }

    /**
     * Get the transitionsInProgress from the [state]. In some cases, the [sheetContent] might be
     * composed before the Navigator is attached, so we specifically return an empty flow if we
     * aren't attached yet.
     */
    internal val transitionsInProgress: StateFlow<Set<NavBackStackEntry>>
        get() = if (attached) {
            state.transitionsInProgress
        } else {
            MutableStateFlow(emptySet())
        }

    /**
     * Access properties of the [ModalBottomSheetLayout]'s [ModalBottomSheetState]
     */
    public val navigatorSheetState: BottomSheetNavigatorSheetState =
        BottomSheetNavigatorSheetState(sheetState)

    /**
     * A [Composable] function that hosts the current sheet content. This should be set as
     * sheetContent of your [ModalBottomSheetLayout].
     */
    @SuppressLint("ProduceStateDoesNotAssignValue")
    internal val sheetContent: @Composable ColumnScope.() -> Unit = {
        val saveableStateHolder = rememberSaveableStateHolder()
        val transitionsInProgressEntries by transitionsInProgress.collectAsState()

        // The latest back stack entry, retained until the sheet is completely hidden
        // While the back stack is updated immediately, we might still be hiding the sheet, so
        // we keep the entry around until the sheet is hidden
        val retainedEntry by produceState<NavBackStackEntry?>(
            initialValue = null,
            key1 = backStack
        ) {
            backStack
                .transform { backStackEntries ->
                    // Always hide the sheet when the back stack is updated
                    // Regardless of whether we're popping or pushing, we always want to hide
                    // the sheet first before deciding whether to re-show it or keep it hidden
                    try {
                        sheetState.hide()
                    } catch (_: CancellationException) {
                        // We catch but ignore possible cancellation exceptions as we don't want
                        // them to bubble up and cancel the whole produceState coroutine
                    } finally {
                        emit(backStackEntries.lastOrNull())
                    }
                }
                .collect {
                    value = it
                }
        }

        if (retainedEntry != null) {
            LaunchedEffect(retainedEntry) {
                sheetState.show()
            }

            BackHandler {
                state.popWithTransition(popUpTo = retainedEntry!!, saveState = false)
            }
        }

        retainedEntry?.let { retainedEntry ->
            val currentOnSheetShown: (NavBackStackEntry?) -> Unit by rememberUpdatedState({
                transitionsInProgressEntries.forEach(state::markTransitionComplete)
            })
            val currentOnSheetDismissed: (NavBackStackEntry) -> Unit by rememberUpdatedState { backStackEntry ->
                if (transitionsInProgressEntries.contains(backStackEntry))
                    state.markTransitionComplete(backStackEntry)
                else state.pop(popUpTo = backStackEntry, saveState = false)
            }
            LaunchedEffect(sheetState, retainedEntry) {
                snapshotFlow { sheetState.isVisible }
                    // We are only interested in changes in the sheet's visibility
                    .distinctUntilChanged()
                    // distinctUntilChanged emits the initial value which we don't need
                    .drop(1)
                    .collect { visible ->
                        if (visible) {
                            currentOnSheetShown(retainedEntry)
                        } else {
                            currentOnSheetDismissed(retainedEntry)
                        }
                    }
            }
            retainedEntry.LocalOwnersProvider(saveableStateHolder) {
                val content =
                    (retainedEntry.destination as Destination).content
                content(retainedEntry)
            }
        }
    }

    override fun onAttach(state: NavigatorState) {
        super.onAttach(state)
        attached = true
    }

    override fun createDestination(): Destination = Destination(
        navigator = this,
        content = {}
    )

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.fastForEach { entry ->
            state.pushWithTransition(entry)
        }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
    }

    /**
     * [NavDestination] specific to [BlockedBottomSheetNavigator]
     */
    @NavDestination.ClassType(Composable::class)
    public class Destination(
        navigator: BlockedBottomSheetNavigator,
        internal val content: @Composable ColumnScope.(NavBackStackEntry) -> Unit
    ) : NavDestination(navigator), FloatingWindow
}
