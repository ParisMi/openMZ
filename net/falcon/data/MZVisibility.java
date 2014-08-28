package net.falcon.data;

public enum MZVisibility {

	LOW(.1f),
	MEDIUM(.4f),
	HIGH(.9f);
	
	Float percent;
	private MZVisibility(Float percnt) {
		percent = percnt;
	}
	
	public Float getPercent() {
		return percent;
	}
}
