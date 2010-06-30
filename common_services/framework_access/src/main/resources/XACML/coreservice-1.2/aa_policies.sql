--
-- PostgreSQL database dump
--

-- Started on 2010-06-28 14:06:25

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = aa, pg_catalog;

--
-- TOC entry 2013 (class 0 OID 138046)
-- Dependencies: 1678
-- Data for Name: escidoc_policies; Type: TABLE DATA; Schema: aa; Owner: postgres
--

INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:administrator-policy-1', 'escidoc:role-administrator', '<Policy PolicyId="Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>

      <AnySubject/>
    </Subjects>
    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"> 
            info:escidoc/names:aa:1.0:action:update-context 
            info:escidoc/names:aa:1.0:action:retrieve-context 
            info:escidoc/names:aa:1.0:action:close-context 
            info:escidoc/names:aa:1.0:action:open-context 
            info:escidoc/names:aa:1.0:action:create-grant 
            info:escidoc/names:aa:1.0:action:revoke-grant 
            info:escidoc/names:aa:1.0:action:retrieve-grant 
            info:escidoc/names:aa:1.0:action:create-user-group-grant 
            info:escidoc/names:aa:1.0:action:retrieve-user-group-grant 
            info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
            info:escidoc/names:aa:1.0:action:add-user-group-selectors 
            info:escidoc/names:aa:1.0:action:remove-user-group-selectors 
            info:escidoc/names:aa:1.0:action:withdraw-container 
            info:escidoc/names:aa:1.0:action:withdraw-item 
            info:escidoc/names:aa:1.0:action:revise-container 
            info:escidoc/names:aa:1.0:action:revise-item 
            info:escidoc/names:aa:1.0:action:retrieve-container 
            info:escidoc/names:aa:1.0:action:retrieve-item 
            info:escidoc/names:aa:1.0:action:retrieve-user-account 
            info:escidoc/names:aa:1.0:action:retrieve-user-group 
          </AttributeValue>

          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Administrator-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>

      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:audience-policy-1', 'escidoc:role-audience', '<Policy PolicyId="Audience-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                    info:escidoc/names:aa:1.0:action:retrieve-content 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Audience-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <Action>
          <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                info:escidoc/names:aa:1.0:action:retrieve-content 
            </AttributeValue>
            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

          </ActionMatch>
        </Action>
      </Actions>
    </Target>
    <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
          <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>

          </Apply>
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
        </Apply>
      </Apply>
      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:visibility" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">audience</AttributeValue>
      </Apply>

      <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
          <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Apply>
        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
      </Apply>
    </Condition>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:collaborator-policy-1', 'escidoc:role-collaborator', '<Policy PolicyId="Collaborator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:retrieve-item 
                                info:escidoc/names:aa:1.0:action:retrieve-container 
                                info:escidoc/names:aa:1.0:action:retrieve-content
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Collaborator-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:collaborator-modifier-policy-1', 'escidoc:role-collaborator-modifier', '<Policy PolicyId="Collaborator-Modifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:retrieve-item 
                                info:escidoc/names:aa:1.0:action:update-item 
                                info:escidoc/names:aa:1.0:action:lock-item 
                                info:escidoc/names:aa:1.0:action:unlock-item 
                                info:escidoc/names:aa:1.0:action:retrieve-container 
                                info:escidoc/names:aa:1.0:action:update-container 
                                info:escidoc/names:aa:1.0:action:lock-container 
                                info:escidoc/names:aa:1.0:action:unlock-container 
                                info:escidoc/names:aa:1.0:action:retrieve-content 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Collaborator-modifier-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:collaborator-modifier-container-add-remove-any-members-policy-1', 'escidoc:role-collaborator-modifier-container-add-remove-any-members', '<Policy PolicyId="Collaborator-Modifier-Container-Add-Remove-any-Members-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:retrieve-container 
                                info:escidoc/names:aa:1.0:action:update-container 
                                info:escidoc/names:aa:1.0:action:lock-container 
                                info:escidoc/names:aa:1.0:action:unlock-container 
                                info:escidoc/names:aa:1.0:action:retrieve-item 
                                info:escidoc/names:aa:1.0:action:retrieve-content 
                                info:escidoc/names:aa:1.0:action:add-members-to-container
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Collaborator-modifier-Container-Add-Remove-any-Members-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:collaborator-modifier-container-add-remove-members-policy-1', 'escidoc:role-collaborator-modifier-container-add-remove-members', '<Policy PolicyId="Collaborator-Modifier-Container-Add-Remove-Members-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:retrieve-container 
                                info:escidoc/names:aa:1.0:action:update-container 
                                info:escidoc/names:aa:1.0:action:lock-container 
                                info:escidoc/names:aa:1.0:action:unlock-container 
                                info:escidoc/names:aa:1.0:action:add-members-to-container
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Collaborator-modifier-Container-Add-Remove-Members-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:collaborator-modifier-container-update-any-members-policy-1', 'escidoc:role-collaborator-modifier-container-update-any-members', '<Policy PolicyId="Collaborator-Modifier-Container-Update-any-Members-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:retrieve-item 
                                info:escidoc/names:aa:1.0:action:update-item 
                                info:escidoc/names:aa:1.0:action:lock-item 
                                info:escidoc/names:aa:1.0:action:unlock-item 
                                info:escidoc/names:aa:1.0:action:retrieve-content 
                                info:escidoc/names:aa:1.0:action:retrieve-container 
                                info:escidoc/names:aa:1.0:action:update-container 
                                info:escidoc/names:aa:1.0:action:lock-container 
                                info:escidoc/names:aa:1.0:action:unlock-container 
                                info:escidoc/names:aa:1.0:action:add-members-to-container
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Collaborator-modifier-Container-Add-Remove-any-Members-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:collaborator-modifier-container-update-direct-members-policy-1', 'escidoc:role-collaborator-modifier-container-update-direct-members', '<Policy PolicyId="Collaborator-Modifier-Container-update-direct-members-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:retrieve-item 
                                info:escidoc/names:aa:1.0:action:update-item 
                                info:escidoc/names:aa:1.0:action:lock-item 
                                info:escidoc/names:aa:1.0:action:unlock-item 
                                info:escidoc/names:aa:1.0:action:retrieve-content 
                                info:escidoc/names:aa:1.0:action:retrieve-container 
                                info:escidoc/names:aa:1.0:action:update-container 
                                info:escidoc/names:aa:1.0:action:lock-container 
                                info:escidoc/names:aa:1.0:action:unlock-container 
                                info:escidoc/names:aa:1.0:action:add-members-to-container
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>

        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Collaborator-modifier-Container-update-direct-members-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:content-relation-manager-policy-1', 'escidoc:role-content-relation-manager', '
