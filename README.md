== Background

The recent releases of Java 8u201/8u202 and 11.0.2 include a fix for a 0-day
vulnerability that could occur while running a class static initializer.
The fix included in these releases prevents JVM optimization during the
static initializer (before the class is initialized). Doing significant
work in the static initializer can see dramatic reduction in performance
from the previous versions 8u192 / 11.0.1.

Clojure supports a well-known file user.clj that is loaded during runtime
initialization (in the clojure.lang.RT class). This is sometimes used to 
load tools during development. Currently user.clj is loaded in the scope
of RT's static inititializer, so any Clojure code run in user.clj is
significantly slower.

One common pattern popularized by Stuart Sierra is the "reloaded" pattern
which will load and initialize the entire server from the user.clj.
Users of this pattern are likely to see dramatic startup time performance
issues with these Java releases.

Links:

* https://groups.google.com/d/msg/mechanical-sympathy/lflljWsKw0M/ubROHyvTDAAJ
* https://bugs.openjdk.java.net/browse/JDK-8215634
* http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded

== Repro

This repo includes a simple case reproducing the issue. foo.clj measures
the time to do some meaningless busy work. You can run this as a
script using `clj`:

```
$ clj foo.clj
```

Or you can run it by loading it in user.clj by adding the `:user` alias:

```
$ clj -A:user
```

The timings were collected on a 2018 Macbook Pro, but this issue is not
OS or architecture dependent.

| JDK | `clj foo.clj` | `clj -A:user` |
| --- | ------------- | ------------- |
| 1.8.0_192 | 37.325987 ms | 40.047081 ms |
| 1.8.0_201 | 32.943197 ms | 577.869153 ms |
| 11 | 39.908689 ms | 58.462183 ms |
| 11.0.2 | 40.357764 ms | 864.505354 ms |

