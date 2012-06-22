package in.missingfaktor.gonbe;

import fj.Effect;
import fj.F;
import fj.P1;
import fj.P2;
import fj.data.Array;
import fj.data.Option;

import java.lang.reflect.Member;
import java.util.Map;
import java.util.NoSuchElementException;

import static fj.P.p;
import static fj.data.Array.array;
import static in.missingfaktor.gonbe.Utils.*;

public abstract class Mirror<A> {
  protected A reflectee;

  Mirror(A reflectee) {
    this.reflectee = reflectee;
  }

  public static ObjectMirror on(Object reflectee) {
    return new ObjectMirror(reflectee);
  }

  public static ClassMirror forType(Class<?> klass) {
    return new ClassMirror(klass);
  }

  public static ClassMirror forType(String className) {
    try {
      return new ClassMirror(Class.forName(className));
    } catch (Exception e) {
      throw new MirroringException(e);
    }
  }

  // Class<?> object which will be introspected.
  protected abstract Class<?> type();

  protected abstract boolean isValid(Member member);

  // Object on which methods, field-accesses will be invoked.
  protected abstract Object receiver();

  // First class function corresponding to isValid(Member) method.
  public <M extends Member> F<M, Boolean> isValid() {
    return new F<M, Boolean>() {
      @Override
      public Boolean f(M m) {
        return isValid(m);
      }
    };
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " on " + reflectee;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Mirror mirror = (Mirror) o;

    if (reflectee != null ? !reflectee.equals(mirror.reflectee) : mirror.reflectee != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return reflectee != null ? reflectee.hashCode() : 0;
  }

  public Map<String, Field> fields() {
    Array<P2<String, Field>> associationList = array(type().getDeclaredFields()).
      filter(this.<java.lang.reflect.Field>isValid()).
      map(new F<java.lang.reflect.Field, P2<String, Field>>() {
        @Override
        public P2<String, Field> f(java.lang.reflect.Field field) {
          breakChains(field);
          return p(field.getName(), new Field(field));
        }
      });
    return toMap(associationList);
  }

  // Unsafe cast.
  public <A> A reflectee() {
    return (A) reflectee;
  }

  public Field field(String name) {
    return new Field(name);
  }

  public Object get(String name) {
    return field(name).get();
  }

  public Object set(String name, Object value) {
    return field(name).set(value);
  }

  public ObjectMirror getM(String name) {
    return field(name).getM();
  }

  public ObjectMirror setM(String name, Object value) {
    return field(name).setM(value);
  }

  public Method method(String name) {
    return new Method(name);
  }

  public <A> A call(String name, Object... args) {
    return method(name).<A>call(args);
  }

  public ObjectMirror callM(String name, Object... args) {
    return method(name).callM(args);
  }

  public final class Field {
    private java.lang.reflect.Field field;

    public Field(String fieldName) {
      final java.lang.reflect.Field field;
      try {
        field = type().getDeclaredField(fieldName);
        if (!isValid(field)) {
          throw new NoSuchElementException("No field named " + fieldName + " found.");
        }
        breakChains(field);
        this.field = field;
      } catch (Exception e) {
        throw new MirroringException(e);
      }
    }

    public Field(java.lang.reflect.Field field) {
      this.field = field;
    }

    public <A> A get() {
      try {
        return (A) field.get(receiver());
      } catch (Exception e) {
        throw new MirroringException(e);
      }
    }

    public ObjectMirror getM() {
      return Mirror.on(get());
    }

    public <A> A set(Object newValue) {
      try {
        field.set(receiver(), newValue);
        return (A) receiver();
      } catch (Exception e) {
        throw new MirroringException(e);
      }
    }

    public ObjectMirror setM(Object newValue) {
      return Mirror.on(set(newValue));
    }
  }

  public class Method {
    private String methodName;

    protected Method(String methodName) {
      this.methodName = methodName;
    }

    public <A> A call(Object... args) {
      Class<?>[] classes = classes(args);
      try {
        java.lang.reflect.Method method = findMethodMatching(classes).valueE("Matching method not found.");
        breakChains(method);
        return (A) method.invoke(receiver(), args);
      } catch (Exception e) {
        throw new MirroringException(e);
      }
    }

    private Option<java.lang.reflect.Method> findMethodMatching(final Class<?>[] classes) {
      Option<java.lang.reflect.Method> exactMatch;
      try {
        exactMatch = Option.some(type().getDeclaredMethod(methodName, classes)).
          filter(Mirror.this.<java.lang.reflect.Method>isValid());
      } catch (NoSuchMethodException e) {
        exactMatch = Option.none();
      }
      P1<Option<java.lang.reflect.Method>> bestMatch = new P1<Option<java.lang.reflect.Method>>() {
        @Override
        public Option<java.lang.reflect.Method> _1() {
          return array(type().getDeclaredMethods()).find(new F<java.lang.reflect.Method, Boolean>() {
            @Override
            public Boolean f(java.lang.reflect.Method method) {
              return method.getName().equals(methodName) &&
                isValid(method) &&
                areCompatible(method.getParameterTypes(), classes);
            }
          });
        }
      };
      return exactMatch.orElse(bestMatch);
    }

    public final ObjectMirror callM(Object... args) {
      return Mirror.on(call(args));
    }
  }
}
