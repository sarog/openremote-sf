package org.openremote.modeler.service.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Config;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.XmlParser;

public class ControllerConfigServiceImpl extends BaseAbstractService<Config> implements ControllerConfigService {
  /*
   private static final Map<String,Set<Config>> defaultConfigs = new HashMap<String,Set<Config>>(); 
   static {
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<Config> allDefaultConfigs = new HashSet<Config>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      for(Config cfg : allDefaultConfigs){
         String categoryName = cfg.getCategory();
         Set<Config> cfgs = defaultConfigs.get(categoryName);
         if(cfgs == null){
            cfgs = new LinkedHashSet<Config>();
            defaultConfigs.put(categoryName, cfgs);
         }
         cfgs.add(cfg);
      }
   }*/
   private UserService userService = null;
   
   @SuppressWarnings("unchecked")
   @Override
   public Set<Config>listAllConfigByCategoryNameForAccouont(String categoryName,Account account) {
      String hql = "select cfg from Config cfg where cfg.category like ? and cfg.account.oid=?";
      Object[] args = new Object[]{categoryName,account.getOid()};
      List<Config> configs = genericDAO.getHibernateTemplate().find(hql, args);
      Set<Config> configSet = new LinkedHashSet<Config>();
      configSet.addAll(configs);
      initializeConfigs(configSet);
      return configSet;
   }

   @Override
   public Config update(Config config) {
      Config cfg = genericDAO.loadById(Config.class, config.getOid());
      cfg.setCategory(config.getCategory());
      cfg.setHint(config.getHint());
      cfg.setName(config.getName());
      cfg.setValue(config.getValue());
      cfg.setValidation(config.getValidation());
      cfg.setOptions(config.getOptions());
      return config;
   }
   
   public Set<Config> saveAll(Set<Config> configs) {
      Set<Config> cfgs = new LinkedHashSet<Config>();
      for (Config cfg : configs) {
         if (cfg.getAccount() == null) {
            cfg.setAccount(userService.getAccount());
            genericDAO.save(cfg);
         } else {
            genericDAO.update(cfg);
         }
         cfgs.add(cfg);
      }
      initializeConfigs(configs);
      return configs;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Set<Config> listAllConfigByCategoryForCurrentAccount(String categoryName) {
      String hql = "select cfg from Config cfg where cfg.category =? and cfg.account.oid=?";
      Account account = userService.getAccount();
      Object[] args = new Object[]{categoryName,account.getOid()};
      List<Config> configs = genericDAO.getHibernateTemplate().find(hql, args);
      Set<Config> configSet = new LinkedHashSet<Config>();
      configSet.addAll(configs);
      initializeConfigs(configSet);
      return configSet;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Set<Config> listAllByAccount(Account account) {
      String hql = "select cfg from Config cfg where cfg.account.oid=?";
      List<Config> configs = genericDAO.getHibernateTemplate().find(hql, account.getOid());
      Set<Config> configSet = new LinkedHashSet<Config>();
      configSet.addAll(configs);
      initializeConfigs(configSet);
      return configSet;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Set<Config> listAllForCurrentAccount() {
      String hql = "select cfg from Config cfg where cfg.account.oid=?";
      List<Config> configs = genericDAO.getHibernateTemplate().find(hql, userService.getAccount().getOid());
      Set<Config> configSet = new LinkedHashSet<Config>();
      configSet.addAll(configs);
      initializeConfigs(configSet);
      return configSet;
   }

   @Override
   public Set<ConfigCategory> listAllCategory() {
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<Config> allDefaultConfigs = new HashSet<Config>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      return categories;
   }

   /*@Override
   public Collection<Config> updateAll(Collection<Config> configs) {
     for(Config cfg : configs){
        if(cfg.getAccount()==null){
           cfg.setAccount(userService.getAccount());
        }
     }
     genericDAO.getHibernateTemplate().saveOrUpdateAll(configs);
      return configs;
   }*/
   
   private static void initializeConfigs(Set<Config> configs){
      Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
      Set<Config> allDefaultConfigs = new HashSet<Config>();
      XmlParser.initControllerConfig(categories, allDefaultConfigs);
      for(Config cfg : configs){
         Config oldCfg = cfg;
         for(Config tmp: allDefaultConfigs){
            if(tmp.getName().equals(cfg.getName())&& tmp.getCategory().equals(cfg.getCategory())){
               oldCfg = tmp;
               break;
            }
         }
         cfg.setHint(oldCfg.getHint());
         cfg.setOptions(oldCfg.getOptions());
         cfg.setValidation(oldCfg.getValidation());
      }
   }
   
}
