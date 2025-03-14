package com.abc.app;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import com.abc.model.Employee;

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

			for (int i = 0; i < empList.size(); i++) {
				reporteesTotalSalary = reporteesTotalSalary + empList.get(i).getEmployeeSalary();
			}

			avgSalary = reporteesTotalSalary / empList.size();
			twentyPercentOfAvgSalary = 20 * avgSalary / 100;
			fiftyPercentOfAvgSalary = 50 * avgSalary / 100;
//			System.out.println(avgSalary + "--" + twentyPercentOfAvgSalary + "--" + fiftyPercentOfAvgSalary);

			if (mgrEmp != null && (mgrEmp.getEmployeeSalary() < twentyPercentOfAvgSalary
					|| mgrEmp.getEmployeeSalary() > fiftyPercentOfAvgSalary)) {
				outliers.add(mgrEmp);
				System.out.println(mgrEmp.getEmployeeFirstName());
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
		for (CSVRecord csvRecord : csvParser) {
			Employee emp = new Employee(Integer.parseInt(csvRecord.get("id")), csvRecord.get("firstName"),
					csvRecord.get("lastName"), Integer.parseInt(csvRecord.get("salary")));

			Integer mgrId = StringUtils.isNotBlank(csvRecord.get("managerId"))
					? Integer.parseInt(csvRecord.get("managerId"))
					: 0;
			basicMap.put(Integer.parseInt(csvRecord.get("id")), emp);

			map.add(basicMap.get(mgrId), emp);
		}
		return map;
	}

}
