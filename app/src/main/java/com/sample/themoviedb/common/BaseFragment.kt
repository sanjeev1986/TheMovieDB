package com.sample.themoviedb.common

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * All fragments in the app should extend this class
 */
abstract class BaseFragment : Fragment() {
    private val errorbar by lazy(LazyThreadSafetyMode.NONE) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "",
            Snackbar.LENGTH_INDEFINITE
        )
    }

    protected fun displayError(message: String, actionTitie: String, action: ((View) -> Unit)?) {
        dismissError()
        errorbar.setText(message)
        errorbar.setAction(actionTitie, action)
        errorbar.show()
    }

    protected fun dismissError() {
        errorbar.dismiss()
    }
}
