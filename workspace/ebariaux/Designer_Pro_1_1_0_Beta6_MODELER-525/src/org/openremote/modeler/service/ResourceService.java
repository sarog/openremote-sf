/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.service;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.cache.CacheOperationException;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Template;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.shared.GraphicalAssetDTO;
import org.openremote.modeler.shared.dto.DTO;
import org.openremote.modeler.shared.dto.DeviceDTO;

/**
 * TODO : this interface is on the way out
 * 
 * @author Allen, Handy
 */
public interface ResourceService
{

  /**
   * @deprecated Should eventually go away, with a direct call to
   * {@link org.openremote.modeler.beehive.BeehiveService} API to download up-to-date
   * account resources from Beehive, not from Designer Cache. See
   * http://jira.openremote.org/browse/MODELER-288
   */
  @Deprecated String downloadZipResource(LocalFileCache cache, List<Panel> panels, long maxOid);

  /**
   * @deprecated unused
   */
  @Deprecated Map<String, Collection<? extends DTO>> getDotImportFileForRender(String sessionId, InputStream inputStream)  throws NetworkException, ConfigurationException, CacheOperationException;

  /**
   * @deprecated seems unused
   */
  @Deprecated File uploadImage(InputStream inputStream, String fileName, String sessionId);

  /**
   * @deprecated Should eventually go away, with a direct API in
   * {@link org.openremote.modeler.beehive.BeehiveService} to upload images to account (and not
   * upload as part of save-cycle). See MODELER-292.
   */
  @Deprecated File uploadImage(InputStream inputStream, String fileName);

  List<GraphicalAssetDTO>getUserImagesURLs();

  String getRelativeResourcePath(String sessionId, String fileName);

  String getRelativeResourcePathByCurrentAccount(String fileName);

  String getPanelsJson(Collection<Panel> panels);

  /**
   * @deprecated Can be replaced with direct call to
   * {@link org.openremote.modeler.service.impl.DesignerState#restore()}
   */
  @Deprecated PanelsAndMaxOid restore();

  /**
   * @deprecated Can be replaced with a direct call to
   * {@link org.openremote.modeler.service.impl.DesignerState#save(java.util.Set)}.
   */
  @Deprecated LocalFileCache saveResourcesToBeehive(Collection<Panel> panels, long maxOid);

  void saveTemplateResourcesToBeehive(Template Template);
  void downloadResourcesForTemplate(long templateOid);

  File getTemplateResource(Template template);
   
   File getTempDirectory(String sessionId);
   
   void deleteImage(String imageName);
}

   
