/* SPDX-License-Identifier: Apache-2.0 */

// This is a generated file - do not edit - changes should be made to the templates amd/or generator to generate this file with changes.

package org.odpi.openmetadata.accessservices.subjectarea.generated.relationships.GovernancePolicyLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.*;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.*;

import org.odpi.openmetadata.accessservices.subjectarea.generated.relationships.GovernancePolicyLink.GovernancePolicyLink;
import org.odpi.openmetadata.accessservices.subjectarea.properties.enums.*;
import org.odpi.openmetadata.accessservices.subjectarea.ffdc.exceptions.InvalidParameterException;
import org.odpi.openmetadata.accessservices.subjectarea.properties.enums.Status;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.common.SystemAttributes;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.line.Line;
import org.odpi.openmetadata.accessservices.subjectarea.utilities.SubjectAreaUtils;

import java.util.*;

/**
 * Static mapping methods to map between GovernancePolicyLink and the omrs Relationship.
 */
public class GovernancePolicyLinkMapper {
       private static final Logger log = LoggerFactory.getLogger(GovernancePolicyLinkMapper.class);
       private static final String className = GovernancePolicyLinkMapper.class.getName();

       public static GovernancePolicyLink mapOmrsRelationshipToGovernancePolicyLink(Relationship omrsRelationship)  throws InvalidParameterException {
     
               if ("GovernancePolicyLink".equals(omrsRelationship.getType().getTypeDefName())) {
                   GovernancePolicyLink governancePolicyLink = new GovernancePolicyLink(omrsRelationship);

                   SystemAttributes systemAttributes = new SystemAttributes();
                   InstanceStatus instanceStatus =  omrsRelationship.getStatus();
                   Status omrsBeanStatus = SubjectAreaUtils.convertInstanceStatusToStatus(instanceStatus);
                   systemAttributes.setStatus(omrsBeanStatus);
                   systemAttributes.setCreatedBy(omrsRelationship.getCreatedBy());
                   systemAttributes.setUpdatedBy(omrsRelationship.getUpdatedBy());
                   systemAttributes.setCreateTime(omrsRelationship.getCreateTime());
                   systemAttributes.setUpdateTime(omrsRelationship.getUpdateTime());
                   systemAttributes.setVersion(omrsRelationship.getVersion());
                   systemAttributes.setGUID(omrsRelationship.getGUID());
                   governancePolicyLink.setSystemAttributes(systemAttributes);

                   InstanceProperties omrsRelationshipProperties = omrsRelationship.getProperties();
                   Iterator omrsPropertyIterator = omrsRelationshipProperties.getPropertyNames();
                   while (omrsPropertyIterator.hasNext()) {
                       String name = (String) omrsPropertyIterator.next();
                       //TODO check if this is a property we expect or whether the type has been added to.
                       // this is a property we expect
                       InstancePropertyValue value = omrsRelationshipProperties.getPropertyValue(name);
                       // supplied guid matches the expected type
                       Object actualValue;
                       switch (value.getInstancePropertyCategory()) {
                                               case PRIMITIVE:
                                                   PrimitivePropertyValue primitivePropertyValue = (PrimitivePropertyValue) value;
                                                   actualValue = primitivePropertyValue.getPrimitiveValue();
                                                   if (GovernancePolicyLink.ATTRIBUTE_NAMES_SET.contains(name)) {
                                                      if (name.equals("description")) {
                                                           governancePolicyLink.setDescription((String)actualValue);
                                                      }
                                                   } else {
                                                       // put out the omrs value object
                                                       if (governancePolicyLink.getExtraAttributes() ==null) {
                                                            Map<String, Object> extraAttributes = new HashMap();
                                                            governancePolicyLink.setExtraAttributes(extraAttributes);
                                                        }
                                                      governancePolicyLink.getExtraAttributes().put(name, primitivePropertyValue);
                                                   }
                                                   break;
                                               case ENUM:
                                                   EnumPropertyValue enumPropertyValue = (EnumPropertyValue) value;
                                                   String symbolicName = enumPropertyValue.getSymbolicName();
                                                   if (GovernancePolicyLink.ENUM_NAMES_SET.contains(name)) {
                                                   } else {
                                                       // put out the omrs value object
                                                        if (governancePolicyLink.getExtraAttributes() ==null) {
                                                            Map<String, Object> extraAttributes = new HashMap();
                                                            governancePolicyLink.setExtraAttributes(extraAttributes);
                                                        }

                                                        governancePolicyLink.getExtraAttributes().put(name, enumPropertyValue);
                                                    }
                       
                                                   break;
                                               case MAP:
                                                    if (governancePolicyLink.MAP_NAMES_SET.contains(name)) {
                                                        MapPropertyValue mapPropertyValue = (MapPropertyValue) value;
                                                        InstanceProperties instancePropertyForMap = (InstanceProperties) mapPropertyValue.getMapValues();

                                                    }
                                                    break;
                                               case ARRAY:
                                               case STRUCT:
                                               case UNKNOWN:
                                                   // error
                                                   break;
                                           }
                       
                                       }   // end while
                                       return governancePolicyLink;

                   } else {
                       // TODO wrong type for this guid
                   }
                   return null;
       }
       public static Relationship mapGovernancePolicyLinkToOmrsRelationship(GovernancePolicyLink governancePolicyLink) {
           Relationship omrsRelationship = Line.createOmrsRelationship(governancePolicyLink);

           SystemAttributes systemAttributes = governancePolicyLink.getSystemAttributes();
           if (systemAttributes!=null) {
               if (systemAttributes.getCreatedBy()!=null)
                   omrsRelationship.setCreatedBy(systemAttributes.getCreatedBy());
               if (systemAttributes.getUpdatedBy()!=null)
                   omrsRelationship.setUpdatedBy(systemAttributes.getUpdatedBy());
               if (systemAttributes.getCreateTime()!=null)
                   omrsRelationship.setCreateTime(systemAttributes.getCreateTime());
               if (systemAttributes.getUpdateTime()!=null)
                   omrsRelationship.setUpdateTime(systemAttributes.getUpdateTime());
               if (systemAttributes.getVersion()!=null)
                   omrsRelationship.setVersion(systemAttributes.getVersion());
               if (systemAttributes.getStatus()!=null) {
                   InstanceStatus instanceStatus = SubjectAreaUtils.convertStatusToStatusInstance(systemAttributes.getStatus());
                   omrsRelationship.setStatus(instanceStatus);
               }
           }

           InstanceProperties instanceProperties = new InstanceProperties();
           // primitives

            if (governancePolicyLink.getDescription()!=null) {
                PrimitivePropertyValue primitivePropertyValue = new PrimitivePropertyValue();
                primitivePropertyValue.setPrimitiveDefCategory(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_STRING);
                primitivePropertyValue.setPrimitiveValue(governancePolicyLink.getDescription());
                instanceProperties.setProperty("description", primitivePropertyValue);
            }
            omrsRelationship.setProperties(instanceProperties);

           return omrsRelationship;
       }
}