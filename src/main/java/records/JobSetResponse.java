package records;

import java.util.List;

public record JobSetResponse(String jobset_size, List<JobToResponse> jobset){
}
