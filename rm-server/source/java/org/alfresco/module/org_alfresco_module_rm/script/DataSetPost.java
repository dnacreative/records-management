package org.alfresco.module.org_alfresco_module_rm.script;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_rm.dataset.DataSetService;
import org.alfresco.module.org_alfresco_module_rm.model.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_rm.model.RmSiteType;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.lang.StringUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class DataSetPost extends DeclarativeWebScript implements RecordsManagementModel
{
   /** Constant for the site name parameter */
   private static final String ARG_SITE_NAME = "site";
   
   /** Constant for the data set id parameter */
   private static final String ARG_DATA_SET_ID = "dataSetId";

   /** Site service */
   private SiteService siteService;

   /** Data set service */
   private DataSetService dataSetService;

   /**
    * Set site service
    * 
    * @param siteService the site service
    */
   public void setSiteService(SiteService siteService)
   {
      this.siteService = siteService;
   }

   /**
    * Data set service
    * 
    * @param dataSetService the data set service
    */
   public void setDataSetService(DataSetService dataSetService)
   {
      this.dataSetService = dataSetService;
   }

   /**
    * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest,
    *      org.springframework.extensions.webscripts.Status,
    *      org.springframework.extensions.webscripts.Cache)
    */
   @Override
   protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
   {
      // Resolve data set id
      String dataSetId = req.getServiceMatch().getTemplateVars().get(ARG_DATA_SET_ID);
      if (StringUtils.isBlank(dataSetId))
      {
         throw new WebScriptException(Status.STATUS_BAD_REQUEST, "A data set id was not provided.");
      }

      // Resolve RM site
      String siteName = req.getParameter(ARG_SITE_NAME);
      if (StringUtils.isBlank(siteName))
      {
         siteName = RmSiteType.DEFAULT_SITE_NAME;
      }

      if (siteService.getSite(siteName) == null)
      {
         throw new WebScriptException(Status.STATUS_BAD_REQUEST, "A Records Management site with the name '"
                  + siteName + "' does not exist.");
      }

      // Resolve documentLibrary (filePlan) container
      NodeRef filePlan = siteService.getContainer(siteName, RmSiteType.COMPONENT_DOCUMENT_LIBRARY);
      if (filePlan == null)
      {
         filePlan = siteService.createContainer(siteName, RmSiteType.COMPONENT_DOCUMENT_LIBRARY,
                  TYPE_FILE_PLAN, null);
      }

      dataSetService.loadDataSet(dataSetId, filePlan);

      Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
      model.put("success", true);

      return model;
   }
}
