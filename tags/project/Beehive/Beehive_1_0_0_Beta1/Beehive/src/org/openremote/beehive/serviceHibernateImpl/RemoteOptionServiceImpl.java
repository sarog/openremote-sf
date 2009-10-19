package org.openremote.beehive.serviceHibernateImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.openremote.beehive.api.dto.RemoteOptionDTO;
import org.openremote.beehive.api.service.RemoteOptionService;
import org.openremote.beehive.domain.RemoteOption;
import org.openremote.beehive.domain.RemoteSection;

/**
 * @author allen.wei 2009-2-18
 */
public class RemoteOptionServiceImpl extends BaseAbstractService<RemoteOption> implements RemoteOptionService {
    public List<RemoteOptionDTO> findByRemoteSectionId(long remoteSectionId) {
        RemoteSection remoteSection = genericDAO.loadById(RemoteSection.class, remoteSectionId);
        List<RemoteOptionDTO> remoteOptionDTOs = new ArrayList<RemoteOptionDTO>();
        for (RemoteOption remoteOption : remoteSection.getRemoteOptions()) {
            RemoteOptionDTO remoteOptionDTO = new RemoteOptionDTO();
            try {
                BeanUtils.copyProperties(remoteOptionDTO, remoteOption);
            } catch (IllegalAccessException e) {
                //TODO handle exception
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                //TODO handle exception
                e.printStackTrace();
            }
            remoteOptionDTOs.add(remoteOptionDTO);
        }
        return remoteOptionDTOs;
    }
}
