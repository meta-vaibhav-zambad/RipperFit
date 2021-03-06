package com.ripperfit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ripperfit.model.Employee;
import com.ripperfit.model.Organization;
import com.ripperfit.model.ResourceRequest;
import com.ripperfit.service.ApproveeRequestService;
import com.ripperfit.service.OrganizationService;
import com.ripperfit.service.ResourceRequestService;
import com.ripperfit.service.UserService;

/**
 * controller class to handle resource requests
 */
@RequestMapping(value = "/request")
@RestController
public class ResourceRequestController {

	private ResourceRequestService resourceRequestService;

	private OrganizationService organizationService;

	private UserService userService;

	private ApproveeRequestService approveRequestService;

	/**
	 * method to get ResourceRequestService object
	 * @return : ResourceRequestService object
	 */
	public ResourceRequestService getResourceRequestService() {
		return resourceRequestService;
	}

	/**
	 * method to set ResourceRequestService object
	 * @param resourceRequestService
	 */
	@Autowired(required=true)
	public void setResourceRequestService(ResourceRequestService resourceRequestService) {
		this.resourceRequestService = resourceRequestService;
	}

	/**
	 * Method to get UserService object
	 * @return userService : UserService object
	 */
	public UserService getUserService() {
		return userService;
	}
	
	/**
	 * Method to set UserService object
	 * @param userService : UserService object to set
	 */
	@Autowired(required=true)
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Method to get ApproveeRequestService object
	 * @return ApproveeRequestService object
	 */
	public ApproveeRequestService getApproveRequestService() {
		return approveRequestService;
	}

	/**
	 * Method to set ApproveeRequestService
	 * @param approveRequestService : ApproveRequestService object to set
	 */
	@Autowired(required=true)
	public void setApproveRequestService(
			ApproveeRequestService approveRequestService) {
		this.approveRequestService = approveRequestService;
	}

	/**
	 * Method to get OrganizationService object
	 * @return OrganizationService object
	 */
	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	/**
	 * Method to set OrganizationService object
	 * @param organizationService : OrganizationService object to set
	 */
	@Autowired(required=true)
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	/**
	 * method to add a new resource request
	 * @param request : ResourceRequest object
	 * @param ucBuilder : UriComponentsBuilder object
	 * @return : ResponseEntity blank object
	 */
	@RequestMapping(value = "/addRequest", method = RequestMethod.POST)
	public ResponseEntity<Void> addResourceRequest(@RequestBody ResourceRequest request) {

		resourceRequestService.addResourceRequest(request);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	/**
	 * method to view individual's resource request
	 * @param emp : Employee object(individual)
	 * @return : ResponseEntity object with list of ResourceRequest objects
	 */
	@RequestMapping(value = "/getRequestByEmployee/{employeeId}", method = RequestMethod.GET)
	public ResponseEntity<List<ResourceRequest>> viewResourceRequestByEmployeeId(@PathVariable("employeeId") int employeeId) {

		Employee employee = this.userService.getEmployeeById(employeeId);

		if(employee != null){

			List<ResourceRequest> list = this.resourceRequestService.getResourceRequestByEmployeeId(employee);
			if(list.isEmpty()) {
				return new ResponseEntity<List<ResourceRequest>>(HttpStatus.NO_CONTENT);
			} else {

				return new ResponseEntity<List<ResourceRequest>>(list, HttpStatus.OK);
			}
		}
		return new ResponseEntity<List<ResourceRequest>>(HttpStatus.NO_CONTENT);
	}

	/**
	 * method to view all resource request
	 * @return : ResponseEntity object with list of ResourceRequest objects
	 */
	@RequestMapping(value = "/getAllRequests", method = RequestMethod.GET)
	public ResponseEntity<List<ResourceRequest>> viewAllResourceRequest() {

		List<ResourceRequest> list = this.resourceRequestService.getAllResourceRequest();
		if(list.isEmpty()) {

			return new ResponseEntity<List<ResourceRequest>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<ResourceRequest>>(list, HttpStatus.OK);
		}
	}
	

	/**
	 * Method to get a particular request by requestId
	 * @param requestId : Id of request
	 * @return ResponseEntity with ResourceRequest object
	 */
	@RequestMapping(value = "/getRequest/{requestId}", method = RequestMethod.GET)
	public ResponseEntity<ResourceRequest> viewResourceRequestById(@PathVariable("requestId") int requestId) {

		ResourceRequest resourceRequest = this.resourceRequestService.getResourceRequestById(requestId);
		if(resourceRequest != null){
			return new ResponseEntity<ResourceRequest>(resourceRequest, HttpStatus.OK);
		}
		return new ResponseEntity<ResourceRequest>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Method to view all requests of an employee which he/she have to approve
	 * @param forwardToId : Id of employee who have to approve the requests
	 * @return ResponseEntity with list of ResourceRequest objects
	 */
	@RequestMapping(value="/getAllRequestToApprove/{forwardToId}",method = RequestMethod.GET)
	public ResponseEntity<List<ResourceRequest>> getAllRequestToApprove(@PathVariable("forwardToId") int forwardToId){

		Employee employee = this.userService.getEmployeeById(forwardToId);
		if(employee != null){

			List<ResourceRequest> resourceRequestList = this.approveRequestService.getResourceRequestListByForwardToId(employee);
			if(!resourceRequestList.isEmpty()){
				return new ResponseEntity<List<ResourceRequest>>(resourceRequestList, HttpStatus.OK);
			}
		}
		return new ResponseEntity<List<ResourceRequest>>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Method to update a request
	 * @param resourceRequest : request to be updated
	 * @return ResponseEntity with ResourceRequest object
	 */
	@RequestMapping(value = "/updateRequest", method = RequestMethod.PUT)
	public ResponseEntity<ResourceRequest> updateResourceRequest(@RequestBody ResourceRequest resourceRequest) {

		this.resourceRequestService.updateResourceRequest(resourceRequest);
		return new ResponseEntity<ResourceRequest>(HttpStatus.OK);
	}
	
	/**
	 * Method to view all requests of a particular organization's employees
	 * @param organizationId : ID of organization
	 * @return ResponseEntity with list of ResourceRequest objects
	 */
	@RequestMapping(value = "/getRequestsByOrganization/{organizationId}", method = RequestMethod.GET)
	public ResponseEntity<List<ResourceRequest>> getRequestsByOrganization(@PathVariable("organizationId") int organizationId) {

		Organization organization = this.organizationService.getOrganizationById(organizationId);
		List<ResourceRequest> list = this.resourceRequestService.getAllRequestsInAnOrganizationForHelpdesk(organization);

		if(list.isEmpty()) {
			return new ResponseEntity<List<ResourceRequest>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<ResourceRequest>>(list, HttpStatus.OK);
		}
	}
	
	/**
	 * Method to get requests by status
	 * @param status : status of the requests
	 * @return ResponseEntity with list of ResourceRequest objects
	 */
	@RequestMapping(value = "/getRequestByStatus/{status}/{organizationName}", method = RequestMethod.GET)
	public ResponseEntity<List<ResourceRequest>> viewResourceRequestByStatus(@PathVariable String status , @PathVariable String organizationName) {
		
		
		List<ResourceRequest> list = this.resourceRequestService.getResourceRequestByStatus(status,organizationName);
		if(list.isEmpty()) {

			return new ResponseEntity<List<ResourceRequest>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<ResourceRequest>>(list, HttpStatus.OK);
		}
	}
}