package net.henryco.struct.parser.drivers;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author Henry on 09/09/16.
 */
public class InputDriver {

    private String[] INDEX_TYPES = new String[]{"[", "]"};
    private String[] ARRAY_TYPES = new String[]{"[", "]", "(", ")"};
    private String[] COMMENT_TYPES = new String[]{"//", "\\\\"};
    private String[] STRING_TYPES = new String[]{"\'", "\""};
    private String[] EQUALS_TYPES = new String[]{"="};
    private String[] IGNORED_TYPES = new String[]{";"};
    private String[] SPLIT_TYPES = new String[]{":", " "};
	private String[] PREPROCESSOR_TYPES = new String[]{"\'"};
    private String[] OPERATOR_TYPES = new String[]
            {">>>", "<<<", ">>", "<<", "&&", "||", "=>", "<=",
                    ">", "<", "+", "-", "/", "*", "|", "!", "?", "%"};
	private String[] REPLACE_FROM = new String[]{};
	private String[] REPLACE_TO = new String[]{};
    private boolean includeTxt = false;

    InputDriver() {

    }

	@SuppressWarnings("unchecked")
    public List<String[]>[] readStructData(String url) {

        List<String[]> bodyList = new ArrayList<>();
        List<String[]> headerList = new ArrayList<>();
        String textLine;
        long time0;
        try {
			time0 = System.nanoTime();
			Object[] arrList = readAndLoadLists(headerList, bodyList, new File(url));
			headerList = (List<String[]>) arrList[0];
			bodyList = (List<String[]>) arrList[1];

            bodyList = prepareBrackets(bodyList, ARRAY_TYPES, STRING_TYPES, includeTxt);
            bodyList = prepareArrayConstructors(prepareDots(bodyList.stream().filter(b -> b.length > 0).collect(Collectors.toCollection(ArrayList::new))), ARRAY_TYPES);

            long time1 = System.nanoTime() - time0;
            String finish = "Compiled INPUT_DRIVER in: " + time1 + " ns";
            printUnderlined(finish, "#");

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String[]>[] returnListArray = new ArrayList[2];
        returnListArray[0] = headerList;
        returnListArray[1] = bodyList;
        return returnListArray;
    }

	@SuppressWarnings("unchecked")
    private Object[] readAndLoadLists(List<String[]>header, List<String[]>body, File structFile) throws IOException{
		System.out.println("IN PROCESS");
		String textLine;
		BufferedReader br = new BufferedReader(new FileReader(structFile));
		while ((textLine = br.readLine()) != null) {
			String trimmedLine = textLine.trim();
			if (checkPrep(trimmedLine)) {
				String[] headers = splitHeader(replaceFor(trimmedLine, EQUALS_TYPES, ":").trim(), PREPROCESSOR_TYPES, SPLIT_TYPES);
				for (int i = 0; i < headers.length; i++) headers[i] = headers[i].substring(headers[i].startsWith(" ")? 1 : 0);
				header.add(processHeaderLine(headers));
				Object[] arrayLists = loadImports(header, body, headers);
				if (arrayLists != null) {
					header = (List<String[]>) arrayLists[0];
					body = (List<String[]>) arrayLists[1];
				}
			} else {
				body = loadBody(body, trimmedLine);

			}
		}
		return new Object[]{header, body};
	}

	private List<String[]> loadBody(List<String[]> body, String trimmedLine) {

		if (checkBody(trimmedLine, COMMENT_TYPES)) {
			trimmedLine = removeComments(trimmedLine, COMMENT_TYPES);
			trimmedLine = prepareReplaces(trimmedLine, REPLACE_FROM, REPLACE_TO);
			trimmedLine = prepareOperators(trimmedLine, OPERATOR_TYPES, STRING_TYPES);
			trimmedLine = prepareArrayIndexes(trimmedLine, INDEX_TYPES, createMultiArray(EQUALS_TYPES, SPLIT_TYPES));

			String[] tokeLine = splitLine(trimmedLine, createMultiArray(ARRAY_TYPES, STRING_TYPES), EQUALS_TYPES, IGNORED_TYPES, SPLIT_TYPES);
			for (int i = 0; i < tokeLine.length; i++) tokeLine[i] = tokeBodyLine(tokeLine[i], ARRAY_TYPES, COMMENT_TYPES, STRING_TYPES, includeTxt);

			//TODO

			body.add(tokeLine);
		}
		return body;
	}

    private String[] processHeaderLine(String[] headerLine) {
		if (headerLine != null && headerLine.length > 0) {
			if (headerLine[0].equalsIgnoreCase("#sugar") && headerLine.length >=3) {
				String[] from = new String[REPLACE_FROM.length + 1];
				String[] to = new String[from.length];
				for (int i = 0; i < from.length - 1; i++) {
					//from[i] = checkRegExCLose(REPLACE_FROM[i]);
					//to[i] = checkRegExCLose(REPLACE_TO[i]);
					from[i] = REPLACE_FROM[i];
					to[i] = REPLACE_TO[i];
				}
				from[from.length - 1] = headerLine[1];
				to[to.length - 1] = headerLine[2];
				REPLACE_FROM = from;
				REPLACE_TO = to;
			}
		}
		return headerLine;
	}

	private static final String[] cloSeq = new String[]{"\\[", "\\]", "\\(", "\\)", "\\*"};
	private static String checkRegExCLose(String seq) {
		System.out.println("CLO");
		System.out.println(Pattern.compile(seq).toString());

		for (String s : cloSeq)
			if (Pattern.quote(seq).matches(s)) {
				String s1 = seq.substring(0, seq.indexOf(s));
				String s2 = seq.substring(seq.indexOf(s), seq.length() - 1);
				System.out.println("FIND: "+s1 + " | \\ |"+s2);
				return s1 + "\\" + s2;
			}
		return seq;
	}

	private static boolean contains(String sym, String word) {

		for (char c : word.toCharArray()) {
			System.out.println(sym.charAt(0) +" :: -- ::" +c);
			if (sym.charAt(0) == c) return true;
		}

		return false;
	}

	private String[] processSaltLine(String[] headerLine) {

		return null;
	}

    private static void printUnderlined(String msg, String sym) {
        System.out.println("");
        System.out.println(msg);
        for (int i = 0; i < msg.length(); i++) System.out.print(sym);
        System.out.println("");
    }


	private static String[] splitHeader(String line, String[] prepSeq, String[] splitSeq){
		ArrayList<String> wordList = new ArrayList<>();
		boolean incase = false;
		char[] arr = line.toCharArray();
		StringBuffer wordCreator = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			for (String s : prepSeq) {
				if (arr[i] == s.charAt(0)) {
					incase = !incase;
					i+=1;
					break;
				}
			}
			if (i < arr.length) {
				if (!incase) for (String s : splitSeq) {
					if (arr[i] == s.charAt(0)) {
						wordList.add(wordCreator.toString());
						wordCreator = new StringBuffer();
					}
				}
				wordCreator.append(arr[i]);
			}
		}
		if (wordCreator.length() > 0) wordList.add(wordCreator.toString());
		return wordList.toArray(new String[wordList.size()]);
	}

