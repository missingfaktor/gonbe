package in.missingfaktor.gonbe;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassMirrorTest extends TestCase {
  @Test
  public void testSimpleConstruction() {
    Assert.assertEquals(Mirror.forType(Foo.class).create(4, "hello"), new Foo(4, "hello"));
    Assert.assertEquals(Mirror.forType(Foo.class).create(), new Foo());
  }

  @Test
  public void testBestMatchConstruction() {
    Assert.assertEquals(Mirror.forType(Pokemon.class).create("Pikachu", 90.27, 25), new Pokemon("Pikachu", 90.27, 25));
  }

  @Test
  public void testSetAndGet() {
    ClassMirror p = Mirror.forType(Pokemon.class);
    Assert.assertEquals(p.get("totalPokemonCount"), Pokemon.totalPokemonCount());
    p.setM("totalPokemonCount", 250);
    Assert.assertEquals(p.get("totalPokemonCount"), Pokemon.totalPokemonCount());
  }

  @Test
  public void testField() {
    ClassMirror p = Mirror.forType(Pokemon.class);
    Mirror.Field f = p.field("totalPokemonCount");
    Assert.assertEquals(f.get(), Pokemon.totalPokemonCount());
    f.set(250);
    Assert.assertEquals(f.get(), Pokemon.totalPokemonCount());
  }

  @Test
  public void testCall() {
    ClassMirror p = Mirror.forType(Pokemon.class);
    Boolean t = p.call("isValidPokemonRank", 391);
    Assert.assertEquals(t.booleanValue(), false);
    String s = p.callM("getMotto").call("substring", 0, 4);
    Assert.assertEquals(Pokemon.getMotto().substring(0, 4), s);
  }

  @Test
  public void testMethod() {
    ClassMirror p = Mirror.forType(Pokemon.class);
    ClassMirror.Method m = p.method("getMotto");
    Assert.assertEquals(m.call(), Pokemon.getMotto());
  }

  @Test
  public void testFields() {
    Map<String, ClassMirror.Field> fields = Mirror.forType(Pokemon.class).fields();
    Map<String, Object> values = new LinkedHashMap<String, Object>();
    for (Map.Entry<String, ClassMirror.Field> field : fields.entrySet()) {
      values.put(field.getKey(), field.getValue().get());
    }
    Assert.assertEquals(values.get("totalPokemonCount"), 250);
  }
}
