package com.ripperfit.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ripperfit.model.Employee;
import com.ripperfit.model.Login;
import com.ripperfit.service.UserService;

/**
 * controller class to handle user data
 */
@RequestMapping(value = "/employee")
@RestController
public class UserController {

	private UserService userService;

	/**
	 * method to get UserServiceObject
	 * @return : UserService object
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * method to set UserService object
	 * @param UserService
	 */
	@Autowired(required = true)
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * method to get employee with his/her email address
	 * @param email : employee's email address
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/getEmployeeByEmail", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployeeByEmail(@RequestParam String email) {
			
		System.out.println("in get controller"+email);
		Employee employee = this.userService.getEmployeeByEmail(email);
		if (employee != null) {
			System.out.println("ffds: "+employee);
			return new ResponseEntity<Employee>(employee,HttpStatus.OK);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.NO_CONTENT);
		}
	}
	
	/**
	 * method to get Employee By EmployeeId
	 * @param id : employee id
	 * @return : ResponseEntity with Employee object
	 */
	@RequestMapping(value = "/getEmployeeById", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployeeById(@RequestParam String Id) {
		
		int id = Integer.parseInt(Id);
		Employee employee = this.userService.getEmployeeById(id);
		if (employee != null) {
			return new ResponseEntity<Employee>(employee,HttpStatus.OK);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.NO_CONTENT);
		}
	}
	
	/**
	 * method to check that email and password are correct for login
	 * @param login : Login object with email and password
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Employee> login(@RequestBody Login login, HttpServletRequest request) {	
		
		HttpSession session = request.getSession();
		Employee employee = this.userService.login(login.getEmail(), login.getPassword());
		
		if (employee != null) {
			createSession(employee.getEmail(), session);
			return new ResponseEntity<Employee>(employee, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/createSession", method = RequestMethod.GET)
	public ResponseEntity<Void> createSession(@RequestParam String email,HttpSession session) {	
		
		session.setAttribute("email", email);
		return new ResponseEntity<Void>(HttpStatus.OK);
		
	}
	
	/**
	 * method to register employee
	 * @param employee : employee object
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
	public ResponseEntity<Void> addEmployee(@RequestBody Employee employee) {
		
		System.out.println("here");
		int result = this.userService.registerEmployee(employee);
		if(result == 1) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		} else if(result == 2) {
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	@RequestMapping(value = "/socialLogin", method = RequestMethod.POST)
	public ResponseEntity<Void> addEmployeeThroughSocialLogin(@RequestBody Employee employee,HttpSession session) {
		
		System.out.println("here");
		int result = this.userService.registerEmployee(employee);
		if(result == 1) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		} else if(result == 2) {
			session.setAttribute("email", employee.getEmail());
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * method to Update Employee
	 * @param employee : employee object
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/updateEmployee", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@RequestBody Employee employee) {
		
		this.userService.updateUser(employee);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	@RequestMapping(value="/logout", method= RequestMethod.GET)
	public void logout(HttpSession session, final ServletResponse response) throws IOException{
		
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		session.invalidate();
		httpResponse.sendRedirect("/RipperFit/");
	}
	
	@RequestMapping(value = "/getEmployee", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> viewRoles() {
		
		List<Employee> list = this.userService.viewAllEmployee();
		if(list.isEmpty()) {

			return new ResponseEntity<List<Employee>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Employee>>(list, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value="/deleteEmployeebyId",method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteEmployeeById(@RequestBody int employeeId){
		
		this.userService.deleteEmployeeById(employeeId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/getCurrentEmployeeObject",method = RequestMethod.GET)
	public ResponseEntity<Employee> getCurrentEmployeeObject(HttpServletRequest request){
		
		HttpSession session = request.getSession();
		Employee employee = this.userService.getEmployeeByEmail((String) session.getAttribute("email"));
		return new ResponseEntity<Employee>(employee, HttpStatus.OK);
	}
}