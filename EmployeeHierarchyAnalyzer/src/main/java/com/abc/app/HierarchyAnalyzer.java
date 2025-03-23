package com.abc.app;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import com.abc.model.Employee;
import com.abc.model.EmployeeTree;
import com.abc.model.Node;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class HierarchyAnalyzer {

	public static void main(String args[]) {
		List<Employee> outlierEmps = computeOutliers(readEmpDataFile());
		System.out.println("Outliers =" + outlierEmps.size());
	}

	private static List<Employee> computeOutliers(MultivaluedMap<Employee, Employee> empDataMap) {
		List<Employee> outliers = new ArrayList<Employee>();
		if(empDataMap == null)
			return outliers;
		
		empDataMap.forEach((mgrEmp, empList) -> {
			int reporteesTotalSalary = 0;
			int avgSalary = 0;
			int twentyPercentOfAvgSalary = 0;
			int fiftyPercentOfAvgSalary = 0;
			int lowerRange = 0;
			int higherRange = 0;
			
			for (int i = 0; i < empList.size(); i++) {
				reporteesTotalSalary = reporteesTotalSalary + empList.get(i).getEmployeeSalary();
			}

			avgSalary = reporteesTotalSalary / empList.size();
			twentyPercentOfAvgSalary = 20 * avgSalary / 100;
			fiftyPercentOfAvgSalary = 50 * avgSalary / 100;
			lowerRange = avgSalary + twentyPercentOfAvgSalary;
			higherRange = avgSalary + fiftyPercentOfAvgSalary;
			
//			if(mgrEmp != null)
//				System.out.println("Mgr Emp =" + mgrEmp.getEmployeeId() + " Mgr Salary =" + mgrEmp.getEmployeeSalary() + "\nLower Range =" + lowerRange + "\nHigher Range =" + higherRange);

			if (mgrEmp != null && (mgrEmp.getEmployeeSalary() < lowerRange || mgrEmp.getEmployeeSalary() > higherRange)) {
				outliers.add(mgrEmp);
				System.out.println("Employee with outlier salary - " + mgrEmp);
			}

		});
		return outliers;
	}

	private static MultivaluedMap<Employee, Employee> readEmpDataFile() {
		try (Reader reader = Files.newBufferedReader(Paths.get("EmpData.csv"));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
			return buildEmployeeDataMap(csvParser);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static MultivaluedMap<Employee, Employee> buildEmployeeDataMap(CSVParser csvParser) {
		Map<Integer, Employee> basicMap = new HashMap<Integer, Employee>();
		MultivaluedMap<Employee, Employee> map = new MultivaluedHashMap<Employee, Employee>();
		
		EmployeeTree<Employee> empTree = new EmployeeTree<Employee>();
		List<Node<Employee>> empNodes = new ArrayList<Node<Employee>>();
		
		for (CSVRecord csvRecord : csvParser) {
			Employee emp = new Employee(Integer.parseInt(csvRecord.get("id")), csvRecord.get("firstName"),
					csvRecord.get("lastName"), Integer.parseInt(csvRecord.get("salary")));

			Integer mgrId = StringUtils.isNotBlank(csvRecord.get("managerId"))
					? Integer.parseInt(csvRecord.get("managerId"))
					: 0;
			basicMap.put(Integer.parseInt(csvRecord.get("id")), emp);

			map.add(basicMap.get(mgrId), emp);
//			System.out.println(basicMap.get(mgrId) + " = " + map);
			
			if(mgrId == 0) {
				empTree.setRootEmployee(new Node(emp));
			} else {
				empNodes.add(new Node(emp));
			}
		}
		identifyDeepReportingLine(populateEmployeeTree(map, empTree, empNodes));

		return map;
	}

	private static void identifyDeepReportingLine(EmployeeTree<Employee> employeeTree) {
		 List<List<Employee>> result = new ArrayList<>(); // Initialize a list to store the level order traversal result.
	      
	        // Early return if the root is null, meaning the tree is empty.
	        if (employeeTree == null) {
	        	System.out.println("No employees in the tree");    	
	        }
	      
	        Deque<Node> queue = new ArrayDeque<>(); // Initialize a queue to hold the nodes to visit.
	        queue.offerFirst(employeeTree.getRootEmployee()); // Place the root node in the queue.
	      
	        // Loop as long as the queue isn't empty.
	        while (!queue.isEmpty()) {
	            List<Employee> employee = new ArrayList<>(); // Initialize a list to store the values at the current level.
	          
	            // Determine the number of nodes at this level.
	            int levelSize = queue.size();
	          
	            // Process each node on the current level.
	            for (int i = 0; i < levelSize; i++) {
	                Node<Employee> currentNode = queue.poll(); // Remove the next node from the queue.
	                employee.add(currentNode.getEmp()); // Add the node's value to the list for this level.
	              
	                // Add all of the current node's children to the queue.
	                for (Node<Employee> child : currentNode.getEmpReportees()) {
	                    queue.offer(child);
	                }
	            }
	          
	            // Add the current level's list of values to the result list.
	            result.add(employee);
            }
	        
	        for(int i = 6; i < result.size(); i++) {
	            System.out.println("Employee with long reporting line -" + result.get(i) + " by " + (i - 5));    	
	        }
	}

	private static EmployeeTree<Employee> populateEmployeeTree(MultivaluedMap<Employee, Employee> map, EmployeeTree<Employee> empTree,
			List<Node<Employee>> empNodes) {
		
		empNodes.forEach((node) -> {
			
			map.forEach((mgrEmp, empList) -> {
				
				if(mgrEmp != null && node.getEmp().getEmployeeId() == mgrEmp.getEmployeeId()) {
					empList.forEach((emp) -> {
//						System.out.println(node.getEmp().getEmployeeId() + "---" + emp.getEmployeeId());
						empNodes.forEach((node1) -> {
							if(emp.getEmployeeId() == node1.getEmp().getEmployeeId()) {
								node.addReportee(node1);
							}
						});
					});
				} 
			});
			
		});

		map.forEach((mgrEmp, empList) -> {
			if(mgrEmp != null && empTree.getRootEmployee().getEmp().getEmployeeId() == mgrEmp.getEmployeeId())
			{
				empList.forEach((emp) -> {
					empNodes.forEach((node) -> {
						if(emp.getEmployeeId() == node.getEmp().getEmployeeId()) {
//							System.out.println(empTree.getRootEmployee().getEmp().getEmployeeId() + ">>>" + node.getEmp().getEmployeeId());
							empTree.getRootEmployee().addReportee(node);
						}
					});
				});
			}
		});
		return empTree;
	}

	
}