<Policy PolicyId="ContentRelationManager-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:create-content-relation 
                        info:escidoc/names:aa:1.0:action:delete-content-relation 
                        info:escidoc/names:aa:1.0:action:retrieve-content-relation 
                        info:escidoc/names:aa:1.0:action:update-content-relation 
                        info:escidoc/names:aa:1.0:action:submit-content-relation 
                        info:escidoc/names:aa:1.0:action:release-content-relation 
                        info:escidoc/names:aa:1.0:action:revise-content-relation 
                        info:escidoc/names:aa:1.0:action:withdraw-content-relation 
                        info:escidoc/names:aa:1.0:action:lock-content-relation 
                        info:escidoc/names:aa:1.0:action:unlock-content-relation 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="ContentRelationManager-policy-rule-0" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:create-content-relation 
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        </Rule>
        <Rule RuleId="ContentRelationManager-policy-rule-1" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                info:escidoc/names:aa:1.0:action:delete-content-relation 
                                info:escidoc/names:aa:1.0:action:retrieve-content-relation 
                                info:escidoc/names:aa:1.0:action:update-content-relation 
                                info:escidoc/names:aa:1.0:action:submit-content-relation 
                                info:escidoc/names:aa:1.0:action:release-content-relation 
                                info:escidoc/names:aa:1.0:action:revise-content-relation 
                                info:escidoc/names:aa:1.0:action:withdraw-content-relation 
                                info:escidoc/names:aa:1.0:action:lock-content-relation 
                                info:escidoc/names:aa:1.0:action:unlock-content-relation 
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:content-relation:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
    </Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:depositor-policy-1', 'escidoc:role-depositor', '<PolicySet PolicySetId="Depositor-policies" PolicyCombiningAlgId="urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides">
    <Target>
        <Subjects>
            <AnySubject/>
        </Subjects>
        <Resources>
            <AnyResource/>
        </Resources>
        <Actions>
            <AnyAction/>
        </Actions>
    </Target>
    <Policy PolicyId="Depositor-policy-staging-file" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:create-staging-file
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="Depositor-policy-staging-file-Rule" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <AnyAction/>
                </Actions>
            </Target>
        </Rule>
    </Policy>
    <Policy PolicyId="Depositor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:create-container 
                        info:escidoc/names:aa:1.0:action:create-item 
                        info:escidoc/names:aa:1.0:action:retrieve-container 
                        info:escidoc/names:aa:1.0:action:retrieve-item 
                        info:escidoc/names:aa:1.0:action:update-container 
                        info:escidoc/names:aa:1.0:action:delete-container 
                        info:escidoc/names:aa:1.0:action:add-members-to-container 
                        info:escidoc/names:aa:1.0:action:remove-members-from-container 
                        info:escidoc/names:aa:1.0:action:lock-container 
                        info:escidoc/names:aa:1.0:action:update-item 
                        info:escidoc/names:aa:1.0:action:delete-item 
                        info:escidoc/names:aa:1.0:action:lock-item 
                        info:escidoc/names:aa:1.0:action:retrieve-content 
                        info:escidoc/names:aa:1.0:action:submit-container 
                        info:escidoc/names:aa:1.0:action:withdraw-container 
                        info:escidoc/names:aa:1.0:action:submit-item 
                        info:escidoc/names:aa:1.0:action:withdraw-item 
                        info:escidoc/names:aa:1.0:action:release-item 
                        info:escidoc/names:aa:1.0:action:release-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="Depositor-policy-rule-0" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:create-container 
                            info:escidoc/names:aa:1.0:action:create-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-1a" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:retrieve-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-1b" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:retrieve-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-2" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:delete-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">in-revision</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-2b" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:update-container 
                            info:escidoc/names:aa:1.0:action:add-members-to-container 
                            info:escidoc/names:aa:1.0:action:remove-members-from-container 
                            info:escidoc/names:aa:1.0:action:lock-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
              <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
<Rule RuleId="Depositor-policy-rule-3" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:delete-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">in-revision</AttributeValue>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
                <Rule RuleId="Depositor-policy-rule-3b" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:update-item 
                            info:escidoc/names:aa:1.0:action:delete-item 
                            info:escidoc/names:aa:1.0:action:lock-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                    <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending released in-revision submitted</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
            </Condition>
        </Rule>
        <Rule RuleId="Depositor-policy-rule-4" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:retrieve-content
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
            <Rule RuleId="Depositor-policy-rule-5" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:submit-container 
                            info:escidoc/names:aa:1.0:action:withdraw-container 
                            info:escidoc/names:aa:1.0:action:release-container
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
                <Rule RuleId="Depositor-policy-rule-6" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:submit-item 
                            info:escidoc/names:aa:1.0:action:withdraw-item 
                            info:escidoc/names:aa:1.0:action:release-item
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Condition>
        </Rule>
    </Policy>
