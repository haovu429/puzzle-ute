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
    int experienceYear;
    List<String> titles;
    List<String> employmentTypes;
    List<String> cities;
    List<String> positions;
    List<String> skills;
    List<String> others;
    int noOfRecords = 15;
    int pageIndex = 0;
    boolean sortById;

}
