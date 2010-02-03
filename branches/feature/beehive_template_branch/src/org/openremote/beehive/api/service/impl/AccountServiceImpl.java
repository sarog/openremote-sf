package org.openremote.beehive.api.service.impl;

import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Code;

public class AccountServiceImpl extends BaseAbstractService<Code> implements AccountService{

   @Override
   public void save(Account a) {
     genericDAO.save(a);
   }

}
