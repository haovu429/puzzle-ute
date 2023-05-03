package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.entities.InvoiceEntity;
import hcmute.puzzle.infrastructure.entities.PackageEntity;
import hcmute.puzzle.infrastructure.entities.SubscribeEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

import java.util.List;

public interface SubscribeService {
  DataResponse subscribePackage(
      UserEntity user, PackageEntity packageEntity, InvoiceEntity invoiceEntity);

  DataResponse subscribePackage(long userId, String packageCode, InvoiceEntity invoiceEntity);

  void checkSubscribed(long userId, long packId);

  List<SubscribeEntity> getCurrentSubscribeByUserIdAndPackId(long userId, long packId);

  DataResponse getCurrentValidSubscriptions(long userId);

  DataResponse getAllSubscriptionsByUserId(long userId);
}
