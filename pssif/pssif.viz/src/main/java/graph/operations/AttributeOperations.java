package graph.operations;

/**
 * Enum with all the possible attribute operations
 * @author Luc
 *
 */
public enum AttributeOperations {
	
	GREATER("Greater than"),
	LESS ("Less than"),
	GREATER_EQUAL("Greater or equal than"),
	LESS_EQUAL("Less or equal than"),
	EQUAL ("Equal"),
	NOT_EQUAL ("Not equal");
	
	private final String name;
	
	AttributeOperations (String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * returns the name of the operation
	 */
	public String toString()
	{
		return name;
	}
	/**
	 * Get an attribute operation by his name
	 * @param s the name of the operation
	 * @return the attribute operation if it does exist, otherwise null
	 */
	public static AttributeOperations getValueOf(String s)
	{
		AttributeOperations res =null;
		AttributeOperations[] all = AttributeOperations.values();
		
		for (AttributeOperations tmp : all)
		{
			if (tmp.getName().equals(s))
				res = tmp;
		}
		
		return res;
	}
}
