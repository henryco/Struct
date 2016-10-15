package net.henryco.struct;

import net.henryco.struct.container.tree.StructNode;

/**
 * @author Henry on 15/10/16.
 */
public interface Structurized {

	Structurized loadFromStruct(StructNode node);
}
