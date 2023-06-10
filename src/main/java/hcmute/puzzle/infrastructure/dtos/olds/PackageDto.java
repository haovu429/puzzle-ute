package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PackageDto {
    private Long id;
    private String name;
    private String code;
    private Long price; // giá bán, không lưu vào db
    private Long cost; // giá gốc
    private Long duration; // tính theo giây
    private Integer numOfJobPost; // só job post mua thêm
    private String description;
    private String serviceType;
    private Date publicTime;
    private String forUserType;
}
