package com.abc.model;

import java.util.ArrayList;
import java.util.List;

public class Node<Employee> {

	private Employee emp;
	private List<Node<Employee>> empReportees;
	
	public Node() {
		super();
		empReportees = new ArrayList<Node<Employee>>();
	}

	public Node(Employee data) {
        this();
		setEmp(data);
    }
	
	/**
	 * Initialize a node with another node's data. This does not copy the node's
	 * children.
	 *
	 * @param node The node whose data is to be copied.
	 */
	public Node(Node<Employee> node) {
		this.emp = node.getEmp();
		empReportees = new ArrayList<Node<Employee>>();
	}

	public void addReportee(Node<Employee> reportee) {
		empReportees.add(reportee);
	}

	
	public List<Node<Employee>> getEmpReportees() {
		return empReportees;
	}

	public void setEmpReportees(List<Node<Employee>> empReportees) {
		this.empReportees = empReportees;
	}

	public Employee getEmp() {
		return emp;
	}

	public void setEmp(Employee emp) {
		this.emp = emp;
	}

	@Override
	public String toString() {
		return "Node [emp=" + emp + ", empReportees=" + empReportees + "]";
	}

}
