package parser;

import container.Struct;
import parser.dataType.DataType;
import parser.dataType.JavaType;
import parser.dataType.StructType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Henry on 09/09/16.
 */
public class InputDriver {

    private String[] ARRAY_TYPES = new String[]{"[", "]", "(", ")", "<", ">"};
    private String[] COMMENT_TYPES = new String[]{"//"};
    private String[] STRING_TYPES = new String[]{"\'", "\""};
    private String[] EQUALS_TYPES = new String[]{"="};
    private String[] IGNORED_TYPES = new String[]{";"};

    public InputDriver() {

    }

    private static void printData(List<String[]> data) {
        System.out.println(" ");
        data.forEach(l -> {
            for (String line : l)
                System.out.print("<"+line+"> ");
            if (l.length > 0)
                System.out.println("");
        });
    }

    public Struct readDataFF(String url) {

        Struct dataStructure = new Struct("main");

        List<String[]> bodyList = new ArrayList<>();
        List<String[]> headerList = new ArrayList<>();
        String textLine;
        long time0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(url));
            time0 = System.nanoTime();

            while ((textLine = br.readLine()) != null) {
                String[] tokeLine;
                String trimmedLine = textLine.trim();

                if (checkImports(trimmedLine)) {
                    String[] headers = splitLine(trimmedLine, new String[0], EQUALS_TYPES, IGNORED_TYPES);

                    for (int i = 0; i < headers.length; i++)
                        headers[i] = headers[i].trim();
                    headerList.add(headers);
                }
                else if (checkBody(trimmedLine, COMMENT_TYPES[0])) {

                    tokeLine = splitLine(trimmedLine, createMultiArray(ARRAY_TYPES, STRING_TYPES),
                            EQUALS_TYPES, IGNORED_TYPES);

                    for (int i = 0; i < tokeLine.length; i++)
                        tokeLine[i] = tokeBodyLine(tokeLine[i], ARRAY_TYPES, COMMENT_TYPES, STRING_TYPES);
                    bodyList.add(tokeLine);
                }
            }


            bodyList = prepareBrackets(bodyList, ARRAY_TYPES, STRING_TYPES);

            ArrayList<String[]> tmp = new ArrayList<>();
            bodyList.forEach( b -> {
                if (b.length > 0) tmp.add(b);
            });
            bodyList = tmp;

                long time00 = System.nanoTime();
                bodyList = new ArrayList<>(prepareDots(bodyList));
                long time2 = System.nanoTime() - time00;
            System.out.println("Refactored in: "+time2+" ns");

            long time1 = System.nanoTime() - time0;
            System.out.println("Compiled in: "+time1+" ns");

        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("################");
        printData(headerList);
        printData(bodyList);

        DataType dataType = getDataTypeFromHeader(headerList);

