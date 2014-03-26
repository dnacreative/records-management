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

import static org.alfresco.po.rm.RmConsoleUsersAndGroups.ADD_BUTTON;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.ADD_USER_FORM;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.SEARCH_USER_BUTTON;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.SEARCH_USER_INPUT;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.addUserButton;
import static org.alfresco.po.rm.RmConsoleUsersAndGroups.selectGroup;
import static org.alfresco.po.rm.RmCreateRulePage.PROPERTY_VALUE_INPUT;
import static org.alfresco.po.rm.RmFolderRulesPage.LINK_BUTTON;
import static org.alfresco.po.rm.RmFolderRulesWithRules.EDIT_BUTTON;
import static org.alfresco.po.rm.RmFolderRulesWithRules.RULE_ITEMS;
import static org.alfresco.webdrone.WebDroneUtil.checkMandotaryParam;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.rm.RmActionSelectorEnterpImpl.PerformActions;
import org.alfresco.po.rm.RmConsolePage;
import org.alfresco.po.rm.RmConsoleUsersAndGroups;
import org.alfresco.po.rm.RmConsoleUsersAndGroups.SystemRoles;
import org.alfresco.po.rm.RmCreateRulePage;
import org.alfresco.po.rm.RmFolderRulesPage;
import org.alfresco.po.rm.RmFolderRulesWithRules;
import org.alfresco.po.rm.RmLinkToRulePage;
import org.alfresco.po.rm.fileplan.FilePlanPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * helper Methods for Regression tests
 *
 * @author Polina Lushchinskaya
 * @version 2.2
 */

public class RmAbstractTest extends AbstractIntegrationTest
{
    private static Log logger = LogFactory.getLog(RmAbstractTest.class);
    protected final static long MAX_WAIT_TIME = 60000;
    private static final By CREATED_ALERT  = By.xpath(".//*[@id='message']/div/span");
    protected static final String SITE_VISIBILITY_PUBLIC = "public";
    protected static final String SITE_VISIBILITY_PRIVATE = "private";
    private static String RESULTS_FOLDER = System.getProperty("user.dir") +
            System.getProperty("file.separator") + "test-output" + System.getProperty("file.separator");
    protected static final String DEFAULT_USER_PASSWORD = "password";

    /**
     * Method returns element locator by visible text
     *
     * @param elementName visible element text
     * @return  element locator
     */
    public By commonLine(String elementName){
        checkMandotaryParam("elementName", elementName);
        return By.xpath("//a[contains(text(), '" + elementName + "')]");
    }
    /**
     * Action renders to RM site
     */
    public FilePlanPage openRmSite()
    {
        drone.navigateTo(shareUrl + "/page/site/rm/documentlibrary");
        return (FilePlanPage) rmSiteDashBoard.selectFilePlan();
    }

