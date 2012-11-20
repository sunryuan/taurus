package com.dp.bigdata.taurus.core;

/**
 * 
 * TaskID
 * @author damon.zhu
 *
 */

public class TaskID extends ID {
	protected static final String TASK = "task";
	private String	identifer;

	public static final String TASKID_REGEX = TASK + SEPARATOR + "[0-9]+"
			+ SEPARATOR + "[0-9]+";

	public TaskID(String identifer, int id) {
		super(id);
		this.identifer = identifer;
	}

	public StringBuilder appendTo(StringBuilder builder) {
		builder.append(SEPARATOR);
		builder.append(identifer);
		builder.append(SEPARATOR);
		builder.append(idFormat.format(id));
		return builder;
	}

	public String toString() {
		return appendTo(new StringBuilder(TASK)).toString();
	}

	public static TaskID forName(String str) throws IllegalArgumentException {
		if (str == null)
			return null;
		try {
			String[] parts = str.split("_");
			if (parts.length == 3) {
				return new TaskID(parts[1], Integer.parseInt(parts[2]));
			}
		} catch (Exception ex) {
		}
		throw new IllegalArgumentException("TaskId string : " + str
				+ " is not properly formed");
	}
	
	public static void main(String args[]){
	    TaskID.forName("task_201209241101_0010");
	}
}
