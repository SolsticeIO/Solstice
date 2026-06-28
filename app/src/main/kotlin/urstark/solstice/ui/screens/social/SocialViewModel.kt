package urstark.solstice.ui.screens.social

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import urstark.solstice.matrix.MatrixClientManager
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    val matrixManager: MatrixClientManager
) : ViewModel() {
    // ViewModel to expose MatrixClientManager state to Compose UI seamlessly
}
