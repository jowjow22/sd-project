package records;

import java.util.List;

public record GetCompanyResponse(String company_size, List<CompanyToResponse> company) {
}
