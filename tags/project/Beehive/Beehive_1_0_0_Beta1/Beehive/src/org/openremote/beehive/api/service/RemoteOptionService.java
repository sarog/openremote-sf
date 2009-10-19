package org.openremote.beehive.api.service;

import java.util.List;

import org.openremote.beehive.api.dto.RemoteOptionDTO;

/**
 * Business service for <code>RemoteOptionDTO</code>
 *
 * @author allen.wei 2009-2-18
 */
public interface RemoteOptionService {
    /**
     * Finds <code>RemoteOptionDTO</code> according to <code>remoteSectionDTO</code> id
     *
     * @param remoteSectionId RemoteSectionDTO id
     * @return RemoteOptionDTO
     */
    List<RemoteOptionDTO> findByRemoteSectionId(long remoteSectionId);
}
