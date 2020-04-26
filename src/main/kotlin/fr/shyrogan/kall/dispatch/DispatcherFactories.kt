package fr.shyrogan.kall.dispatch

import fr.shyrogan.kall.Subscription
import fr.shyrogan.kall.dispatch.implementation.EmptyDispatcher
import fr.shyrogan.kall.dispatch.implementation.OptimizedDispatcher
import fr.shyrogan.kall.dispatch.implementation.SingletonDispatcher

/**
 * The [DispatcherFactories] is the object used by any bus to create a [Dispatcher] instance,
 * that means you can implement your own easily.
 */
object DispatcherFactories {

    /**
     * Allows custom [DispatcherFactory] to be created and used by any event bus
     */
    private val customDispatchers = mutableListOf<DispatcherFactory<Any>>()

    /**
     * Contains the defaults [Dispatcher] implementations
     */
    private val defaultDispatchers = arrayOf(
            EmptyDispatcher.SUPPLIER assuming EmptyDispatcher.CONDITION,
            SingletonDispatcher.SUPPLIER assuming SingletonDispatcher.CONDITION,
            OptimizedDispatcher.SUPPLIER assuming OptimizedDispatcher.CONDITION
    )

    /**
     * Provides a [DispatcherFactory] and register it into the [customDispatchers]
     */
    fun provide(factory: DispatcherFactoryHelper.() -> DispatcherFactory<Any>) {
        customDispatchers += factory(DispatcherFactoryHelper)
    }

    /**
     * Returns the fastest [Dispatcher] for [listeners]
     */
    @Suppress("unchecked_cast")
    fun <T: Any> findFor(listeners: MutableList<Subscription<T>>)
            = (customDispatchers.firstOrNull { it.condition(listeners.size) } as DispatcherFactory<T>?
            ?: defaultDispatchers.first { it.condition(listeners.size) } as DispatcherFactory<T>)
            .supplier(listeners)

}

/**
 * A type alias used by the [DispatcherFactories]
 */
typealias DispatcherSupplier<T> = (MutableList<Subscription<T>>) -> Dispatcher<T>

/**
 * A type alias used by the [DispatcherFactories]
 */
typealias DispatcherCondition = (Int) -> Boolean

/**
 * A class used to make the
 */
object DispatcherFactoryHelper {
    /**
     * This function allows an easier inline syntax
     */
    fun supplier(supplier: DispatcherSupplier<Any>) = supplier

    /**
     * This function allows an easier inline syntax
     */
    fun condition(condition: DispatcherCondition) = condition
}

/**
 * Infix function used to create [DispatcherFactory]
 */
infix fun <T: Any> DispatcherSupplier<T>.assuming(condition: DispatcherCondition)
        = DispatcherFactory(this, condition)

/**
 * A data class used by the [Dispatcher]
 */
data class DispatcherFactory<T: Any>(
        val supplier: DispatcherSupplier<T>,
        val condition: DispatcherCondition
)