        return dataStructure;
    }




    private static DataType getDataTypeFromHeader(List<String[]> headerList) {
        for (String[] aHeaderList : headerList)
            if (aHeaderList[0].contains("type"))
                if (aHeaderList[1].equalsIgnoreCase("java")) return new JavaType();
        return new StructType();
    }


    private static boolean needRefactor(List<String[]> list) {
        for (String[] aList : list) if (aList[0].contains(".")) return true;
        return false;
    }



    private static String[] splitLine(String bodyLine, String[] exceptions, String[] eqSymbols, String[] ignored) {
        String newBodyLine = replaceFor(bodyLine, eqSymbols, ":");
        newBodyLine = replaceFor(newBodyLine, ignored, " ");

        String[] newLine = newBodyLine.split(":");
        ArrayList<String> lineList = new ArrayList<>();
        for (String line : newLine) {
            line = line.trim();
            int ex = 0;
            String extraSym = "";
            boolean extra = false;
            for (String s : exceptions) {
                if (line.startsWith(s)) {
                    ex += 1;
                    break;
                } else if (line.contains(s)) extra = true;
            }
            if (extra) {
                int firstSym = 0;
                char[] charArray = line.toCharArray();
                for (int f = 0; f < charArray.length; f++) {
                    if (checkCharMatch(charArray[f], exceptions)) {
                        firstSym = f;
                        break;
                    }
                }
                String firSt = line.substring(0, firstSym).trim();
                String secSt = line.substring(firstSym);
                line = firSt;
                extraSym = secSt;
            }
            if (ex > 0) lineList.add(line);
            else {
                String[] listLine = line.split(" ");
                for (String l : listLine) if (!l.isEmpty()) lineList.add(l);
            }
            if (extra) lineList.add(extraSym);
        }return lineList.toArray(new String[lineList.size()]);
    }






    private static String tokeBodyLine(String toke, String[] arrType, String[] commentsType, String[] txtType) {

        for (String aCommentsType : commentsType)
            if (toke.contains(aCommentsType)) {
                toke = toke.substring(0, toke.indexOf(aCommentsType));
                break;
            }
        for (int z = 0; z < arrType.length - 1; z += 2)
            if (toke.startsWith(arrType[z]) && toke.endsWith(arrType[z+1])) return toke.trim();
        int[] start_end = new int[2];
        int actual = 0;
        char[] arr = toke.toCharArray();
        for (int i = 0; i < arr.length; i++)
            for (String tx : txtType) {
                if (arr[i] == tx.toCharArray()[0]){
                    start_end[actual] = i;
                    actual++;
                    break;
                }
            }
        if (actual >= 2) return toke.substring((start_end[0] + 1), start_end[1]);
        return toke.trim();
    }





    private static String[] prepareArrays(String[] arr, String[] arrTypes, String[] txtTypes) {

        for (int i = 0; i < arr.length; i++)
            for (int z = 0; z < arrTypes.length - 1; z += 2)
                if (arr[i].startsWith(arrTypes[z]) && arr[i].endsWith(arrTypes[z+1])) {
                    StringBuilder buffer = new StringBuilder();
                    char[] charArray = arr[i].toCharArray();
                    boolean open = false;
                    for (char ch : charArray) {
                        if (checkCharMatch(ch, txtTypes)) open = !open;
                        if (!open && ch != ' ' && !checkCharMatch(ch, txtTypes)) buffer.append(ch);
                        else if (!checkCharMatch(ch, txtTypes) && open) buffer.append(ch);
                    }   arr[i] = buffer.toString();
                    break;
                }
        return arr;
    }





    private static List<String[]> prepareBrackets(List<String[]> dataList, String[] arrType,
                                                  String[] txtType) {

        int bracketsForRemove = 0;
        int bracketsOpen = 0;
        int iterationsNeed = dataList.size();
        for (int i = 0; i < iterationsNeed; i++) {

            String[] arr = prepareArrays(dataList.get(i), arrType, txtType);
            if(arr[0].contains("}")) {

                bracketsOpen -= 1;
                if (bracketsForRemove > 0 && bracketsOpen == bracketsForRemove) {
                    dataList.set(i, new String[0]);
                    bracketsForRemove -= 1;
                }
            }
            if ((!arr[0].contains("{") && !arr[0].contains("}")) && (arr.length < 2 || arr[arr.length-1].isEmpty())) {

                i += 1;
                if(dataList.get(i)[0].contains("{")) {

                    dataList.set(i-1, new String[]{arr[0], "{"});
                    dataList.set(i, new String[0]);
                    bracketsOpen += 1;
                }
            }
            else if (!arr[0].contains("{") && arr.length >= 2 && arr[arr.length-1].contains("{")) {
                bracketsOpen += 1;
            }
            if (arr[0].contains("{") && i > 0 && dataList.get(i-1).length >= 2
                    && !dataList.get(i-1)[1].isEmpty()) {

                bracketsForRemove += 1;
                bracketsOpen += 1;
                dataList.set(i, new String[0]);
            }
            else if (arr[0].contains("{") && i > 0 && (dataList.get(i-1).length == 0
                    || dataList.get(i-1)[0].isEmpty() || dataList.get(i-1)[0].contains("}"))) {

                bracketsForRemove += 1;
                bracketsOpen += 1;
                dataList.set(i, new String[0]);
            }
            else if (arr[0].contains("{") && i == 0) {

                dataList.set(0, new String[0]);
                dataList.set((dataList.size() - 1), new String[0]);
                iterationsNeed -= 1;
            }
        }
        return dataList;
    }


    private static List<String[]> prepareDots(List<String[]> dataList) {

        List<String[]> closure = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            String[] arr = dataList.get(i);
            if (arr.length > 1 && replaceFor(arr[0], new String[]{"."}, ",").contains(",")) {
                String[] structFields = replaceFor(arr[0], new String[]{"."}, ",").split(",");
                int closures = structFields.length - 1;
                int forDelete = i + 1;

                for (int k = 0; k < closures; k++)
                    closure.add(new String[]{structFields[k], "{"});
                closure.add(new String[]{structFields[closures], arr[1]});

                if (!arr[1].contains("{"))
                    for (int z = 0; z < closures; z++)
                        closure.add(new String[]{"}"});
                else {
                    int otherClosures = 1;
                    int j = i+1;
                    while(otherClosures > 0){
                        String[] nextLine = dataList.get(j);
                        for (String aNextLine : nextLine) {
                            if (aNextLine.contains("{") && nextLine.length > 1) otherClosures += 1;
                            else if (aNextLine.contains("}")) otherClosures -= 1;
                        }
                        j += 1;
                        if (nextLine.length > 0) closure.add(nextLine);
                    }
                    closure.add(new String[]{"}"});
                    forDelete = j;
                }
                for (int p = forDelete; p < dataList.size(); p++)
                    closure.add(dataList.get(p));
                break;
            }   else closure.add(arr);
        }
        /* Possible may need hard clone List */
        if (needRefactor(closure)) return prepareDots(closure);
        return closure;
    }




    private static boolean checkCharMatch(char ch, String[] type) {
        for (String tp : type) if (ch == tp.toCharArray()[0]) return true;
        return false;
    }
    private static String replaceFor(String line, String[] symbols, String replaceSym) {

        char[] charArray = line.toCharArray();
        for (int i = 0; i < charArray.length; i++)
            if (checkCharMatch(charArray[i], symbols)) charArray[i] = replaceSym.toCharArray()[0];
        return new String(charArray);
    }
    private static boolean checkImports(String line) {
        return line.contains("#");
    }
    private static boolean checkBody(String line, String commentSym) {

        return !line.startsWith(commentSym) && !line.isEmpty();
    }
    private static boolean containsStrong(String data, String value) {
        return data.contains(value) && (data.length() == value.length());
    }
    private static String[] createMultiArray(String[] arr1, String[] arr2) {
        String[] multi = new String[arr1.length + arr2.length];
        ArrayList<String> tmp = new ArrayList<>();
        Collections.addAll(tmp, arr1);
        Collections.addAll(tmp, arr2);
        multi = (tmp).toArray(multi);
        return multi;
    }




    public String[] getARRAY_TYPES() {
        return ARRAY_TYPES;
    }
    public InputDriver setARRAY_TYPES(String[] ARRAY_TYPES) {
        this.ARRAY_TYPES = ARRAY_TYPES;
        return this;
    }
    public String[] getCOMMENT_TYPES() {
        return COMMENT_TYPES;
    }
    public InputDriver setCOMMENT_TYPES(String[] COMMENT_TYPES) {
        this.COMMENT_TYPES = COMMENT_TYPES;
        return this;
    }
    public String[] getSTRING_TYPES() {
        return STRING_TYPES;
    }
    public InputDriver setSTRING_TYPES(String[] STRING_TYPES) {
        this.STRING_TYPES = STRING_TYPES;
        return this;
    }
    public String[] getEQUALS_TYPES() {
        return EQUALS_TYPES;
    }
    public InputDriver setEQUALS_TYPES(String[] EQUALS_TYPES) {
        this.EQUALS_TYPES = EQUALS_TYPES;
        return this;
    }
    public String[] getIGNORED_TYPES() {
        return IGNORED_TYPES;
    }
    public InputDriver setIGNORED_TYPES(String[] IGNORED_TYPES) {
        this.IGNORED_TYPES = IGNORED_TYPES;
        return this;
    }

}
