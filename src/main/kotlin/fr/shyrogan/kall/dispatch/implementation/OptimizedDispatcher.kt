package fr.shyrogan.kall.dispatch.implementation

import fr.shyrogan.kall.Cancellable
import fr.shyrogan.kall.Subscription
import fr.shyrogan.kall.dispatch.Dispatcher
import fr.shyrogan.kall.dispatch.DispatcherCondition
import fr.shyrogan.kall.dispatch.DispatcherFactories
import fr.shyrogan.kall.dispatch.DispatcherSupplier

class OptimizedDispatcher<T: Any>(subscriptions: MutableList<Subscription<T>>): Dispatcher<T>(subscriptions) {

    init {
        this.subscriptions.sortByDescending { it.priority }
    }

    /**
     * Calls [message] for each [subscriptions]
     */
    override fun dispatch(message: T) {
        // Invoke each listeners
        val size = subscriptions.size
        for(i in 0 until size) {
            subscriptions[i].receive(message)
            if(message is Cancellable && message.isCancelled)
                break
        }
    }

    /**
     * Avoid recreating a new [OptimizedDispatcher] instance when registering a
     * new [subscription].
     */
    override fun register(subscription: Subscription<T>) = apply {
        subscriptions.add(subscription)
        subscriptions.sortByDescending { it.priority }
    }

    /**
     * Only recreates a new [Dispatcher] instance if the size is smaller or equals to one
     */
    override fun unregister(subscription: Subscription<T>) = run {
        subscriptions.remove(subscription)
        if(subscriptions.size <= 1) {
            return@run DispatcherFactories.findFor(subscriptions)
        }
        subscriptions.sortByDescending { it.priority }
        return@run this
    }

    /**
     * Avoid recreating a new [OptimizedDispatcher] instance when registering a
     * new [subscription].
     */
    override fun registerAll(subscription: List<Subscription<T>>) = apply {
        subscriptions.addAll(subscription)
        subscriptions.sortByDescending { it.priority }
    }

    /**
     * Only recreates a new [Dispatcher] instance if the size is smaller or equals to one
     */
    override fun unregisterAll(subscription: List<Subscription<T>>) = run {
        subscriptions.removeAll(subscription)
        if(subscriptions.size <= 1) {
            return@run DispatcherFactories.findFor(subscriptions)
        }
        subscriptions.sortByDescending { it.priority }
        return@run this
    }

    companion object {
        /**
         * The [DispatcherSupplier] for the [EmptyDispatcher]
         */
        val SUPPLIER: DispatcherSupplier<Any> = { OptimizedDispatcher(it.toMutableList()) }

        /**
         * The [DispatcherCondition] for the [EmptyDispatcher]
         */
        val CONDITION: DispatcherCondition = { size -> size > 1 }
    }

}