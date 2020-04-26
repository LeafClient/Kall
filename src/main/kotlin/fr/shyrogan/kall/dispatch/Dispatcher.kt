package fr.shyrogan.kall.dispatch

import fr.shyrogan.kall.Subscription

/**
 * The [Dispatcher] is to contain and operate in various [Listener] instances, there
 * are various implementation of it, adapting it to consume as less performances as possible.
 */
abstract class Dispatcher<T: Any>(open var subscriptions: MutableList<Subscription<T>> = ArrayList()) {

    /**
     * Registers [subscription] to the current listeners, the [Dispatcher] might
     * change so please consider using the [Dispatcher] returned by this method.
     */
    open fun register(subscription: Subscription<T>): Dispatcher<T>
        = DispatcherFactories.findFor(subscriptions.apply { add(subscription) })

    /**
     * Registers [subscription] to the current listeners, the [Dispatcher] might
     * change so please consider using the [Dispatcher] returned by this method.
     */
    open fun registerAll(subscription: List<Subscription<T>>): Dispatcher<T>
            = DispatcherFactories.findFor(subscriptions.apply { addAll(subscription) })

    /**
     * Unregisters [subscription] to the current listeners, the [Dispatcher] might
     * change so please consider using the [Dispatcher] returned by this method.
     */
    open fun unregister(subscription: Subscription<T>): Dispatcher<T>
        = DispatcherFactories.findFor(subscriptions.apply { remove(subscription) })

    /**
     * Registers [subscription] to the current listeners, the [Dispatcher] might
     * change so please consider using the [Dispatcher] returned by this method.
     */
    open fun unregisterAll(subscription: List<Subscription<T>>): Dispatcher<T>
            = DispatcherFactories.findFor(subscriptions.apply { removeAll(subscription) })

    /**
     * Dispatches the [message] to each of the Listener contained by
     * this [Dispatcher]
     */
    abstract fun dispatch(message: T)

}