package fr.shyrogan.kall.subscription

/**
 * [Filter] are used to invoke a listener only if [T] message returns true when [passes] is
 * invoked. Allowing you to filter the message in-coming.
 */
interface Filter<T: Any> {

    /**
     * Method invoked to check if [message] should reach the subscription
     * containing this [Filter]
     */
    fun passes(message: T): Boolean

}

/**
 * Creates a new [Filter] instance that calls specified [filter]
 */
inline fun <T: Any> filter(crossinline filter: (T) -> Boolean)
    = object : Filter<T> { override fun passes(message: T) = filter(message) }