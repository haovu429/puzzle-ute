package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.entities.InvoiceEntity;
import hcmute.puzzle.infrastructure.entities.PackageEntity;
import hcmute.puzzle.infrastructure.entities.SubscriptionEntity;
import hcmute.puzzle.infrastructure.entities.UserEntity;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

import java.util.List;

public interface SubscriptionService {
  DataResponse subscribePackage(
      UserEntity user, PackageEntity packageEntity, InvoiceEntity invoiceEntity);

  DataResponse subscribePackage(long userId, String packageCode, InvoiceEntity invoiceEntity);

  void checkSubscribed(long userId, long packId);

  List<SubscriptionEntity> getCurrentSubscribeByUserIdAndPackId(long userId, long packId);

  DataResponse getCurrentValidSubscriptions(long userId);

  DataResponse getAllSubscriptionsByUserId(long userId);
}