	@SuppressWarnings("unchecked")
	private Object[] loadImports(List<String[]> headerList, List<String[]> bodyList, String[] lineWords) throws IOException {
		System.out.println();
		for (String n : lineWords) System.out.print("|"+n+"| ");
		if (lineWords[0].equalsIgnoreCase("#import")) {
			Object[] arrList = null;
			if (lineWords[1].equalsIgnoreCase("struct")) {
				if(!StructDef.instance().called) {
					arrList = readAndLoadLists(headerList, bodyList, StructDef.STRUCT_LIBRARY);
					StructDef.instance().called = true;
				}
			} else {
				File importFile = File.createTempFile("transit_"+hashCode(), "struct");
				FileWriter importWriter = new FileWriter(importFile);
				BufferedReader reader = new BufferedReader(new FileReader(lineWords[1]));
				String contain;
				boolean firstTime = true;
				StringBuilder outValue = new StringBuilder();
				while ((contain = reader.readLine()) != null) {
					if (firstTime && !contain.startsWith("#") && !contain.equalsIgnoreCase("\n")) {
						firstTime = false;
						outValue.append("\nimports: {\n").append("\tspaces: {\n");
					}
					outValue.append(contain).append("\n");
				}
				outValue.append("\n}\n}\n");
				importWriter.write(outValue.toString());
				importWriter.close();
				arrList = readAndLoadLists(headerList, bodyList, importFile);
			}
			if (arrList != null) {
				headerList = (ArrayList<String[]>) arrList[0];
				bodyList = (ArrayList<String[]>) arrList[1];
			}
		}
		return new Object[]{headerList, bodyList};
	}

