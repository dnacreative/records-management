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
package org.alfresco.module.org_alfresco_module_rm.script.classification;

import static org.alfresco.module.org_alfresco_module_rm.script.classification.ClassifyContentBase.CLASSIFICATION_AGENCY;
import static org.alfresco.module.org_alfresco_module_rm.script.classification.ClassifyContentBase.CLASSIFICATION_LEVEL_ID;
import static org.alfresco.module.org_alfresco_module_rm.script.classification.ClassifyContentBase.CLASSIFICATION_REASONS;
import static org.alfresco.module.org_alfresco_module_rm.script.classification.ClassifyContentBase.CLASSIFIED_BY;
import static org.alfresco.util.WebScriptUtils.getStringValueFromJSONObject;
import static org.alfresco.util.WebScriptUtils.is4xxError;
import static org.alfresco.util.WebScriptUtils.putValuetoJSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationAspectProperties;
import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationLevelManager;
import org.alfresco.module.org_alfresco_module_rm.classification.ClassificationReasonManager;
import org.alfresco.module.org_alfresco_module_rm.classification.ContentClassificationService;
import org.alfresco.module.org_alfresco_module_rm.test.util.BaseWebScriptUnitTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptException;

import com.google.common.collect.Sets;

/**
 * Classify content REST API POST implementation unit test.
 *
 * @author Tuna Aksoy
 * @since 3.0.a
 */
public class ClassifyContentPostUnitTest extends BaseWebScriptUnitTest
{
    /** Classpath location of ftl template for web script */
    private static final String WEBSCRIPT_TEMPLATE = WEBSCRIPT_ROOT_RM + "classification/classifycontent.post.json.ftl";

    /** Constants */
    private static final String STORE_TYPE = "store_type";
    private static final String STORE_ID = "store_id";
    private static final String ID = "id";
    private static final String SUCCESS = "success";
    private static final String LEVEL_ID = "aLevelId";
    private static final String BY = "bySomeone";
    private static final String AGENCY = "anAgency";
    private static final String REASON1_ID = "reason1Id";
    private static final String REASON2_ID = "reason2Id";

    /** ClassifyContentPost webscript instance */
    private @Spy @InjectMocks ClassifyContentPost webScript;

    /** Mocked content classification service */
    private @Mock ContentClassificationService mockedContentClassificationService;
    /** Mocked classification level manager */
    private @Mock ClassificationLevelManager mockedClassificationLevelManager;
    /** Mocked classification reason manager */
    private @Mock ClassificationReasonManager mockedClassificationReasonManager;

    /** Captor for the classification aspect properties. */
    private @Captor ArgumentCaptor<ClassificationAspectProperties> propertiesDTOCaptor;
    /**
     * @see org.alfresco.module.org_alfresco_module_rm.test.util.BaseWebScriptUnitTest#getWebScript()
     */
    @Override
    protected DeclarativeWebScript getWebScript()
    {
        return webScript;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_rm.test.util.BaseWebScriptUnitTest#getWebScriptTemplate()
     */
    @Override
    protected String getWebScriptTemplate()
    {
        return WEBSCRIPT_TEMPLATE;
    }

    @Test
    public void testClassifyContent() throws Exception
    {
        // Setup web script parameters
        Map<String, String> parameters = buildParameters
        (
            STORE_TYPE,       record.getStoreRef().getProtocol(),
            STORE_ID,         record.getStoreRef().getIdentifier(),
            ID,               record.getId()
        );

        // Build JSON to send to server
        String content = buildContent().toString();

        // Execute web script
        JSONObject json = executeJSONWebScript(parameters, content);
        assertNotNull(json);
        assertTrue(json.has(SUCCESS));
        assertEquals(getStringValueFromJSONObject(json, SUCCESS), Boolean.TRUE.toString());

        // Verify that the classify content method was called
        verify(mockedContentClassificationService, times(1)).classifyContent(propertiesDTOCaptor.capture(), eq(record));

        // Check the values in the DTO.
        ClassificationAspectProperties propertiesDTO = propertiesDTOCaptor.getValue();
        assertEquals(LEVEL_ID, propertiesDTO.getClassificationLevelId());
        assertEquals(BY, propertiesDTO.getClassifiedBy());
        assertEquals(AGENCY, propertiesDTO.getClassificationAgency());
        assertEquals(Sets.newHashSet(REASON1_ID, REASON2_ID), propertiesDTO.getClassificationReasonIds());
    }

    @Test
    public void classifyingWithBlankClassifiedByShouldReturn4xxResponse() throws Exception
    {
        // Setup web script parameters
        Map<String, String> parameters = buildParameters
        (
            STORE_TYPE,       record.getStoreRef().getProtocol(),
            STORE_ID,         record.getStoreRef().getIdentifier(),
            ID,               record.getId()
        );

        final String whitespace = "  \t  ";

        JSONObject jsonObj = buildContent();
        putValuetoJSONObject(jsonObj, CLASSIFIED_BY, whitespace);
        String json = jsonObj.toString();

        // Execute web script
        boolean exceptionThrown = false;
        try
        {
            executeJSONWebScript(parameters, json);
        }
        catch (WebScriptException expected)
        {
            exceptionThrown = true;
            assertTrue("HTTP rsp should have been a 400 error. Was " + expected.getStatus(),
                       is4xxError(expected));
        }

        assertTrue("Expected exception was not thrown", exceptionThrown);
    }

    /** Helper method to build the request content. */
    private JSONObject buildContent()
    {
        JSONObject content = new JSONObject();
        putValuetoJSONObject(content, CLASSIFICATION_LEVEL_ID, LEVEL_ID);
        putValuetoJSONObject(content, CLASSIFIED_BY, BY);
        putValuetoJSONObject(content, CLASSIFICATION_AGENCY, AGENCY);

        JSONObject classificationReason1 = new JSONObject();
        putValuetoJSONObject(classificationReason1, ID, REASON1_ID);
        JSONObject classificationReason2 = new JSONObject();
        putValuetoJSONObject(classificationReason2, ID, REASON2_ID);

        JSONArray classificationReasons = new JSONArray();
        classificationReasons.put(classificationReason1);
        classificationReasons.put(classificationReason2);
        putValuetoJSONObject(content, CLASSIFICATION_REASONS, classificationReasons);

        return content;
    }
}