</PolicySet>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-ingester', 'escidoc:role-ingester', '<Policy PolicyId="Ingester-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>
    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                    info:escidoc/names:aa:1.0:action:ingest 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Ingester-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:md-editor-policy-container', 'escidoc:role-md-editor', '<Policy PolicyId="MD-Editor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
    <Target>
        <Subjects>
            <AnySubject/>
        </Subjects>
        <Resources>
            <AnyResource/>
        </Resources>
        <Actions>
            <Action>
                <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                    info:escidoc/names:aa:1.0:action:retrieve-item 
                    info:escidoc/names:aa:1.0:action:update-item 
                    info:escidoc/names:aa:1.0:action:lock-item 
                    info:escidoc/names:aa:1.0:action:submit-item  
                    info:escidoc/names:aa:1.0:action:retrieve-content 
                    info:escidoc/names:aa:1.0:action:retrieve-container 
                    info:escidoc/names:aa:1.0:action:update-container 
                    info:escidoc/names:aa:1.0:action:lock-container 
                    info:escidoc/names:aa:1.0:action:submit-container 
                    info:escidoc/names:aa:1.0:action:revise-item 
                    info:escidoc/names:aa:1.0:action:revise-container 
                    info:escidoc/names:aa:1.0:action:add-members-to-container 
                    info:escidoc/names:aa:1.0:action:remove-members-from-container
                    </AttributeValue>
                    <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </ActionMatch>
            </Action>
        </Actions>
    </Target>
    <Rule RuleId="MD-Editor-policy-rule-si" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:submit-item 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-retrievei" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:retrieve-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released in-revision withdrawn</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted released in-revision</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-mi" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:update-item 
                        info:escidoc/names:aa:1.0:action:lock-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-revisei" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:revise-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-rcontent" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:retrieve-content
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-sc" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:submit-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-retrievec" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:retrieve-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released in-revision withdrawn</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted released in-revision</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="MD-Editor-policy-rule-mc" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:update-container 
                        info:escidoc/names:aa:1.0:action:lock-container 
                        info:escidoc/names:aa:1.0:action:add-members-to-container 
                        info:escidoc/names:aa:1.0:action:remove-members-from-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-revisec" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:revise-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:privileged-viewer-policy-1', 'escidoc:role-privileged-viewer', '<Policy PolicyId="Privileged-Viewer-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <Action>
          <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                info:escidoc/names:aa:1.0:action:retrieve-content 
            </AttributeValue>
            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
          </ActionMatch>

        </Action>
      </Actions>
    </Target>
    <Rule RuleId="Privileged-Viewer-policy-rule-0" Effect="Permit">
      <Target>
        <Subjects>
          <AnySubject/>
        </Subjects>
        <Resources>
          <AnyResource/>
        </Resources>
        <Actions>
          <Action>
            <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
              <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                info:escidoc/names:aa:1.0:action:retrieve-content 
              </AttributeValue>
              <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </ActionMatch>
          </Action>
        </Actions>
      </Target>
    </Rule>
  </Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:statistics-editor-policy-1', 'escidoc:role-statistics-editor', '<Policy PolicyId="Statistics-editor-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                                    info:escidoc/names:aa:1.0:action:create-aggregation-definition 
                                    info:escidoc/names:aa:1.0:action:create-report-definition 
                                    info:escidoc/names:aa:1.0:action:create-statistic-data 
                                    info:escidoc/names:aa:1.0:action:delete-aggregation-definition 
                                    info:escidoc/names:aa:1.0:action:delete-report-definition 
                                    info:escidoc/names:aa:1.0:action:retrieve-scope 
                                    info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition 
                                    info:escidoc/names:aa:1.0:action:retrieve-report-definition 
                                    info:escidoc/names:aa:1.0:action:update-scope 
                                    info:escidoc/names:aa:1.0:action:update-report-definition 
                                    info:escidoc/names:aa:1.0:action:preprocess-statistics</AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Statistics-Editor-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:statistics-reader-policy-1', 'escidoc:role-statistics-reader', '<Policy PolicyId="Statistics-reader-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>

    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
            info:escidoc/names:aa:1.0:action:retrieve-report 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Statistics-reader-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>

      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <Action>
          <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                info:escidoc/names:aa:1.0:action:retrieve-report 
            </AttributeValue>
            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
          </ActionMatch>
        </Action>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-system-administrator', 'escidoc:role-system-administrator', '<Policy PolicyId="System-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>
    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:retrieve-method-mappings 
                            info:escidoc/names:aa:1.0:action:ingest 
                            info:escidoc/names:aa:1.0:action:check-user-privilege 
                            info:escidoc/names:aa:1.0:action:create-role 
                            info:escidoc/names:aa:1.0:action:delete-role 
                            info:escidoc/names:aa:1.0:action:retrieve-role 
                            info:escidoc/names:aa:1.0:action:update-role 
                            info:escidoc/names:aa:1.0:action:evaluate 
                            info:escidoc/names:aa:1.0:action:find-attribute 
                            info:escidoc/names:aa:1.0:action:retrieve-roles 
                            info:escidoc/names:aa:1.0:action:create-user-account 
                            info:escidoc/names:aa:1.0:action:delete-user-account 
                            info:escidoc/names:aa:1.0:action:retrieve-user-account 
                            info:escidoc/names:aa:1.0:action:update-user-account 
                            info:escidoc/names:aa:1.0:action:activate-user-account 
                            info:escidoc/names:aa:1.0:action:deactivate-user-account 
                            info:escidoc/names:aa:1.0:action:create-grant 
                            info:escidoc/names:aa:1.0:action:retrieve-grant 
                            info:escidoc/names:aa:1.0:action:retrieve-current-grants 
                            info:escidoc/names:aa:1.0:action:revoke-grant 
                            info:escidoc/names:aa:1.0:action:create-user-group 
                            info:escidoc/names:aa:1.0:action:delete-user-group 
                            info:escidoc/names:aa:1.0:action:retrieve-user-group 
                            info:escidoc/names:aa:1.0:action:update-user-group 
                            info:escidoc/names:aa:1.0:action:activate-user-group 
                            info:escidoc/names:aa:1.0:action:deactivate-user-group 
                            info:escidoc/names:aa:1.0:action:add-user-group-selectors 
                            info:escidoc/names:aa:1.0:action:remove-user-group-selectors 
                            info:escidoc/names:aa:1.0:action:create-user-group-grant 
                            info:escidoc/names:aa:1.0:action:retrieve-user-group-grant 
                            info:escidoc/names:aa:1.0:action:revoke-user-group-grant 
                            info:escidoc/names:aa:1.0:action:logout 
                            info:escidoc/names:aa:1.0:action:create-unsecured-actions 
                            info:escidoc/names:aa:1.0:action:delete-unsecured-actions 
                            info:escidoc/names:aa:1.0:action:retrieve-unsecured-actions 
                            info:escidoc/names:aa:1.0:action:create-metadata-schema 
                            info:escidoc/names:aa:1.0:action:delete-metadata-schema 
                            info:escidoc/names:aa:1.0:action:retrieve-metadata-schema 
                            info:escidoc/names:aa:1.0:action:update-metadata-schema 
                            info:escidoc/names:aa:1.0:action:create-container 
                            info:escidoc/names:aa:1.0:action:delete-container 
                            info:escidoc/names:aa:1.0:action:update-container 
                            info:escidoc/names:aa:1.0:action:retrieve-container 
                            info:escidoc/names:aa:1.0:action:submit-container 
                            info:escidoc/names:aa:1.0:action:release-container 
                            info:escidoc/names:aa:1.0:action:revise-container 
                            info:escidoc/names:aa:1.0:action:withdraw-container 
                            info:escidoc/names:aa:1.0:action:container-move-to-context 
                            info:escidoc/names:aa:1.0:action:add-members-to-container 
                            info:escidoc/names:aa:1.0:action:remove-members-from-container 
                            info:escidoc/names:aa:1.0:action:lock-container 
                            info:escidoc/names:aa:1.0:action:unlock-container 
                            info:escidoc/names:aa:1.0:action:create-content-model 
                            info:escidoc/names:aa:1.0:action:delete-content-model 
                            info:escidoc/names:aa:1.0:action:retrieve-content-model 
                            info:escidoc/names:aa:1.0:action:update-content-model 
                            info:escidoc/names:aa:1.0:action:create-context 
                            info:escidoc/names:aa:1.0:action:delete-context 
                            info:escidoc/names:aa:1.0:action:retrieve-context 
                            info:escidoc/names:aa:1.0:action:update-context 
                            info:escidoc/names:aa:1.0:action:close-context 
                            info:escidoc/names:aa:1.0:action:open-context 
                            info:escidoc/names:aa:1.0:action:create-item 
                            info:escidoc/names:aa:1.0:action:delete-item 
                            info:escidoc/names:aa:1.0:action:retrieve-item 
                            info:escidoc/names:aa:1.0:action:update-item 
                            info:escidoc/names:aa:1.0:action:submit-item 
                            info:escidoc/names:aa:1.0:action:release-item 
                            info:escidoc/names:aa:1.0:action:revise-item 
                            info:escidoc/names:aa:1.0:action:withdraw-item 
                            info:escidoc/names:aa:1.0:action:retrieve-content 
                            info:escidoc/names:aa:1.0:action:item-move-to-context 
                            info:escidoc/names:aa:1.0:action:lock-item 
                            info:escidoc/names:aa:1.0:action:unlock-item 
                            info:escidoc/names:aa:1.0:action:create-toc 
                            info:escidoc/names:aa:1.0:action:delete-toc 
                            info:escidoc/names:aa:1.0:action:retrieve-toc 
                            info:escidoc/names:aa:1.0:action:update-toc 
                            info:escidoc/names:aa:1.0:action:submit-toc 
                            info:escidoc/names:aa:1.0:action:release-toc 
                            info:escidoc/names:aa:1.0:action:revise-toc 
                            info:escidoc/names:aa:1.0:action:withdraw-toc 
                            info:escidoc/names:aa:1.0:action:create-content-relation 
                            info:escidoc/names:aa:1.0:action:delete-content-relation 
                            info:escidoc/names:aa:1.0:action:retrieve-content-relation 
                            info:escidoc/names:aa:1.0:action:update-content-relation 
                            info:escidoc/names:aa:1.0:action:submit-content-relation 
                            info:escidoc/names:aa:1.0:action:release-content-relation 
                            info:escidoc/names:aa:1.0:action:revise-content-relation 
                            info:escidoc/names:aa:1.0:action:withdraw-content-relation 
                            info:escidoc/names:aa:1.0:action:lock-content-relation 
                            info:escidoc/names:aa:1.0:action:unlock-content-relation 
                            info:escidoc/names:aa:1.0:action:query-semantic-store 
                            info:escidoc/names:aa:1.0:action:create-xml-schema 
                            info:escidoc/names:aa:1.0:action:delete-xml-schema 
                            info:escidoc/names:aa:1.0:action:retrieve-xml-schema 
                            info:escidoc/names:aa:1.0:action:update-xml-schema 
                            info:escidoc/names:aa:1.0:action:create-organizational-unit 
                            info:escidoc/names:aa:1.0:action:delete-organizational-unit 
                            info:escidoc/names:aa:1.0:action:retrieve-organizational-unit 
                            info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit 
                            info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit 
                            info:escidoc/names:aa:1.0:action:update-organizational-unit 
                            info:escidoc/names:aa:1.0:action:open-organizational-unit 
                            info:escidoc/names:aa:1.0:action:close-organizational-unit 
                            info:escidoc/names:aa:1.0:action:fmdh-export 
                            info:escidoc/names:aa:1.0:action:fadh-get-datastream-dissemination 
                            info:escidoc/names:aa:1.0:action:fddh-get-fedora-description 
                            info:escidoc/names:aa:1.0:action:create-aggregation-definition 
                            info:escidoc/names:aa:1.0:action:delete-aggregation-definition 
                            info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition 
                            info:escidoc/names:aa:1.0:action:update-aggregation-definition 
                            info:escidoc/names:aa:1.0:action:create-report-definition 
                            info:escidoc/names:aa:1.0:action:delete-report-definition 
                            info:escidoc/names:aa:1.0:action:retrieve-report-definition 
                            info:escidoc/names:aa:1.0:action:update-report-definition 
                            info:escidoc/names:aa:1.0:action:retrieve-report 
                            info:escidoc/names:aa:1.0:action:create-statistic-data 
                            info:escidoc/names:aa:1.0:action:create-scope 
                            info:escidoc/names:aa:1.0:action:delete-scope 
                            info:escidoc/names:aa:1.0:action:retrieve-scope 
                            info:escidoc/names:aa:1.0:action:update-scope 
                            info:escidoc/names:aa:1.0:action:preprocess-statistics 
                            info:escidoc/names:aa:1.0:action:create-workflow-type 
                            info:escidoc/names:aa:1.0:action:delete-workflow-type 
                            info:escidoc/names:aa:1.0:action:retrieve-workflow-type 
                            info:escidoc/names:aa:1.0:action:update-workflow-type 
                            info:escidoc/names:aa:1.0:action:create-workflow-template 
                            info:escidoc/names:aa:1.0:action:delete-workflow-template 
                            info:escidoc/names:aa:1.0:action:retrieve-workflow-template 
                            info:escidoc/names:aa:1.0:action:update-workflow-template 
                            info:escidoc/names:aa:1.0:action:create-workflow-definition 
                            info:escidoc/names:aa:1.0:action:delete-workflow-definition 
                            info:escidoc/names:aa:1.0:action:retrieve-workflow-definition 
                            info:escidoc/names:aa:1.0:action:update-workflow-definition 
                            info:escidoc/names:aa:1.0:action:create-workflow-instance 
                            info:escidoc/names:aa:1.0:action:delete-workflow-instance 
                            info:escidoc/names:aa:1.0:action:retrieve-workflow-instance 
                            info:escidoc/names:aa:1.0:action:update-workflow-instance 
                            info:escidoc/names:aa:1.0:action:create-staging-file 
                            info:escidoc/names:aa:1.0:action:retrieve-staging-file 
                            info:escidoc/names:aa:1.0:action:extract-metadata 
                            info:escidoc/names:aa:1.0:action:delete-objects 
                            info:escidoc/names:aa:1.0:action:get-purge-status 
                            info:escidoc/names:aa:1.0:action:get-recache-status 
                            info:escidoc/names:aa:1.0:action:get-reindex-status 
                            info:escidoc/names:aa:1.0:action:decrease-reindex-status 
                            info:escidoc/names:aa:1.0:action:recache 
                            info:escidoc/names:aa:1.0:action:reindex 
                            info:escidoc/names:aa:1.0:action:load-examples 
    			    info:escidoc/names:aa:1.0:action:create-set-definition 
    			    info:escidoc/names:aa:1.0:action:update-set-definition 
			    info:escidoc/names:aa:1.0:action:delete-set-definition
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="System-Administrator-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:moderator-policy-1', 'escidoc:role-moderator', '<Policy PolicyId="Moderator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
    <Target>
        <Subjects>
            <AnySubject/>
        </Subjects>
        <Resources>
            <AnyResource/>
        </Resources>
        <Actions>
            <Action>
                <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                    info:escidoc/names:aa:1.0:action:retrieve-item 
                    info:escidoc/names:aa:1.0:action:update-item 
                    info:escidoc/names:aa:1.0:action:lock-item 
                    info:escidoc/names:aa:1.0:action:submit-item 
                    info:escidoc/names:aa:1.0:action:revise-item 
                    info:escidoc/names:aa:1.0:action:release-item 
                    info:escidoc/names:aa:1.0:action:retrieve-content 
                    info:escidoc/names:aa:1.0:action:retrieve-container 
                    info:escidoc/names:aa:1.0:action:update-container 
                    info:escidoc/names:aa:1.0:action:lock-container 
                    info:escidoc/names:aa:1.0:action:add-members-to-container 
                    info:escidoc/names:aa:1.0:action:remove-members-from-container  
                    info:escidoc/names:aa:1.0:action:submit-container 
                    info:escidoc/names:aa:1.0:action:revise-container 
                    info:escidoc/names:aa:1.0:action:release-container 
                    info:escidoc/names:aa:1.0:action:create-grant 
                    info:escidoc/names:aa:1.0:action:create-user-group-grant 
                    </AttributeValue>
                    <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </ActionMatch>
            </Action>
        </Actions>
    </Target>
    <Rule RuleId="Moderator-policy-rule-si" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:submit-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-revisei" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:revise-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-ri" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:release-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-retrievei" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:retrieve-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released in-revision withdrawn</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted released in-revision</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-mi" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:update-item 
                        info:escidoc/names:aa:1.0:action:lock-item
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:latest-version-modified-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
                    </Apply>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-rcontent" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:retrieve-content
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-sc" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:submit-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-revisec" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:revise-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-rc" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:release-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted</AttributeValue>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-retrievec" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:retrieve-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released in-revision withdrawn</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending submitted released in-revision</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-mc" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:update-container 
                        info:escidoc/names:aa:1.0:action:lock-container 
                        info:escidoc/names:aa:1.0:action:add-members-to-container 
                        info:escidoc/names:aa:1.0:action:remove-members-from-container
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
            <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                    <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                </Apply>
            </Apply>
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
                <Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
                    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">submitted released</AttributeValue>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                        <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </Apply>
                </Apply>
                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:latest-version-modified-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                    </Apply>
                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                            <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </Apply>
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">pending</AttributeValue>
                    </Apply>
                </Apply>
            </Apply>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-grant-create" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:create-grant 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on:context" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-moderator:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Condition>
    </Rule>
    <Rule RuleId="Moderator-policy-rule-user-group-grant-create" Effect="Permit">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:create-user-group-grant 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
            <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
                <ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on:context" DataType="http://www.w3.org/2001/XMLSchema#string"/>
            </Apply>
            <SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-moderator:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </Condition>
    </Rule>
    <!-- not needed any longer, some roles can be retrieved by default policies. User accounts can be retrieved in any case if this user has user-account-admin privileges. Not part of the moderator policy 
    <Rule RuleId="Moderator-policy-rule-cregrpandgrnts" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <Action>
                        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                            info:escidoc/names:aa:1.0:action:retrieve-user-account 
                            info:escidoc/names:aa:1.0:action:retrieve-role
                            </AttributeValue>
                            <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                        </ActionMatch>
                    </Action>
                </Actions>
            </Target>
        </Rule>
