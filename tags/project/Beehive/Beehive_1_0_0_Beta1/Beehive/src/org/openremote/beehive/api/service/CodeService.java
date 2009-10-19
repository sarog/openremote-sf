package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.api.dto.CodeDTO;

/**
 * Business service for <code>CodeDTO</code>
 *
 * @author allen.wei 2009-2-18
 */
public interface CodeService {

    /**
     * Finds <code>CodeDTOs</code> according to <code>RemoteSectionDTO</code> id
     *
     * @param remoteSectionId RemoteSectionDTO id
     * @return a list of CodeDTOs
     */
    List<CodeDTO> findByRemoteSectionId(long remoteSectionId);
}
