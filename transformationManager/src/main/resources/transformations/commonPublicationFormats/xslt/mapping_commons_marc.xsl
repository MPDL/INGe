<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:misc="http://www.editura.de/ns/2012/misc"
	xmlns:misc-marc="http://www.editura.de/ns/2012/misc-marc"
	xmlns:local="http://www.editura.de/ns/2012/local"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:eterms="${xsd.metadata.escidocprofile.types}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:eves="http://purl.org/escidoc/metadata/ves/0.1/"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:marc="http://www.loc.gov/MARC21/slim" exclude-result-prefixes="xs xd misc misc-marc local" version="2.0">
	<xsl:key name="pubman-to-marc" match="misc:mapping" use="misc:source"/>
	<xsl:key name="marc-e-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'code-e']"/>
	<xsl:key name="marc-4-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'code-4']"/>
	<xsl:variable name="local:mapping_marc_degrees">
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/master</misc:source>
			<misc:target>Master</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/diploma</misc:source>
			<misc:target>Diploma</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor</misc:source>
			<misc:target>Bachelor</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/phd</misc:source>
			<misc:target>PhD Dissertation</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/magister</misc:source>
			<misc:target>Magister</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation</misc:source>
			<misc:target>Habilitation</misc:target>
		</misc:mapping>
		<misc:mapping>
			<misc:source>http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen</misc:source>
			<misc:target>Staatsexamen</misc:target>
		</misc:mapping>
	</xsl:variable>
	<xsl:variable name="local:mapping_marc_relators">
		<misc:mapping-table xml:id="mapping_marc_relators" group="relator terms and codes">
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/AUT</misc:source>
				<misc:target misc:use="code-e"/>
				<misc:target misc:use="code-4">aut</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/ART</misc:source>
				<misc:target misc:use="code-e">Artist</misc:target>
				<misc:target misc:use="code-4">art</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/EDT</misc:source>
				<misc:target misc:use="code-e">Editor</misc:target>
				<misc:target misc:use="code-4">edt</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/creator-roles/painter</misc:source>
				<misc:target misc:use="code-e">Painter</misc:target>
				<misc:target misc:use="code-4">art</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/PHT</misc:source>
				<misc:target misc:use="code-e">Photographer</misc:target>
				<misc:target misc:use="code-4">pht</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/ILL</misc:source>
				<misc:target misc:use="code-e">Illustrator</misc:target>
				<misc:target misc:use="code-4">ill</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/CMM</misc:source>
				<misc:target misc:use="code-e">Commentator</misc:target>
				<misc:target misc:use="code-4">cmm</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/TRC</misc:source>
				<misc:target misc:use="code-e">Transcriber</misc:target>
				<misc:target misc:use="code-4">trc</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/TRL</misc:source>
				<misc:target misc:use="code-e">Translator</misc:target>
				<misc:target misc:use="code-4">trl</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/SAD</misc:source>
				<misc:target misc:use="code-e">Scientific advisor</misc:target>
				<misc:target misc:use="code-4">sad</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/THS</misc:source>
				<misc:target misc:use="code-e">Thesis advisor</misc:target>
				<misc:target misc:use="code-4">ths</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/CTB</misc:source>
				<misc:target misc:use="code-e">Contributor</misc:target>
				<misc:target misc:use="code-4">ctb</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/creator-roles/publisher</misc:source>
				<misc:target misc:use="code-e">Publisher</misc:target>
				<misc:target misc:use="code-4">pbl</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/HNR</misc:source>
				<misc:target misc:use="code-e">Honoree</misc:target>
				<misc:target misc:use="code-4">hnr</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/creator-roles/founder</misc:source>
				<misc:target misc:use="code-e">Founder</misc:target>
				<misc:target misc:use="code-4">fpy</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/creator-roles/referee</misc:source>
				<misc:target misc:use="code-e">Referee</misc:target>
				<misc:target misc:use="code-4">oth</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/INV</misc:source>
				<misc:target misc:use="code-e">Inventor</misc:target>
				<misc:target misc:use="code-4">inv</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://www.loc.gov/loc.terms/relators/APP</misc:source>
				<misc:target misc:use="code-e">Applicant</misc:target>
				<misc:target misc:use="code-4">app</misc:target>
			</misc:mapping>
		</misc:mapping-table>
	</xsl:variable>
	<xsl:key name="pubman_genre-to-marc" match="misc:mapping" use="misc:source"/>
	<xsl:key name="marc-996a-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq '996a']"/>
	<xsl:key name="marc-997a-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq '997a']"/>
	<xsl:key name="marc-type-of-record-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'type-of-record']"/>
	<xsl:key name="marc-bibliographic-level-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'bibliographic-level']"/>
	<xsl:key name="marc-multipart-resource-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'multipart-resource']"/>
	<xsl:key name="marc-nature-of-contents-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'nature-of-contents']"/>
	<xsl:key name="marc-conference-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'conference']"/>
	<xsl:key name="marc-festschrift-to-pubman" match="misc:mapping" use="misc:target[@misc:use eq 'festschrift']"/>
	<xsl:variable name="local:mapping_pubman_genres">
		<misc:mapping-table xml:id="mapping_pubman_genres" group="Genre">
			<misc:remark group="Genre">The following table yields the mapping between PubMan genres and some MARC related subfields and codes. The lines in the MARC column have the following meaning: # german name for the genre (used in 996 $a) # englisch name of the genre (used in 997 $a) # type of record (used in leader and controlfield 008) # bibliographic level (used in leader and controlfield 008) # multipart resource (used in leader) # natur of contents (used in controlfield 008) # conference (used in controlfield 008) # festschrift (used in controlfield 008) The #-symbol stands for one single space.</misc:remark>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/article</misc:source>
				<misc:target misc:use="996a">Zeitschriftenartikel</misc:target>
				<misc:target misc:use="997a">article</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/Book</misc:source>
				<misc:target misc:use="996a">Buch</misc:target>
				<misc:target misc:use="997a">book</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/BookItem</misc:source>
				<misc:target misc:use="996a">Buchkapitel</misc:target>
				<misc:target misc:use="997a">book chapter</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/book-review</misc:source>
				<misc:target misc:use="996a">Rezension</misc:target>
				<misc:target misc:use="997a">book review</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">o</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/case-note</misc:source>
				<misc:target misc:use="996a">Entscheidungsanmerkung</misc:target>
				<misc:target misc:use="997a">case note</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">v</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/case-study</misc:source>
				<misc:target misc:use="996a">Fallbesprechung</misc:target>
				<misc:target misc:use="997a">case study</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/collected-edition</misc:source>
				<misc:target misc:use="996a">Sammelwerk</misc:target>
				<misc:target misc:use="997a">collected edition</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">c</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/commentary</misc:source>
				<misc:target misc:use="996a">Kommentar</misc:target>
				<misc:target misc:use="997a">commentary</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/ConferencePaper</misc:source>
				<misc:target misc:use="996a">Konferenzbeitrag</misc:target>
				<misc:target misc:use="997a">conference paper</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">1</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/conference-report</misc:source>
				<misc:target misc:use="996a">Konferenzbericht</misc:target>
				<misc:target misc:use="997a">conference report</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">1</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/ConferencePoster</misc:source>
				<misc:target misc:use="996a">Poster</misc:target>
				<misc:target misc:use="997a">conference poster</misc:target>
				<misc:target misc:use="type-of-record">k</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">1</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook</misc:source>
				<misc:target misc:use="996a">Beitrag in Handbuch</misc:target>
				<misc:target misc:use="997a">contribution to handbook</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">f</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia</misc:source>
				<misc:target misc:use="996a">Beitrag in Lexikon</misc:target>
				<misc:target misc:use="997a">contribution to encyclopedia</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift</misc:source>
				<misc:target misc:use="996a">Beitrag in Festschrift</misc:target>
				<misc:target misc:use="997a">contribution to festschrift</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">1</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary</misc:source>
				<misc:target misc:use="996a">Beitrag in Kommentar</misc:target>
				<misc:target misc:use="997a">contribution to commentary</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition</misc:source>
				<misc:target misc:use="996a">Beitrag in Sammelwerk</misc:target>
				<misc:target misc:use="997a">contribution to collected-edition</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture</misc:source>
				<misc:target misc:use="996a">Lehrmaterial</misc:target>
				<misc:target misc:use="997a">courseware lecture</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/editorial</misc:source>
				<misc:target misc:use="996a">Editorial</misc:target>
				<misc:target misc:use="997a">editorial</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia</misc:source>
				<misc:target misc:use="996a">Enzyklopädie</misc:target>
				<misc:target misc:use="997a">encyclopedia</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">e</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/festschrift</misc:source>
				<misc:target misc:use="996a">Festschrift</misc:target>
				<misc:target misc:use="997a">festschrift</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">1</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/handbook</misc:source>
				<misc:target misc:use="996a">Handbuch</misc:target>
				<misc:target misc:use="997a">handbook</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">f</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/issue</misc:source>
				<misc:target misc:use="996a">Sonderheft</misc:target>
				<misc:target misc:use="997a">issue</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">b</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/journal</misc:source>
				<misc:target misc:use="996a">Zeitschrift</misc:target>
				<misc:target misc:use="997a">journal</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">s</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/manual</misc:source>
				<misc:target misc:use="996a">Bedienungsanleitung</misc:target>
				<misc:target misc:use="997a">manual</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/manuscript</misc:source>
				<misc:target misc:use="996a">Manuskript</misc:target>
				<misc:target misc:use="997a">manuscript</misc:target>
				<misc:target misc:use="type-of-record">t</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract</misc:source>
				<misc:target misc:use="996a">Meeting Abstract</misc:target>
				<misc:target misc:use="997a">meeting-abstract</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">a</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/monograph</misc:source>
				<misc:target misc:use="996a">Monografie</misc:target>
				<misc:target misc:use="997a">monograph</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/multi-volume</misc:source>
				<misc:target misc:use="996a">mehrbändiges Werk</misc:target>
				<misc:target misc:use="997a">multi volume</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/newspaper</misc:source>
				<misc:target misc:use="996a">Zeitung</misc:target>
				<misc:target misc:use="997a">newspaper</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">s</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article</misc:source>
				<misc:target misc:use="996a">Zeitungsartikel</misc:target>
				<misc:target misc:use="997a">newspaper-article</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/Patent</misc:source>
				<misc:target misc:use="996a">Patent</misc:target>
				<misc:target misc:use="997a">patent</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">j</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/opinion</misc:source>
				<misc:target misc:use="996a">Stellungnahme</misc:target>
				<misc:target misc:use="997a">opinion</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m/a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/other</misc:source>
				<misc:target misc:use="996a">sonstige</misc:target>
				<misc:target misc:use="997a">other</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">#</misc:target>
				<misc:target misc:use="multipart-resource">#</misc:target>
				<misc:target misc:use="nature-of-contents">|</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/paper</misc:source>
				<misc:target misc:use="996a">Forschungspapier</misc:target>
				<misc:target misc:use="997a">paper</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/proceedings</misc:source>
				<misc:target misc:use="996a">Konferenzband</misc:target>
				<misc:target misc:use="997a">proceedings</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">s/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">1</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/Report</misc:source>
				<misc:target misc:use="996a">Bericht</misc:target>
				<misc:target misc:use="997a">report</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/series</misc:source>
				<misc:target misc:use="996a">Reihe</misc:target>
				<misc:target misc:use="997a">series</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">s</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:source>
				<misc:target misc:use="996a">Vortrag</misc:target>
				<misc:target misc:use="997a">talk-at-event</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">#</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/Thesis</misc:source>
				<misc:target misc:use="996a">Hochschulschrift</misc:target>
				<misc:target misc:use="997a">thesis</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">m</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/film</misc:source>
				<misc:target misc:use="996a">Film</misc:target>
				<misc:target misc:use="997a">film</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/data-publication</misc:source>
				<misc:target misc:use="996a">Datenpublikation</misc:target>
				<misc:target misc:use="997a">data publication</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/pre-registration-paper</misc:source>
				<misc:target misc:use="996a">Pre-Registration Paper</misc:target>
				<misc:target misc:use="997a">pre-registration paper</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/registered-report</misc:source>
				<misc:target misc:use="996a">Registered Report</misc:target>
				<misc:target misc:use="997a">registered report</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/preprint</misc:source>
				<misc:target misc:use="996a">Preprint</misc:target>
				<misc:target misc:use="997a">preprint</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/blog-post</misc:source>
				<misc:target misc:use="996a">Blogbeitrag</misc:target>
				<misc:target misc:use="997a">blog post</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/interview</misc:source>
				<misc:target misc:use="996a">Interview</misc:target>
				<misc:target misc:use="997a">interview</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/escidoc/metadata/ves/publication-types/software</misc:source>
				<misc:target misc:use="996a">Software</misc:target>
				<misc:target misc:use="997a">software</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a/m</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>http://purl.org/eprint/type/review-article</misc:source>
				<misc:target misc:use="996a">Review Article</misc:target>
				<misc:target misc:use="997a">review article</misc:target>
				<misc:target misc:use="type-of-record">a</misc:target>
				<misc:target misc:use="bibliographic-level">a</misc:target>
				<misc:target misc:use="multipart-resource">b</misc:target>
				<misc:target misc:use="nature-of-contents">#</misc:target>
				<misc:target misc:use="conference">0</misc:target>
				<misc:target misc:use="festschrift">0</misc:target>
			</misc:mapping>
		</misc:mapping-table>
	</xsl:variable>
	<xsl:function name="misc-marc:pubman-genre" as="xs:string">
		<xsl:param name="marc:record" as="element(marc:record)"/>
		<xsl:variable name="df996a" as="xs:string?" select="$marc:record/marc:datafield[@tag eq '996']/marc:subfield[@code eq 'a']"/>
		<xsl:variable name="df997a" as="xs:string?" select="$marc:record/marc:datafield[@tag eq '997']/marc:subfield[@code eq 'a']"/>
		<xsl:variable name="leader" as="xs:string?" select="$marc:record/marc:leader"/>
		<xsl:variable name="controlfield-008" as="xs:string?" select="$marc:record/marc:controlfield[@tag eq '008']"/>
		<xsl:variable name="type-of-record" as="xs:string" select="string(translate(misc-marc:type-of-record($leader), ' |', '##') )"/>
		<xsl:variable name="bibliographic-level" as="xs:string" select="string(translate(misc-marc:bibliographic-level($leader), ' |', '##') )"/>
		<xsl:variable name="multipart-resource" as="xs:string" select="string(translate(misc-marc:multipart-resource-record-level($leader), ' |', '##') )"/>
		<xsl:variable name="nature-of-contents" as="xs:string" select="string(translate(misc-marc:nature-of-contents($marc:record), ' |', '##') )"/>
		<xsl:variable name="conference" as="xs:string" select="string(translate(misc-marc:conference($marc:record), ' |', '##') )"/>
		<xsl:variable name="festschrift" as="xs:string" select="string(translate(misc-marc:festschrift($marc:record), ' |', '##') )"/>
		<xsl:variable name="self-has-issn" as="xs:boolean" select="some $i in $marc:record/marc:datafield[@tag eq '022']/marc:subfield[@code eq 'a'] satisfies normalize-space($i)"/>
		<xsl:variable name="self-has-isbn" as="xs:boolean" select="some $i in $marc:record/marc:datafield[@tag eq '020']/marc:subfield[@code eq 'a'] satisfies normalize-space($i)"/>
		<xsl:variable name="host-item-has-issn" as="xs:boolean" select="boolean($marc:record/marc:datafield[@tag eq '773']/marc:subfield[@code eq 'x'][normalize-space(.)])"/>
		<xsl:variable name="host-item-has-isbn" as="xs:boolean" select="boolean($marc:record/marc:datafield[@tag eq '773']/marc:subfield[@code eq 'z'][normalize-space(.)])"/>
		<xsl:variable name="host-item-7-3" as="xs:string?" select="$marc:record/marc:datafield[@tag eq '773']/marc:subfield[@code eq '7'][string-length(normalize-space(.) ) eq 4]/substring(normalize-space(.), 4, 1)"/>
		<xsl:variable name="leader-matches" as="element(misc:mapping)*" select="$local:mapping_pubman_genres//misc:mapping [if ($type-of-record = ('', '#') ) then true() else misc:target[@misc:use eq 'type-of-record'] eq $type-of-record] [if ($bibliographic-level = ('', '#') ) then true() else misc:target[@misc:use eq 'bibliographic-level'] eq $bibliographic-level] [if ($multipart-resource = ('', '#') ) then true() else misc:target[@misc:use eq 'multipart-resource'] eq $multipart-resource] "/>
		<xsl:variable name="cf008-matches" as="element(misc:mapping)*" select="$local:mapping_pubman_genres//misc:mapping [if ($nature-of-contents = ('', '#') ) then true() else misc:target[@misc:use eq 'nature-of-contents'] eq $nature-of-contents] [if ($conference = ('', '#') ) then true() else misc:target[@misc:use eq 'conference'] eq $conference] [if ($festschrift = ('', '#') ) then true() else misc:target[@misc:use eq 'festschrift'] eq $festschrift] "/>
		<xsl:choose>
			<xsl:when test="normalize-space($df996a) and count(key('marc-996a-to-pubman', normalize-space($df996a), $local:mapping_pubman_genres ) ) eq 1">
				<xsl:sequence select="key('marc-996a-to-pubman', normalize-space($df996a), $local:mapping_pubman_genres)/misc:source"/>
			</xsl:when>
			<xsl:when test="normalize-space($df997a) and count(key('marc-997a-to-pubman', normalize-space($df997a), $local:mapping_pubman_genres) ) eq 1">
				<xsl:sequence select="key('marc-997a-to-pubman', normalize-space($df997a), $local:mapping_pubman_genres)/misc:source"/>
			</xsl:when>
			<xsl:when test="normalize-space($leader) and count($leader-matches) eq 1">
				<xsl:sequence select="$leader-matches/misc:source"/>
			</xsl:when>
			<xsl:when test="normalize-space($controlfield-008) and count($cf008-matches) eq 1">
				<xsl:sequence select="$cf008-matches/misc:source"/>
			</xsl:when>
			<xsl:when test="normalize-space($leader) and normalize-space($controlfield-008) and count($leader-matches intersect $cf008-matches) eq 1">
				<xsl:sequence select="($leader-matches intersect $cf008-matches)/misc:source"/>
			</xsl:when>
			<xsl:when test="normalize-space($controlfield-008) and count($local:mapping_pubman_genres//misc:mapping[misc:target[@misc:use eq 'nature-of-contents'] eq $nature-of-contents]) eq 1">
				<xsl:sequence select="$local:mapping_pubman_genres//misc:mapping[misc:target[@misc:use eq 'nature-of-contents'] eq $nature-of-contents]/misc:source"/>
			</xsl:when>
			<xsl:when test="$host-item-7-3 eq 'c'">
				<xsl:sequence select="'http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition'"/>
			</xsl:when>
			<xsl:when test="not($self-has-issn or $self-has-isbn) and ($host-item-has-issn or ($host-item-7-3 eq 'b') or ($host-item-7-3 eq 's') )">
				<xsl:sequence select="'http://purl.org/escidoc/metadata/ves/publication-types/article'"/>
			</xsl:when>
			<xsl:when test="not($self-has-issn or $self-has-isbn) and ($host-item-has-isbn or ($host-item-7-3 eq 'a') or ($host-item-7-3 eq 'm') )">
				<xsl:sequence select="'http://purl.org/eprint/type/BookItem'"/>
			</xsl:when>
			<xsl:when test="$self-has-issn">
				<xsl:sequence select="'http://purl.org/escidoc/metadata/ves/publication-types/journal'"/>
			</xsl:when>
			<xsl:when test="$self-has-isbn">
				<xsl:sequence select="'http://purl.org/eprint/type/Book'"/>
			</xsl:when>
			<xsl:when test="$type-of-record eq 'm' and $bibliographic-level eq 'm'">
				<xsl:sequence select="'http://purl.org/eprint/type/Book'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="'http://purl.org/escidoc/metadata/ves/publication-types/other'"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[mapping_commons_marc.xsl#misc-marc:pubman-genre] could not retrieve escidoc:genre from marc record 
						<xsl:value-of select="if (normalize-space($df997a) ) then concat(' (', $df997a, ')') else () "/> (Leader: »
						<xsl:value-of select="$leader"/>«)
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:type-of-record" as="xs:string?">
		<xsl:param name="leader" as="xs:string?"/>
		<xsl:sequence select="substring($leader, 7, 1)"/>
	</xsl:function>
	<xsl:function name="misc-marc:bibliographic-level" as="xs:string?">
		<xsl:param name="leader" as="xs:string?"/>
		<xsl:sequence select="substring($leader, 8, 1)"/>
	</xsl:function>
	<xsl:function name="misc-marc:multipart-resource-record-level" as="xs:string?">
		<xsl:param name="leader" as="xs:string?"/>
		<xsl:sequence select="substring($leader, 20, 1)"/>
	</xsl:function>
	<xsl:function name="misc-marc:nature-of-contents" as="xs:string">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="type-of-cf008" as="xs:string" select="misc-marc:type-of-cf008($MARC_record)"/>
		<xsl:choose>
			<xsl:when test="$type-of-cf008 = ('Books')">
				<xsl:sequence select="substring($MARC_record/marc:controlfield[@tag eq '008'], 25, 1)"/>
			</xsl:when>
			<xsl:when test="$type-of-cf008 = ('Continuing Resources')">
				<xsl:sequence select="substring($MARC_record/marc:controlfield[@tag eq '008'], 26, 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:conference" as="xs:string">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="type-of-cf008" as="xs:string" select="misc-marc:type-of-cf008($MARC_record)"/>
		<xsl:choose>
			<xsl:when test="$type-of-cf008 = ('Books', 'Continuing Resources')">
				<xsl:sequence select="substring($MARC_record/marc:controlfield[@tag eq '008'], 30, 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:festschrift" as="xs:string?">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="type-of-cf008" as="xs:string" select="misc-marc:type-of-cf008($MARC_record)"/>
		<xsl:choose>
			<xsl:when test="$type-of-cf008 = ('Books')">
				<xsl:sequence select="substring($MARC_record/marc:controlfield[@tag eq '008'], 31, 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:type-of-date_publication-status" as="xs:string?">
		<xsl:param name="controlfield-008" as="xs:string?"/>
		<xsl:sequence select="substring($controlfield-008, 7, 1)"/>
	</xsl:function>
	<xsl:function name="misc-marc:eterms_creator_role-to-marc_relator_code" as="xs:string">
		<xsl:param name="eterms_creator-role" as="xs:string?"/>
		<xsl:variable name="marc-relator-code" as="xs:string?" select="key('pubman-to-marc', normalize-space($eterms_creator-role), $local:mapping_marc_relators)/misc:target[@misc:use eq 'code-4']"/>
		<xsl:choose>
			<xsl:when test="normalize-space($marc-relator-code)">
				<xsl:sequence select="$marc-relator-code"/>
			</xsl:when>
			<xsl:otherwise>oth</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:eterms_creator_role-to-marc_relator_term" as="xs:string?">
		<xsl:param name="eterms_creator-role" as="xs:string?"/>
		<xsl:variable name="marc-relator-term" as="xs:string?" select="key('pubman-to-marc', normalize-space($eterms_creator-role), $local:mapping_marc_relators)/misc:target[@misc:use eq 'code-e']"/>
		<xsl:sequence select="if (normalize-space($marc-relator-term)) then $marc-relator-term else ()"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication_type-to-marc_996_a" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="normalize-space(key('pubman_genre-to-marc', normalize-space($pubman_publication_type), $local:mapping_pubman_genres)/misc:target[@misc:use eq '996a'] )"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication_type-to-marc_997_a" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="normalize-space(key('pubman_genre-to-marc', normalize-space($pubman_publication_type), $local:mapping_pubman_genres)/misc:target[@misc:use eq '997a'] )"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication-type-to-marc_nature-of-contents" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="local:leader-controlfield-item('nature-of-contents', $pubman_publication_type)"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication-type-to-marc_type-of-record" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="local:leader-controlfield-item('type-of-record', $pubman_publication_type)"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication-type-to-marc_bibliographic-level" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="local:leader-controlfield-item('bibliographic-level', $pubman_publication_type)"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication-type-to-marc_multipart-resource" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="local:leader-controlfield-item('multipart-resource', $pubman_publication_type)"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication-type-to-marc_conference" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="local:leader-controlfield-item('conference', $pubman_publication_type)"/>
	</xsl:function>
	<xsl:function name="misc-marc:pubman_publication-type-to-marc_festschrift" as="xs:string">
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:sequence select="local:leader-controlfield-item('festschrift', $pubman_publication_type)"/>
	</xsl:function>
	<xsl:function name="local:leader-controlfield-item" as="xs:string">
		<xsl:param name="use" as="xs:string"/>
		<xsl:param name="pubman_publication_type" as="xs:string?"/>
		<xsl:variable name="temp" as="xs:string?" select="key('pubman_genre-to-marc', normalize-space($pubman_publication_type), $local:mapping_pubman_genres)/misc:target[@misc:use eq $use]"/>
		<xsl:choose>
			<xsl:when test="normalize-space($temp) eq '#'">
				<xsl:sequence select=" ' ' "/>
			</xsl:when>
			<xsl:when test="normalize-space($temp)">
				<xsl:sequence select="$temp"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select=" ' ' "/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:type-of-cf008" as="xs:string">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="leader" as="xs:string?" select="$MARC_record/marc:leader"/>
		<xsl:variable name="type-of-record" as="xs:string" select="misc-marc:type-of-record($leader)"/>
		<xsl:variable name="bibliografic-level" as="xs:string" select="misc-marc:bibliographic-level($leader)"/>
		<xsl:choose>
			<xsl:when test="(($type-of-record = ('a')) and ($bibliografic-level = ('a', 'c', 'd', 'm') ) ) or ($type-of-record = ('t'))">
				<xsl:sequence select="'Books'"/>
			</xsl:when>
			<xsl:when test="($type-of-record = ('a')) and ($bibliografic-level = ('b', 'i', 's'))">
				<xsl:sequence select="'Continuing Resources'"/>
			</xsl:when>
			<xsl:when test="$type-of-record = ('c', 'd', 'i', 'j')">
				<xsl:sequence select="'Music'"/>
			</xsl:when>
			<xsl:when test="$type-of-record = ('e', 'f')">
				<xsl:sequence select="'Maps'"/>
			</xsl:when>
			<xsl:when test="$type-of-record = ('g', 'k', 'o', 'r')">
				<xsl:sequence select="'Visual Materials'"/>
			</xsl:when>
			<xsl:when test="$type-of-record = ('m')">
				<xsl:sequence select="'Computer Files'"/>
			</xsl:when>
			<xsl:when test="$type-of-record = ('p')">
				<xsl:sequence select="'Mixed Materials'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[mapping_commons_marc.xsl#misc-marc:type-of-cf008] could not retrieve type of marc record (Leader: »
						<xsl:value-of select="$leader"/>«)
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:form-of-item" as="xs:string">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="type-of-cf008" as="xs:string" select="misc-marc:type-of-cf008($MARC_record)"/>
		<xsl:choose>
			<xsl:when test="$type-of-cf008 = ('Books', 'Computer Files', 'Music', 'Continuing Resources', 'Mixed Materials')">
				<xsl:sequence select="substring($MARC_record/marc:controlfield[@tag eq '008'], 24, 1)"/>
			</xsl:when>
			<xsl:when test="$type-of-cf008 = ('Maps', 'Visual Materials')">
				<xsl:sequence select="substring($MARC_record/marc:controlfield[@tag eq '008'], 30, 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="misc-marc:marc_relator_code-to-eterms_creator_role" as="xs:string">
		<xsl:param name="relator-code" as="xs:string?"/>
		<xsl:variable name="eterms_creator_role" as="xs:string?" select="key('marc-4-to-pubman', normalize-space($relator-code), $local:mapping_marc_relators)/misc:source"/>
		<xsl:sequence select="if (normalize-space($eterms_creator_role)) then $eterms_creator_role else ''"/>
	</xsl:function>
	<xsl:function name="misc-marc:marc_relator_term-to-eterms_creator_role" as="xs:string">
		<xsl:param name="relator-term" as="xs:string?"/>
		<xsl:variable name="eterms_creator_role" as="xs:string?" select="key('marc-e-to-pubman', normalize-space($relator-term), $local:mapping_marc_relators)/misc:source"/>
		<xsl:sequence select="if (normalize-space($eterms_creator_role)) then $eterms_creator_role else ''"/>
	</xsl:function>
	<xsl:function name="misc-marc:language" as="xs:string">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:sequence select="string(misc-marc:languages($MARC_record)[1])"/>
	</xsl:function>
	<xsl:function name="misc-marc:languages" as="xs:string*">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="lang-from-cf008" as="xs:string?" select="normalize-space(translate(substring($MARC_record/marc:controlfield[@tag eq '008'], 36, 3), '# |', ''))[misc:is-iso-639-2-b(.)]"/>
		<xsl:variable name="lang-from-df041-iso639-2" as="xs:string*" select="$MARC_record/marc:datafield[@tag eq '041']/marc:subfield[@code eq 'a'][misc:is-iso-639-2-b(.)]"/>
		<xsl:variable name="lang-from-df041-iso639-3" as="xs:string*" select="$MARC_record/marc:datafield[@tag eq '041']/marc:subfield[@code eq 'a']/misc:iso-639-3_to_iso-639-2(.)[normalize-space(.)]"/>
		<xsl:variable name="all-lang" as="xs:string*" select="$lang-from-cf008, $lang-from-df041-iso639-2, $lang-from-df041-iso639-3"/>
		<xsl:variable name="first-lang" as="xs:string?" select="$all-lang[1]"/>
		<xsl:sequence select="$first-lang, distinct-values($all-lang)[. ne $first-lang]"/>
	</xsl:function>
	<xsl:function name="misc-marc:display-constant-505" as="xs:string">
		<xsl:param name="datafield-505" as="element(marc:datafield)"/>
		<xsl:sequence select="misc-marc:display-constant-505($datafield-505, misc-marc:language($datafield-505/..))"/>
	</xsl:function>
	<xsl:function name="misc-marc:display-constant-505" as="xs:string">
		<xsl:param name="datafield-505" as="element(marc:datafield)"/>
		<xsl:param name="language-as-iso-639-2" as="xs:string?"/>
		<xsl:variable name="ind1" as="xs:string" select="normalize-space($datafield-505/@ind1)"/>
		<xsl:choose>
			<xsl:when test="$ind1 eq '0'">
				<xsl:choose>
					<xsl:when test="$language-as-iso-639-2 eq 'ger'">Inhalt: </xsl:when>
					<xsl:otherwise>Contents: </xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$ind1 eq '1'">
				<xsl:choose>
					<xsl:when test="$language-as-iso-639-2 eq 'ger'">Vorläufiger Inhalt: </xsl:when>
					<xsl:otherwise>Incomplete contents: </xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$ind1 eq '2'">
				<xsl:choose>
					<xsl:when test="$language-as-iso-639-2 eq 'ger'">Enthält u.a.: </xsl:when>
					<xsl:otherwise>Partial contents: </xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>