package abika.sinau.tokofakes.libraries.core.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

actual abstract class ViewModel {
    actual val viewModelScope: CoroutineScope
        get() = object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = SupervisorJob() + Dispatchers.Main // SupervisorJob -> Coroutine Job yang paling atas

        }

    actual fun cleared() {
        viewModelScope.cancel()
    }
}

@Composable
actual fun <T : ViewModel> rememberViewModel(isRetain: Boolean, viewModel: () -> T): T {
    val host = LocalViewModelHost.current
    val vm = remember {
        if (isRetain) {
            host.getViewModel(viewModel.invoke())
        } else {
            viewModel.invoke()
        }
    }

    if (!isRetain) {
        DisposableEffect(vm) {
            onDispose {
                vm.cleared()
            }
        }
    }

    return vm
}