    private static String[] splitLine(String bodyLine, String[] exceptions, String[] eqSymbols, String[] ignored,
                                      String[] split) {
        String newBodyLine = replaceFor(bodyLine, eqSymbols, ":");
        newBodyLine = replaceFor(newBodyLine, ignored, " ");

        ArrayList<String> lineList = new ArrayList<>();
        newBodyLine = newBodyLine.trim();
        int ex = 0;
        String extraSym = "";
        boolean extra = false;

        for (String s : exceptions) {
            if (newBodyLine.startsWith(s)) {
                ex += 1;
                break;
            } else if (newBodyLine.contains(s)) extra = true;
        }
        if (extra) {
            int firstSym = 0;
            char[] charArray = newBodyLine.toCharArray();
            for (int f = 0; f < charArray.length; f++) {
                if (checkCharMatch(charArray[f], exceptions)) {
                    firstSym = f;
                    break;
                }
            }
            String firSt = newBodyLine.substring(0, firstSym).trim();
            String secSt = newBodyLine.substring(firstSym);
            newBodyLine = firSt;
            extraSym = secSt;
        }
        if (ex > 0) lineList.add(newBodyLine);
        else {
            String[] listLine = tokeSplit(newBodyLine, split);
            for (String l : listLine) if (!l.isEmpty()) lineList.add(l);
        }
        if (extra) lineList.add(extraSym);
        return lineList.toArray(new String[lineList.size()]);
    }

    private static String[] tokeSplit(String line, String[] seq) {

        List<String> tmpList = new ArrayList<>();
        tmpList.add(line);
        List<String> strList = tokeSeq(tmpList, seq);

        return strList.toArray(new String[strList.size()]);
    }

    private static List<String> tokeSeq(List<String> strList, String[] sec) {

        List<String> srcList = new ArrayList<>(strList);
        for (String aSec : sec) {
            List<String> tmp = new ArrayList<>();
            for (String aSrcList : srcList) {
                String[] split = aSrcList.split(aSec);
                Arrays.stream(split).forEach(tmp::add);
            }
            srcList = new ArrayList<>(tmp);
        }
        return srcList;
    }


    /**
     * Experimental might be troubles
     */
    private static String removeComments(String line, String[] commentType) {
        for (String aCommentsType : commentType)
            if (line.contains(aCommentsType)) {
                line = line.substring(0, line.indexOf(aCommentsType));
                break;
            }
        return line;
    }


    private static String tokeBodyLine(String toke, String[] arrType, String[] commentsType, String[] txtType, boolean includeTxt) {

        toke = removeComments(toke, commentsType);
        for (int z = 0; z < arrType.length - 1; z += 2)
            if (toke.startsWith(arrType[z]) && toke.endsWith(arrType[z + 1])) return toke.trim();
        int[] start_end = new int[2];
        int actual = 0;
        char[] arr = toke.toCharArray();
        if (!includeTxt)
            for (int i = 0; i < arr.length; i++)
                for (String tx : txtType)
                    if (arr[i] == tx.toCharArray()[0]) {
                        start_end[actual] = i;
                        actual++;
                        break;
                    }
        if (actual >= 2) return toke.substring((start_end[0] + 1), start_end[1]);
        return toke.trim();
    }

    private static String prepareReplaces(String line, String[] from, String[] to) {
		for (int i = 0; i < from.length; i++) line = line.replaceAll(from[i], to[i]);
		//System.out.println("REPLACE:::   "+line);
		return line;
	}

	private List<String[]> prepareLongReplaces(List<String[]> body, String line[], String[] trigger, String[] args, String[][] longSeq) {

		for (int i = 0; i < trigger.length; i++)
			if (line[0].contentEquals(trigger[i])) {
				for (String sequence : longSeq[i])
					body = loadBody(body, sequence);
				return body;
			}//TODO
		return body;
	}

