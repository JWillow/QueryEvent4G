package org.homework.mcep.extractor

class DependOnToken {

	String id;
	String value;
	
	
	// ------------
	// BUILDER PART
	// ------------
	private DependOnToken() {}
	
	public static Builder builder() {
		return new Builder()
	}
	
	public static class Builder {
		private String id
		private String value
		
		public Builder identifiedBy(String id) {
			this.id = id
			return this
		}
		
		public Builder matchValue(String value) {
			this.value = value
			return this
		}
		
		public DependOnToken build() {
			DependOnToken dot = new DependOnToken()
			dot.id = this.id
			dot.value = this.value
			return dot
		}
	}
	
	
	
	
}
