package fr.shyrogan.kall

import fr.shyrogan.kall.message.Cancellable
import fr.shyrogan.kall.subscription.Subscription

abstract class Dispatcher<T: Any> {

    abstract fun dispatch(message: T)

    companion object {
        fun <T: Any> optimizedFor(list: MutableList<Subscription<T>>) = when(list.size) {
            0    -> EmptyDispatcher()
            1    -> SingletonDispatcher(list.first())
            else -> OptimizedDispatcher(list)
        }
    }

}

class EmptyDispatcher<T: Any>: Dispatcher<T>() {
    override fun dispatch(message: T) {}
    override fun toString() = "EmptyDispatcher()"
}

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

data class SingletonDispatcher<T: Any>(val listener: Subscription<T>): Dispatcher<T>() {
    override fun dispatch(message: T) = listener.receive(message)
}