package fr.shyrogan.kall.subscription

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
    val filters: Array<Filter<in T>>

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
    override val filters: Array<Filter<in T>> = emptyArray()
}

/**
 * A [Subscription] with no [Filter], allowing us to avoid useless operations
 * for empty filters
 */
abstract class FilteredSubscription<T: Any>(
        override val topic: Class<T>, override val priority: Int,
        override val filters: Array<Filter<in T>>
): Subscription<T>