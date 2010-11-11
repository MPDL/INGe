<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:r="http://www.escidoc.de/validation" xmlns:fn="http://www.escidoc.de/functions">

	<xsl:output encoding="UTF-8" indent="yes" method="xml"/>

	<xsl:param name="validation-schema"/>
	
	<xsl:variable name="max-counter" select="999"/>

	<xsl:variable name="validation-schema-content">
		<xsl:copy-of select="document($validation-schema)"/>
	</xsl:variable>

	<xsl:variable name="fields">
		<xsl:for-each select="/r:ruler/r:fields/r:root">
			<xsl:copy-of select="document(@schema)"/>
		</xsl:for-each>
	</xsl:variable>
		
	<xsl:template match="/">
	
		<xsl:variable name="context" select="*"/>
	
		<iso:schema>
			<xsl:for-each select="$validation-schema-content/iso:schema/processing-instruction()">
				<xsl:apply-templates select="$context">
					<xsl:with-param name="rule-text" select="."/>
				</xsl:apply-templates>
			</xsl:for-each>
		</iso:schema>
	</xsl:template>
	
	<xsl:template match="/r:ruler">
		<xsl:param name="rule-text"/>

		<xsl:if test="$rule-text = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'RuletextEmptyError'), 'Rule-text is empty')"/></xsl:if>

		<xsl:variable name="rule-element" select="substring-before($rule-text, ':')"/>
		<xsl:if test="$rule-element = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'RuleElementEmptyError'), 'Rule-element is empty')"/></xsl:if>
		<xsl:if test="not(exists(/r:ruler/r:fields/r:root[@name = $rule-element]))"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'RuleElementNotDefinedError'), concat('Rule element ', $rule-element, ' is not defined'))"/></xsl:if>
		
		<xsl:variable name="rule-type" select="substring-before(substring-after($rule-text, ': '), ':')"/>
		<xsl:if test="$rule-type = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'RuletypeEmptyError'), 'Rule-type is empty')"/></xsl:if>
		<xsl:if test="not(exists(/r:ruler/r:grammar/r:term[@name = $rule-type]))"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'RuletypeNotDefinedError'), concat('Rule-type ', $rule-type, ' is not defined'))"/></xsl:if>
		
		<xsl:variable name="rule-message" select="substring-after($rule-text, ' else report ')"/>
		<xsl:if test="$rule-message = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'RuleMessageEmptyError'), 'Rule message is empty')"/></xsl:if>
		
		<xsl:processing-instruction name="rule"><xsl:value-of select="$rule-text"/></xsl:processing-instruction>
		
		<iso:pattern name="rule{position()}" id="rule{position()}">
			<iso:rule context="{$fields/xs:schema/xs:element[@name = $rule-element]/@name}">
				<iso:assert>
					<xsl:attribute name="test">
						<xsl:apply-templates select="/r:ruler/r:grammar/r:term[@name = $rule-type]">
							<xsl:with-param name="rule-text" select="normalize-space(substring-before(substring-after(substring-after($rule-text, ': '), ': '), ' else report '))"/>
						</xsl:apply-templates>
					</xsl:attribute>
					<xsl:value-of select="$rule-message"/>
				</iso:assert>
			</iso:rule>
		</iso:pattern>
	</xsl:template>
	
	<xsl:template match="r:term">
		<xsl:param name="rule-text"/>

		<xsl:apply-templates select="*[1]">
			<xsl:with-param name="rule-text" select="$rule-text"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="r:word">
		<xsl:param name="rule-text"/>
		<xsl:param name="counter" select="1"/>
		
		<xsl:choose>
			<xsl:when test="@pattern != ''">
				<xsl:value-of select="@pattern"/>

				<xsl:if test="$counter &lt; $max-counter">
					<xsl:apply-templates select="following-sibling::*[1]">
						<xsl:with-param name="rule-text" select="$rule-text"/>
					</xsl:apply-templates>
				</xsl:if>
				
			</xsl:when>
			<xsl:when test="$rule-text = '(' or $rule-text = ')' or $rule-text = ' and ' or $rule-text = ' or '">
				<xsl:value-of select="$rule-text"/>
			</xsl:when>
			<xsl:when test="@vocabulary != ''">
			
				<xsl:variable name="vocabulary-name" select="@vocabulary"/>
				<xsl:if test="not(exists(/r:ruler/r:vocabularies/r:vocabulary[@name = $vocabulary-name]))"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'VocabularyNotDefinedError'), concat('Vocabulary ', $vocabulary-name, ' is not defined'))"/></xsl:if>
		
				<xsl:variable name="word" select="substring-before(concat($rule-text, ' '), ' ')"/>
				<xsl:if test="$word = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'WordEmptyError'), 'Word is empty')"/></xsl:if>
				<xsl:if test="not(exists(/r:ruler/r:vocabularies/r:vocabulary[@name = $vocabulary-name]/r:word[@name = $word]))"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'WordNotDefinedError'), concat('Word ', $word, ' is not defined in vocabulary ',  $vocabulary-name))"/></xsl:if>
					
				<xsl:variable name="pattern" select="/r:ruler/r:vocabularies/r:vocabulary[@name = $vocabulary-name]/r:word[@name = $word]/@pattern"/>
				<xsl:if test="$pattern = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'PatternEmptyError'), 'Pattern is empty')"/></xsl:if>
				
				<xsl:variable name="before-value">
					<xsl:choose>
						<xsl:when test="contains($pattern, '#V')">
							<xsl:value-of select="substring-before($pattern, '#V')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$pattern"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="after-value">
					<xsl:choose>
						<xsl:when test="contains($pattern, '#V')">
							<xsl:value-of select="substring-after($pattern, '#V')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="''"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="contains($before-value, '#N')">
						<xsl:value-of select="substring-before($before-value, '#N')"/>
						<xsl:if test="$counter &lt; $max-counter">
							<xsl:apply-templates select="following-sibling::*[local-name() != 'value'][1]">
								<xsl:with-param name="rule-text" select="normalize-space(substring-after(substring-after($rule-text, ' '), ' '))"/>
								<xsl:with-param name="counter" select="$counter + 1"/>
							</xsl:apply-templates>
						</xsl:if>
						<xsl:value-of select="substring-after($before-value, '#N')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$before-value"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="contains($pattern, '#V')">
					<xsl:if test="$counter &lt; $max-counter">
						<xsl:apply-templates select="following-sibling::r:value[1]">
							<xsl:with-param name="rule-text" select="normalize-space(substring-after($rule-text, ' '))"/>
							<xsl:with-param name="counter" select="$counter + 1"/>
						</xsl:apply-templates>
					</xsl:if>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="contains($after-value, '#N')">
						<xsl:value-of select="substring-before($after-value, '#N')"/>
						<xsl:if test="$counter &lt; $max-counter">
							<xsl:apply-templates select="following-sibling::*[local-name() != 'value'][1]">
								<xsl:with-param name="rule-text" select="normalize-space(substring-after(substring-after($rule-text, ' '), ' '))"/>
								<xsl:with-param name="counter" select="$counter + 1"/>
							</xsl:apply-templates>
						</xsl:if>
						<xsl:value-of select="substring-after($after-value, '#N')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$after-value"/>
					</xsl:otherwise>
				</xsl:choose>

			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'VocabularyOrPatternEmptyError'), 'Vocabulary and pattern are empty')"/>
			</xsl:otherwise>
		</xsl:choose>
				
	</xsl:template>
	
	<xsl:template match="r:value">
		<xsl:param name="rule-text"/>
		<xsl:param name="counter" select="1"/>
		
		<xsl:variable name="value-text" select="substring-before(concat($rule-text, ' '), ' ')"/>
		<xsl:if test="$value-text = ''"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'ValueTextEmptyError'), 'Value text is empty')"/></xsl:if>
		
		<xsl:value-of select="$value-text"/>
		
	</xsl:template>
	
	<xsl:template match="r:section">
		<xsl:param name="rule-text"/>
		<xsl:param name="counter" select="1"/>
		
		<xsl:variable name="section-text" select="normalize-space(fn:get-section-text($rule-text, *[1]))"/>

		<xsl:choose>
			<xsl:when test="starts-with($rule-text, '(')">
				
				<xsl:if test="$counter &lt; $max-counter">
					<xsl:apply-templates select="." mode="subsection">
						<xsl:with-param name="rule-text" select="$section-text"/>
						<xsl:with-param name="counter" select="$counter + 1"/>
					</xsl:apply-templates>
				</xsl:if>

			</xsl:when>
			<xsl:otherwise>
			
				<xsl:if test="$counter &lt; $max-counter">
					<xsl:apply-templates select="*[1]">
						<xsl:with-param name="rule-text" select="$rule-text"/>
						<xsl:with-param name="counter" select="$counter + 1"/>
					</xsl:apply-templates>
				</xsl:if>
				
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:if test="$counter &lt; $max-counter">
			<xsl:apply-templates select="following-sibling::*[1]">
				<xsl:with-param name="rule-text" select="normalize-space(substring-after($rule-text, $section-text))"/>
				<xsl:with-param name="counter" select="$counter + 1"/>
			</xsl:apply-templates>
		</xsl:if>

	</xsl:template>
	
	<xsl:template match="r:section" mode="subsection">
		<xsl:param name="rule-text"/>
		<xsl:param name="counter" select="1"/>

			<xsl:choose>
			<xsl:when test="$rule-text = ' and '">
				<xsl:text> and </xsl:text>
			</xsl:when>
			<xsl:when test="$rule-text = ' or '">
				<xsl:text> or </xsl:text>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, '(')">

				<xsl:variable name="current-context" select="."/>
			
				<xsl:for-each select="fn:split-term($rule-text, '', 0, ())">
					<xsl:if test="$counter &lt; $max-counter">
						<xsl:apply-templates select="$current-context/*[1]">
							<xsl:with-param name="rule-text" select="."/>
							<xsl:with-param name="counter" select="$counter + 1"/>
						</xsl:apply-templates>
					</xsl:if>
				</xsl:for-each>
				
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$counter &lt; $max-counter">
					<xsl:apply-templates select="*[1]">
						<xsl:with-param name="rule-text" select="$rule-text"/>
						<xsl:with-param name="counter" select="$counter + 1"/>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	<xsl:function name="fn:split-term">
		<xsl:param name="rule-text"/>
		<xsl:param name="consumed-text"/>
		<xsl:param name="level"/>
		<xsl:param name="current-result"/>

		<xsl:choose>
			<xsl:when test="$rule-text = '' or $level = -1">
				<xsl:copy-of select="data($current-result)"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, ' and ') and $level = 0">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, ' and '), '', 0, ($current-result, ' and ', $consumed-text))"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, ' or ') and $level = 0">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, ' or '), '', 0, ($current-result, ' or ', $consumed-text))"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, '(')">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, '('), '', $level + 1, ($current-result, '('))"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, ')')">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, ')'), '', $level - 1, ($current-result, $consumed-text, ')'))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="fn:split-term(substring($rule-text, 2), concat($consumed-text, substring($rule-text, 1, 1)), $level, $current-result)"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
	<xsl:function name="fn:get-section-text" as="xs:string">
		<xsl:param name="rule-text"/>
		<xsl:param name="context"/>

		<xsl:variable name="result">
			<xsl:choose>
				<xsl:when test="starts-with($rule-text, '(')">
					<xsl:value-of select="string-join(fn:split-term($rule-text, '', 0, ()), '')"/>
				</xsl:when>
				<xsl:when test="$context/name() = 'r:word' and $context/@vocabulary != ''">
					<xsl:value-of select="substring-before(concat($rule-text, ' '), ' ')"/>
					<xsl:text> </xsl:text>
					<xsl:if test="exists($context/following-sibling::*[1])">
						<xsl:value-of select="fn:get-section-text(normalize-space(substring-after($rule-text, ' ')), $context/following-sibling::*[1])"/>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$context/name() = 'r:value'">
					<xsl:value-of select="substring-before(concat($rule-text, ' '), ' ')"/>
					<xsl:text> </xsl:text>
					<xsl:if test="exists($context/following-sibling::*[1])">
						<xsl:value-of select="fn:get-section-text(normalize-space(substring-after($rule-text, ' ')), $context/following-sibling::*[1])"/>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:value-of select="$result"/>
		
	</xsl:function>
	
</xsl:stylesheet>