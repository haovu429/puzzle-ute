package hcmute.puzzle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JobPostDTO implements Serializable {
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
  private long minBudget;
  private long maxBudget;

  //@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  //@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date dueTime;
  private String workStatus;
  private boolean blind;
  private boolean deaf;
  private boolean communicationDis;
  private boolean handDis;
  private boolean labor;
  private String skills;
  private String positions;
  private long views = 0;
  private boolean isActive;
  private boolean isDeleted;
  // private List<Long> applicationIds = new ArrayList<>();
  // private List<Long> savedCandidateIds = new ArrayList<>();
  // private List<Long> skillIds = new ArrayList<>();
  // private List<Long> companyIds = new ArrayList<>();

  private long createdEmployerId;
}
