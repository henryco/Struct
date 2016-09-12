package parser.drivers;

import parser.dataType.DataType;
import parser.dataType.JavaType;
import parser.dataType.StructType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Henry on 12/09/16.
 */
public class Drivers {

    public static final InputDriver input = new InputDriver();
    private static final Drivers thisDriver = new Drivers();


    public static Drivers printDataFile(List<String[]>[] arr) {
        Arrays.stream(arr).forEach(Drivers::printData);
        return thisDriver;
    }
    private static Drivers printData(List<String[]> data) {
        System.out.println(" ");
        data.forEach(l -> {
            for (String line : l)
                System.out.print("<"+line+"> ");
            if (l.length > 0)
                System.out.println("");
        });
        return thisDriver;
    }
    public static DataType getDataTypeFromHeader(List<String[]> headerList) {
        for (String[] aHeaderList : headerList)
            if (aHeaderList[0].contains("type"))
                if (aHeaderList[1].equalsIgnoreCase("java")) return new JavaType();
        return new StructType();
    }

}
