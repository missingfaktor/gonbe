package in.missingfaktor.gonbe;

public class Pokemon {
  private Integer rank;
  private String name;
  private double power;

  private static int totalPokemonCount = 150;

  public static int totalPokemonCount() {
    return totalPokemonCount;
  }

  public static boolean isValidPokemonRank(int n) {
    return 1 <= n && n <= totalPokemonCount;
  }

  public static String getMotto() {
    return "gotta catch them all!";
  }

  public Pokemon(String name, double power, Integer rank) {
    this.name = name;
    this.power = power;
    this.rank = rank;
  }

  public String getName() {
    return name;
  }

  public Pokemon battle(Pokemon that) {
    if (this.power < that.power) {
      return that;
    } else {
      return this;
    }
  }

  @Override
  public String toString() {
    return "Pokemon{" +
      "rank=" + rank +
      ", name='" + name + '\'' +
      ", power=" + power +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Pokemon pokemon = (Pokemon) o;

    if (Double.compare(pokemon.power, power) != 0) {
      return false;
    }
    if (name != null ? !name.equals(pokemon.name) : pokemon.name != null) {
      return false;
    }
    if (rank != null ? !rank.equals(pokemon.rank) : pokemon.rank != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = rank != null ? rank.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    temp = power != +0.0d ? Double.doubleToLongBits(power) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public Integer getRank() {
    return rank;
  }
}
