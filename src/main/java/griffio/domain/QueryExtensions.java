package griffio.domain;

import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.types.dsl.BooleanExpression;
/**
 * isSalaryRelevant is added to the QSalaryDetail generated class
 */
public final class QueryExtensions {

  @QueryDelegate(SalaryDetail.class)
  public static BooleanExpression isSalaryRelevant(QSalaryDetail detail) {
    return detail.salaryName.notEqualsIgnoreCase("other");
  }
}