    private static String prepareArrayIndexes(String line, String[] arrIndex, String[] exc) {
        StringBuilder builder = new StringBuilder();
        char[] arr = line.toCharArray();
        boolean findExc = false;
        boolean needRepeat = false;
        for (int i = 0; i < arr.length; i += 1) {
            if (!findExc && checkCharMatch(arr[i], exc)) findExc = true;
            if (!findExc)
                for (int z = 0; z < arrIndex.length - 1; z += 2) {
                    if (arr[i] == arrIndex[z].charAt(0)) {
                        i += 1;
                        builder.append('.');
                        needRepeat = true;
                        break;
                    } else if (arr[i] == arrIndex[z + 1].charAt(0)) {
                        i += 1;
                        needRepeat = true;
                        break;
                    }
                }
			if (i < arr.length) {
				if (!findExc && arr[i] != ' ') builder.append(arr[i]);
				else if (findExc) builder.append(arr[i]);
			}
        }
        if (needRepeat) return prepareArrayIndexes(builder.toString(), arrIndex, exc);
        return builder.toString();
    }


    private static String prepareOperators(String line, String[] operators, String[] txtTypes) {

        char[] arr = line.toCharArray();
        StringBuilder stringBuffer = new StringBuilder();

        boolean isOpen = false;
        for (int i = 0; i < arr.length; i++) {
            for (String txt : txtTypes)
                if (txt.toCharArray()[0] == arr[i]) {
                    isOpen = !isOpen;
                    break;
                }
            if (!isOpen && i>0)
                for (String st : operators) {
                    char[] tested = st.toCharArray();
                    int flag = 0;

                    for (int k = 0; k < tested.length; k++)
                        if (i + k < arr.length && arr[i + k] == tested[k]) flag += 1;
                    if (flag == tested.length) {
                        stringBuffer.append(' ');
                        for (char t : tested) stringBuffer.append(t);
                        stringBuffer.append(' ');
                        i += flag;
                        break;
                    }
                }
            if (i < arr.length) stringBuffer.append(arr[i]);
        }
        return stringBuffer.toString();
    }


    private static String[] prepareArrays(String[] arr, String[] arrTypes, String[] txtTypes, boolean includeTXT) {

        for (int i = 0; i < arr.length; i++)
            for (int z = 0; z < arrTypes.length - 1; z += 2)
                if (arr[i].startsWith(arrTypes[z]) && arr[i].endsWith(arrTypes[z + 1])) {
                    StringBuilder buffer = new StringBuilder();
                    char[] charArray = arr[i].toCharArray();
                    boolean open = false;
                    for (char ch : charArray) {
                        if (checkCharMatch(ch, txtTypes)) {
                            open = !open;
                            if (includeTXT) buffer.append(ch);
                        }
                        if (!open && ch != ' ' && !checkCharMatch(ch, txtTypes)) buffer.append(ch);
                        else if (!checkCharMatch(ch, txtTypes) && open) buffer.append(ch);
                    }
                    arr[i] = buffer.toString();
                    break;
                }
        return arr;
    }


    /**
     * Weak part of code, need to be replaced with better one, but later...
     */
    private static List<String[]> prepareBrackets(List<String[]> dataList, String[] arrType,
                                                  String[] txtType, boolean includeTXT) {

        int bracketsForRemove = 0;
        int bracketsOpen = 0;
        int iterationsNeed = dataList.size();
        for (int i = 0; i < iterationsNeed; i++) {

            String[] arr = prepareArrays(dataList.get(i), arrType, txtType, includeTXT);
            if (arr[0].contains("}")) {

                bracketsOpen -= 1;
                if (bracketsForRemove > 0 && bracketsOpen == bracketsForRemove) {
                    dataList.set(i, new String[0]);
                    bracketsForRemove -= 1;
                }
            }
            if ((!arr[0].contains("{") && !arr[0].contains("}")) && (arr.length < 2 || arr[arr.length - 1].isEmpty())) {

                i += 1;
                if (dataList.get(i)[0].contains("{")) {

                    dataList.set(i - 1, new String[]{arr[0], "{"});
                    dataList.set(i, new String[0]);
                    bracketsOpen += 1;
                }
            } else if (!arr[0].contains("{") && arr.length >= 2 && arr[arr.length - 1].contains("{")) {
                bracketsOpen += 1;
            }
            if (arr[0].contains("{") && i > 0 && dataList.get(i - 1).length >= 2
                    && !dataList.get(i - 1)[1].isEmpty()) {

                bracketsForRemove += 1;
                bracketsOpen += 1;
                dataList.set(i, new String[0]);
            } else if (arr[0].contains("{") && i > 0 && (dataList.get(i - 1).length == 0
                    || dataList.get(i - 1)[0].isEmpty() || dataList.get(i - 1)[0].contains("}"))) {

                bracketsForRemove += 1;
                bracketsOpen += 1;
                dataList.set(i, new String[0]);
            } else if (arr[0].contains("{") && i == 0) {

                dataList.set(0, new String[0]);
                dataList.set((dataList.size() - 1), new String[0]);
                iterationsNeed -= 1;
            }
        }
        return dataList;
    }


