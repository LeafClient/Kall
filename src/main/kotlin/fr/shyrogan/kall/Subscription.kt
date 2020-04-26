package fr.shyrogan.kall

typealias SubscriptionHandler<T> = (T) -> Unit

/**
 * Represents a [Subscription] to our dispatchers
 */
interface Subscription<T: Any> {

    /**
     * Returns the [topic]
     */
    val topic: Class<T>

    /**
     * Returns the [priority]
     */
    val priority: Int

    /**
     * Returns the [Filter] instances
     */
    val filters: Array<Filter<T>>

    /**
     * Method invoked when [message] is received
     */
    fun receive(message: T)

}

/**
 * A [Subscription] with no [Filter], allowing us to avoid useless operations
 * for empty filters
 */
abstract class NonFilteredSubscription<T: Any>(
        override val topic: Class<T>, override val priority: Int
): Subscription<T> {
    /**
     * Returns an empty array
     */
    override val filters: Array<Filter<T>> = emptyArray()
}

/**
 * A [Subscription] with no [Filter], allowing us to avoid useless operations
 * for empty filters
 */
abstract class FilteredSubscription<T: Any>(
        override val topic: Class<T>, override val priority: Int,
        override val filters: Array<Filter<T>>
): Subscription<T>

/**
 * Inline function that allows you to create a fast [Subscription] for
 * dispatchers
 */
@JvmOverloads
inline fun <reified T: Any> subscription(
        priority: Int = 0, filters: Array<Filter<T>> = emptyArray(), crossinline handler: SubscriptionHandler<T>
): Subscription<T> {
    if(filters.isEmpty())
        return object: NonFilteredSubscription<T>(T::class.java, priority) { override fun receive(message: T) = handler(message) }

    return object : FilteredSubscription<T>(T::class.java, priority, filters) {
        override fun receive(message: T) {
            if(filters.any { !it.passes(message) })
                handler(message)
        }
    }
}