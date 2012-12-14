package com.dp.bigdata.taurus.core;

/**
 * InstanceID
 * @author damon.zhu
 *
 */
public class InstanceID extends ID {
	protected static final String INSTANCE = "instance";

	public static final String INSTANCEID_REGEX = INSTANCE + SEPARATOR + "[0-9]+"
			+ SEPARATOR + "[0-9]+";
	
	private final String indentify;
	
	public InstanceID(TaskID taskID, int id) {
		super(id);
		String[] splits = taskID.toString().split(SEPARATOR);
		indentify = splits[1] + SEPARATOR + splits[2];
	}

	public InstanceID(String indentify, int id){
		super(id);
		this.indentify = indentify;
	}
	
	public StringBuilder appendTo(StringBuilder builder) {
		builder.append(SEPARATOR);
		builder.append(indentify);
		builder.append(SEPARATOR);
		builder.append(idFormat.format(id));
		return builder;
	}

	@Override
    public String toString() {
		return appendTo(new StringBuilder(INSTANCE)).toString();
	}
	
	public static InstanceID forName(String instanceID) throws IllegalArgumentException {
		if (instanceID == null)
			return null;
		try {
			String[] parts = instanceID.split("_");
			if (parts.length == 4) {
				return new InstanceID(parts[1] + "_" + parts[2], Integer.parseInt(parts[3]));
			}
		} catch (Exception ex) {
		}
		throw new IllegalArgumentException("InstanceID string : " + instanceID
				+ " is not properly formed");
	}
}
