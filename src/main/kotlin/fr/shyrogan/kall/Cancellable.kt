package fr.shyrogan.kall

/**
 * Represents an object that can be cancelled using the [isCancelled] method,
 * if a cancellable message is published and cancelled, it breaks the publish.
 */
abstract class Cancellable(var isCancelled: Boolean) {

    /**
     * Cancels this [Cancellable] object
     */
    fun cancel() = apply {
        isCancelled = true
    }

}