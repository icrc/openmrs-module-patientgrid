package org.openmrs.module.patientgrid;

public class FilterDef {
	
	private FilterOperator operator;
	
	private Object operand;
	
	public enum FilterOperator {
		EQUALS
	}
	
	public FilterDef(FilterOperator operator, Object operand) {
		this.operator = operator;
		this.operand = operand;
	}
	
	/**
	 * Gets the operator
	 *
	 * @return the operator
	 */
	public FilterOperator getOperator() {
		return operator;
	}
	
	/**
	 * Gets the operand
	 *
	 * @return the operand
	 */
	public Object getOperand() {
		return operand;
	}
	
}
