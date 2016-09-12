package parser.dataType;

import java.util.List;

/**
 * @author Henry on 10/09/16.
 */
public abstract class DataType {

    public static DataType getDataTypeFromHeader(List<String[]> headerList) {
        for (String[] aHeaderList : headerList)
            if (aHeaderList[0].contains("type"))
                if (aHeaderList[1].equalsIgnoreCase("java")) return new JavaType();
        return new StructType();
    }
}
