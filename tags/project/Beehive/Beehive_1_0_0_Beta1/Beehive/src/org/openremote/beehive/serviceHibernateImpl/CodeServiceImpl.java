package org.openremote.beehive.serviceHibernateImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.openremote.beehive.api.dto.CodeDTO;
import org.openremote.beehive.api.service.CodeService;
import org.openremote.beehive.domain.Code;
import org.openremote.beehive.domain.RemoteSection;

/**
 * @author allen.wei 2009-2-18
 */
public class CodeServiceImpl extends BaseAbstractService<Code> implements CodeService {
    public List<CodeDTO> findByRemoteSectionId(long remoteSectionId) {
        List<CodeDTO> codeDTOs = new ArrayList<CodeDTO>();
        RemoteSection remoteSection = genericDAO.loadById(RemoteSection.class, remoteSectionId);
        for (Code code : remoteSection.getCodes()) {
            CodeDTO codeDTO = new CodeDTO();
            try {
                BeanUtils.copyProperties(codeDTO, code);
            } catch (IllegalAccessException e) {
                //TODO handle exception
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                //TODO handle exception
                e.printStackTrace();
            }
            codeDTOs.add(codeDTO);
        }
        return codeDTOs;
    }
}
