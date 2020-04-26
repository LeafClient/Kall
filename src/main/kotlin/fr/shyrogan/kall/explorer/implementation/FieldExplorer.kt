package fr.shyrogan.kall.explorer.implementation

import fr.shyrogan.kall.Subscription
import fr.shyrogan.kall.explorer.SubscriptionExplorer

/**
 * Explores subscriptions stored inside of a field
 */
@Suppress("unchecked_cast")
object FieldExplorer: SubscriptionExplorer {

    /**
     * Looks for each field of type [Subscription] and return their value
     */
    override fun explore(instance: Any): List<Subscription<Any>>
        = instance::class.java.declaredFields
            .filter { Subscription::class.java.isAssignableFrom(it.type) }
            .map {
                it.isAccessible = true
                it[instance] as Subscription<Any>
            }

}