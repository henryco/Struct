import parser.drivers.Drivers;
import parser.drivers.InputDriver;

import java.util.Arrays;
import java.util.List;

/**
 * @author Henry on 12/09/16.
 */
public class Struct {

    public static final InputDriver in = Drivers.getInputDriver();
    private static final Struct thisDriver = new Struct();


    public static Struct printDataFile(List<String[]>[] arr) {
        Arrays.stream(arr).forEach(Struct::printData);
        return thisDriver;
    }
    private static Struct printData(List<String[]> data) {
        System.out.println(" ");
        data.forEach(l -> {
            for (String line : l)
                System.out.print("|"+line+"| ");
            if (l.length > 0)
                System.out.println("");
        });
        return thisDriver;
    }


}
