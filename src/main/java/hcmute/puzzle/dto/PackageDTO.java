package hcmute.puzzle.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PackageDTO {
    private long id;
    private String name;
    private String code;
    private Long price; // giá bán, không lưu vào db
    private long cost; // giá gốc
    private long duration; // tính theo giây
    private String description;
    private String serviceType;
    private Date publicTime;
    private String forUserType;
}
