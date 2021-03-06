/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.test.integration.classify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.alfresco.po.rm.dialog.classification.ClassifyContentDialog;
import org.alfresco.po.share.browse.documentlibrary.Document;
import org.alfresco.po.share.browse.documentlibrary.DocumentActions;
import org.alfresco.po.share.browse.documentlibrary.DocumentIndicators;
import org.alfresco.po.share.browse.documentlibrary.DocumentLibrary;
import org.alfresco.po.share.browse.documentlibrary.InplaceRecord;
import org.alfresco.po.share.details.document.DocumentActionsPanel;
import org.alfresco.po.share.details.document.DocumentDetails;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * Classify document integration test
 *
 * @author David Webster
 * @since 3.0.a
 */
public class ClassifyDocument extends BaseTest
{
    /**
     * Doc Lib
     */
    @Autowired
    private DocumentLibrary documentLibrary;

    /**
     * Doc Details page
     */
    @Autowired
    private DocumentDetails documentDetails;

    /** The classify content dialog. */
    @Autowired
    private ClassifyContentDialog classifyContentDialog;

    /**
     * Check that the classify action is available on the document library page and the document preview page.
     *
     * Nb. There is no explicit acceptance criteria for this, but it is clearly required for RM-2078.
     */
    @Test
    (
        groups = { "integration" },
        description = "Verify classify action is available",
        dependsOnGroups = { "GROUP_DOCUMENT_EXISTS" }
    )
    public void checkClassifyActionAvailable()
    {
        // Open Collab site DocumentLibrary.
        openPage(documentLibrary, COLLAB_SITE_ID);
        Document document = documentLibrary.getDocument(DOCUMENT);

        // Document should not be classified
        assertFalse(document.hasIndicator(DocumentIndicators.CLASSIFIED));

        // verify document actions
        assertTrue(document.isActionClickable(DocumentActions.CLASSIFY));

        // verify the Edit Classification button is not available if the document has not been classified
        String[] clickableActions = document.getClickableActions();
        assertFalse("Expected the Edit Classification button not to be available for document that has not been classified",
                 Arrays.asList(clickableActions).contains(DocumentActions.EDIT_CLASSIFICATION));

        // navigate to the document details page
        document.clickOnLink();

        // verify that all the expected actions are available
        assertTrue(documentDetails.getDocumentActionsPanel()
                .isActionClickable(DocumentActionsPanel.CLASSIFY));
        // verify the Edit Classification button is not available in Document Details
        assertFalse("Expected the Edit Classification button not to be available in Document Details if the document has not been classified",
                 documentDetails.getDocumentActionsPanel().isActionAvailable(DocumentActionsPanel.EDIT_CLASSIFICATION));
        // Click on classify action in order to validate the dialog
        documentDetails.getDocumentActionsPanel().clickOnAction(DocumentActionsPanel.CLASSIFY, classifyContentDialog);

        final String userFullName = "Administrator";
        assertEquals(userFullName, classifyContentDialog.getClassifiedBy());
        assertFalse(classifyContentDialog.isClassifyButtonEnabled());

        // Do not actually classify the document.
        classifyContentDialog.clickOnCancel();

        // verify the Edit Classification button is not available in Document Details after cancelling the classification
        assertFalse("Expected the Edit Classification button not to be available in Document Details after cancelling the classification",
                 documentDetails.getDocumentActionsPanel()
                .isActionAvailable(DocumentActionsPanel.EDIT_CLASSIFICATION));
    }

    /**
     * Check that the classify action is not available on the document in several cases.
     *
     * Given that content is either a working copy
     * TODO Or content synced to the cloud
     * Or content has been shared (via QuickShare)
     * Or content that is already classified (covered by {@link ContentClassificationDialogTests#classifyDocument})
     * When I view the available actions
     * Then the classify content action will not be available
     */
    @Test
    (
        groups = { "integration" },
        description = "Verify classify action is not available for various cases",
        dependsOnGroups = { "GROUP_SHARED_DOCUMENT_EXISTS" }
    )
    public void checkClassifyActionUnavailableInVariousCases()
    {
        openPage(documentLibrary, COLLAB_SITE_ID);

        Document sharedDocument = documentLibrary.getDocument(SHARED_DOCUMENT);
        assertFalse(sharedDocument.isActionClickable(DocumentActions.CLASSIFY));

        // TODO Create test for document that has been synced to the cloud.

        // upload document
        documentLibrary.getToolbar()
            .clickOnUpload()
            .uploadFile(LOCKED_DOCUMENT);

        // lock the document
        documentLibrary
            .getDocument(LOCKED_DOCUMENT)
            .clickOnEditOffline();

        try
        {
            // reopen the page to avoid "save" dialog
            openPage(documentLibrary, COLLAB_SITE_ID);

            // check that you can't classify a locked document
            Document lockedDocument = documentLibrary.getDocument(LOCKED_DOCUMENT);
            assertFalse(lockedDocument.isActionClickable(DocumentActions.CLASSIFY));
        }
        finally
        {
            // unlock the document so the site can be deleted later
            documentLibrary
                .getDocument(LOCKED_DOCUMENT)
                .clickOnCancelEdit();
        }
    }