-->
</Policy>
');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-system-inspector', 'escidoc:role-system-inspector', '<Policy PolicyId="System-Inspector-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>
    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
            info:escidoc/names:aa:1.0:action:retrieve-role 
            info:escidoc/names:aa:1.0:action:retrieve-roles 
            info:escidoc/names:aa:1.0:action:evaluate 
            info:escidoc/names:aa:1.0:action:retrieve-user-account 
            info:escidoc/names:aa:1.0:action:retrieve-grant 
            info:escidoc/names:aa:1.0:action:retrieve-user-group 
            info:escidoc/names:aa:1.0:action:retrieve-user-group-grant 
            info:escidoc/names:aa:1.0:action:logout 
            info:escidoc/names:aa:1.0:action:retrieve-unsecured-actions 
            info:escidoc/names:aa:1.0:action:retrieve-metadata-schema 
            info:escidoc/names:aa:1.0:action:retrieve-container 
            info:escidoc/names:aa:1.0:action:retrieve-content-model 
            info:escidoc/names:aa:1.0:action:retrieve-context 
            info:escidoc/names:aa:1.0:action:retrieve-item 
            info:escidoc/names:aa:1.0:action:retrieve-content 
            info:escidoc/names:aa:1.0:action:retrieve-toc 
            info:escidoc/names:aa:1.0:action:retrieve-content-relation 
            info:escidoc/names:aa:1.0:action:query-semantic-store 
            info:escidoc/names:aa:1.0:action:retrieve-xml-schema 
            info:escidoc/names:aa:1.0:action:retrieve-organizational-unit 
            info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit 
            info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit 
            info:escidoc/names:aa:1.0:action:retrieve-aggregation-definition 
            info:escidoc/names:aa:1.0:action:retrieve-report-definition 
            info:escidoc/names:aa:1.0:action:retrieve-report 
            info:escidoc/names:aa:1.0:action:retrieve-scope 
            info:escidoc/names:aa:1.0:action:retrieve-staging-file 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="System-Inspector-policy-rule-0" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-user-group-inspector', 'escidoc:role-user-group-inspector', '<Policy PolicyId="User-Group-Inspector-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
  <Target>
    <Subjects>
      <AnySubject/>
    </Subjects>
    <Resources>
      <AnyResource/>
    </Resources>
    <Actions>
      <Action>
        <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
          <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
            info:escidoc/names:aa:1.0:action:retrieve-user-group 
          </AttributeValue>
          <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
        </ActionMatch>
      </Action>
    </Actions>
  </Target>
  <Rule RuleId="Depositor-policy-rule-1a" Effect="Permit">
    <Target>
      <Subjects>
        <AnySubject/>
      </Subjects>
      <Resources>
        <AnyResource/>
      </Resources>
      <Actions>
        <AnyAction/>
      </Actions>
    </Target>
  </Rule>
</Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:content-relation-modifier-policy-1', 'escidoc:role-content-relation-modifier', '
<Policy PolicyId="ContentRelationModifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
        <Target>
            <Subjects>
                <AnySubject/>
            </Subjects>
            <Resources>
                <AnyResource/>
            </Resources>
            <Actions>
                <Action>
                    <ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                        info:escidoc/names:aa:1.0:action:delete-content-relation 
                        info:escidoc/names:aa:1.0:action:retrieve-content-relation 
                        info:escidoc/names:aa:1.0:action:update-content-relation 
                        info:escidoc/names:aa:1.0:action:submit-content-relation 
                        info:escidoc/names:aa:1.0:action:release-content-relation 
                        info:escidoc/names:aa:1.0:action:revise-content-relation 
                        info:escidoc/names:aa:1.0:action:withdraw-content-relation 
                        info:escidoc/names:aa:1.0:action:lock-content-relation 
                        info:escidoc/names:aa:1.0:action:unlock-content-relation 
                        </AttributeValue>
                        <ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                    </ActionMatch>
                </Action>
            </Actions>
        </Target>
        <Rule RuleId="ContentRelationModifier-policy-rule-1" Effect="Permit">
            <Target>
                <Subjects>
                    <AnySubject/>
                </Subjects>
                <Resources>
                    <AnyResource/>
                </Resources>
                <Actions>
                    <AnyAction/>
                </Actions>
            </Target>
        </Rule>
    </Policy>');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-context-administrator', 'escidoc:role-context-administrator', '<Policy PolicyId="Context-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">            info:escidoc/names:aa:1.0:action:create-context             info:escidoc/names:aa:1.0:action:retrieve-context             info:escidoc/names:aa:1.0:action:update-context             info:escidoc/names:aa:1.0:action:delete-context             info:escidoc/names:aa:1.0:action:close-context             info:escidoc/names:aa:1.0:action:open-context  info:escidoc/names:aa:1.0:action:retrieve-role</AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="Context-Administrator-policy-rule-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:create-context </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
	<Rule RuleId="Context-Administrator-policy-rule-2" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:retrieve-context                     info:escidoc/names:aa:1.0:action:update-context                     info:escidoc/names:aa:1.0:action:delete-context                     info:escidoc/names:aa:1.0:action:open-context                     info:escidoc/names:aa:1.0:action:close-context                   </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:context:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Context-Administrator-policy-rule-3" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                  info:escidoc/names:aa:1.0:action:retrieve-role
                  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">escidoc:role-audience 
