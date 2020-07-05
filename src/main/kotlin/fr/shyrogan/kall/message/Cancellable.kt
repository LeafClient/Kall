package fr.shyrogan.kall.message

/**
 * [Cancellable] messages are a specific kind of message written to be "cancelled". In that case,
 * the event bus will break the call iteration and will not call future subscriptions to avoid
 * conflicts.
 */
abstract class Cancellable(var isCancelled: Boolean = false) {

    fun cancel() = apply { isCancelled = true }

}