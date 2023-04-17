package hcmute.puzzle.services.impl;

import hcmute.puzzle.exception.CustomException;
import hcmute.puzzle.model.ModelQuery;
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

  public static String TYPE_MATCH_TEXT = "TEXT";
  public static String TYPE_MATCH_NUMBER = "NUMBER";

  private static String checkTypeList(List list) {
    if (list != null && !list.isEmpty()) {
      if (list.get(0) instanceof Integer || list.get(0) instanceof Long) {
        return TYPE_MATCH_NUMBER;
      } else if (list.get(0) instanceof String) {
        return TYPE_MATCH_TEXT;
      }
    }
    return null;
  }

  private String containmentCondition(String tableName, String fieldName, String text) {
    String filter = " " + tableName + "." + fieldName + " LIKE '%" + text + "%' ";

    return filter;
  }

  private String equalCondition(String tableName, String fieldName, Object object) {
    String filter = " " + tableName + "." + fieldName + " = " + String.valueOf(object) + " ";
    return filter;
  }

  private String buildCondition(String tableName, String fieldName, ModelQuery modelQuery) {
    if (modelQuery.getQueryType().equals(ModelQuery.TYPE_QUERY_LIKE)) {
      if (modelQuery.getAttributeType().equals(ModelQuery.TYPE_ATTRIBUTE_STRING)) {
        return containmentCondition(
            tableName, fieldName, String.valueOf(modelQuery.getCompareValue()));
      }
    } else if (modelQuery.getQueryType().equals(ModelQuery.TYPE_QUERY_EQUAL)) {
      if (modelQuery.getAttributeType().equals(ModelQuery.TYPE_ATTRIBUTE_NUMBER)
          || modelQuery.getAttributeType().equals(ModelQuery.TYPE_ATTRIBUTE_BOOLEAN)) {
        return equalCondition(tableName, fieldName, modelQuery.getCompareValue());
      }
    }
    throw new CustomException("ErrorDefine filter job post");
  }

  private String filterContain(String tableName, String fieldName, List<ModelQuery> modelQueries) {
    if (modelQueries == null || modelQueries.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder(" AND ( ");
    query.append(buildCondition(tableName, fieldName, modelQueries.get(0)));

    if (modelQueries.size() > 1) {
      for (int i = 1; i < modelQueries.size(); i++) {
        query.append(" OR " + buildCondition(tableName, fieldName, modelQueries.get(i)));
      }
    }

    query.append(")");
    return query.toString();
  }

  private String filterEqualNumber(String tableName, String fieldName, List<Long> numbers) {
    if (numbers == null || numbers.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder(" AND ( ");
    query.append(equalCondition(tableName, fieldName, numbers.get(0)));

    if (numbers.size() > 1) {
      for (int i = 1; i < numbers.size(); i++) {
        query.append(" OR " + equalCondition(tableName, fieldName, numbers.get(i)));
      }
    }

    query.append(")");
    return query.toString();
  }

  // Filter in one table with key of Other
  private String filterContainOtherOneTable(
      String tableName, List<String> fieldNames, List<ModelQuery> modelQueries) {
    if (modelQueries == null
        || modelQueries.isEmpty()
        || fieldNames == null
        || fieldNames.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder("");
    for (int i = 0; i < fieldNames.size(); i++) {
      // first field not " OR "
      if (i == 0) {
        query.append(buildCondition(tableName, fieldNames.get(i), modelQueries.get(0)));
      } else {
        query.append(" OR " + buildCondition(tableName, fieldNames.get(i), modelQueries.get(0)));
      }

      if (modelQueries.size() > 1) {
        for (int j = 1; j < modelQueries.size(); j++) {
          query.append(" OR " + buildCondition(tableName, fieldNames.get(i), modelQueries.get(j)));
        }
      }
    }
    return query.toString();
  }

  // Filter in multi table with key of Other
  private String filterContainOther(
      List<TableQuery> tableQueryList, List<ModelQuery> modelQueries) {
    if (tableQueryList == null
        || tableQueryList.isEmpty()
        || modelQueries == null
        || modelQueries.isEmpty()) {
      return "";
    }

    StringBuilder query = new StringBuilder(" AND ( ");
    // first field not " OR "
    query.append(
        filterContainOtherOneTable(
            tableQueryList.get(0).getName(), tableQueryList.get(0).getFieldQuery(), modelQueries));

    for (int i = 1; i < tableQueryList.size(); i++) {
      query.append(
          " OR "
              + filterContainOtherOneTable(
                  tableQueryList.get(i).getName(),
                  tableQueryList.get(i).getFieldQuery(),
                  modelQueries));
    }

    query.append(" ) ");

    return query.toString();
  }

  private String filterBetweenValue(
      String objectAlias, String fieldName, ModelQuery min, ModelQuery max) {
    String filter = "";

    if (min != null) {
      filter =
          filter
              + " AND "
              + objectAlias
              + "."
              + fieldName
              + " >= "
              + String.valueOf(min.getCompareValue())
              + " ";
    }

    if (max != null) {
      if (max.getAttributeType().equals(ModelQuery.TYPE_ATTRIBUTE_DATE)) {

      } else {
        filter =
            filter
                + " AND "
                + objectAlias
                + "."
                + fieldName
                + " <= "
                + String.valueOf(max.getCompareValue())
                + " ";
      }
    }
    return filter;
  }

  public String deleteLastSubString(String str, String lastSubString) {
    if (lastSubString != null && !lastSubString.isEmpty()) {
      int numOfLastCharacter = lastSubString.length();
      if (numOfLastCharacter > str.length()) {
        throw new CustomException("num of last character is greater string length");
      }

      if (str != null && str.length() > 0 && str.endsWith(lastSubString)) {

        str = str.substring(0, str.length() - numOfLastCharacter);
      }
    }
    return str;
  }

  public String selectInListValueSpecialAttribute(
      String objectAlias, String fieldName, List<ModelQuery> valueOfSpecialAttribute) {
    String query = "";
    if (fieldName != null && !fieldName.isEmpty()) {
      query = " AND " + objectAlias + "." + fieldName + " IN (";
      if (!valueOfSpecialAttribute.isEmpty()) {
        String divisionSymbol = ", ";
        for (ModelQuery value : valueOfSpecialAttribute) {
          query = query + String.valueOf(value.getCompareValue()) + divisionSymbol;
        }
        query = deleteLastSubString(query, divisionSymbol);
      } else {
        query = query + " -1 ";
      }

      query = query + ") ";
    }
    return query;
  }

  @Override
  public List filterObject(
      String objectName,
      // List<Long> manufacturer,
      List<SearchBetween> searchBetweens,
      Map<String, List<ModelQuery>> fieldSearchValue,
      Map<String, List<ModelQuery>> valueSpecialAttributes,
      List<String> commonFieldSearch,
      List<ModelQuery> valueCommonFieldSearch,
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

    if (fieldSearchValue != null) {
      fieldSearchValue.forEach(
          (key, value) -> {
            strQuery.append(filterContain(objectAlias, key, value));
          });
    }
    if (valueSpecialAttributes != null) {
      valueSpecialAttributes.forEach(
          (key, value) -> {
            strQuery.append(selectInListValueSpecialAttribute(objectAlias, key, value));
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
