SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = aa, pg_catalog;


/*

This file contains the changes that have to be applied to core-service 1.2 escidoc-core database.
Description + sql 
ESCIDOC_ROLES
Newly added roles
"escidoc:role-cone-closed-vocabulary-editor";"CoNE-Closed-Vocabulary-Editor";""
"escidoc:role-cone-open-vocabulary-editor";"CoNE-Open-Vocabulary-Editor";""
 "escidoc:role-context-administrator";"Context-Administrator";""
 "escidoc:role-context-modifier";"Context-Modifier";""
 "escidoc:role-user-account-administrator";"User-Account-Administrator";""
 "escidoc:role-user-account-inspector";"User-Account-Inspector";""
*/

INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-cone-closed-vocabulary-editor', 'CoNE-Closed-Vocabulary-Editor', NULL, 'escidoc:user42', CURRENT_TIMESTAMP, 'escidoc:user42', 
CURRENT_TIMESTAMP);

INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-cone-open-vocabulary-editor', 'CoNE-Open-Vocabulary-Editor', NULL, 'escidoc:user42', CURRENT_TIMESTAMP, 'escidoc:user42', 
CURRENT_TIMESTAMP);
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-context-modifier', 'Context-Modifier', NULL, 'escidoc:user42', CURRENT_TIMESTAMP, 'escidoc:user42', CURRENT_TIMESTAMP);
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-context-administrator', 'Context-Administrator', NULL, 'escidoc:user42', CURRENT_TIMESTAMP, 
'escidoc:user42',CURRENT_TIMESTAMP);
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES 
('escidoc:role-user-account-administrator', 'User-Account-Administrator', NULL, 'escidoc:user42', CURRENT_TIMESTAMP, 'escidoc:user42', 
CURRENT_TIMESTAMP);
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES 
('escidoc:role-user-account-inspector', 'User-Account-Inspector', NULL, 'escidoc:user42', CURRENT_TIMESTAMP, 'escidoc:user42', 
CURRENT_TIMESTAMP);


/*ESCIDOC_POLICIES
====================
changed policies:
 "escidoc:default-policy-1";"escidoc:role-default-user"
 "escidoc:moderator-policy-1";"escidoc:role-moderator"
 "escidoc:policy-user-group-administrator";"escidoc:role-user-group-administrator"

new policies:
 "escidoc:policy-context-administrator";"escidoc:role-context-administrator"
 "escidoc:policy-context-modifier";"escidoc:role-context-modifier"
 "escidoc:policy-user-account-administrator";"escidoc:role-user-account-administrator"
 "escidoc:policy-user-account-inspector";"escidoc:role-user-account-inspector"

*/

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
	
	<Rule RuleId="Context-modifier-policy-rule-user-group-grant-create" Effect="Permit">
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

delete from escidoc_policies where role_id in ('escidoc:role-default-user', 'escidoc:role-moderator', 'escidoc:role-user-group-administrator');

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



/*
SCOPE_DEF
--removed scope_def
 "escidoc:scope-def-role-moderator-7";"escidoc:role-moderator";"user-account";""
--new scope-defs
 "escidoc:scope-def-role-context-modifier";"escidoc:role-context-modifier";"context";"info:escidoc/names:aa:1.0:resource:context-id"
 "escidoc:scope-def-role-context-modifier-5";"escidoc:role-context-modifier";"user-account";""
 "escidoc:scope-def-role-context-modifier-6";"escidoc:role-context-modifier";"role";""
 "escidoc:scope-def-role-user-account-inspector";"escidoc:role-user-account-inspector";"user-account";"urn:oasis:names:tc:xacml:1.0:resource:resource-id"

*/
delete from aa.scope_def where id='escidoc:scope-def-role-moderator-7' and role_id='escidoc:role-moderator';
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-user-account-inspector', 'escidoc:role-user-account-inspector', 'user-account', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-context-modifier', 'escidoc:role-context-modifier', 'context', 'info:escidoc/names:aa:1.0:resource:context-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-context-modifier-6', 'escidoc:role-context-modifier', 'role', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-context-modifier-5', 'escidoc:role-context-modifier', 'user-account', NULL);


