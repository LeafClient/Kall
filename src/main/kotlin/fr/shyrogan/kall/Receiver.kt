package fr.shyrogan.kall

import fr.shyrogan.kall.subscription.*

/**
 * A [Receiver] is used by Kall to mark a class which contains subscriptions, only these marked classes
 * can register subscriptions.
 * This allows us to avoid caching (compared to other Java buses).
 */
abstract class Receiver {

    /**
     * A list used by Kall to contain the [Subscription]
     */
    val subscriptions by lazy {
        javaClass.declaredFields
                .filter { Subscription::class.java.isAssignableFrom(it.type) }
                .map {
                    val wasAccessible = it.isAccessible
                    it.isAccessible = true
                    val subscription = it[this] as Subscription<*>
                    it.isAccessible = wasAccessible

                    subscription
                }
                .groupBy(Subscription<*>::topic)
    }

}

/**
 * Inline function that allows you to create a fast [Subscription] for
 * dispatchers
 */
@JvmOverloads
inline fun <reified T: Any> Receiver.subscription(
        priority: Int = 0, filters: Array<Filter<in T>> = emptyArray(), crossinline handler: SubscriptionHandler<T>
): Subscription<T> {
    if(filters.isEmpty())
        return object: NonFilteredSubscription<T>(T::class.java, priority) { override fun receive(message: T) = handler(message) }

    return object : FilteredSubscription<T>(T::class.java, priority, filters) {
        override fun receive(message: T) {
            if(filters.none { !it.passes(message) })
                handler(message)
        }
    }
}
