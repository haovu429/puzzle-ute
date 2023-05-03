package hcmute.puzzle.infrastructure.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseProduct {
  private long idStore;
  private String province;
  private String district;
  private String ward;
  private String productCode;
  private String productName;
  private long quantity;

  private long inventory;

  public ResponseProduct() {}

  public ResponseProduct(long id, String code, String name, long quantity) {
    this.idStore = id;
    this.productCode = code;
    this.productName = name;
    this.quantity = quantity;
  }

  public ResponseProduct(
      long idStore,
      String province,
      String district,
      String ward,
      String productCode,
      String productName,
      long quantity,
      long inventory) {
    this.idStore = idStore;
    this.province = province;
    this.district = district;
    this.ward = ward;
    this.productCode = productCode;
    this.productName = productName;
    this.quantity = quantity;
    this.inventory = inventory;
  }
}
