package in.missingfaktor.gonbe;

import fj.F;
import fj.data.Array;
import fj.data.Option;

import java.lang.reflect.Constructor;
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

  public ConstructorMirror constructor() {
    return new ConstructorMirror();
  }

  public Object create(Object... args) {
    return constructor().create(args);
  }

  public final class ConstructorMirror {
    public Object create(Object... args) {
      if (args == null) {
        args = new Object[0];
      }
      Class<?>[] classes = classes(args);
      try {
        Constructor<?> c = findConstructorMatching(classes).valueE("Matching constructor not found.");
        breakChains(c);
        return c.newInstance(args);
      } catch (Exception e) {
        throw new MirroringException(e);
      }
    }
  }

  private Option<Constructor<?>> findConstructorMatching(final Class<?>[] classes) {
    Array<Constructor<?>> cs = array(type().getDeclaredConstructors());
    Array<Class<?>> ks = array(classes);
    return cs.find(new F<Constructor<?>, Boolean>() {
      @Override
      public Boolean f(Constructor<?> constructor) {
        return Arrays.equals(constructor.getParameterTypes(), classes);
      }
    }).orElse(cs.find(new F<Constructor<?>, Boolean>() {
      @Override
      public Boolean f(Constructor<?> constructor) {
        return areCompatible(constructor.getParameterTypes(), classes);
      }
    }));
  }
}
