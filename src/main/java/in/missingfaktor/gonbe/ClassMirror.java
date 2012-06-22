package in.missingfaktor.gonbe;

import fj.F;
import fj.P1;
import fj.data.Array;
import fj.data.Option;

import java.lang.reflect.Member;
import java.util.Arrays;

import static fj.data.Array.array;
import static in.missingfaktor.gonbe.Utils.*;

public final class ClassMirror extends Mirror<Class<?>> {
  ClassMirror(Class<?> reflectee) {
    super(reflectee);
  }

  @Override
  protected Class<?> type() {
    return reflectee;
  }

  @Override
  protected boolean isValid(Member member) {
    return isStatic(member);
  }

  @Override
  protected Object receiver() {
    return null;
  }

  public Constructor constructor() {
    return new Constructor();
  }

  public Object create(Object... args) {
    return constructor().create(args);
  }

  public final class Constructor {
    public Object create(Object... args) {
      Class<?>[] classes = classes(args);
      try {
        java.lang.reflect.Constructor<?> c = findConstructorMatching(classes).valueE("Matching constructor not found.");
        breakChains(c);
        return c.newInstance(args);
      } catch (Exception e) {
        throw new MirroringException(e);
      }
    }

    private Option<java.lang.reflect.Constructor<?>> findConstructorMatching(final Class<?>[] classes) {
      final Array<java.lang.reflect.Constructor<?>> cs = array(type().getDeclaredConstructors());
      Array<Class<?>> ks = array(classes);
      Option<java.lang.reflect.Constructor<?>> exactMatch = cs.find(new F<java.lang.reflect.Constructor<?>, Boolean>() {
        @Override
        public Boolean f(java.lang.reflect.Constructor<?> constructor) {
          return Arrays.equals(constructor.getParameterTypes(), classes);
        }
      });
      P1<Option<java.lang.reflect.Constructor<?>>> bestMatch = new P1<Option<java.lang.reflect.Constructor<?>>>() {
        @Override
        public Option<java.lang.reflect.Constructor<?>> _1() {
          return cs.find(new F<java.lang.reflect.Constructor<?>, Boolean>() {
            @Override
            public Boolean f(java.lang.reflect.Constructor<?> constructor) {
              return areCompatible(constructor.getParameterTypes(), classes);
            }
          });
        }
      };
      return exactMatch.orElse(bestMatch);
    }
  }
}
