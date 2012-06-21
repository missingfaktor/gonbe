package in.missingfaktor.gonbe;

import fj.F;
import fj.P2;
import fj.data.Array;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static fj.data.Array.array;

final class Utils {
  private Utils() {
  }

  public static <M extends Member> boolean isStatic(M member) {
    return Modifier.isStatic(member.getModifiers());
  }

  public static <A extends AccessibleObject> A breakChains(A object) {
    if (!object.isAccessible()) {
      object.setAccessible(true);
    }
    return object;
  }

  public static Class<?>[] classes(Object[] objects) {
    Array<Class<?>> classes = array(objects).map(new F<Object, Class<?>>() {
      @Override
      public Class<?> f(Object o) {
        return o.getClass();
      }
    });
    return Array.copyOf(classes.array(), objects.length, Class[].class);
  }

  public static boolean areCompatible(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
    Array<Class<?>> ds = array(declaredTypes);
    Array<Class<?>> as = array(actualTypes);
    return ds.length() == as.length() && ds.zip(as).forall(new F<P2<Class<?>, Class<?>>, Boolean>() {
      @Override
      public Boolean f(P2<Class<?>, Class<?>> t) {
        return normalize(t._1()).isAssignableFrom(normalize(t._2()));
      }
    });
  }

  public static Class<?> normalize(Class<?> klass) {
    Class<?> n = primitiveNormalForms.get(klass);
    return n == null ? klass : n;
  }

  public static final Map<Class<?>, Class<?>> primitiveNormalForms;

  static {
    Map<Class<?>, Class<?>> temp = new HashMap<Class<?>, Class<?>>();
    temp.put(boolean.class, Boolean.class);
    temp.put(char.class, Character.class);
    temp.put(short.class, Short.class);
    temp.put(byte.class, Byte.class);
    temp.put(int.class, Integer.class);
    temp.put(long.class, Long.class);
    temp.put(float.class, Float.class);
    temp.put(double.class, Double.class);
    primitiveNormalForms = Collections.unmodifiableMap(temp);
  }

  public static <K, V> Map<K, V> toMap(Iterable<P2<K, V>> associationList) {
    Map<K, V> map = new HashMap<K, V>();
    for (P2<K, V> pair : associationList) {
      map.put(pair._1(), pair._2());
    }
    return map;
  }
}
