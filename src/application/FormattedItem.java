package application;

import java.io.File;

public class FormattedItem {
	private Key<String, String> stepnumber = new Key<>();
	private Key<String, String> step  = new Key<>();
	private Key<String, String> data  = new Key<>();
	private Key<String, String> result = new Key<>();
	
	
	public FormattedItem() {
		
	}
	public FormattedItem(Step step) {
		this.stepnumber.setKey("\"#\"");
		this.stepnumber.setValue("\"" + step.getStepnum().toString() + "\"");
		this.step.key = "\"Step\"";
		this.step.setValue("\"" + step.getStep().toString() + "\"");
		this.data.setKey("\"Data\"");
		this.data.setValue("\"" + step.getData().toString() + "\"");
		this.result.setKey("\"Expected Result\"");
		this.result.setValue("\"" + step.getResult().toString() + "\"");
	}
	public FormattedItem(Key<String, String> stepnumber, Key<String, String> step, Key<String, String> data,
			Key<String, String> result) {
		super();
		this.stepnumber = stepnumber;
		this.step = step;
		this.data = data;
		this.result = result;
	}
	public Key<String, String> getStepnumber() {
		return stepnumber;
	}
	public void setStepnumber(Key<String, String> stepnumber) {
		this.stepnumber = stepnumber;
	}
	public Key<String, String> getStep() {
		return step;
	}
	public void setStep(Key<String, String> step) {
		this.step = step;
	}
	public Key<String, String> getData() {
		return data;
	}
	public void setData(Key<String, String> data) {
		this.data = data;
	}
	public Key<String, String> getResult() {
		return result;
	}
	public void setResult(Key<String, String> result) {
		this.result = result;
	}
	
	public void saveJSON(File file) {
		
	}
}
