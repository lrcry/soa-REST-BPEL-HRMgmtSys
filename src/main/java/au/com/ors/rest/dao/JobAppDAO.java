package au.com.ors.rest.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import au.com.ors.rest.bean.JobApplication;

@Component
public class JobAppDAO {
	/**
	 * Create a new job application<br/>
	 * 
	 * @param application
	 *            job application object
	 * @return created job application object
	 */
	public JobApplication create(JobApplication application) {
		return null;
	}

	/**
	 * Update an existing job application<br/>
	 * 
	 * @param application
	 *            job application to be updated
	 */
	public void update(JobApplication application) {

	}

	/**
	 * Delete an existing job application<br/>
	 * 
	 * @param application
	 *            job application to be deleted
	 */
	public void delete(JobApplication application) {

	}

	/**
	 * Retrieve all job applications<br/>
	 * 
	 * @return list of job applications
	 */
	public List<JobApplication> findAll() {
		return null;
	}

	/**
	 * Retrieve a job application by its application ID<br/>
	 * 
	 * @param _appId
	 *            application ID
	 * @return a job application
	 */
	public JobApplication findById(String _appId) {
		return null;
	}

	/**
	 * Retrieve job applications by which the application is for<br/>
	 * 
	 * @param _jobId
	 *            job ID
	 * @return list of job applications
	 */
	public List<JobApplication> findByJobPostingId(String _jobId) {
		return null;
	}
}