escidoc:role-collaborator-modifier-container-add-remove-any-members escidoc:role-collaborator-modifier-container-add-remove-members escidoc:role-collaborator-modifier-container-update-any-members escidoc:role-collaborator-modifier-container-update-direct-members escidoc:role-collaborator-modifier escidoc:role-collaborator escidoc:role-content-relation-manager escidoc:role-content-relation-modifier escidoc:role-cone-closed-vocabulary-editor escidoc:role-cone-open-vocabulary-editor escidoc:role-moderator escidoc:role-privileged-viewer escidoc:role-depositor</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
</Policy>
');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-user-account-inspector', 'escidoc:role-user-account-inspector', '<Policy PolicyId="User-Account-Inspector-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">            info:escidoc/names:aa:1.0:action:retrieve-user-account           </AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
  <Rule RuleId="Depositor-policy-rule-1a" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<AnyAction/>
			</Actions>
		</Target>
	</Rule>
</Policy>


');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:default-policy-1', 'escidoc:role-default-user', '<Policy PolicyId="Default-User-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                     info:escidoc/names:aa:1.0:action:retrieve-container                     info:escidoc/names:aa:1.0:action:retrieve-context                     info:escidoc/names:aa:1.0:action:retrieve-item                     info:escidoc/names:aa:1.0:action:retrieve-content                     info:escidoc/names:aa:1.0:action:retrieve-organizational-unit                     info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit                     info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit                     info:escidoc/names:aa:1.0:action:retrieve-content-model                     info:escidoc/names:aa:1.0:action:retrieve-content-relation                     info:escidoc/names:aa:1.0:action:unlock-container                     info:escidoc/names:aa:1.0:action:unlock-item                     info:escidoc/names:aa:1.0:action:logout                     info:escidoc/names:aa:1.0:action:retrieve-user-account                     info:escidoc/names:aa:1.0:action:retrieve-current-user-account                     info:escidoc/names:aa:1.0:action:update-user-account                     info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                     info:escidoc/names:aa:1.0:action:retrieve-staging-file                     info:escidoc/names:aa:1.0:action:query-semantic-store                     info:escidoc/names:aa:1.0:action:retrieve-report                     info:escidoc/names:aa:1.0:action:retrieve-set-definition                     info:escidoc/names:aa:1.0:action:get-repository-info                     info:escidoc/names:aa:1.0:action:retrieve-grant                     info:escidoc/names:aa:1.0:action:create-grant                     info:escidoc/names:aa:1.0:action:revoke-grant                     info:escidoc/names:aa:1.0:action:retrieve-user-group-grant                     info:escidoc/names:aa:1.0:action:create-user-group-grant                     info:escidoc/names:aa:1.0:action:revoke-user-group-grant 					info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                    info:escidoc/names:aa:1.0:action:retrieve-content-relation info:escidoc/names:aa:1.0:action:retrieve-role info:escidoc/names:aa:1.0:action:retrieve-user-group</AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="Default-User-policy-rule-0" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-content-model                         info:escidoc/names:aa:1.0:action:logout                         info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                         info:escidoc/names:aa:1.0:action:retrieve-staging-file                         info:escidoc/names:aa:1.0:action:retrieve-report                         info:escidoc/names:aa:1.0:action:retrieve-set-definition                         info:escidoc/names:aa:1.0:action:get-repository-info						info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                         info:escidoc/names:aa:1.0:action:retrieve-current-user-account                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-container                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-2" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-context                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">opened closed</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:context:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-3" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-item                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-4" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-content                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
					<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
						<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</Apply>
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">withdrawn</AttributeValue>
				</Apply>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:visibility" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">public</AttributeValue>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:component:item:version-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-5" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-organizational-unit                         info:escidoc/names:aa:1.0:action:retrieve-children-of-organizational-unit                         info:escidoc/names:aa:1.0:action:retrieve-parents-of-organizational-unit                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">opened closed</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:organizational-unit:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-5-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-content-relation                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">released</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:content-relation:public-status" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-6" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:unlock-container                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:container:lock-owner" DataType="http://www.w3.org/2001/XMLSchema#string"/>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-7" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:unlock-item                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:item:lock-owner" DataType="http://www.w3.org/2001/XMLSchema#string"/>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-8" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-user-account                         info:escidoc/names:aa:1.0:action:retrieve-grant                     info:escidoc/names:aa:1.0:action:update-user-account                   </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:handle" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:login-name" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-8-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-user-group-grant </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:group-membership" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
		</Condition>
	</Rule>
		<Rule RuleId="Default-User-policy-rule-8-1-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-user-group</AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:group-membership" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-8-2" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-grant                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-9" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:query-semantic-store                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-10" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                             info:escidoc/names:aa:1.0:action:create-grant                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-11" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                             info:escidoc/names:aa:1.0:action:create-user-group-grant                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-12" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                             info:escidoc/names:aa:1.0:action:revoke-grant                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-13" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                             info:escidoc/names:aa:1.0:action:revoke-user-group-grant                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:grant:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Default-User-policy-rule-14" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-role                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
					<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
						<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</Apply>
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
			<Apply FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
				<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">escidoc:role-audience escidoc:role-collaborator-modifier-container-add-remove-any-members escidoc:role-collaborator-modifier-container-add-remove-members escidoc:role-collaborator-modifier-container-update-any-members escidoc:role-collaborator-modifier-container-update-direct-members escidoc:role-user-account-inspector escidoc:role-collaborator-modifier escidoc:role-collaborator escidoc:role-content-relation-manager escidoc:role-content-relation-modifier</AttributeValue>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
		</Condition>
	</Rule>
