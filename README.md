<h1 align="center">Kall</h1>
<div align="center">
  <strong>A powerful yet lightweight EventBus for Kotlin</strong>
</div>

## Getting Started

Once you added the library into your project, you'll need to create a new EventBus instance.
If you there's only a single event bus in your project I'd recommend using an object, otherwise
store it into a variable for example:
````kotlin
object MyEventBus: EventBus()
````

Now, you need to declare subscriptions  
First, implement the `Receiver` interface:
````kotlin
class MyClass: Receiver() {

}
````
The inline function ``subscription`` allows us to create a 
subscription with Kotlin:
````kotlin
private val onMessage = subscription<String>(priority = 0, filters = emptyArray()) { message ->
    // Now you can use message!
    println("Received $message !")
}
````
Obviously you don't have to specify a priority of 0 and empty filters but this example
just shows you they are available.

Now we just need to register our class instance to the EventManager using
````kotlin
MyEventBus.register(classInstance)
````
and call our message using
````kotlin
MyEventBus.dispatch("Hewlo GitHub")
````

Which results into:
````
Received Hewlo Github !
````

## Adapting to all situations

Kall is a smart library, it'll automatically adapt depending of the situation
to provide high performance. Here are a few examples:  
- A `Subscription` that has no `Filter` will result into a `NonFilteredSubscription`: An implementation of Subscription that has no operations
associated with filters.
- A `Dispatcher` that has no `Subscription` will result into a no-op `EmptyDispatcher`
- A `Dispatcher` that has one `Subscription` will result into a `SingletonDispatcher` which is made for a single `Subscription`

## Credits

- [N3xuz](https://github.com/feature) for teaching me a lot of little tricks to optimize the library
