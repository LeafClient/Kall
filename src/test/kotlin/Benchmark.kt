import fr.shyrogan.kall.EventBus
import fr.shyrogan.kall.Receiver
import fr.shyrogan.kall.Subscription
import fr.shyrogan.kall.subscription
import kotlin.system.measureNanoTime

fun main() {
    println("REGISTRATION: Took ${Benchmark.runRegistration() * 1.0e-6}ms!")
    println("INVOKE: Took ${Benchmark.runInvoke() * 1.0e-6}ms!")
}

object Benchmark {

    private val bus = EventBus()

    init {
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
        for(i in 0..1_000_000) {
            bus.dispatch("")
        }
    }

    class StringReceiver: Receiver {
        override val subscriptions = mutableListOf<Subscription<*>>()

        private val onString = subscription<String> {}
    }

}