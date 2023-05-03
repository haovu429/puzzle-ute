package hcmute.puzzle.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelQuery {

    public static String TYPE_ATTRIBUTE_NUMBER = "TYPE_ATTRIBUTE_NUMBER";
    public static String TYPE_ATTRIBUTE_STRING = "TYPE_ATTRIBUTE_STRING";
    public static String TYPE_ATTRIBUTE_DATE = "TYPE_ATTRIBUTE_DATE";
    public static String TYPE_ATTRIBUTE_BOOLEAN = "TYPE_ATTRIBUTE_BOOLEAN";

    public static String TYPE_QUERY_IN = "TYPE_QUERY_IN";

    public static String TYPE_QUERY_EQUAL = "TYPE_QUERY_EQUAL";
    public static String TYPE_QUERY_LESS = "TYPE_QUERY_LESS";
    public static String TYPE_QUERY_GREATER = "TYPE_QUERY_GREATER";
    public static String TYPE_QUERY_LIKE = "TYPE_QUERY_LIKE";

    String attributeName;
    String queryType;
    String attributeType;
    Object compareValue;

    public ModelQuery(String queryType, String attributeType, Object compareValue) {
        this.queryType = queryType;
        this.attributeType = attributeType;
        this.compareValue = compareValue;
    }
}
