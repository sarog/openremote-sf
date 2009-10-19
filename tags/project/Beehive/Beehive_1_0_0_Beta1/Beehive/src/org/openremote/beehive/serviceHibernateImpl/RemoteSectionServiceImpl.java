package org.openremote.beehive.serviceHibernateImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.dto.ModelDTO;
import org.openremote.beehive.api.dto.RemoteSectionDTO;
import org.openremote.beehive.api.service.RemoteSectionService;
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.domain.RemoteSection;
import org.openremote.beehive.utils.FileUtil;
import org.openremote.beehive.utils.StringUtil;

/**
 * {@inheritDoc}
 *
 * @author Dan 2009-2-6
 */
public class RemoteSectionServiceImpl extends BaseAbstractService<RemoteSection> implements RemoteSectionService {

//    private static Logger logger = Logger.getLogger(RemoteSectionServiceImpl.class.getName());

    /**
     * {@inheritDoc}
     */
    public File exportFile(long id) {
    	RemoteSection remoteSection = genericDAO.loadById(RemoteSection.class, id);
    	String content = remoteSection.allText();
    	String filePath = StringUtil.appendFileSeparator(new Configuration()
				.getDownloadDir())
				+ remoteSection.filePath();
		FileUtil.writeFile(filePath, content);
        return new File(filePath);
    }
    /**
     * {@inheritDoc}
     */
    public String exportText(long id) {
        return genericDAO.loadById(RemoteSection.class, id).allText();
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream exportStream(long id) {
    	return new ByteArrayInputStream(exportText(id).getBytes());
    }

    public List<RemoteSectionDTO> findByModelId(long modelId) {
        Model model = genericDAO.loadById(Model.class, modelId);
        List<RemoteSectionDTO> remoteSectionDTOs = new ArrayList<RemoteSectionDTO>();
        for (RemoteSection remoteSection : model.getRemoteSections()) {
            RemoteSectionDTO remoteSectionDTO = new RemoteSectionDTO();
            try {
                BeanUtils.copyProperties(remoteSectionDTO, remoteSection);
            } catch (IllegalAccessException e) {
                //TODO handle exception
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                //TODO handle exception
                e.printStackTrace();
            }
            remoteSectionDTOs.add(remoteSectionDTO);
        }
        return remoteSectionDTOs;
    }

    public RemoteSectionDTO loadFisrtRemoteSectionByModelId(long modelId) {
        Model model = genericDAO.loadById(Model.class, modelId);
        RemoteSectionDTO remoteSectionDTO = new RemoteSectionDTO();
        if (model.getRemoteSections().size() > 0) {
            try {
                BeanUtils.copyProperties(remoteSectionDTO, model.getRemoteSections().get(0));
            } catch (IllegalAccessException e) {
                //TODO handle exception
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                //TODO handle exception
                e.printStackTrace();
            }
            return remoteSectionDTO;
        }
        throw new IllegalStateException("A Model should have one RemoteSection at least.");
    }

    public RemoteSectionDTO loadSectionById(long sectionId) {
        RemoteSectionDTO remoteSectionDTO = new RemoteSectionDTO();
        try {
            BeanUtils.copyProperties(remoteSectionDTO, loadById(sectionId));
        } catch (IllegalAccessException e) {
            //TODO handle exception
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            //TODO handle exception
            e.printStackTrace();
        }
        return remoteSectionDTO;
    }

    public ModelDTO loadModelById(long sectionId) {
        Model model = loadById(sectionId).getModel();
        ModelDTO modelDTO = new ModelDTO();
        try {
            BeanUtils.copyProperties(modelDTO,model);
        } catch (IllegalAccessException e) {
            //TODO handle exception
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            //TODO handle exception
            e.printStackTrace();
        }
        return modelDTO;
    }


}
