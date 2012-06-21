gonbe
=====

A terse reflection API for Java
--------------------------------
gonbe is a simple wrapper for the java.lang.reflect package.

This project is similar to [jOOR](https://github.com/jOOQ/jOOR), but with the following key differences:
* `ObjectMirror` and `ClassMirror` are represented with separate types, whereas jOOR conflates the two (under a name `Reflect`).
* The `get`, `set`, and `call` methods on `Mirror` have their m-suffixed counterparts which wrap the output in `Mirror`. e.g. `Mirror.on("hello").call("charAt", 0)` returns `'h'` whereas `Mirror.on("hello").callM("charAt", 0)` returns `Mirror.on('h')`.
* The implementation makes extensive use of [functionaljava](http://functionaljava.org/), though that's irrelevant from the user point of view.

Example
--------

    // With java.lang.reflect:
    PrivateObject privateObject = new PrivateObject("The Private Value");
    Class<?>[] paramTypes = { int.class, String.class };
    Object[] args = { 3, "Hello" };
    Method privateStringMethod = PrivateObject.class.getDeclaredMethod("getPrivateString", paramTypes);
    privateStringMethod.setAccessible(true);
    String returnValue = (String) privateStringMethod.invoke(privateObject, args);

    // With gonbe:
    PrivateObject privateObject = new PrivateObject("The Private Value");
    String returnValue = Mirror.on(privateObject).call("getPrivateString", 3, "hello");

