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
package org.alfresco.po.rm.functional;

import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.rm.fileplan.filter.FilePlanFilter;
import org.alfresco.po.rm.fileplan.filter.hold.HoldsContainer;
import org.alfresco.po.rm.fileplan.toolbar.CreateNewHoldDialog;
import org.alfresco.po.rm.util.RmPageObjectUtils;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test suite tests the following new features:
 * <p>
 * <ul>
 *  <li>Creating a new hold in the holds container</li>
 *  <li>Viewing the manage permissions page for the for hold container</li>
 *  <li>Viewing the manage permissions page for the newly created hold</li>
 *  <li>Viewing the details page of the hold container</li>
 *  <li>Deleting the new hold in the holds container</li>
 * </ul>
 * <p>
 * @author Tuna Aksoy
 * @version 2.2
 */
@Listeners(FailedTestListener.class)
public class HoldContainerIntTest extends AbstractIntegrationTest
{
    /** Constants for the new hold dialog */
    private static final String NAME = "New Hold";
    private static final String REASON = "Reason for hold";
    /** Member variables */
    HoldsContainer holdsContainer;
    FilePlanPage filePlan;
    FilePlanFilter filePlanFilter;

    @Test
    public void createNewHold()
    {
        filePlan = rmSiteDashBoard.selectFilePlan().render();
        filePlanFilter = filePlan.getFilePlanFilter();
        holdsContainer = filePlanFilter.selectHoldsContainer().render();
        CreateNewHoldDialog newHoldDialog = holdsContainer.selectCreateNewHold().render();
        newHoldDialog.enterName(NAME);
        newHoldDialog.enterReason(REASON);
        //newHoldDialog.tickDeleteHold(true);
        holdsContainer = ((HoldsContainer) newHoldDialog.selectSave()).render(NAME);
    }

    @Test(dependsOnMethods="createNewHold")
    public void managePermissionsForRoot()
    {
        RmPageObjectUtils.select(drone, By.cssSelector("button[id$='default-holdPermissions-button-button']"));
        WebElement addUserOrGroupButton = drone.findAndWait(By.cssSelector("button[id$='-addusergroup-button-button']"));
        addUserOrGroupButton.click();
        WebElement doneButton = drone.findAndWait(By.cssSelector("button[id$='-finish-button-button']"));
        doneButton.click();
    }

    @Test(dependsOnMethods="managePermissionsForRoot")
    public void managePermissions()
    {
        filePlan = rmSiteDashBoard.selectFilePlan().render();
        filePlanFilter = filePlan.getFilePlanFilter();
        holdsContainer = filePlanFilter.selectHoldsContainer().render();
        FileDirectoryInfo hold = holdsContainer.getFileDirectoryInfo(NAME);
        WebElement actions = hold.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOverOnElement(actions);
        hold.findElement(By.cssSelector("div.rm-manage-permissions>a")).click();
    }

    @Test(dependsOnMethods="managePermissions")
    public void editDetails()
    {
        filePlan = rmSiteDashBoard.selectFilePlan().render();
        filePlanFilter = filePlan.getFilePlanFilter();
        holdsContainer = filePlanFilter.selectHoldsContainer().render();
        FileDirectoryInfo hold = holdsContainer.getFileDirectoryInfo(NAME);
        WebElement actions = hold.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOverOnElement(actions);
        hold.findElement(By.cssSelector("div.rm-edit-details>a")).click();
    }

    @Test(dependsOnMethods="editDetails")
    public void deleteHold()
    {
        filePlan = rmSiteDashBoard.selectFilePlan().render();
        filePlanFilter = filePlan.getFilePlanFilter();
        holdsContainer = filePlanFilter.selectHoldsContainer().render();
        FileDirectoryInfo hold = holdsContainer.getFileDirectoryInfo(NAME);
        WebElement actions = hold.findElement(By.cssSelector("td:nth-of-type(5)"));
        drone.mouseOverOnElement(actions);
        hold.findElement(By.cssSelector("div.rm-delete>a")).click();
        drone.find(By.cssSelector("div#prompt div.ft span span button")).click();
    }
}