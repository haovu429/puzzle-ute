package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.entities.Package;
import hcmute.puzzle.infrastructure.entities.Subscription;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.infrastructure.models.response.DataResponse;

import java.util.List;

public interface SubscriptionService {
  DataResponse subscribePackage(
      User user, Package aPackage, Invoice invoice);

  DataResponse subscribePackage(long userId, String packageCode, Invoice invoice);

  void checkSubscribed(long userId, long packId);

  List<Subscription> getCurrentSubscribeByUserIdAndPackId(long userId, long packId);

  DataResponse getCurrentValidSubscriptions(long userId);

  DataResponse getAllSubscriptionsByUserId(long userId);
}
