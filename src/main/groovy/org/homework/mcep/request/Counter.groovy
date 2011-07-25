package org.homework.mcep.request

class Counter {

	private static List<Integer> counters = [];
	private static List<Integer> idValues = []


	public static void reset() {
		counters.clear()
		idValues.clear()
	}
	public static String getId() {
		StringBuffer strBuffer = new StringBuffer()
		for(int i=0;i<idValues.size();i++) {
			strBuffer.append(idValues.get(i))
			if(i + 1 < idValues.size()) {
				strBuffer.append(".")
			}
		}
		return strBuffer.toString()
	}

	public static void start() {
		if(counters.size() == 0 || counters.size() == idValues.size()) {
			counters.add(1)
		} else {
			int counter = counters.get(idValues.size())
			counter ++
			counters.set(idValues.size(), counter)
			if(counters.size() > idValues.size() + 1) {
				counters.set(idValues.size() + 1, 0)
			}
		}
		idValues << counters.get(idValues.size())
	}

	public static void stop() {
		idValues.remove(idValues.size() -1)
	}
}
