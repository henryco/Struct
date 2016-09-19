package container;

import container.tree.StructTree;

import java.util.List;

/**
 * @author Henry on 19/09/16.
 */
public interface StructContainer {

     static StructTree tree() {
        return new StructTree();
    }
     static StructTree tree(List<String[]>[] data) {
        return new StructTree(data);
    }
}
