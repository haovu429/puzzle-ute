package hcmute.puzzle.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ResponseTurnover {
  private long idStore;
  private String province;
  private String district;
  private String ward;
  private long total;

  public ResponseTurnover(long idStore, String province, String district, String ward, long total) {
    this.idStore = idStore;
    this.province = province;
    this.district = district;
    this.ward = ward;
    this.total = total;
  }

  public ResponseTurnover() {}
}
