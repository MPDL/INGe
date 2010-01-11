<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:variable name="genre-ves">
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/journal">journal</enum>
		<enum uri="http://purl.org/eprint/type/Book">book</enum>
		<enum uri="http://purl.org/eprint/type/BookItem">book-item</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/proceedings">proceedings</enum>
		<enum uri="http://purl.org/eprint/type/ConferencePaper">conference-paper</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/conference-report">conference-report</enum>
		<enum uri="http://purl.org/eprint/type/ConferencePoster">poster</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/article">article</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/issue">issue</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture">courseware-lecture</enum>
		<enum uri="http://purl.org/eprint/type/Thesis">thesis</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/series">series</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/manuscript">manuscript</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/other">other</enum>
		<enum uri="http://purl.org/eprint/type/Report">report</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/paper">paper</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event">talk-at-event</enum>
		
		<!-- NOT YET IMPLEMENTED -->
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article">newspaper-article</enum>		
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/working-paper">working-paper</enum>		
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/manual">manual</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/webpage">webpage</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/editorial">editorial</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook">contribution-to-handbook</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia">contribution-to-encyclopedia</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift">contribution-to-festschrift</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary">contribution-to-commentary</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition">contribution-to-collected-edition</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/book-review">book-review</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/opinion">opinion</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/case-study">case-study</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/case-note">case-note</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/monograph">monograph</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/newspaper">newspaper</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia">encyclopedia</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/multi-volume">multi-volume</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/commentary">commentary</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/handbook">handbook</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/collected-edition">collected-edition</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/festschrift">festschrift</enum>		
		<enum uri="http://purl.org/escidoc/metadata/ves/publication-types/journal-article">journal-article</enum>
	</xsl:variable>
	
	<xsl:variable name="creator-ves">
		<enum uri="http://www.loc.gov/loc.terms/relators/AUT">author</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/ART">artist</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/EDT">editor</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/creator-roles/painter">painter</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/PHT">photographer</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/ILL">illustrator</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/CMM">commentator</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/TRC">transcriptor</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/TRL">translator</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/SAD">scientific advisor</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/THS">thesis advisor</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/CTB">contributor</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/creator-roles/publisher">publisher</enum>
		<enum uri="http://www.loc.gov/loc.terms/relators/HNR">honoree</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/creator-roles/founder">founder</enum>
	</xsl:variable>
	
	<xsl:variable name="degree-ves">
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/master">master</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/diploma">diploma</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor">bachelor</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/phd">phd</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/magister">magister</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation">habilitation</enum>	
		<enum uri="http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen">staatsexamen</enum>	
	</xsl:variable>
	
	<xsl:variable name="reviewMethod-ves">
		<enum uri="http://purl.org/escidoc/metadata/ves/review-methods/internal">internal</enum>
		<enum uri="http://purl.org/eprint/status/PeerReviewed">peer-reviewed</enum>
		<enum uri="http://purl.org/escidoc/metadata/ves/review-methods/no-review">no-review</enum>			
	</xsl:variable>
</xsl:stylesheet>