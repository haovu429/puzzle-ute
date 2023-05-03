package hcmute.puzzle.infrastructure.models;

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
    int noOfRecords = 15;
    int pageIndex = 0;
    boolean sortById;
}
