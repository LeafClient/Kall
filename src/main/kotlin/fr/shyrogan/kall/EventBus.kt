package fr.shyrogan.kall

import fr.shyrogan.kall.subscription.Subscription

/**
 * The [EventBus] provided by Kall to register subscriptions and dispatch messages.
 */
@Suppress("unchecked_cast")
open class EventBus {

    private val cache = mutableMapOf<Class<*>, MutableList<Subscription<*>>>()

    fun register(receiver: Receiver) {
        receiver.subscriptions.forEach { (topic, subscriptions) ->
            cache[topic]?.also { cached ->
                cached.addAll(subscriptions)
                cached.sortBy { it.priority }
            } ?: cache.put(topic, subscriptions.toMutableList())
        }
    }

    fun unregister(receiver: Receiver) {
        receiver.subscriptions.forEach { (topic, subscriptions) ->
            cache[topic]?.also { cached ->
                cached.removeAll(subscriptions)
                cached.sortBy { it.priority }
            }
        }
    }

    fun <T: Any> add(subscription: Subscription<T>) = plusAssign(subscription)

    operator fun <T: Any> plusAssign(subscription: Subscription<T>)
            = cache.getOrPut(subscription.topic) { ArrayList() }.let { cached ->
        cached.add(subscription)
        cached.sortBy { it.priority }
    }

    fun <T: Any> remove(subscription: Subscription<T>) = minusAssign(subscription)

    operator fun <T: Any> minusAssign(subscription: Subscription<T>)
            = cache.getOrPut(subscription.topic) { ArrayList() }.let { cached ->
        cached.remove(subscription)
        cached.sortBy { it.priority }
    }

    fun <T: Any> dispatch(message: T): T {
        val subscriptions = cache[message::class.java] ?: return message
        val dispatcher = Dispatcher.optimizedFor(subscriptions as MutableList<Subscription<T>>)

        dispatcher.dispatch(message)
        return message
    }

}