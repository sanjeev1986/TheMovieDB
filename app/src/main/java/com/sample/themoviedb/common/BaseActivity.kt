package com.sample.themoviedb.common

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

/**
 * All Activities in the app should extend this class
 */
abstract class BaseActivity : AppCompatActivity() {

    fun prepareErrorSnackBar(
        errorMessage: String,
        actionText: String,
        onDismiss: (View) -> Unit
    ) = Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionText) {
            onDismiss(it)
        }

}