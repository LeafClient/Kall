import fr.shyrogan.kall.EventBus
import fr.shyrogan.kall.Receiver
import fr.shyrogan.kall.subscription.Subscription
import fr.shyrogan.kall.subscription
import fr.shyrogan.kall.subscription.filter
import kotlin.system.measureNanoTime

fun main() {
    println("REGISTRATION: Took ${Benchmark.runRegistration() * 1.0e-6}ms!")
    println("INVOKE: Took ${Benchmark.runInvoke() * 1.0e-6}ms!")
}

object Benchmark {

    private val bus = EventBus()

    init {
        bus.register(StringReceiver())
        bus.register(StringReceiver())
        bus.register(StringReceiver())
        bus.register(StringReceiver())
    }

    fun runRegistration(): Long = measureNanoTime {
        val receiver = StringReceiver()
        for(i in 0..1_000_000) {
            bus.register(receiver)
            bus.unregister(receiver)
        }
    }

    fun runInvoke(): Long = measureNanoTime {
        for(i in 0 until 1_000_000) {
            bus.dispatch(" aaa aa")
        }
    }

    class StringReceiver: Receiver() {
        private val onString = subscription<Double>(filters = arrayOf(filter<Number> { it.toDouble() > 0 })) {}
    }

}