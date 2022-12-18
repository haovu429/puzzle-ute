package hcmute.puzzle.services;

import hcmute.puzzle.entities.InvoiceEntity;
import hcmute.puzzle.entities.PackageEntity;
import hcmute.puzzle.entities.SubscribeEntity;
import hcmute.puzzle.entities.UserEntity;
import hcmute.puzzle.response.DataResponse;

import java.util.List;

public interface SubscribeService {
  DataResponse subscribePackage(
      UserEntity user, PackageEntity packageEntity, InvoiceEntity invoiceEntity);

  DataResponse subscribePackage(long userId, String packageCode, InvoiceEntity invoiceEntity);

  void checkSubscribed(long userId, long packId);

  List<SubscribeEntity> getCurrentSubscribeByUserIdAndPackId(long userId, long packId);

  DataResponse getCurrentSubscribe(long userId);
}
