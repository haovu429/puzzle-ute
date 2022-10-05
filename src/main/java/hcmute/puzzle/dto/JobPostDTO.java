package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JobPostDTO {
    private long id;
    private String title;
    private String employmentType;
    private String workplaceType;
    private String description;
    private String city;
    private String address;
    private String educationLevel;
    private int experienceYear;
    private int quantity;
    private long budget;
    private Date dueTime;
    private String workStatus;
    private boolean blind;
    private boolean deaf;
    private boolean communicationDis;
    private boolean handDis;
    private boolean labor;

    private List<Long> applicationIds = new ArrayList<>();
    private List<Long> savedCandidateIds = new ArrayList<>();
    private List<Long> skillIds = new ArrayList<>();
    private List<Long> companyIds = new ArrayList<>();

    private long createdEmployerId;
}
