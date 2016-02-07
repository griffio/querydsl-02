package griffio.domain;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.group.Group;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.querydsl.collections.CollQueryFactory.from;
import static com.querydsl.core.group.GroupBy.*;

@Test
public class EmployeeTest {

  List<Employee> employees;

  @BeforeTest
  public void setUp() throws Exception {
    employees = ImmutableList.of(
        new Employee(1L, 25, new BigDecimal("1.80"), 80),
        new Employee(1L, 30, new BigDecimal("1.69"), 60),
        new Employee(1L, 35, new BigDecimal("1.74"), 70));
  }
  // SELECT count(*), max(age), min(height), avg(weight) FROM person
  //(3, Optional[35], Optional[1.69], Optional[70.0])

  @Test
  public void aggregate() throws Exception {

    ///Map? (3, Optional[35], Optional[1.69], Optional[70.0])

    List<Group> transform = from(QEmployee.employee, employees)
        .transform(
            groupBy(QEmployee.employee.id)
                .list(max(QEmployee.employee.age), min(QEmployee.employee.bonus), avg(QEmployee.employee.retirementAge)));

    for (Group group : transform) {
      System.out.println("group = " + group);
    }

  }
}