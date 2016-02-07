package griffio.domain;

import com.google.common.base.Objects;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryProjection;

import java.math.BigDecimal;

@QueryEntity
public final class Employee {

  private Long id;
  private int age;
  private BigDecimal bonus;
  private int retirementAge;

  @QueryProjection
  public Employee(Long id, int age, BigDecimal bonus, int retirementAge) {
    this.id = id;
    this.age = age;
    this.bonus = bonus;
    this.retirementAge = retirementAge;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .add("age", age)
        .add("bonus", bonus)
        .add("retirementAge", retirementAge)
        .toString();
  }

  public int getAge() {
    return age;
  }

  public BigDecimal getBonus() {
    return bonus;
  }

  public int getRetirementAge() {
    return retirementAge;
  }

  public Long getId() {return id;}
}