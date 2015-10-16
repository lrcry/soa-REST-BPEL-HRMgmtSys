package au.com.ors.rest.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoCheckRes implements Serializable{

	public AutoCheckRes(
		@JsonProperty("pdvResult")	String pdvResult, 
		@JsonProperty("crvResult") String crvResult) {
		super();
		this.pdvResult = pdvResult;
		this.crvResult = crvResult;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2093430850480247677L;
	
	private String pdvResult;
	private String crvResult;
	public String getPdvResult() {
		return pdvResult;
	}
	public void setPdvResult(String pdvResult) {
		this.pdvResult = pdvResult;
	}
	public String getCrvResult() {
		return crvResult;
	}
	public void setCrvResult(String crvResult) {
		this.crvResult = crvResult;
	}
}
