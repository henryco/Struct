package net.henryco.struct.container.exceptions;

/**
 * @author Henry on 21/09/16.
 */
public class StructContainerException extends RuntimeException {

    protected static final String DEFAULT_MSG = "StructContainerException";

    public StructContainerException() {
        super(DEFAULT_MSG);
    }

    public StructContainerException(String message) {
        super(traceMSG(message));
    }

    public static String traceMSG(String varName) {
        return "invalid field \" "+varName+" \"";
    }
}
