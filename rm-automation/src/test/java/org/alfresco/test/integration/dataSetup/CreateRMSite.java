/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.test.integration.dataSetup;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.create.SiteType;
import org.alfresco.po.share.userdashboard.dashlet.MySitesDashlet;
import org.alfresco.test.BaseTest;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

/**
 * Create RM site
 *
 * @author Tuna Aksoy, David Webster
 * @since 3.0.a
 */
public class CreateRMSite extends BaseTest
{
    /**
     * my sites dashlet
     */
    @Autowired
    private MySitesDashlet mySitesDashlet;

    /** data prep services */
    @Autowired
    private SiteService siteService;

    /**
     * Create RM site for Integration tests
     */
    @Test
    (
        groups = { "integration", "GROUP_RM_SITE_EXISTS" },
        description = "Create RM Site"
    )
    public void createRMSite()
    {
        // create RM site
        openPage(userDashboardPage);

        if (!mySitesDashlet.siteExists(RM_SITE_ID))
        {
            // create RM site
            mySitesDashlet.clickOnCreateSite()
                .setSiteType(SiteType.RM_SITE)
                .clickOnOk();

            // navigate back to the user dashboard
            openPage(userDashboardPage);
        }
        // ensure the rm site exists
        Assert.assertTrue(mySitesDashlet.siteExists(RM_SITE_ID));

        // enter the rm site via the my sites dashlet
        mySitesDashlet.clickOnRMSite(RM_SITE_ID);
    }

    /** data clean-up */
    @AfterSuite(alwaysRun = true)
    protected void deleteRMSite() throws Exception
    {
     // delete site
        if (siteService.exists(RM_SITE_ID, getAdminName(), getAdminPassword()))
        {
            siteService.delete(getAdminName(), getAdminPassword(), "", RM_SITE_ID);
        }
    }
}
