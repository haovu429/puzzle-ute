package hcmute.puzzle.services.impl;

import hcmute.puzzle.infrastructure.converter.Converter;
import hcmute.puzzle.infrastructure.entities.Invoice;
import hcmute.puzzle.infrastructure.entities.Package;
import hcmute.puzzle.infrastructure.entities.Subscription;
import hcmute.puzzle.infrastructure.entities.User;
import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.infrastructure.repository.EmployerRepository;
import hcmute.puzzle.infrastructure.repository.PackageRepository;
import hcmute.puzzle.infrastructure.repository.SubscribeRepository;
import hcmute.puzzle.infrastructure.repository.UserRepository;
import hcmute.puzzle.infrastructure.models.response.DataResponse;
import hcmute.puzzle.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

  @Autowired EmployerRepository employerRepository;

  @Autowired UserRepository userRepository;

  @Autowired PackageRepository packageRepository;

  @Autowired SubscribeRepository subscribeRepository;

  @Autowired Converter converter;

  @PersistenceContext public EntityManager em;

  public DataResponse subscribePackage(
      User user, Package aPackage, Invoice invoice) {

    checkSubscribed(user.getId(), aPackage.getId());
    Date nowTime = new Date();
    Subscription subscribe = new Subscription();
    subscribe.setStartTime(nowTime);
    subscribe.setExpirationTime(new Date(nowTime.getTime() + aPackage.getDuration()));
    subscribe.setAPackage(aPackage);
    subscribe.setRegUser(user);

    // Hoá đơn đã lưu trong db rồi thì mới set
    // Lưu hai chiều vì để cascade là DETACH
    subscribe.setPaymentTransactionCode(invoice.getTransactionCode());

    subscribeRepository.save(subscribe);

    return null;
  }

  public DataResponse subscribePackage(
      long userId, String packageCode, Invoice invoice) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new CustomException("Employer isn't exists");
    }

    Optional<Package> packageEntity = packageRepository.findByCode(packageCode.toLowerCase());
    if (packageEntity.isEmpty()) {
      throw new CustomException("Employer isn't exists");
    }

    return subscribePackage(user.get(), packageEntity.get(), invoice);
  }

  public void checkSubscribed(long userId, long packId) {
    List<Subscription> subscribes =
            getCurrentSubscribeByUserIdAndPackId(userId, packId);
    if (subscribes != null && !subscribes.isEmpty()) {
      throw new CustomException(
              "you have already subscribed to this package, and it is valid until "
                      + subscribes.get(0).getExpirationTime());
      // subscrie đầu tiên sẽ có thời gian lâu nhất
    }
  }

  // Lấy đối tượng đăng ký dịch vụ chỉ định đang có hiện tại (chưa hết hạn)
  public List<Subscription> getCurrentSubscribeByUserIdAndPackId(long userId, long packId) {
    List<Subscription> subscribeEntities = new ArrayList<>();
    try {
      StringBuilder sql =
          new StringBuilder(
              "SELECT sub FROM SubscribeEntity sub, PackageEntity pack, UserEntity u WHERE sub.packageEntity.id = pack.id "
                  + "AND sub.regUser.id = u.id AND u.id=:userId AND pack.id =:packId AND sub.expirationTime > :nowTime ORDER BY sub.expirationTime DESC NULLS LAST");
      // Join example with addEntity and addJoin

      // Cần sort expirationTime null phải ở sau
      // CustomNullsFirstInterceptor customSql = new CustomNullsFirstInterceptor();

      subscribeEntities =
          em.createQuery(sql.toString())
              .setParameter("userId", userId)
              .setParameter("packId", packId)
              .setParameter("nowTime", new Date())
              .getResultList();

      // Check order by expirationTime
      //      for (SubscribeEntity subscribe : subscribeEntities) {
      //        System.out.println(subscribe.getExpirationTime());
      //      }

    } catch (NoResultException e) {
      System.out.println("Chua co ban ghi nao!");
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return subscribeEntities;
  }

  // Lấy các đối tượng đăng ký các dịch vụ đang có hiện tại (chưa hết hạn)
  public DataResponse getCurrentValidSubscriptions(long userId) {
    String sql =
        "SELECT sub, pack FROM Subscription sub, Package pack, User u WHERE sub.packageEntity.id = pack.id"
            + " AND sub.regUser.id = u.id AND u.id=:userId AND sub.expirationTime > :nowTime";
    // Join example with addEntity and addJoin
    List<Object[]> rows =
        em.createQuery(sql)
            .setParameter("userId", userId)
            .setParameter("nowTime", new Date())
            .getResultList();

    List<Map<String, Object>> response = new ArrayList<>();
    for (Object[] row : rows) {
      Map<String, Object> subAndPack = new HashMap<>();
      Subscription subscribe = (Subscription) row[0];
      System.out.println("Application Info::" + subscribe);
      Package aPackage = (Package) row[1];
      System.out.println("Candidate Info::" + aPackage);
      subAndPack.put("subscribe", converter.toDTO(subscribe));
      subAndPack.put("package", converter.toDTO(aPackage));
      response.add(subAndPack);
    }
    return new DataResponse(response);
  }

  public DataResponse getAllSubscriptionsByUserId(long userId) {
    String sql =
            "SELECT sub, pack FROM Subscription sub, Package pack, User u WHERE sub.packageEntity.id = pack.id"
                    + " AND sub.regUser.id = u.id AND u.id=:userId";
    // Join example with addEntity and addJoin
    List<Object[]> rows =
            em.createQuery(sql)
                    .setParameter("userId", userId)
                    .getResultList();
    Date nowTime = new Date();
    List<Map<String, Object>> response = new ArrayList<>();
    for (Object[] row : rows) {
      Map<String, Object> subAndPack = new HashMap<>();
      Subscription subscribe = (Subscription) row[0];
      //System.out.println("subscribe Info::" + subscribe);
      Package aPackage = (Package) row[1];
      //System.out.println("packageEntity Info::" + packageEntity);
      subAndPack.put("subscribe", converter.toDTO(subscribe));
      subAndPack.put("package", converter.toDTO(aPackage));
      if (subscribe.getExpirationTime().getTime() > nowTime.getTime()){
        subAndPack.put("is_expired", false);
      } else {
        subAndPack.put("is_expired", true);
      }

      response.add(subAndPack);
    }
    return new DataResponse(response);
  }
}
