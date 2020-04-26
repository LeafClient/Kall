package fr.shyrogan.kall.explorer

import fr.shyrogan.kall.Subscription

/**
 * [SubscriptionExplorer] are used by the event bus to look for [Subscription] instances
 * inside of a class.
 */
interface SubscriptionExplorer {

    /**
     * Explores [instance] and looks for each [Subscription] contained.
     */
    fun explore(instance: Any): List<Subscription<Any>>

}