</Policy>
');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-user-group-administrator', 'escidoc:role-user-group-administrator', '<Policy PolicyId="User-Group-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">            info:escidoc/names:aa:1.0:action:create-user-group             info:escidoc/names:aa:1.0:action:retrieve-user-group             info:escidoc/names:aa:1.0:action:update-user-group             info:escidoc/names:aa:1.0:action:delete-user-group             info:escidoc/names:aa:1.0:action:activate-user-group             info:escidoc/names:aa:1.0:action:deactivate-user-group             info:escidoc/names:aa:1.0:action:retrieve-user-group-grant info:escidoc/names:aa:1.0:action:create-user-group-grant             info:escidoc/names:aa:1.0:action:revoke-user-group-grant             info:escidoc/names:aa:1.0:action:add-user-group-selectors             info:escidoc/names:aa:1.0:action:remove-user-group-selectors            info:escidoc/names:aa:1.0:action:retrieve-role          </AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="User-Group-Administrator-policy-rule-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:create-user-group                  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
	<Rule RuleId="User-Group-Administrator-policy-rule-2" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:retrieve-user-group                     info:escidoc/names:aa:1.0:action:update-user-group                     info:escidoc/names:aa:1.0:action:delete-user-group                     info:escidoc/names:aa:1.0:action:activate-user-group                     info:escidoc/names:aa:1.0:action:deactivate-user-group                     info:escidoc/names:aa:1.0:action:add-user-group-selectors                     info:escidoc/names:aa:1.0:action:remove-user-group-selectors                     info:escidoc/names:aa:1.0:action:revoke-user-group-grant                     info:escidoc/names:aa:1.0:action:retrieve-user-group-grant                   </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="User-Group-Administrator-policy-rule-3" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">info:escidoc/names:aa:1.0:action:retrieve-role</AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">escidoc:role-user-group-inspector escidoc:role-user-group-administrator</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="User-Group-Administrator-policy-rule-4" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">info:escidoc/names:aa:1.0:action:create-user-group-grant</AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-group:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
</Policy>
');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-user-account-administrator', 'escidoc:role-user-account-administrator', '<Policy PolicyId="User-Account-Administrator-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">            info:escidoc/names:aa:1.0:action:create-user-account             info:escidoc/names:aa:1.0:action:retrieve-user-account             info:escidoc/names:aa:1.0:action:update-user-account             info:escidoc/names:aa:1.0:action:activate-user-account             info:escidoc/names:aa:1.0:action:deactivate-user-account             info:escidoc/names:aa:1.0:action:deactivate-user-account            info:escidoc/names:aa:1.0:action:revoke-grant   info:escidoc/names:aa:1.0:action:retrieve-grant          </AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="User-Account-Administrator-policy-rule-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:create-user-account                   </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
	<Rule RuleId="User-Account-Administrator-policy-rule-2" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:retrieve-user-account                     info:escidoc/names:aa:1.0:action:update-user-account                     info:escidoc/names:aa:1.0:action:activate-user-account                     info:escidoc/names:aa:1.0:action:deactivate-user-account  	info:escidoc/names:aa:1.0:action:retrieve-grant                  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
			<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:organizational-unit" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:organizational-unit" DataType="http://www.w3.org/2001/XMLSchema#string"/>
		</Condition>
	</Rule>
	<Rule RuleId="User-Account-Administrator-policy-rule-3" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                    info:escidoc/names:aa:1.0:action:revoke-grant                  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:or">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:and">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:organizational-unit" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:organizational-unit" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
					<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
						<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
							<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
						</Apply>
						<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
							<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
						</Apply>
					</Apply>
				</Apply>
			</Apply>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="urn:oasis:names:tc:xacml:1.0:subject:subject-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
				<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
					<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:created-by" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</Apply>
			</Apply>
		</Condition>
	</Rule>
