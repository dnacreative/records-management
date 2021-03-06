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
package org.alfresco.po.common.site;

import org.alfresco.po.common.renderable.Renderable;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Site navigation base implementation
 *
 * @author Roy Wetherall
 */
public abstract class SiteNavigation extends Renderable
{
    /** dashboard link */
    @FindBy(css="div[id='HEADER_SITE_DASHBOARD'] a")
    protected Link dashboard;

    /** document library link */
    @FindBy(css="div[id='HEADER_SITE_DOCUMENTLIBRARY'] a")
    protected Link documentLibrary;

    /** site members link */
    @FindBy(css="div[id='HEADER_SITE_MEMBERS'] a")
    protected Link siteMembers;

    /** Invite users link. */
    @FindBy(css="div#HEADER_SITE_INVITE a")
    protected Link inviteUsers;
}
