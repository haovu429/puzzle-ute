package hcmute.puzzle.services.Impl;

import hcmute.puzzle.model.SearchBetween;
import hcmute.puzzle.model.TableQuery;
import hcmute.puzzle.services.SearchService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

  @PersistenceContext public EntityManager em;

  private String containmentCondition(String tableName, String fieldName, String text) {
    String filter = " " + tableName + "." + fieldName + " LIKE '%" + text + "%' ";
    return filter;
  }

  private String filterContain(String tableName, String fieldName, List<String> texts) {
    if (texts == null || texts.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder(" AND ( ");
    query.append(containmentCondition(tableName, fieldName, texts.get(0)));

    if (texts.size() > 1) {
      for (int i = 1; i < texts.size(); i++) {
        query.append(" OR " + containmentCondition(tableName, fieldName, texts.get(i)));
      }
    }

    query.append(")");
    return query.toString();
  }

  // Filter in one table with key of Other
  private String filterContainOtherOneTable(
      String tableName, List<String> fieldNames, List<String> texts) {
    if (texts == null || texts.isEmpty() || fieldNames == null || fieldNames.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder("");
    for (int i = 0; i < fieldNames.size(); i++) {
      // first field not " OR "
      if (i == 0) {
        query.append(containmentCondition(tableName, fieldNames.get(i), texts.get(0)));
      } else {
        query.append(" OR " + containmentCondition(tableName, fieldNames.get(i), texts.get(0)));
      }

      if (texts.size() > 1) {
        for (int j = 1; j < texts.size(); j++) {
          query.append(" OR " + containmentCondition(tableName, fieldNames.get(i), texts.get(j)));
        }
      }
    }
    return query.toString();
  }

  // Filter in multi table with key of Other
  private String filterContainOther(List<TableQuery> tableQueryList, List<String> texts) {
    if (tableQueryList == null || tableQueryList.isEmpty() || texts == null || texts.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder(" AND ( ");
    // first field not " OR "
    query.append(
        filterContainOtherOneTable(
            tableQueryList.get(0).getName(), tableQueryList.get(0).getFieldQuery(), texts));

    for (int i = 1; i < tableQueryList.size(); i++) {
      query.append(
          " OR "
              + filterContainOtherOneTable(
                  tableQueryList.get(i).getName(), tableQueryList.get(i).getFieldQuery(), texts));
    }

    query.append(" ) ");

    return query.toString();
  }

  private String filterBetweenValue(String objectAlias, String fieldName, Double min, Double max) {
    String filter = "";

    if (min != null) {
      filter = filter + " AND " + objectAlias + "." + fieldName + " >= " + min + " ";
    }

    if (max != null) {
      filter = filter + " AND " + objectAlias + "." + fieldName + " <= " + max + " ";
    }

    return filter;
  }

  @Override
  public List filterObject(
      String objectName,
      // List<Long> manufacturer,
      List<SearchBetween> searchBetweens,
      Map<String, List<String>> fieldSearch,
      List<String> commonFieldSearch,
      List<String> valueCommonFieldSearch,
      int noOfRecords,
      int pageIndex,
      boolean sortById) {
    String objectAlias = objectName.substring(0, 3).toLowerCase();
    // add condition id > 0 to fix error miss WHERE
    StringBuilder strQuery =
        new StringBuilder(
            "SELECT "
                + objectAlias
                + " FROM "
                + objectName
                + " "
                + objectAlias
                + " WHERE "
                + objectAlias
                + ".id > 0 ");
    // String filterManufacturer = filterManufacturer(manufacturer);
    // strQuery.append(filterManufacturer);

    if (searchBetweens != null) {
      searchBetweens.forEach(
          searchBetween -> {
            if (searchBetween != null) {
              strQuery.append(
                  filterBetweenValue(
                      objectAlias,
                      searchBetween.getFieldSearch(),
                      searchBetween.getMin(),
                      searchBetween.getMax()));
            }
          });
    }

    if (fieldSearch != null) {
      fieldSearch.forEach(
          (key, value) -> {
            strQuery.append(filterContain(objectAlias, key, value));
          });
    }

    //    strQuery.append(filterContain(objectAlias, "screen", screen));
    //    strQuery.append(filterContain(objectAlias, "cpu", cpu));
    //    strQuery.append(filterContain(objectAlias, "ram", ram));
    //    strQuery.append(filterContain(objectAlias, "hardDrive", hardDrive));

    //    List<String> fieldOthersForProduct = new ArrayList<>();
    //    fieldOthersForProduct.add("productName");
    //    fieldOthersForProduct.add("description");
    //    fieldOthersForProduct.add("category");

    if (commonFieldSearch != null
        && valueCommonFieldSearch != null
        && !commonFieldSearch.isEmpty()
        && !valueCommonFieldSearch.isEmpty()) {
      TableQuery objectTb = new TableQuery(objectAlias, commonFieldSearch);

      //    List<String> fieldOthersForLaptop = new ArrayList<>();
      //    fieldOthersForLaptop.add("design");
      //    fieldOthersForLaptop.add("graphicsCard");
      //    TableQuery laptopTb = new TableQuery("l", fieldOthersForLaptop);

      List<TableQuery> tableQueryList = new ArrayList<>();
      tableQueryList.add(objectTb);
      // tableQueryList.add(laptopTb);

      strQuery.append(filterContainOther(tableQueryList, valueCommonFieldSearch));
    }

    if (sortById) {
      strQuery.append("ORDER BY " + objectAlias + ".id ASC NULLS LAST");
    }

    List result =
        em.createQuery(strQuery.toString())
            .setMaxResults(noOfRecords)
            .setFirstResult(pageIndex * noOfRecords)
            .getResultList();

    return result;
  }
}
