package hcmute.puzzle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CandidateFilter {
    List<String> educationLevels;
    List<String>  skills;
    List<String> positions;
    List<String> services;
    List<String> others;
    int noOfRecords;
    int pageIndex;
    boolean sortById;
}
