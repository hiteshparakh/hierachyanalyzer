package com.abc.model;

import java.util.ArrayList;
import java.util.List;

public class Node<Employee> {

	private Employee emp;
	private List<Node<Employee>> empReportees;
	private Node<Employee> empManager;

	
	public Node() {
		super();
		empReportees = new ArrayList<Node<Employee>>();
	}

	public Node(Employee data) {
        this.emp = data;
        this.empReportees = new ArrayList<Node<Employee>>();
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
		reportee.setEmpManager(this);
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

	public Node<Employee> getEmpManager() {
		return empManager;
	}

	public void setEmpManager(Node<Employee> empManager) {
		this.empManager = empManager;
	}

	@Override
	public String toString() {
		return "Node [emp=" + emp + ", empManager=" + empManager + "]";
	}

//	@Override
//	public String toString() {
//		return "Node [emp=" + emp + ", empReportees=" + empReportees + ", empManager=" + empManager + "]";
//	}

}
