package hcmute.puzzle.utils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.EmptyInterceptor;

// https://hibernate.atlassian.net/browse/HHH-465?page=..
// https://stackoverflow.com/questions/3683174/hibernate-order-by-with-nulls-last
public class CustomNullsFirstInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -3156853534261313031L;

    private static final String ORDER_BY_TOKEN = "order by";

    public String onPrepareStatement(String sql) {

        int orderByStart = sql.toLowerCase().indexOf(ORDER_BY_TOKEN);
        if (orderByStart == -1) {
            return super.onPrepareStatement(sql);
        }
        orderByStart += ORDER_BY_TOKEN.length() + 1;
        int orderByEnd = sql.indexOf(")", orderByStart);
        if (orderByEnd == -1) {
            orderByEnd = sql.indexOf(" UNION ", orderByStart);
            if (orderByEnd == -1) {
                orderByEnd = sql.length();
            }
        }
        String orderByContent = sql.substring(orderByStart, orderByEnd);
        String[] orderByNames = orderByContent.split("\\,");
        for (int i=0; i<orderByNames.length; i++) {
            if (orderByNames[i].trim().length() > 0) {
//                if (orderByNames[i].trim().toLowerCase().endsWith("desc")) {
//                    orderByNames[i] += " NULLS LAST";
//                } else {
//                    orderByNames[i] += " NULLS FIRST";
//                }
                orderByNames[i] += " NULLS LAST";
            }
        }
        orderByContent = StringUtils.join(orderByNames, ",");
        sql = sql.substring(0, orderByStart) + orderByContent + sql.substring(orderByEnd);
        return super.onPrepareStatement(sql);
    }

}