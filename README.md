# How Java thread locals work

## Background boring personal story

We had a problem with ThreadLocals in my working environment (Spring boot web app). The team was discussing the reasons behind the problem, so we decided to ensure our knowledge of how the ThreadLocals work in Java is correct... because there are differences between different programming languages

## ThreadLocals (Non inheritable) ones

When we call to `.set` in the object, it will put the value in the ThreadLocal, this value will **only* be available in this thread and NOT in any of its child. Accessing the value from a child will return a null

## InheritableThreadLocals

Again, it works the same as regular ThreadLocals, with the only difference that when the current thread has set a value X, and then creates and starts a new child thread, from the moment we invoke the `.start()` method, the current value will be copied to the child thread, as there is not referencing, changing the value from the child, doesn't affect the parent, nor changing from the parent affects the child

## Testing if object references are copied and not "cloned" is out of scope

This scenario has not been tested, and can't ensure if the behavior is, it would just copy the reference to the object, or the entire object. We don't use objects as thread locals, but PRs are welcome :hugging:

Example of not tested behavior

```java
@Data
@AllArgsConstructor
class Some {
    private int foo;
    private String bar;
}
```
...

```java

var instance = new Some(4, "bar");
ThreadLocal<Some> inheritable = new InheritableThreadLocal<>();
inheritable.set(instance);
// Create... and spawn child thread that mutates the object changing foo prop
assertThat(instance.getFoo()).isEqualTo(4); // True???, ... false maybe??

```

## Reactor/ Spring Async/Spring Batch/ThreadPoolExecutor and other thread polls

Due to the way thread polls work it is **impossible** to use the ThreadLocal inside it... because as shown in the tests changing the value from the childrens won't affect the parent.... and what parent???.... when you use a thread poll those are spawned on demand, and may or may not be spawned by the child currently requesting it... Some of those technologies provides ways to ensure information is passed, for example "ContextAwareThreads"

