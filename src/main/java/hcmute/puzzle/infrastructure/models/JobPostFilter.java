package hcmute.puzzle.infrastructure.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class JobPostFilter {
    Long minBudget;
    Long maxBudget;
    List<String> experienceYear;
    List<String> titles;
    List<String> employmentTypes;
    List<String> cities;
    List<String> positions;
    List<String> skills;
    List<String> others;
    List<Long> categoryIds;
    List<Long> companyIds;
    int numDayAgo = -1;
    boolean active = true;
    int noOfRecords = 6;
    int pageIndex = 0;
    boolean sortById = true;

}