    /**
     * Action creates Inbound Rule
     *
     * @param ruleTitle rule name
     * @param ruleAction rule action
     * @param applytosubfolders does rule apply to subfolders
     * @param isNoRule dies any rules already exists
     */
    protected void createInboundRule(String ruleTitle, PerformActions ruleAction, boolean applytosubfolders, boolean isNoRule)
    {
        RmCreateRulePage rulesPage;
        FilePlanPage filePlan = drone.getCurrentPage().render();
        if (isNoRule)
        {
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            rulesPage = manageRulesPage.openCreateRulePage().render();
        }
        else
        {
            RmFolderRulesWithRules manageRulesPage = filePlan.selectManageRulesWithRules().render();
            rulesPage = manageRulesPage.clickNewRuleButton().render();
        }
        rulesPage.fillNameField(ruleTitle);
        //RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
        WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();
        whenSelectorEnter.selectInbound();
        rulesPage.selectRmAction(ruleAction.getValue());
        if (applytosubfolders)
        {
            rulesPage.selectApplyToSubfolderCheckbox();
        }
        rulesPage.clickCreate().render();
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleTitle))
            {
                ruleItem.click();

                drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
            }
        }
    }

    /**
     * Action creates Outbound Rule
     *
     * @param ruleTitle rule name
     * @param ruleAction rule action
     * @param applytosubfolders does rule apply to subfolders
     * @param isNoRule dies any rules already exists
     */
    protected void createOutboundRule(String ruleTitle, PerformActions ruleAction, boolean applytosubfolders, boolean isNoRule)
    {
        RmCreateRulePage rulesPage;
        FilePlanPage filePlan = drone.getCurrentPage().render();
        if (isNoRule)
        {
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            rulesPage = manageRulesPage.openCreateRulePage().render();
        }
        else
        {
            RmFolderRulesWithRules manageRulesPage = filePlan.selectManageRulesWithRules().render();
            rulesPage = manageRulesPage.clickNewRuleButton().render();
        }
        rulesPage.fillNameField(ruleTitle);
        //RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
        WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();
        whenSelectorEnter.selectOutbound();
        rulesPage.selectRmAction(ruleAction.getValue());
        if(applytosubfolders)
        {
            rulesPage.selectApplyToSubfolderCheckbox();
        }
        rulesPage.clickCreate().render();
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleTitle))
            {
                ruleItem.click();

                drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
            }
        }
    }

    /**
     * Action creates Update Rule
     *
     * @param ruleTitle rule name
     * @param ruleAction rule action
     * @param applytosubfolders does rule apply to subfolders
     * @param isNoRule dies any rules already exists
     */
    protected void createUpdateRule(String ruleTitle, PerformActions ruleAction, boolean applytosubfolders, boolean isNoRule)
    {
        RmCreateRulePage rulesPage;
        FilePlanPage filePlan = drone.getCurrentPage().render();
        if (isNoRule)
        {
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            rulesPage = manageRulesPage.openCreateRulePage().render();
        }
        else
        {
            RmFolderRulesWithRules manageRulesPage = filePlan.selectManageRulesWithRules().render();
            rulesPage = manageRulesPage.clickNewRuleButton().render();
        }
        rulesPage.fillNameField(ruleTitle);
        //RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
        WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();
        whenSelectorEnter.selectUpdate();
        rulesPage.selectRmAction(ruleAction.getValue());
        if (applytosubfolders)
        {
            rulesPage.selectApplyToSubfolderCheckbox();
        }
        rulesPage.clickCreate().render();
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleTitle))
            {
                ruleItem.click();

                drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
            }
        }
    }

    /**
     * Action creates Set Property Value Rule
     *
     * @param ruleTitle rule title
     * @param property property
     * @param value value of property to be set
     * @param applytosubfolders does rule apply to subfolders
     * @param isNoRule does any rules already exists
     */
    protected void createSetPropertyValueRule(String ruleTitle, String property, String value, boolean applytosubfolders, boolean isNoRule)
    {
        RmCreateRulePage rulesPage;
        FilePlanPage filePlan = drone.getCurrentPage().render();
        if (isNoRule)
        {
            RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();
            rulesPage = manageRulesPage.openCreateRulePage().render();
        }
        else
        {
            RmFolderRulesWithRules manageRulesPage = filePlan.selectManageRulesWithRules().render();
            rulesPage = manageRulesPage.clickNewRuleButton().render();
        }
        rulesPage.fillNameField(ruleTitle);
        //RmActionSelectorEnterpImpl actionSelectorEnter = rulesPage.getActionOptionsObj();
        WhenSelectorImpl whenSelectorEnter = rulesPage.getWhenOptionObj();
        whenSelectorEnter.selectInbound();
        rulesPage.selectRmAction(PerformActions.SET_PROPERTY_VALUE.getValue());
        rulesPage.selectSetProperty(property);
        type(PROPERTY_VALUE_INPUT, value);
        if (applytosubfolders)
        {
            rulesPage.selectApplyToSubfolderCheckbox();
        }
        rulesPage.clickCreate().render();
        List<WebElement> ruleItems = drone.findAndWaitForElements(RULE_ITEMS);
        for (WebElement ruleItem : ruleItems)
        {
            if (ruleItem.getText().contains(ruleTitle))
            {
                ruleItem.click();

                drone.findAndWait(EDIT_BUTTON, MAX_WAIT_TIME);
            }
        }
    }

    /**
     * Action Creates Inbound rule, With no Rules and applyed to subfolders
     *
     * @param ruleTitle rule title
     * @param ruleAction rule action
     */
    protected void createInboundRule(String ruleTitle, PerformActions ruleAction)
    {
        createInboundRule(ruleTitle, ruleAction, true, true);
    }

    /**
     * Action Creates Outbound rule, With no Rules and applyed to subfolders
     *
     * @param ruleTitle rule title
     * @param ruleAction rule action
     */
    protected void createOutboundRule(String ruleTitle, PerformActions ruleAction)
    {
        createOutboundRule(ruleTitle, ruleAction, true, true);
    }

    /**
     * Action Creates Outbound rule, With no Rules and applyed to subfolders
     *
     * @param ruleTitle rule title
     * @param ruleAction rule action
     */
    protected void createUpdateRule(String ruleTitle, PerformActions ruleAction)
    {
        createUpdateRule(ruleTitle, ruleAction, true, true);
    }

    /**
     * Action create Link To Rule
     *
     * @param folderName name of folder with rule
     * @param ruleName rule name
     * @return {@link RmLinkToRulePage}
     */
    protected RmLinkToRulePage linkToRule(String folderName, String ruleName)
    {
        FilePlanPage filePlan = drone.getCurrentPage().render();
        RmFolderRulesPage manageRulesPage = filePlan.selectManageRules().render();

        manageRulesPage.openLinkToDialog();
        WebElement siteLink = drone.findAndWait(By.xpath("//div[contains(@id, 'sitePicker')]//span[contains(text(), 'Records Management')]"), MAX_WAIT_TIME);
        siteLink.click();
        WebElement folderLink = drone.findAndWait(By.xpath("//span[contains(text(), '"+folderName+"')]"), MAX_WAIT_TIME);
        folderLink.click();
        WebElement ruleLink = drone.findAndWait(By.xpath("//div[contains(@id, 'rulePicker')]//span[contains(text(), '"+ruleName+"')]"), MAX_WAIT_TIME);
        ruleLink.click();
        drone.waitUntilElementClickable(LINK_BUTTON, MAX_WAIT_TIME);
        manageRulesPage.clickLink();
        return new RmLinkToRulePage(drone).render();
    }

    /**
     * login with default username/password
     */
    protected void login()
    {
        login(username, password);
    }


    /**
     * Helper to report error details for a test.
     *
     * @param driver
     *            WebDrone Instance
     * @param testName
     *            String test case ID
     * @param t
     *            t Throwable Error & Exception to include testng assert
     *            failures being reported as Errors
     */

    /**
     * FIXME: Is this method (and so other methods in this class) copied from some other class? If so
     * we should think if it makes sense to use that class (by adding the dependency for example?)
     */
    protected void reportError(WebDrone driver, String testName, Throwable t)
    {
        logger.error("Error in Test: " + testName, t);
        try
        {
            saveScreenShot(driver, testName);
            savePageSource(testName);

        }
        catch (IOException e)
        {
            Assert.fail("Unable to save screen shot of Test: " + testName + " : " + getCustomStackTrace(t));
        }
        Assert.fail("Error in Test: " + testName + " : " + getCustomStackTrace(t));
    }

    /**
     * Helper to Take a ScreenShot. Saves a screenshot in target folder
     * <RESULTS_FOLDER>
     *
     * @param methodName
     *            String This is the Test Name / ID
     * @return void
     * @throws Exception
     *             if error
     */
    public static void saveScreenShot(WebDrone drone, String methodName) throws IOException
    {
        if (drone != null)
        {
            File file = drone.getScreenShot();
            File tmp = new File(RESULTS_FOLDER + methodName + ".png");
            FileUtils.copyFile(file, tmp);
        }
    }

    /**
     * Helper to return the stack trace as a string for reporting purposes.
     *
     * @param ex
     *            exception / error
     * @return String: stack trace
     */
    protected static String getCustomStackTrace(Throwable ex)
    {

        final StringBuilder result = new StringBuilder();
        result.append(ex.toString());
        final String newline = System.getProperty("line.separator");
        result.append(newline);

        for (StackTraceElement element : ex.getStackTrace())
        {
            result.append(element);
            result.append(newline);
        }
        return result.toString();
    }

    /**
     * Create alfresco enterprise user with password 'password'
     *
     * @param userName username
     */
    protected void CreateUser(String userName){
        try {
            createEnterpriseUser(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Action waits undel created/deleted alert disappears
     */
    public void waitUntilCreatedAlert()
    {
        drone.waitUntilElementPresent(CREATED_ALERT, 5);
        drone.waitUntilElementDeletedFromDom(CREATED_ALERT, 5);
    }

    /**
     * Helper method to create Collaboration site
     */
    public void createCollaborationSite(String siteName)
    {

        // Click create site dialog
        CreateSitePage createSite = rmSiteDashBoard.getRMNavigation().selectCreateSite().render();
        Assert.assertTrue(createSite.isCreateSiteDialogDisplayed());

        // Create RM Site
        SiteDashboardPage site = ((SiteDashboardPage) createSite.createNewSite(siteName).render());
        Assert.assertNotNull(site);
    }

    /**
     * Method Create folder in collaboration site
     *
     * @param siteName site in which folder should create
     * @param folderName nema for newly created folder
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage createRemoteFolder(String siteName, String folderName)
    {
        //Navigate to DocLibPage
        drone.navigateTo(shareUrl + "/page/site/"+siteName+"/documentlibrary");
        DocumentLibraryPage docPage = drone.getCurrentPage().render();
        NewFolderPage form = docPage.getNavigation().selectCreateNewFolder();
        docPage = form.createNewFolder(folderName, folderName).render();
        Assert.assertNotNull(docPage);
        FileDirectoryInfo folder = getItem(docPage.getFiles(), folderName);
        Assert.assertNotNull(folder);
        Assert.assertEquals(folder.getName(), folderName);
        return new DocumentLibraryPage(drone).render();
    }

    /**
     * Verify if Item exists
     *
     * @param items List of items
     * @param name item name
     * @return {@link FileDirectoryInfo}
     */
    private  FileDirectoryInfo getItem(List<FileDirectoryInfo> items, final String name)
    {
        for(FileDirectoryInfo item : items)
        {
            if(name.equalsIgnoreCase(item.getName()))
            {
                return item;
            }
        }
        return null;
    }

    /**
     * Action creates rule for folder in collaboration site
     *
     * @param folderName folder name
     * @param ruleName name of created rule
     */
    public void createRuleForRemoteFolder(String folderName, String ruleName)
    {
        checkMandotaryParam("folderName", folderName);
        checkMandotaryParam("ruleName", ruleName);

        DocumentLibraryPage docPage = drone.getCurrentPage().render();
        FolderRulesPage folderRulesPage = docPage.getFileDirectoryInfo(folderName).selectManageRules().render();
        Assert.assertTrue(folderRulesPage.isPageCorrect(folderName));

        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField(ruleName);
        createRulePage.fillDescriptionField(ruleName);

        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.VERSIONABLE.getValue());

        createRulePage.selectApplyToSubfolderCheckbox();
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
    }

    /**
     * Verifies if element presented on page
     *
     * @param element element locator
     * @return does element presented or not
     */
    public boolean isElementPresent(By element)
    {
        checkMandotaryParam("element", element);

        try
        {
            return drone.findAndWait(element, 3000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Verifies if Failure, error message displayed
     *
     * @return does failure exists or not
     */
    public boolean isFailureMessageAppears()
    {
        try
        {
            // FIXME: No hard coded text (Need tag to error message)
            WebElement failure = drone.findAndWait(By.xpath("//div[text()='Failure']"));
            return failure.isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Action assigned user to role
     *
     * @param userName Name of assigbed user
     * @param roleName Role name applied to user
     * @return {@link RmConsoleUsersAndGroups}
     */
    public RmConsoleUsersAndGroups assignUserToRole(String userName, String roleName)
    {
        checkMandotaryParam("userName", userName);
        checkMandotaryParam("roleName", roleName);

        openRmSite();
        FilePlanPage filePlan = (FilePlanPage) rmSiteDashBoard.selectFilePlan();
        RmConsolePage consolePage= filePlan.openRmConsolePage();
        RmConsoleUsersAndGroups newRole = consolePage.openUsersAndGroupsPage();
        selectGroup(drone, roleName);

        //Add user
        click(ADD_BUTTON);
        drone.findAndWait(ADD_USER_FORM).isDisplayed();

        //Search for User
        WebElement searchInput = drone.findAndWait(SEARCH_USER_INPUT, MAX_WAIT_TIME);
        searchInput.clear();
        searchInput.sendKeys(userName);
        click(SEARCH_USER_BUTTON);
        for(int i=0; i<3; i++)
        {
            if(!isElementPresent(addUserButton(userName)))
            {
                click(SEARCH_USER_BUTTON);
            }
            else
            {
                break;
            }
        }
        click(addUserButton(userName));
        newRole.waitUntilCreatedAlert();
        return new RmConsoleUsersAndGroups(drone).render();
    }

    /**
     * Method returns All Select Box Options
     *
     * @param Select select element locator
     * @param timeout long timeout waiting for element
     * @return List of Elements in select box
     */
    protected List<WebElement> getAllSelectOptions(By Select, long timeout)
    {
        WebElement dropDownList = getDrone().findAndWait(Select, timeout);
        List<WebElement> listItems = dropDownList.findElements(By.tagName("option"));
        return listItems;
    }

    /**
     * Method creates a RM Admin user
     *
     * @param userName user name of created user
     */
    protected void createRmAdminUser(String userName)
    {
        logger.info("Trying to create user - " + userName);
        ShareUtil.logout(drone);

        CreateUser(userName);
        login();
        assignUserToRole(userName, SystemRoles.RECORDS_MANAGEMENT_ADMINISTRATOR.getValue());

        ShareUtil.logout(drone);
    }
}