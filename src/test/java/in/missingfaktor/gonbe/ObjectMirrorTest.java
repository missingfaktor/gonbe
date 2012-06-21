package in.missingfaktor.gonbe;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;

public class ObjectMirrorTest extends TestCase {

  @Test
  public void testFieldStuff() {
    final Pokemon bulbasaur = new Pokemon("bulbasaur", 124.3, 1);
    final ObjectMirror m = Mirror.on(bulbasaur);
    ObjectMirror.Field field = m.field("name");
    Assert.assertEquals(field.get(), bulbasaur.getName());
    field.set("ivysaur");
    m.set("rank", 2);
    Assert.assertEquals(m.get("rank"), bulbasaur.getRank());
    Assert.assertEquals(field.get(), "ivysaur");
  }

  @Test
  public void testMethodStuff() {
    final Foo foo = new Foo(5, "ko");
    Foo foo2 = ObjectMirror.on(foo).callM("nextFoo", 4).callM("nextFoo", 9).reflectee();
    Assert.assertEquals(foo2.getBar(), 18);
    Assert.assertEquals(foo2.getBaz(), "ko");
  }
}
