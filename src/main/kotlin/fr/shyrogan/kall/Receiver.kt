package fr.shyrogan.kall

/**
 * A [Receiver] is used by Kall to mark a class which contains subscriptions, only these marked classes
 * can register subscriptions.
 * This allows us to avoid caching (compared to other Java buses).
 */
interface Receiver {

    /**
     * A list used by Kall to contain the [Subscription]
     */
    val subscriptions: MutableList<Subscription<*>>

}

/**
 * Inline function that allows you to create a fast [Subscription] for
 * dispatchers
 */
@JvmOverloads
inline fun <reified T: Any> Receiver.subscription(
    priority: Int = 0, filters: Array<Filter<T>> = emptyArray(), crossinline handler: SubscriptionHandler<T>
): Subscription<T> {
    if(filters.isEmpty())
        return object: NonFilteredSubscription<T>(T::class.java, priority) { override fun receive(message: T) = handler(message) }.also {
            subscriptions += it
        }

    return object : FilteredSubscription<T>(T::class.java, priority, filters) {
        override fun receive(message: T) {
            if(filters.any { !it.passes(message) })
                handler(message)
        }
    }.also {
        subscriptions += it
    }
}