    /**
     * Check that a user with no security clearance doesn't see the 'Classify' action.
     *
     * Given that I have a security clearance level of 'No Clearance'
     * And 'write' permission on the unclassified content
     * When I view the unclassified content
     * Then I cannot set a classification
     */
    @Test
    (
        groups = { "integration" },
        description = "Check that a user with no security clearance doesn't see the 'Classify' action",
        dependsOnGroups = { "GROUP_UNCLEARED_USER_IN_COLLAB_SITE" }
    )
    public void checkUnclearedUserCannotClassify()
    {
        String documentName = UUID.randomUUID().toString();

        openPage(UNCLEARED_USER, DEFAULT_PASSWORD, documentLibrary, COLLAB_SITE_ID);
        documentLibrary.getToolbar()
            .clickOnUpload()
            .uploadFile(documentName);

        Document document = documentLibrary.getDocument(documentName);
        assertFalse("Classify action should not be shown to uncleared user.",
                    document.isActionClickable(DocumentActions.CLASSIFY));
    }

    /**
     * Check that a user with no security clearance doesn't see the 'Edit Classification' action.
     *
     * Given that I have a security clearance level of 'No Clearance'
     * And 'write' permission on content with unclassified security clearance
     * When I view that content
     * Then I cannot edit its classification
     */
    @Test
    (
        groups = { "integration" },
        description = "Check that a user with no security clearance doesn't see the 'Edit Classification' action",
        dependsOnGroups = { "GROUP_UNCLEARED_USER_IN_COLLAB_SITE" }
    )
    @AlfrescoTest(jira="RM-2417")
    public void checkUnclearedUserCannotEditClassification()
    {
        String documentName = UUID.randomUUID().toString();

        openPage(documentLibrary, COLLAB_SITE_ID);
        documentLibrary.getToolbar()
            .clickOnUpload()
            .uploadFile(documentName);

        // Classify document with admin *explicitly as unclassified*
        documentLibrary.getDocument(documentName)
            .clickOnAction(DocumentActionsPanel.CLASSIFY, classifyContentDialog);

        classifyContentDialog.setLevel(UNCLASSIFIED_CLASSIFICATION_LEVEL_TEXT)
            .setClassifiedBy(CLASSIFIED_BY)
            .setAgency(CLASSIFICATION_AGENCY)
            .addReason(CLASSIFICATION_REASON)
            .clickOnClassify();

        // Check that the Edit classification action is not available for uncleared user
        openPage(UNCLEARED_USER, DEFAULT_PASSWORD, documentLibrary, COLLAB_SITE_ID);
        assertFalse("Edit Classification action should not be available for uncleared user.",
                Arrays.asList(documentLibrary.getDocument(documentName).getClickableActions()).contains(DocumentActions.EDIT_CLASSIFICATION));
        documentLibrary.getDocument(documentName).clickOnLink(documentDetails);
        assertFalse("Edit Classification action should not be available for uncleared user in Document Details.",
                documentDetails.getDocumentActionsPanel().isActionAvailable(DocumentActions.EDIT_CLASSIFICATION));
    }

    /**
     * Check that the 'Classify' action exists for an in-place record.
     *
     * Given that content has been declared as a record
     * And I have sufficient clearance to classify the record
     * When I view the available actions
     * Then the classify action will be available
     */
    @Test
    (
        groups = { "integration" },
        description = "Check that the 'Classify' action exists for an in-place record",
        dependsOnGroups = { "GROUP_IN_PLACE_RECORD_EXISTS" }
    )
    public void checkInplaceRecordCanBeClassified()
    {
        openPage(documentLibrary, COLLAB_SITE_ID);

        InplaceRecord inPlaceRecord = documentLibrary.getInplaceRecord(IN_PLACE_RECORD);
        assertTrue(inPlaceRecord.isActionClickable(DocumentActions.CLASSIFY));
    }

    /**
     * Check that I can't sync classified content to the cloud
     *
     * Given that content has been classified
     * When I view the available options
     * Then I am unable to sync the content
    */
    @Test
    (
        groups = {"integration"},
        description = "Check that we can't sync classified content",
        dependsOnGroups = {"GROUP_SECRET_DOCUMENT_EXISTS"}
    )
    public void checkWeCannotSyncClassifiedContent()
    {
        openPage(documentLibrary, COLLAB_SITE_ID);

        // On Document Library Page
        assertFalse(documentLibrary.getDocument(SECRET_DOCUMENT)
            .isShareDocumentAvailable());

        // On Document Details Page
        assertFalse(documentLibrary.getDocument(SECRET_DOCUMENT)
            .clickOnLink()
            .getSocialActions()
            .isShareDocumentAvailable());
    }

    /*
    RM-2078 Acceptance criteria currently without tests.

    User with no 'write' permission on unclassified content can not set the content classification

    Given that I do not have 'write' permissions on the unclassified content
    And may have a security clearance level above 'No Clearance'
    When I view the unclassified content
    Then I am unable to set a classification
    */
}