package in.missingfaktor.gonbe;

import java.lang.reflect.Member;

import static in.missingfaktor.gonbe.Utils.isStatic;

public final class ObjectMirror extends Mirror<Object> {
  ObjectMirror(Object reflectee) {
    super(reflectee);
  }

  @Override
  protected Class<?> type() {
    return reflectee.getClass();
  }

  @Override
  protected boolean isValid(Member member) {
    return !isStatic(member);
  }

  @Override
  protected Object receiver() {
    return reflectee;
  }
}