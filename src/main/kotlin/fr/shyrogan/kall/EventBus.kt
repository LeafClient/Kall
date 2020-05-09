package fr.shyrogan.kall

import fr.shyrogan.kall.dispatch.Dispatcher
import fr.shyrogan.kall.dispatch.DispatcherFactories
import fr.shyrogan.kall.explorer.SubscriptionExplorer
import fr.shyrogan.kall.explorer.implementation.FieldExplorer

@Suppress("unchecked_cast")
open class EventBus(private val subscriptionExplorer: SubscriptionExplorer = FieldExplorer) {

    /**
     * Associates a class to its [Dispatcher] instance.
     */
    private val dispatcherMap = mutableMapOf<Class<*>, Dispatcher<*>>()

    /**
     * Explores [instance] using the [SubscriptionExplorer] and register each [Subscription]
     * found.
     */
    fun exploreAndRegister(instance: Any) {
        subscriptionExplorer.explore(instance)
                .groupBy { it.topic }
                .forEach { (_, subscription) ->
                    registerAll(subscription)
                }
    }

    /**
     * Explores [instance] using the [SubscriptionExplorer] and register each [Subscription]
     * found.
     */
    fun exploreAndUnregister(instance: Any) {
        subscriptionExplorer.explore(instance)
                .groupBy { it.topic }
                .forEach { (_, subscription) ->
                    unregisterAll(subscription)
                }
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