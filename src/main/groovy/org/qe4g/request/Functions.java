package org.qe4g.request;

import java.util.List;

/**
 * During the building treatment, this interface is used by builder that can
 * handle {@link Function} for communicate these {@link Function} to
 * {@link Request}
 * 
 * @author Willow
 */
public interface Functions {
	List<Function> getFunctions();
}
