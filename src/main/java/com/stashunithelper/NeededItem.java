package com.stashunithelper;

import lombok.Value;

/** One item the current unit needs, and where the player has it. */
@Value
class NeededItem
{
	String name;
	OwnedItems.Where where;
}
