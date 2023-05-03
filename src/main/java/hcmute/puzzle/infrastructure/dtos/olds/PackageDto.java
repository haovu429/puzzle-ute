package hcmute.puzzle.infrastructure.dtos.olds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PackageDto {
    private long id;
    private String name;
    private String code;
    private Long price; // giá bán, không lưu vào db
    private long cost; // giá gốc
    private long duration; // tính theo giây
    private int numOfJobPost; // só job post mua thêm
    private String description;
    private String serviceType;
    private Date publicTime;
    private String forUserType;
}
