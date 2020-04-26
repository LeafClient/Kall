package fr.shyrogan.kall.dispatch.implementation

import fr.shyrogan.kall.Subscription
import fr.shyrogan.kall.dispatch.Dispatcher
import fr.shyrogan.kall.dispatch.DispatcherCondition
import fr.shyrogan.kall.dispatch.DispatcherSupplier

class EmptyDispatcher<T: Any>: Dispatcher<T>() {

    /**
     * A no-operation [dispatch] implementation that will make our
     * calls are faster.
     */
    override fun dispatch(message: T) {}

    companion object {
        /**
         * The [DispatcherSupplier] for the [EmptyDispatcher]
         */
        val SUPPLIER: DispatcherSupplier<Any> = { EmptyDispatcher() }

        /**
         * The [DispatcherCondition] for the [EmptyDispatcher]
         */
        val CONDITION: DispatcherCondition = { size -> size == 0 }
    }

}