/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.statuscache.rules;

import java.io.File;

import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.Constants;
import org.openremote.controller.model.event.Range;
import org.openremote.controller.protocol.Event;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceType;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.io.ResourceFactory;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DroolsEventProcessor extends EventProcessor
{


  // TODO : integrate with the so-called "deployer"



  // Private Instance Fields ----------------------------------------------------------------------

  private KnowledgeBase kb;




  // Constructors ---------------------------------------------------------------------------------

  public DroolsEventProcessor()
  {
    System.out.println("------ Started DROOLS Event Processor --------");

//    init();
  }


  // Implements EventProcessor --------------------------------------------------------------------

  @Override public Event push(Event event)
  {
    if (kb == null)
    {
      System.out.println("! No Knowledgebase initialized, skipping event");   // TODO
      return event;
    }

    StatelessKnowledgeSession session = kb.newStatelessKnowledgeSession();

//    if (event instanceof Range)
//    {
//      Range range = (Range)event;
//      session.execute(range);
//    }

    session.execute(event);

    return event;
  }



  @Override public void init()
  {
    KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    ControllerConfiguration config = ServiceContext.getControllerConfiguration();
    String path = config.getResourcePath();

    path = path + "/rules/";      // TODO : use URI or system specific path separators

    File dir = new File(path);

    File[] files = dir.listFiles();              // TODO : security manager - privileged code

    if (files == null)
    {
      System.out.println("! No Rules Found !");   // TODO

      return;
    }


    for (File file : files)
    {
      if (file.getName().endsWith(".drl"))
      {
        builder.add(ResourceFactory.newFileResource(file), ResourceType.DRL);

        System.out.println("++++ Added Rule Definition: " + file.getName());
      }

      else if (file.getName().endsWith(".csv"))
      {
        DecisionTableConfiguration conf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        conf.setInputType(DecisionTableInputType.CSV);

        builder.add(ResourceFactory.newFileResource(file), ResourceType.DTABLE, conf);

        System.out.println("++++ Added Decision Table: " + file.getName());
      }

      else
      {
        System.out.println("Skipped unrecognized rule file: " + file.getName());
      }
    }

    if (builder.hasErrors())
    {
      throw new RuntimeException(builder.getErrors().toString());
    }

    kb = builder.newKnowledgeBase();

    kb.addKnowledgePackages(builder.getKnowledgePackages());

  }
}

