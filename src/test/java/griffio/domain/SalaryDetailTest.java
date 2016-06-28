package griffio.domain;

import com.google.common.collect.ImmutableList;
import com.querydsl.collections.CollQueryFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.querydsl.collections.CollQueryFactory.from;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.sum;
import static griffio.domain.QEmployeeSalary.employeeSalary;
import static griffio.domain.QSalaryDetail.create;
import static griffio.domain.QSalaryDetail.salaryDetail;

@Test
public class SalaryDetailTest {

  SalaryDetail bigBonus = new SalaryDetail("Bonus", new BigDecimal("25000"));
  SalaryDetail commission = new SalaryDetail("Commission", new BigDecimal("1000"));
  SalaryDetail gross = new SalaryDetail("Gross", new BigDecimal("75000"));
  SalaryDetail smallBonus = new SalaryDetail("Bonus", new BigDecimal("5000"));

  List<SalaryDetail> details;
  List<String> relevantSalaryNames = ImmutableList.of("Bonus", "Commission", "Gross");

  @BeforeTest
  public void setUp() throws Exception {
    details = ImmutableList.of(bigBonus, commission, gross, smallBonus);
  }

  public void big_bonus_salary_details() throws Exception {

    BigDecimal payThreshold = new BigDecimal("20000");
    BooleanExpression isBonusSalary = salaryDetail.salaryName.equalsIgnoreCase("Bonus");
    BooleanExpression isGreaterThanThreshold = salaryDetail.salary.goe(payThreshold);
    BooleanExpression isBonusAboveThreshold = isBonusSalary.and(isGreaterThanThreshold);

    List<SalaryDetail> actual = from(salaryDetail, details).where(isBonusAboveThreshold).fetch();

    assertThat(actual).containsExactly(bigBonus);
  }

  public void relevant_salary_details_for_threshold() throws Exception {

    BooleanBuilder isRelevantSalaryName = new BooleanBuilder();

    for (String salaryName : relevantSalaryNames) {
      isRelevantSalaryName.or(salaryDetail.salaryName.eq(salaryName));
    }
    //mutable boolean builder
    isRelevantSalaryName.and(salaryDetail.salary.gt(new BigDecimal("50000")));

    List<SalaryDetail> actual = from(salaryDetail, details).select(salaryDetail).where(isRelevantSalaryName).fetch();

    assertThat(actual).containsExactly(gross);

  }

  public void sum_relevant_salaries() {

    BigDecimal actual = from(salaryDetail, details).select(salaryDetail.salary.sum()).fetchOne();

    assertThat(actual).isEqualTo(new BigDecimal("106000.00"));
  }

  public void unique_salaries_from_employees_salaries() {

    ImmutableList<EmployeeSalary> employeesSalaries = ImmutableList.of(new EmployeeSalary(details), new EmployeeSalary(details));

    List<String> actual = CollQueryFactory.from(employeeSalary, employeesSalaries)
        .select(salaryDetail.salaryName)
        .innerJoin(employeeSalary.salaryDetails, salaryDetail)
        .where(salaryDetail.isSalaryRelevant())
        .distinct()
        .fetch();

    assertThat(actual).containsExactly("Bonus", "Commission", "Gross");
  }

  public void aggregated_by_salary_name() {

    ImmutableList<SalaryDetail> salaryDetails = ImmutableList.of(smallBonus, commission, bigBonus);

    List<SalaryDetail> actual = from(salaryDetail, salaryDetails)
        .orderBy(salaryDetail.salaryName.asc())
        .transform(groupBy(salaryDetail.salaryName)
            .list(create(salaryDetail.salaryName, sum(salaryDetail.salary))));

    assertThat(actual).containsExactly(smallBonus.add(bigBonus), commission);
  }

  public void sum_relevant_salary() throws Exception {

    ImmutableList<SalaryDetail> salaryDetails = ImmutableList.of(smallBonus, commission, bigBonus);

    BigDecimal thresholdForPayPeriod = new BigDecimal("12500");

    StringExpression caseSalaryName = new CaseBuilder()
        .when(salaryDetail.isSalaryRelevant().and(salaryDetail.salary.goe(thresholdForPayPeriod)))
        .then(salaryDetail.salaryName)
        .otherwise("Not Relevant");

    Map<String, BigDecimal> actual = from(salaryDetail, salaryDetails)
        .transform(groupBy(caseSalaryName).as(sum(salaryDetail.salary)));

    assertThat(actual).containsEntry(bigBonus.getSalaryName(), bigBonus.getSalary());
    assertThat(actual).containsEntry("Not Relevant", smallBonus.getSalary().add(commission.getSalary()));
  }
}