</Policy>
');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:policy-context-modifier', 'escidoc:role-context-modifier', '<Policy PolicyId="Context-Modifier-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
            info:escidoc/names:aa:1.0:action:retrieve-context 
            info:escidoc/names:aa:1.0:action:update-context 
            info:escidoc/names:aa:1.0:action:delete-context 
            info:escidoc/names:aa:1.0:action:close-context 
            info:escidoc/names:aa:1.0:action:open-context 
            info:escidoc/names:aa:1.0:action:retrieve-role
            info:escidoc/names:aa:1.0:action:create-grant
            info:escidoc/names:aa:1.0:action:create-user-group-grant
          </AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="Context-Modifier-policy-rule-1" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">
                    info:escidoc/names:aa:1.0:action:retrieve-context 
                    info:escidoc/names:aa:1.0:action:update-context 
                    info:escidoc/names:aa:1.0:action:delete-context 
                    info:escidoc/names:aa:1.0:action:open-context 
                    info:escidoc/names:aa:1.0:action:close-context 
                  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
	<Rule RuleId="Context-Modifier-policy-rule-3" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                  info:escidoc/names:aa:1.0:action:retrieve-role                  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="info:escidoc/names:aa:1.0:function:string-contains">
			<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">escidoc:role-audience escidoc:role-collaborator-modifier-container-add-remove-any-members escidoc:role-collaborator-modifier-container-add-remove-members escidoc:role-collaborator-modifier-container-update-any-members escidoc:role-collaborator-modifier-container-update-direct-members escidoc:role-collaborator-modifier escidoc:role-collaborator escidoc:role-content-relation-manager escidoc:role-content-relation-modifier escidoc:role-cone-closed-vocabulary-editor escidoc:role-cone-open-vocabulary-editor escidoc:role-moderator escidoc:role-privileged-viewer escidoc:role-depositor escidoc:role-context-modifier</AttributeValue>
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
		</Condition>
	</Rule>
	<Rule RuleId="Context-modifier-policy-rule-grant-create" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                            info:escidoc/names:aa:1.0:action:create-grant  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-context-modifier:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>		</Condition>
	</Rule>
	
	<Rule RuleId="Context-modifier-policy-rule-grant-create" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                            info:escidoc/names:aa:1.0:action:create-user-group-grant  </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
		<Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-is-in">
			<Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-one-and-only">
				<ResourceAttributeDesignator AttributeId="info:escidoc/names:aa:1.0:resource:user-grant:grant:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
			</Apply>
			<SubjectAttributeDesignator SubjectCategory="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="info:escidoc/names:aa:1.0:subject:role-grant:escidoc:role-context-modifier:assigned-on" DataType="http://www.w3.org/2001/XMLSchema#string"/>
		</Condition>
	</Rule>
</Policy>
');

INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:role-cone-open-vocabulary-editor-policy-1', 'escidoc:role-cone-open-vocabulary-editor', '<Policy PolicyId="Cone-User-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">info:escidoc/names:aa:1.0:action:retrieve-content-model                         info:escidoc/names:aa:1.0:action:logout                         info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                         info:escidoc/names:aa:1.0:action:retrieve-staging-file                         info:escidoc/names:aa:1.0:action:retrieve-report                         info:escidoc/names:aa:1.0:action:retrieve-set-definition                         info:escidoc/names:aa:1.0:action:get-repository-info						info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                         info:escidoc/names:aa:1.0:action:retrieve-current-user-account</AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="Default-User-policy-rule-0" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-content-model                         info:escidoc/names:aa:1.0:action:logout                         info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                         info:escidoc/names:aa:1.0:action:retrieve-staging-file                         info:escidoc/names:aa:1.0:action:retrieve-report                         info:escidoc/names:aa:1.0:action:retrieve-set-definition                         info:escidoc/names:aa:1.0:action:get-repository-info						info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                         info:escidoc/names:aa:1.0:action:retrieve-current-user-account                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
</Policy>
');
INSERT INTO escidoc_policies (id, role_id, xml) VALUES ('escidoc:role-cone-closed-vocabulary-editor-policy-2', 'escidoc:role-cone-closed-vocabulary-editor', '<Policy PolicyId="Cone-User-closed-policy" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:ordered-permit-overrides">
	<Target>
		<Subjects>
			<AnySubject/>
		</Subjects>
		<Resources>
			<AnyResource/>
		</Resources>
		<Actions>
			<Action>
				<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
					<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">info:escidoc/names:aa:1.0:action:retrieve-content-model                         info:escidoc/names:aa:1.0:action:logout                         info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                         info:escidoc/names:aa:1.0:action:retrieve-staging-file                         info:escidoc/names:aa:1.0:action:retrieve-report                         info:escidoc/names:aa:1.0:action:retrieve-set-definition                         info:escidoc/names:aa:1.0:action:get-repository-info						info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                         info:escidoc/names:aa:1.0:action:retrieve-current-user-account</AttributeValue>
					<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
				</ActionMatch>
			</Action>
		</Actions>
	</Target>
	<Rule RuleId="Default-User-policy-rule-0" Effect="Permit">
		<Target>
			<Subjects>
				<AnySubject/>
			</Subjects>
			<Resources>
				<AnyResource/>
			</Resources>
			<Actions>
				<Action>
					<ActionMatch MatchId="info:escidoc/names:aa:1.0:function:string-contains">
						<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">                         info:escidoc/names:aa:1.0:action:retrieve-content-model                         info:escidoc/names:aa:1.0:action:logout                         info:escidoc/names:aa:1.0:action:retrieve-objects-filtered                         info:escidoc/names:aa:1.0:action:retrieve-staging-file                         info:escidoc/names:aa:1.0:action:retrieve-report                         info:escidoc/names:aa:1.0:action:retrieve-set-definition                         info:escidoc/names:aa:1.0:action:get-repository-info						info:escidoc/names:aa:1.0:action:retrieve-registered-predicates                         info:escidoc/names:aa:1.0:action:retrieve-current-user-account                         </AttributeValue>
						<ActionAttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" DataType="http://www.w3.org/2001/XMLSchema#string"/>
					</ActionMatch>
				</Action>
			</Actions>
		</Target>
	</Rule>
</Policy>
');

-- Completed on 2010-06-28 14:06:27

--
-- PostgreSQL database dump complete
--

