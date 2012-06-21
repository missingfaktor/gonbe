package in.missingfaktor.gonbe;

final class Foo {
  private int bar;
  private String baz;

  public Foo(int bar, String baz) {
    this.bar = bar;
    this.baz = baz;
  }

  public int getBar() {
    return bar;
  }

  public String getBaz() {
    return baz;
  }

  public Foo() {
    this(1, "Hello");
  }

  public Foo nextFoo(int n) {
    return new Foo(bar + n, this.baz);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Foo foo = (Foo) o;

    if (bar != foo.bar) {
      return false;
    }
    if (baz != null ? !baz.equals(foo.baz) : foo.baz != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = bar;
    result = 31 * result + (baz != null ? baz.hashCode() : 0);
    return result;
  }
}
