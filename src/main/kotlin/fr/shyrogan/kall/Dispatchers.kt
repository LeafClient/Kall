package fr.shyrogan.kall

import fr.shyrogan.kall.message.Cancellable
import fr.shyrogan.kall.subscription.Subscription

/**
 * Depending on the amount subscriptions, the way we dispatch messages might change,
 * and [Dispatcher] is used as the super-class to each of these ways to dispatch messages.
 */
abstract class Dispatcher<T: Any> {

    abstract fun dispatch(message: T)

    companion object {
        /**
         * Returns a [Dispatcher] instance optimized for [list]
         */
        fun <T: Any> optimizedFor(list: MutableList<Subscription<T>>) = when(list.size) {
            0    -> EmptyDispatcher()
            1    -> SingletonDispatcher(list.first())
            else -> OptimizedDispatcher(list)
        }
    }

}

/**
 * A no-operation [Dispatcher] implementation.
 */
class EmptyDispatcher<T: Any>: Dispatcher<T>() {
    override fun dispatch(message: T) {}
    override fun toString() = "EmptyDispatcher()"
}

/**
 * An optimized [Dispatcher] for a lot of subscriptions
 */
data class OptimizedDispatcher<T: Any>(
        private val subscriptions: MutableList<Subscription<T>>
): Dispatcher<T>() {
    override fun dispatch(message: T) {
        // Invoke each listeners
        val size = subscriptions.size
        for(i in 0 until size) {
            subscriptions[i].receive(message)
            if(message is Cancellable && message.isCancelled)
                break
        }
    }
}

/**
 * A [Dispatcher] for a single [subscription]
 */
data class SingletonDispatcher<T: Any>(val subscription: Subscription<T>): Dispatcher<T>() {
    override fun dispatch(message: T) = subscription.receive(message)
}