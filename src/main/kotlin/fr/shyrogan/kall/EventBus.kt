package fr.shyrogan.kall

import fr.shyrogan.kall.dispatch.Dispatcher
import fr.shyrogan.kall.dispatch.DispatcherFactories

@Suppress("unchecked_cast")
open class EventBus() {

    /**
     * Associates a class to its [Dispatcher] instance.
     */
    private val dispatcherMap = mutableMapOf<Class<*>, Dispatcher<*>>()

    /**
     * Registers each subscriptions contained by the [receiver]
     */
    fun register(receiver: Receiver) = receiver.subscriptions.forEach {
        register(it)
    }

    /**
     * Registers each subscriptions contained by the [receiver]
     */
    fun unregister(receiver: Receiver) = receiver.subscriptions.forEach {
        unregister(it)
    }

    /**
     * Registers [subscription] to its dispatcher
     */
    fun <T: Any> register(subscription: Subscription<T>) {
        val dispatcherInstance = dispatcherMap[subscription.topic]
                ?: DispatcherFactories.findFor(listOf(subscription))
        dispatcherInstance as Dispatcher<T>

        dispatcherMap[subscription.topic] = dispatcherInstance.register(subscription)
    }

    /**
     * Unregisters [subscription] to its dispatcher
     */
    fun <T: Any> unregister(subscription: Subscription<T>) {
        val dispatcherInstance = dispatcherMap[subscription.topic]
                ?: DispatcherFactories.findFor(listOf(subscription))
        dispatcherInstance as Dispatcher<T>

        dispatcherMap[subscription.topic] = dispatcherInstance.unregister(subscription)
    }

    /**
     * Registers [subscription] to its dispatcher
     */
    fun <T: Any> registerAll(subscription: List<Subscription<T>>) {
        if(subscription.isEmpty())
            return

        val topic = subscription[0].topic
        val dispatcherInstance = dispatcherMap[topic]
                ?: DispatcherFactories.findFor(subscription)
        dispatcherInstance as Dispatcher<T>

        dispatcherMap[topic] = dispatcherInstance.registerAll(subscription)
    }

    /**
     * Unregisters [subscription] to its dispatcher
     */
    fun <T: Any> unregisterAll(subscription: List<Subscription<T>>) {
        if(subscription.isEmpty())
            return

        val topic = subscription[0].topic
        val dispatcherInstance = dispatcherMap[topic]
                ?: return
        dispatcherInstance as Dispatcher<T>

        dispatcherMap[topic] = dispatcherInstance.unregisterAll(subscription)
    }

    /**
     * Dispatch [message] to the concerned [Dispatcher] if it exists.
     */
    fun <T: Any> dispatch(message: T): T {
        val dispatcherInstance = dispatcherMap[message::class.java] ?: return message
        dispatcherInstance as Dispatcher<T>

        dispatcherInstance.dispatch(message)
        return message
    }

}