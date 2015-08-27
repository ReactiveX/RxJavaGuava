RxJavaGuava
===========
A simple utility class for interop between RxJava and Guava's `ListenableFuture`. It also features a static factory `GuavaTranformers` class which transforms an `Observable<T>` into the following Guava collections. 

    Observable<ImmutableList<T>>
    
    Observable<Immutable<Set<T>>
    
    Observable<ImmutableMap<K,T>>
    
    Observable<ImmutableListMultimap<K,V>>

