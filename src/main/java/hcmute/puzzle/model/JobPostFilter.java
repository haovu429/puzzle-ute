package hcmute.puzzle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class JobPostFilter {
    long minBudget;
    long maxBudget;
    List<String> experienceYear;
    List<String> titles;
    List<String> employmentTypes;
    List<String> cities;
    List<String> positions;
    List<String> skills;
    List<String> others;
    int noOfRecords = 6;
    int pageIndex = 0;
    boolean sortById = true;

}