    private static List<String[]> prepareDots(List<String[]> dataList) {

        List<String[]> closure = new ArrayList<>();
        boolean oneMoreLoop = false;
        for (int i = 0; i < dataList.size(); i++) {
            String[] arr = dataList.get(i);
            if (arr.length > 1 && replaceFor(arr[0], new String[]{"."}, ",").contains(",")) {
                oneMoreLoop = true;
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
                    int j = i + 1;
                    while (otherClosures > 0) {
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
            } else closure.add(arr);
        }
        /* Possible may need hard clone List */
        if (oneMoreLoop) return prepareDots(closure);
        return closure;
    }


    private static List<String[]> prepareArrayConstructors(List<String[]> dataList, String[] arrTypes) {

        List<String[]> lines = new ArrayList<>();
        boolean oneMoreLoop = false;
        for (String[] line : dataList) {
            if (checkStartEnd(line[line.length - 1], arrTypes)) {
                oneMoreLoop = true;
                String word = line[line.length - 1].substring(1, line[line.length - 1].length() - 1);
                String[] arrWords = prepareInternalArray(word.split(","), arrTypes);
                line[line.length - 1] = "{";
                lines.add(line);
                int outNumb = 0;
                for (String arrWord : arrWords) {
                    if (!checkStart(arrWord, arrTypes) && checkEnd(arrWord, arrTypes)) {
                        outNumb -= 1;
                        lines.add(softSplit(arrWord, arrTypes));
                    } else lines.add(new String[]{Integer.toString(outNumb), arrWord});
                    outNumb += 1;
                }
                lines.add(new String[]{"}"});
            } else lines.add(line);
        }
        /* Possible may need hard clone List */
        if (oneMoreLoop) return prepareArrayConstructors(lines, arrTypes);
        return lines;
    }

    private static String[] prepareInternalArray(String[] arrWords, String[] arrTypes) {

        List<String> words = new ArrayList<>();
        for (int i = 0; i < arrWords.length; i++) {
            if (checkStart(arrWords[i], arrTypes) || checkStartMatches(arrWords[i], arrTypes)) {
                if (checkEnd(arrWords[i], arrTypes)) words.add(arrWords[i]);
                else {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(arrWords[i]);
                    for (int z = i + 1; z < arrWords.length; z++) {
                        i = z;
                        buffer.append(",").append(arrWords[z]);
                        if (checkEnd(arrWords[z], arrTypes)) break;
                    }
                    words.add(buffer.toString());
                }
            } else words.add(arrWords[i]);
        }
        return words.toArray(new String[words.size()]);
    }

    private static String[] softSplit(String word, String[] symbols) {
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        char[] arr = word.toCharArray();
        int open = 0;
        for (int z = 0; z < arr.length; z += 1) {
            for (int i = 0; i < symbols.length - 1; i += 2)
                if (arr[z] == symbols[i].charAt(0)) {
                    builder2.append(arr[z]);
                    open += 1;
                    z += 1;
                    break;
                } else if (arr[z] == symbols[i + 1].charAt(0)) {
                    builder2.append(arr[z]);
                    open -= 1;
                    z += 1;
                    break;
                }
            if (z < arr.length) {
                if (open == 0) builder1.append(arr[z]);
                else if (open > 0) builder2.append(arr[z]);
            }
        }
        return new String[]{builder1.toString(), builder2.toString()};
    }

    private static boolean checkStartMatches(String word, String[] tests) {
        for (int i = 0; i < tests.length - 1; i += 2)
            if (word.contains(tests[i])) return true;
        return false;
    }

    private static boolean checkStartEnd(String word, String[] tests) {
        for (int k = 0; k < tests.length - 1; k += 2)
            if (word.startsWith(tests[k]) && word.endsWith(tests[k + 1])) return true;
        return false;
    }

    private static boolean checkStart(String word, String[] tests) {
        for (int k = 0; k < tests.length - 1; k += 2)
            if (word.startsWith(tests[k])) return true;
        return false;
    }

    private static boolean checkEnd(String word, String[] tests) {
        for (int k = 0; k < tests.length - 1; k += 2)
            if (word.endsWith(tests[k + 1])) return true;
        return false;
    }

    private static boolean checkCharMatch(char ch, String[] type) {
        for (String tp : type) if (ch == tp.toCharArray()[0]) return true;
        return false;
    }

    private static boolean checkCharMatch(char ch, char tested) {
        return ch == tested;
    }

    private static String replaceFor(String line, String[] symbols, String replaceSym) {

        char[] charArray = line.toCharArray();
        for (int i = 0; i < charArray.length; i++)
            if (checkCharMatch(charArray[i], symbols)) charArray[i] = replaceSym.toCharArray()[0];
        return new String(charArray);
    }


    private static boolean checkPrep(String line) {
        return line.contains("#");
    }

    private static boolean checkBody(String line, String[] commentSym) {

        int flag = 0;
        for (String st : commentSym) if (line.startsWith(st) || line.isEmpty()) flag += 1;
        return flag == 0;
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


    protected String[] getARRAY_TYPES() {
        return ARRAY_TYPES;
    }

    protected InputDriver setARRAY_TYPES(String[] ARRAY_TYPES) {
        this.ARRAY_TYPES = ARRAY_TYPES;
        return this;
    }

    protected String[] getCOMMENT_TYPES() {
        return COMMENT_TYPES;
    }

    protected InputDriver setCOMMENT_TYPES(String[] COMMENT_TYPES) {
        this.COMMENT_TYPES = COMMENT_TYPES;
        return this;
    }

    protected String[] getSTRING_TYPES() {
        return STRING_TYPES;
    }

    protected InputDriver setSTRING_TYPES(String[] STRING_TYPES) {
        this.STRING_TYPES = STRING_TYPES;
        return this;
    }

    protected String[] getEQUALS_TYPES() {
        return EQUALS_TYPES;
    }

    protected InputDriver setEQUALS_TYPES(String[] EQUALS_TYPES) {
        this.EQUALS_TYPES = EQUALS_TYPES;
        return this;
    }

    protected String[] getIGNORED_TYPES() {
        return IGNORED_TYPES;
    }

    protected InputDriver setIGNORED_TYPES(String[] IGNORED_TYPES) {
        this.IGNORED_TYPES = IGNORED_TYPES;
        return this;
    }

    protected boolean isIncludeTxt() {
        return includeTxt;
    }

    protected InputDriver setIncludeTxt(boolean includeTxt) {
        this.includeTxt = includeTxt;
        return this;
    }

    private static class StructDef {
		private static StructDef _instance = null;
		boolean called = false;
		static StructDef instance() {
			return (_instance == null) ? new StructDef() : _instance;
		}
		static final File STRUCT_LIBRARY;
		static final String libraryCode =
				"//Struct file, by @HenryCo since 2k16\n" +
						"#sugar -> , \n" +
						"#sugar external ext.file\n" +
						"#sugar 'new ' imports'::spaces::'\n" +
						"#sugar "+ "\\("+" "+"' \\("+"'\n"+
						"#sugar 'function \\{' 'function: \\{'\n" +
						"#sugar 'function  \\{' 'function: \\{'\n" +
						"#sugar 'function ' 'function.'\n" +
						"#sugar 'function  ' 'function.'\n"+
						"imports: {\n" +
						"\tspaces: {\n" +
						"\t\tnull = 0;\n" +
						"\t\tvoid = &null;\n" +
						"\t\tVoid(&void)\n" +
						"\t}\n" +
						"}";
		static {
			STRUCT_LIBRARY = new File("structLib.struct");
			System.out.println("<<<STRUCT INSTANCE>>>");
			try {
				if(STRUCT_LIBRARY.createNewFile()) {
					FileWriter writer = new FileWriter(STRUCT_LIBRARY);
					writer.write(libraryCode);
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
