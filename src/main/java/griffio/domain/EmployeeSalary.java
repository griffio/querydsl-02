package griffio.domain;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.annotations.QueryEntity;

import java.util.List;

@QueryEntity
public final class EmployeeSalary {

  List<SalaryDetail> salaryDetails;

  public EmployeeSalary(List<SalaryDetail> salaryDetails) {
    this.salaryDetails = ImmutableList.copyOf(salaryDetails);
  }

  public List<SalaryDetail> getSalaryDetails() {
    return salaryDetails;
  }
}
