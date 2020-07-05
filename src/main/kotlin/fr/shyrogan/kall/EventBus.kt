package fr.shyrogan.kall

import fr.shyrogan.kall.subscription.Subscription

@Suppress("unchecked_cast")
open class EventBus {

    private val cache = mutableMapOf<Class<*>, MutableList<Subscription<*>>>()

    fun register(receiver: Receiver) {
        receiver.subscriptions.forEach { (topic, subscriptions) ->
            cache[topic]?.addAll(subscriptions) ?: cache.put(topic, subscriptions.toMutableList())
        }
    }

    fun unregister(receiver: Receiver) {
        receiver.subscriptions.forEach { (topic, subscriptions) ->
            cache[topic]?.removeAll(subscriptions)
        }
    }

    fun <T: Any> add(subscription: Subscription<T>)
            = plusAssign(subscription)

    operator fun <T: Any> plusAssign(subscription: Subscription<T>)
            = cache.getOrPut(subscription.topic) { ArrayList() }.plusAssign(subscription)

    fun <T: Any> remove(subscription: Subscription<T>)
            = minusAssign(subscription)

    operator fun <T: Any> minusAssign(subscription: Subscription<T>)
            = cache.getOrPut(subscription.topic) { ArrayList() }.minusAssign(subscription)

    fun <T: Any> dispatch(message: T): T {
        val subscriptions = cache[message::class.java] ?: return message
        val dispatcher = Dispatcher.optimizedFor(subscriptions as MutableList<Subscription<T>>)

        dispatcher.dispatch(message)
        return message
    }

}