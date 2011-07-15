package org.homework.mcep.extractor

/**
 * Value Object who represents a dependency for an {@link Extractor}. A dependency refer to a token in {@link Extractor} dialect. 
 * @author Willow
 */
class DependOnToken {

	/**
	 * Token id
	 */
	String id;

	/**
	 * Token value to match
	 */
	String value;


	// ------------
	// BUILDER PART
	// ------------
	private DependOnToken() {}

	public static Builder builder() {
		return new Builder()
	}

	/**
	 * Builder to used to create a new Instance of {@link DependOnToken}
	 */
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

		/**
		 * Build a new Instance of {@link DependOnToken}
		 * @return
		 */
		public DependOnToken build() {
			DependOnToken dot = new DependOnToken()
			dot.id = this.id
			dot.value = this.value
			return dot
		}
	}




}
