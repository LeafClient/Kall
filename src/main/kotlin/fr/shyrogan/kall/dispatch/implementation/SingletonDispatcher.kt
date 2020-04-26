package fr.shyrogan.kall.dispatch.implementation

import fr.shyrogan.kall.Subscription
import fr.shyrogan.kall.dispatch.Dispatcher
import fr.shyrogan.kall.dispatch.DispatcherCondition
import fr.shyrogan.kall.dispatch.DispatcherSupplier

class SingletonDispatcher<T: Any>(listeners: MutableList<Subscription<T>>): Dispatcher<T>(listeners) {

    private val listener = listeners[0]

    /**
     * Calls [message] for [listener]
     */
    override fun dispatch(message: T) = listener.receive(message)

    companion object {
        /**
         * The [DispatcherSupplier] for the [EmptyDispatcher]
         */
        val SUPPLIER: DispatcherSupplier<Any> = { SingletonDispatcher(it.toMutableList()) }

        /**
         * The [DispatcherCondition] for the [EmptyDispatcher]
         */
        val CONDITION: DispatcherCondition = { size -> size == 1 }
    }

}