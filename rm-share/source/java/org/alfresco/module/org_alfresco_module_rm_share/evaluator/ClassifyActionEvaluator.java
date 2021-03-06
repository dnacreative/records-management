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
package org.alfresco.module.org_alfresco_module_rm_share.evaluator;

import java.util.ArrayList;

import org.alfresco.web.evaluator.HasAspectEvaluator;
import org.json.simple.JSONObject;

/**
 * Checks if the user can classify a content
 *
 * @author David Webster
 * @since 3.0.a
 */
public class ClassifyActionEvaluator extends HasAspectEvaluator
{
    /** Node attribute */
    private static final String NODE = "node";

    /** Classified aspect */
    private static final String ASPECT_CLASSIFIED = "clf:classified";

    /** Current classification property */
    private static final String PROP_CURRENT_CLASSIFICATION = "clf:currentClassification";

    /** Has the current user clearance */
    private static final String HAS_CURRENT_USER_CLEARANCE = "hasCurrentUserClearance";

    /**
     * @see org.alfresco.web.evaluator.HasAspectEvaluator#evaluate(org.json.simple.JSONObject)
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;

        JSONObject node = (JSONObject) jsonObject.get(NODE);
        if (node != null && (Boolean) node.get(HAS_CURRENT_USER_CLEARANCE))
        {
            ArrayList<String> aspects = new ArrayList<>();
            aspects.add(ASPECT_CLASSIFIED);
            setAspects(aspects);

            if (super.evaluate(jsonObject))
            {
                if ((getProperty(jsonObject, PROP_CURRENT_CLASSIFICATION)) == null)
                {
                    result = true;
                }
            }
            else
            {
                result = true;
            }
        }

        return result;
    }
}