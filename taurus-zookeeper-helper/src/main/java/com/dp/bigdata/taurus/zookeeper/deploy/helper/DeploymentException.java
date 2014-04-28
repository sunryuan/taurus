package com.dp.bigdata.taurus.zookeeper.deploy.helper;

/**
 * DeploymentException
 * 
 * @author damon.zhu
 */
public class DeploymentException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = -8585287528951985497L;

	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Constructors.
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	private int status=1;
	
	public DeploymentException() {
		super();
	}

	public DeploymentException(String msg) {
		super(msg);
	}

	public DeploymentException(Throwable cause) {
		super(cause);
	}

	public DeploymentException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public DeploymentException setStatus(int status){
		this.status=status;
		return this;
	}
	
	public int getStatus(){
		return status;
	}

	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Interface.
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	/**
	 * <p>
	 * Return the exception that is the underlying cause of this exception.
	 * </p>
	 * <p>
	 * This may be used to find more detail about the cause of the error.
	 * </p>
	 * 
	 * @return the underlying exception, or <code>null</code> if there is not one.
	 */
	public Throwable getUnderlyingException() {
		return super.getCause();
	}

	@Override
	public String toString() {
		Throwable cause = getUnderlyingException();
		if (cause == null || cause == this) {
			return super.toString();
		} else {
			return super.toString() + " [See nested exception: " + cause + "]";
		}
	}

}
